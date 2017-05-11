package org.geneticrepair;

import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import org.geneticrepair.util.GeneticRepairSettings;
import org.geneticrepair.util.GeneticRepairUtil;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.processmining.framework.log.LogReader;
import org.processmining.framework.models.heuristics.HeuristicsNet;
import org.semanticweb.kaon2.p;

import nl.tue.tm.is.epc.EPC;

public class GeneticRepairTest {
	// public GeneticRepai
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String filePath = System.getProperty("user.dir") + File.separator + "logs" + File.separator
				+ "none-free-choice_R_ABPM.mxml.gz";
		String epcPath = System.getProperty("user.dir") + File.separator + "models" + File.separator
				+ "non-free-choice_O_ABPM.epml";
		
		String record =  System.getProperty("user.dir") + File.separator + "models" + File.separator
				+ "record2.txt";
		GeneticRepairUtil util = new GeneticRepairUtil();
		LogReader logReader = util.readLog(filePath);
		EPC epc = util.readEPC(epcPath);
		
		for(float i = (float) 0.5;i<= 0.5;){
			
			
			String outPath = System.getProperty("user.dir") + File.separator + "models" + File.separator
					+"non-free-choice_opt_"+i+".epml";
		
			GeneticRepairSettings settings = new GeneticRepairSettings(100, 200, i, 1-i);
			GeneticRepair repair = new GeneticRepair(logReader, epc, settings);
			
			long startTime = System.nanoTime();
			HeuristicsNet[] populuation = repair.modelRepair();
			//util.exportHN(outPath, populuation[0]);
			long estimatedTime = System.nanoTime() - startTime;
			util.exportEPC(outPath, populuation[0]);
			util.write(1000,populuation[0],record,estimatedTime);
			//util.writ			
			i +=  0.1;
		}
		
	}
}
