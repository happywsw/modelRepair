package org.geneticrepair.similarity;
import java.util.List;
import java.util.Map;

import org.geneticrepair.similarity.highlevelop.HighLevelOP;
import org.jbpt.petri.log.Log;
import org.processmining.framework.models.ModelGraphEdge;
import org.processmining.framework.models.ModelGraphVertex;
import org.processmining.framework.models.epcpack.ConfigurableEPC;
import org.processmining.framework.models.epcpack.EPCConnector;
import org.processmining.framework.models.epcpack.EPCEvent;
import org.processmining.framework.models.epcpack.EPCFunction;
import org.semanticweb.kaon2.ne;

import java.io.File;
import java.io.Reader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import de.bpt.hpi.graph.Graph;
import graph.EPCHelper;
import nl.tue.tm.is.epc.Arc;
import nl.tue.tm.is.epc.Connector;
import nl.tue.tm.is.epc.EPC;
import nl.tue.tm.is.epc.Event;
import nl.tue.tm.is.epc.Function;
import nl.tue.tm.is.epc.Node;
public class ModelSimilarity {
	
	//calculate the similarity between original and modify model
	public double calculateSim(String originalModel, String modifyModel) {
		
		EPC originalEPC = EPC.loadEPML(originalModel);
		EPC modifyEPC = EPC.loadEPML(modifyModel);
		originalEPC.cleanEPC();
		modifyEPC.cleanEPC();
		
		EPCHelper epc = new EPCHelper(originalEPC);
		EPCHelper epc2 = new EPCHelper(modifyEPC);
		
		Graph oriGraph = epc.getGraph();
		Graph modGraph = epc2.getGraph();
		
		//List<String> operationList = HighLevelOP.GetHighLevelOPList(oriGraph, modGraph);
		
		double similarity = HighLevelOP.GetHighLevelOPSimilarity(oriGraph, modGraph);
		DecimalFormat format = new DecimalFormat("0.00");

	//	System.out.printf("%9s", operationList.size() + "/" + format.format(similarity));
		System.out.printf("%9s",format.format(similarity));
		return similarity;
	}
	
	public double calculateSim(EPC originalEPC, EPC modifyEPC) {
		
		EPCHelper epc = new EPCHelper(originalEPC);
		EPCHelper epc2 = new EPCHelper(modifyEPC);
		
		Graph oriGraph = epc.getGraph();
		Graph modGraph = epc2.getGraph();
		
		//List<String> operationList = HighLevelOP.GetHighLevelOPList(oriGraph, modGraph);
		
		double similarity = HighLevelOP.GetHighLevelOPSimilarity(oriGraph, modGraph);

		return similarity;
	}
		
	public static void main(String[] args){
		
		//calculate similarity between original model and modify model
		
		String originalPath =  System.getProperty("user.dir") + "\\models\\non-free-choice_R_ABPM.epml";
		String modifyPath =    System.getProperty("user.dir") + "\\models\\non-free-choice_opt_1.0.epml";
		EPC originalEPC = EPC.loadEPML(originalPath);
		EPC modifyEPC = EPC.loadEPML(modifyPath);
		originalEPC.cleanEPC();
		modifyEPC.cleanEPC();
		
		long start = System.nanoTime();
		double similarity = new ModelSimilarity().calculateSim(originalEPC, modifyEPC);
		System.out.println("time"+(System.nanoTime()-start)/Math.pow(10, 9)+"s");
		//calculate delta(log,originalModel)
		System.out.println("similarity:"+similarity);
	}
}
