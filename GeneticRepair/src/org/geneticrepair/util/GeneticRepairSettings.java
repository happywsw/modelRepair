package org.geneticrepair.util;

import nl.tue.declare.appl.design.constraint.gui.IConstraintGroupActionListener;

public class GeneticRepairSettings {
	private int populationSize;
	private int initialPopulationType;
	private int maxNumGenerations;
	private double mutationRate;
	private int mutationType;
	private int crossoverType;
	private double crossoverRate;
	private long seed;
	private int fitnessType;
	private int selectionMethodType;
	private double elitismRate;
	private long power; //to be used during the building of the causal/start/end matrices.
	private double alpha;
	private double belta;
	
	public GeneticRepairSettings(){
		
	}
	
	public GeneticRepairSettings(int populationSize,int maxNumGeneration,double alpha,double belta){
		this.populationSize = populationSize;
		this.maxNumGenerations = maxNumGeneration;
		this.alpha = alpha;
		this.belta = belta;
		
		this.initialPopulationType = GeneticRepairConstants.INITIAL_POPULATION_TYPE;
		this.mutationRate =  GeneticRepairConstants.MUTATION_RATE;
		this.mutationType = GeneticRepairConstants.MUTATION_TYPE;
		this.crossoverType = GeneticRepairConstants.CROSSOVER_TYPE;
		this.crossoverRate = GeneticRepairConstants.CROSSOVER_RATE;
		this.seed = GeneticRepairConstants.SEED;
		this.fitnessType = GeneticRepairConstants.FITNESS_TYPE;
		this.selectionMethodType = GeneticRepairConstants.SELECTION_TYPE;
		this.elitismRate = GeneticRepairConstants.ELITISM_RATE;
		this.power = GeneticRepairConstants.POWER;
		
	}

	public int getPopulationSize() {
		return populationSize;
	}

	public void setPopulationSize(int populationSize) {
		this.populationSize = populationSize;
	}

	public int getInitialPopulationType() {
		return initialPopulationType;
	}

	public void setInitialPopulationType(int initialPopulationType) {
		this.initialPopulationType = initialPopulationType;
	}

	public int getMaxNumGenerations() {
		return maxNumGenerations;
	}

	public void setMaxNumGenerations(int maxNumGenerations) {
		this.maxNumGenerations = maxNumGenerations;
	}

	public double getMutationRate() {
		return mutationRate;
	}

	public void setMutationRate(double mutationRate) {
		this.mutationRate = mutationRate;
	}

	public int getMutationType() {
		return mutationType;
	}

	public void setMutationType(int mutationType) {
		this.mutationType = mutationType;
	}

	public int getCrossoverType() {
		return crossoverType;
	}

	public void setCrossoverType(int crossoverType) {
		this.crossoverType = crossoverType;
	}

	public double getCrossoverRate() {
		return crossoverRate;
	}

	public void setCrossoverRate(double crossoverRate) {
		this.crossoverRate = crossoverRate;
	}

	public long getSeed() {
		return seed;
	}

	public void setSeed(long seed) {
		this.seed = seed;
	}

	public int getFitnessType() {
		return fitnessType;
	}

	public void setFitnessType(int fitnessType) {
		this.fitnessType = fitnessType;
	}

	public int getSelectionMethodType() {
		return selectionMethodType;
	}

	public void setSelectionMethodType(int selectionMethodType) {
		this.selectionMethodType = selectionMethodType;
	}

	public double getElitismRate() {
		return elitismRate;
	}

	public void setElitismRate(double elitismRate) {
		this.elitismRate = elitismRate;
	}

	public long getPower() {
		return power;
	}

	public void setPower(long power) {
		this.power = power;
	}

	public double getAlpha() {
		return alpha;
	}

	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}

	public double getBelta() {
		return belta;
	}

	public void setBelta(double belta) {
		this.belta = belta;
	}
	
	
}
