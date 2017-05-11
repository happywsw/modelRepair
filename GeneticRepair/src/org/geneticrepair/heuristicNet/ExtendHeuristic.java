package org.geneticrepair.heuristicNet;

import org.processmining.framework.log.LogEvents;
import org.processmining.framework.models.heuristics.HeuristicsNet;

public class ExtendHeuristic extends HeuristicsNet{
	double similarity;
	double logFitness;
	public ExtendHeuristic(LogEvents events) {
		super(events);
		// TODO Auto-generated constructor stub
		
		this.similarity = 0;
		this.logFitness = 0;
	}
	
	public void resetlogFitness() {
		setFitness(0);
	}

	public void setlogFitness(double d) {
		logFitness = d;
	}
	
	public void resetSimilarity() {
		setFitness(0);
	}

	public void setSimilarity(double d) {
		similarity = d;
	}
	
	public double getLogFitness(){
		return logFitness;
	}
	
	public double getSimilarity(){
		return similarity;
	}
}