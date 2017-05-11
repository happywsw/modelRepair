package org.geneticrepair.util;


public class GeneticRepairConstants {
	public static final int INITIAL_POPULATION_TYPE = 0;
	public static final int POPULATION_SIZE = 100;
	public static final double ELITISM_RATE = 0.02;
	public static final int MAX_GENERATION = 1000;
	public static final int FITNESS_TYPE = 4;
	public static final int SELECTION_TYPE = 1;
	public static final double CROSSOVER_RATE = 0.8;
	public static final int CROSSOVER_TYPE = 3;
	public static final double MUTATION_RATE = 0.2;
	public static final int MUTATION_TYPE = 2;
	public static final long POWER = 1;

	public static final long SEED = 1;
	public static final double KAPPA = 0.025;
	public static final double GAMMA = 0.025;
	
	public static final double alpha = 0.5;
	public static final double belta = 0.5;

	public static final String FN = "File Name";
	public static final String BF = "Best Fitness";
	public static final String AF = "Average Fitness";
	public static final String SD = "Standard Deviation";
	public static final String PC = "Proper Completion";
	public static final String PS = "Population Size";
	public static final String GN = "Generation Number";
	public static final String FT = "Fitness Type";
	public static final String TT = "Tournament Type";
	public static final String MR = "Maximum Runs";
	public static final String S = "Seed";
	public static final String ST = "Selection Type";
	public static final String UGO = "Use Genetic Operators";
	public static final String MRt = "Mutation Rate";
	public static final String MTp = "Mutation Type";
	public static final String CRt = "Crossover Rate";
	public static final String CTp = "Crossover Type";
	public static final String IPT = "Initial Population Type";
	public static final String POW = "Power";
	public static final String ELI = "Elitism Rate";
	public static final String ET = "Elapsed Time(ms)";
	
	
	public static final String EV = "Evaluation value";
	public static final String FP = "Fitness Parameters";
	public static final String SIM = "Similarity";
	public static final String ALPHA = "Alpha Parameters";
	public static final String BELTA  = "Belta Parameters";
	public static final String DO = "Deviation Original Model";
	public static final String DR = "Deviation Repair Model";

	public static final String[] logLine = {FN, BF, AF, SD, PC, PS, GN, FT, MR, ET, 
										   EV,FP,SIM,ALPHA,BELTA,DO,DR};

}
