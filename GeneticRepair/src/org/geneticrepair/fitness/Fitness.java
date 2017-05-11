/***********************************************************
 *      This software is part of the ProM package          *
 *             http://www.processmining.org/               *
 *                                                         *
 *            Copyright (c) 2003-2006 TU/e Eindhoven       *
 *                and is licensed under the                *
 *            Common Public License, Version 1.0           *
 *        by Eindhoven University of Technology            *
 *           Department of Information Systems             *
 *                 http://is.tm.tue.nl                     *
 *                                                         *
 **********************************************************/

package org.geneticrepair.fitness;

import java.util.Iterator;
import java.util.Random;

import org.geneticrepair.similarity.ModelSimilarity;
import org.geneticrepair.similarity.util.HNNetToEPC;
import org.geneticrepair.util.GeneticRepairConstants;
import org.geneticrepair.util.MapIdenticalIndividuals;
import org.processmining.converting.HNNetToEPCConverter;
import org.processmining.framework.log.LogReader;
import org.processmining.framework.log.ProcessInstance;
import org.processmining.framework.models.epcpack.ConfigurableEPC;
import org.processmining.framework.models.heuristics.ExtraBehaviorParser;
import org.processmining.framework.models.heuristics.HNSet;
import org.processmining.framework.models.heuristics.HNSubSet;
import org.processmining.framework.models.heuristics.HeuristicsNet;
import org.processmining.framework.util.MethodsForWorkflowLogDataStructures;
import org.processmining.mining.epcmining.EPCResult;

import nl.tue.tm.is.epc.EPC;



public class Fitness {
	private double fitness = 0;
	private double similarity = 0;
	private double logFitness = 0;
	private EPC epc = null;
	private LogReader logReader = null;
	private HeuristicsNet[] population = null;
	private ExtraBehaviorParser[] parser = null;

	private double[] numPIsWithMissingTokens = null; // PI = process instance
	private double[] numMissingTokens = null; // PI = process instance
	private double[] numPIsWithExtraTokensLeftBehind = null;
	private double[] numExtraTokensLeftBehind = null;
	private double[] numParsedWMEs = null;
	private double[] numEnabledWMEs = null;
	private double[] numIntersectingDuplicatesPerInputElement;
	private double[] numIntersectingDuplicatesPerOutputElement;
	private double maximumNumberIntersectingElements = 0.0;
	private MapIdenticalIndividuals mapping = null;
	private double kappa = 0.025;
	private double gamma = 0.0;
	private double alpha;
	private double belta;
	private Random generator = null;
	private ModelSimilarity modelUtil = new ModelSimilarity();
	private long time = 0;
	private long time2 = 0;
	// public DTExtraBehaviorPunishmentFitness(LogReader log) {
	// generator = new Random(Long.MAX_VALUE);
	// logReader = log;
	// }

	/**
	 * Creates a fitness object for the given log and parameters. NOTE: If the
	 * array parameters does not contain all the parameter values, the missing
	 * ones are assigned to zero.
	 */
	public Fitness(LogReader log, EPC epc, double alpha, double belta) {
		generator = new Random(Long.MAX_VALUE);
		this.logReader = log;
		this.epc = epc;
		this.alpha = alpha;
		this.belta = belta;
	}

	public HeuristicsNet[] calculate(HeuristicsNet[] pop) {

		population = pop;
		mapping = new MapIdenticalIndividuals(population);
		createParser();
		createFitnessVariables();
		resetDuplicatesActualFiringAndArcUsage();
		calculatePartialFitness();
		calculateStructuralPunishment();

		return assignFitness();

	}

	private void calculateStructuralPunishment() {

		// Motivation: The union sets of the subsets in the I/O of duplicates
		// should not intersect. In other words, duplicates cannot share input
		// or
		// output elements. Whenever this situation occurs, the individual
		// is punished. The more common elements the duplicates have, the higher
		// the punishment.

		maximumNumberIntersectingElements = 0;
		// calculating the punishment for every individual in the population
		for (int i = 0; i < population.length; i++) {
			numIntersectingDuplicatesPerInputElement[i] = checkIntersectingElements(
					population[i].getReverseDuplicatesMapping(), population[i].getDuplicatesMapping(),
					population[i].getInputSets());
			numIntersectingDuplicatesPerOutputElement[i] = checkIntersectingElements(
					population[i].getReverseDuplicatesMapping(), population[i].getDuplicatesMapping(),
					population[i].getOutputSets());

			if (maximumNumberIntersectingElements < (numIntersectingDuplicatesPerInputElement[i]
					+ numIntersectingDuplicatesPerOutputElement[i])) {
				maximumNumberIntersectingElements = numIntersectingDuplicatesPerInputElement[i]
						+ numIntersectingDuplicatesPerOutputElement[i];
			}

		}

	}

	private double checkIntersectingElements(HNSubSet[] duplicates, int[] duplicatesLabel, HNSet[] sets) {
		double structuralPunishment = 0;

		for (int i = 0; i < duplicates.length; i++) {
			// creating the counter for the intersections...
			int[] intersection = new int[duplicates.length];

			if (duplicates[i].size() > 1) { // the element has duplicates...
				for (int k = 0; k < duplicates[i].size(); k++) {
					int[] union = mapToDuplicateLabel(duplicatesLabel, HNSet.getUnionSet(sets[duplicates[i].get(k)]));
					for (int m = 0; m < union.length; m++) {
						intersection[union[m]]++;
					}

				}

				// add the partial punishment due to intersections...
				for (int j = 0; j < intersection.length; j++) {
					if (intersection[j] > 1) {
						structuralPunishment += intersection[j];
					}
				}
			}
		}

		return structuralPunishment;
	}

	private int[] mapToDuplicateLabel(int[] duplicatesLabel, HNSubSet union) {

		int[] mapping = new int[union.size()];

		for (int i = 0; i < union.size(); i++) {
			mapping[i] = duplicatesLabel[union.get(i)];
		}

		return mapping;
	}

	private void resetDuplicatesActualFiringAndArcUsage() {
		for (int i = 0; i < population.length; i++) {
			population[i].resetDuplicatesActualFiring();
			population[i].resetArcUsage();
		}
	}

	private void createParser() {
		// creating a parser for every individual
		parser = new ExtraBehaviorParser[population.length];
		for (int i = 0; i < parser.length; i++) {
			if (mapping.getMap(i) < 0) {
				parser[i] = new ExtraBehaviorParser(population[i], generator);
			}
		}
	}

	private void createFitnessVariables() {
		numPIsWithMissingTokens = new double[population.length];
		numMissingTokens = new double[population.length];
		numPIsWithExtraTokensLeftBehind = new double[population.length];
		numExtraTokensLeftBehind = new double[population.length];
		numParsedWMEs = new double[population.length];
		numEnabledWMEs = new double[population.length];
		numIntersectingDuplicatesPerInputElement = new double[population.length];
		numIntersectingDuplicatesPerOutputElement = new double[population.length];
	}

	private void calculatePartialFitness() {

		ProcessInstance pi = null;
		int numSimilarPIs = 0;
		int numMissingTokens = 0;
		int numExtraTokensLeftBehind = 0;

		Iterator logReaderInstanceIterator = logReader.instanceIterator();
		while (logReaderInstanceIterator.hasNext()) {
			pi = (ProcessInstance) logReaderInstanceIterator.next();
			numSimilarPIs = MethodsForWorkflowLogDataStructures.getNumberSimilarProcessInstances(pi);
			for (int i = 0; i < population.length; i++) {
				if (mapping.getMap(i) < 0) { // we need to compute the partial
												// fitness
					parser[i].parse(pi);
					// partial assignment to variables
					numMissingTokens = parser[i].getNumMissingTokens();
					if (numMissingTokens > 0) {
						this.numPIsWithMissingTokens[i] += numSimilarPIs;
						this.numMissingTokens[i] += (numMissingTokens * numSimilarPIs);
					}

					numExtraTokensLeftBehind = parser[i].getNumExtraTokensLeftBehind();
					if (numExtraTokensLeftBehind > 0) {
						this.numPIsWithExtraTokensLeftBehind[i] += numSimilarPIs;
						this.numExtraTokensLeftBehind[i] += (numExtraTokensLeftBehind * numSimilarPIs);
					}
					numParsedWMEs[i] += (parser[i].getNumParsedElements() * numSimilarPIs);
					numEnabledWMEs[i] += (parser[i].getNumTotalEnabledElements() * numSimilarPIs);
				}
			}
		}
	}

	/*
	 * fitness = f1 - kappa * f2 - gamma * f3; where: f1 is based on parsed
	 * tasks, missing tokens and tokens left behind. f2 is based on the number
	 * of enabled tasks during the parsing. f3 is based on the number of
	 * duplicates with intersecting I/O elements.
	 */

	private HeuristicsNet[] assignFitness() {

		double fitness = 0;
		double f1 = 0;
		double f2 = 0;
		double f3 = 0;

		double numATEsAtLog = 0;
		double numPIsAtLog = 0;
		double missingTokensDenominator = 0.001;
		double unusedTokensDenominator = 0.001;
		double maxEnabledWMEs = Double.MIN_VALUE;
		int indexIdenticalIndividual = 0;

		numATEsAtLog = logReader.getLogSummary().getNumberOfAuditTrailEntries();
		numPIsAtLog = logReader.getLogSummary().getNumberOfProcessInstances();
		maxEnabledWMEs = getMaxValue(numEnabledWMEs);
		
		long numTime = 0;
		long simTime = 0;
		for (int i = 0; i < population.length; i++) {

			indexIdenticalIndividual = mapping.getMap(i);

			if (indexIdenticalIndividual < 0) {
				missingTokensDenominator = numPIsAtLog - numPIsWithMissingTokens[i] + 1;

				unusedTokensDenominator = numPIsAtLog - numPIsWithExtraTokensLeftBehind[i] + 1;

				f1 = (numParsedWMEs[i] - ((numMissingTokens[i] / missingTokensDenominator)
							+ (numExtraTokensLeftBehind[i] / unusedTokensDenominator))) / numATEsAtLog;

				f2 = numEnabledWMEs[i];

				f2 /= maxEnabledWMEs; // normalizing...
					/*
					 * if(maximumNumberIntersectingElements > 0){ f3 =
					 * (numIntersectingDuplicatesPerInputElement[i] +
					 * numIntersectingDuplicatesPerOutputElement[i]) /
					 * maximumNumberIntersectingElements; }else{ f3 = 0.0; }
					 * fitness = f1 - kappa * f2 - gamma * f3;
					 */
				logFitness = (f1 - kappa * f2);
			
					
				long start1 = System.nanoTime();
				EPC modifyEPC = new HNNetToEPC().convert(population[i]);
				time2 += (System.nanoTime() - start1);
				if(modifyEPC != null){
					if(belta!=0){
						//long start2 = System.nanoTime();
						similarity = modelUtil.calculateSim(epc, modifyEPC);
						//time += (System.nanoTime() - start2);
					}
					fitness = alpha * logFitness + belta * similarity;		
				}else{
					fitness = 0;
				}	
			
				population[i].setFitness(fitness);
				population[i].setlogFitness(logFitness);
				population[i].setSimilarity(similarity);

			} else {

				population[i].setFitness(population[indexIdenticalIndividual].getFitness());
				population[i].setSimilarity(population[indexIdenticalIndividual].getSimilarity());
				population[i].setlogFitness(population[indexIdenticalIndividual].getLogFitness());

				population[i]
						.setDuplicatesActualFiring(population[indexIdenticalIndividual].getDuplicatesActualFiring());
				population[i].setArcUsage(population[indexIdenticalIndividual].getArcUsage());

			}
		}
		
		return population;

	}

	private double getMaxValue(double[] array) {
		double max = Double.MIN_VALUE;
		for (int i = 0; i < array.length; i++) {
			if (max < array[i]) {
				max = array[i];
			}
		}
		return max;
	}
	public long getTime(){
		return this.time;
	}
	
	public long getTime2(){
		return this.time2;
	}
}