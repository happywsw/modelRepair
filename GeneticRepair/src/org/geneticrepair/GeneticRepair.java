package org.geneticrepair;
import java.security.PrivilegedActionException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

import org.geneticrepair.fitness.Fitness;
import org.geneticrepair.operation.EnhancedCrossover;
import org.geneticrepair.operation.EnhancedMutation;
import org.geneticrepair.population.BuildInitialPopulation;
import org.geneticrepair.population.BuildNextGeneration;
import org.geneticrepair.population.GeneticMiningMatricesWithHeuristics;
import org.geneticrepair.util.GeneticRepairConstants;
import org.geneticrepair.util.GeneticRepairSettings;
import org.geneticrepair.util.MethodsOverIndividuals;
import org.processmining.framework.log.LogReader;
import org.processmining.framework.models.heuristics.HeuristicsNet;
import org.processmining.mining.geneticrepair.population.GeneticMiningMatrices;
import org.geneticrepair.selection.DTTournamentSelection5;
import nl.tue.tm.is.epc.EPC;



public class GeneticRepair {
	private double alpha;
	private double belta;
	private double logFitness = 0;
	private double similarity = 0;
	
	private LogReader logReader;
	private EPC epc;
	private GeneticRepairSettings setting;
	private HeuristicsNet[] populations = null;
	private Random generator = null;
	
	private long startTime;
	
	private BuildInitialPopulation initialPopulation = null;
	private BuildNextGeneration nextGeneration = null;
	private Fitness fitness = null;
	
	private LogReader log = null;
	private GeneticMiningMatrices genMinMatrices = null;
	private DTTournamentSelection5 selectionMethod = null;
	private  EnhancedCrossover crossover = null;
	private EnhancedMutation mutation = null;
	private long power = GeneticRepairConstants.POWER;
	private double mutationRate = GeneticRepairConstants.MUTATION_RATE;
	private double crossRate = GeneticRepairConstants.CROSSOVER_RATE;
	
	public GeneticRepair(){
		
	}
	
	public GeneticRepair(LogReader logReader,EPC epc,GeneticRepairSettings setting){		
		if((logReader == null) || (epc == null) ){
			System.out.println("The log or EPC model is invaild...");
		}
		this.log = logReader;
		this.epc = epc;
		this.setting = setting;
		this.alpha = setting.getAlpha();
		this.belta = setting.getBelta();
		//modelRepair();

	}
	
	public HeuristicsNet[] modelRepair(){
		double alpha = setting.getAlpha();
		double belta = setting.getBelta();
		long elapsedTime = 0;
		boolean logIndividuals;
		int numTimesBestIndividualIsTheSame = 0;
		int numRuns = 0;
		double mean = 0,variance = 0,standardDeviation = 0;
		
		HeuristicsNet bestIndividual = null;
		
		long startTime = System.nanoTime();
		
		startGlobalVariables(log);
		//System.out.println("Build the initial population.......");
		
		//this.startTime = (new Date()).getTime();
		
		populations = initialPopulation.build(new HeuristicsNet[setting.getPopulationSize()]);
		
		
		//System.out.println("Calculate the fitness..................");
		
		populations = fitness.calculate(populations);
		Arrays.sort(populations);
		
		bestIndividual = populations[populations.length - 1];
		//mean = calculateMean(populations);
		//standardDeviation = calculateStandardDeviation(populations);
		//variance = calculateVariance(populations);
		//elapsedTime = (new Date()).getTime() - startTime;
		
	//	System.out.println(evaluation,fitness,similarity,mean,);
	//	printStatistics(numRuns,mean,variance,standardDeviation,
		//		+populations[populations.length - 1].getFitness(),elapsedTime);
		
		numRuns++;
		
		while (populations[populations.length - 1].getFitness() < 1.0 && numRuns < setting.getMaxNumGenerations() &&
				numTimesBestIndividualIsTheSame < (setting.getMaxNumGenerations() / 2)) {
			//System.out.println("Building generation " + numRuns + "...........");
			
			if(bestIndividual.equals(populations[populations.length-1])){
				numTimesBestIndividualIsTheSame ++;
			}else{
				bestIndividual = populations[populations.length - 1];
				numTimesBestIndividualIsTheSame = 0;
			}
			
			populations = nextGeneration.build(populations);
			populations = fitness.calculate(populations);
			Arrays.sort(populations);
			
			mean = calculateMean(populations);
						
		
			printStatistics(numRuns,mean,populations[populations.length - 1].getFitness(),populations[populations.length - 1].getLogFitness(),
					populations[populations.length - 1].getSimilarity());
			numRuns++;
		}
		Arrays.sort(populations);
		
		populations = MethodsOverIndividuals.removeUnusedElements(populations, fitness);
		MethodsOverIndividuals.decreasinglyOrderPopulation(populations);
		populations = MethodsOverIndividuals.removeDuplicates(populations);
		//return new DTGeneticMinerResult(populations, log);
		
		System.out.println("time:::"+fitness.getTime()+","+fitness.getTime()/(100*10));
		System.out.println("time2:::"+fitness.getTime2()+","+fitness.getTime()/(100*10));
		return populations;
	}
	
	private void startGlobalVariables(LogReader log) {

		logReader = log;
		
		generator = new Random(setting.getSeed());
		
		genMinMatrices = new GeneticMiningMatricesWithHeuristics(generator, log,power);
		
		initialPopulation = new BuildInitialPopulation(generator, log, genMinMatrices);

		fitness = new Fitness(log,epc,alpha,belta);

		selectionMethod = new DTTournamentSelection5(generator);

		crossover = new EnhancedCrossover(generator);


		mutation = new EnhancedMutation(generator,mutationRate);


		nextGeneration =  new BuildNextGeneration(selectionMethod, generator,
				 crossRate, mutationRate, GeneticRepairConstants.ELITISM_RATE, crossover, mutation);

	}
	
	public double calculateMean(HeuristicsNet[] population){
		double mean = 0;

		for (int i = 0; i < population.length; i++) {
			mean += population[i].getFitness();
		}

		mean /= population.length;

		return mean;
	}
	
	public double calculateStandardDeviation(HeuristicsNet[] population) {
		double standardDeviation = 0;
		double variance = 0;

		variance = calculateVariance(population);
		standardDeviation = Math.sqrt(variance);

		return standardDeviation;
	}

	public double calculateVariance(HeuristicsNet[] population) {
		double mean = 0;
		double variance = 0;

		mean = calculateMean(population);

		for (int i = 0; i < population.length; i++) {
			variance += Math.pow(population[i].getFitness() - mean, 2);
		}

		variance /= population.length;

		return variance;
	}
	
	private void printStatistics(int run, double mean,double bestFitness,double logFitness,double similarity) {

               //setting the separator for the decimals
                DecimalFormatSymbols dfs = new DecimalFormatSymbols();
                dfs.setDecimalSeparator('.');

                //creating the object that will format the doubles
                DecimalFormat df = new DecimalFormat("0.000", dfs);
                df.setDecimalSeparatorAlwaysShown(true);

                //printing the actual numbers
                System.out.println("Generation = " + run + " <|> Average fitness = " +
                                  df.format(mean) + " <|> bestFitness = " + df.format(bestFitness) + " <|> logFitness = " +
                                df.format(logFitness) + " <|> similarity = " +
                                df.format(similarity));

	}
	
	

} 
