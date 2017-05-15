package test.convert;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import nl.tue.tm.is.epc.EPC;

import de.bpt.hpi.graph.Graph;
import graph.EPCHelper;
import similarity.highlevelop.HighLevelOP;

public class ConvertTest {
	
	public static void TestHighLevelOperation(Graph graph1, Graph graph2)
	{
		List<String> oplist = HighLevelOP.GetHighLevelOPList(graph1, graph2);
		
		for(String op : oplist){
			System.out.println(op);
		}		
	}
	
	public static void main(String [] args){
		String name1 = "\\models\\loop_test.epml";	//B3.s00000179__s00002108.epml
		String name2 = "\\models\\loop_test_2.epml";   //B3.s00000281__s00002490.epml
		//B3.s00000179__s00002108.epml  B3.s00000557__s00006400.epml
		String filepath1 = System.getProperty("user.dir") + name1;
		String filepath2 = System.getProperty("user.dir") + name2;
		
		System.err.println(name1);
		System.err.println(name2);
		System.out.println();
		
		try{
			EPC epcmodel1 = EPC.loadEPML(filepath1);
			epcmodel1.cleanEPC();
			EPC epcmodel2 = EPC.loadEPML(filepath2);
			epcmodel2.cleanEPC();
			long start = System.nanoTime();
			EPCHelper epc1 = new EPCHelper(epcmodel1, filepath1);
			EPCHelper epc2 = new EPCHelper(epcmodel2, filepath2);
			
			
			Graph graph1 = epc1.getGraph();
			Graph graph2 = epc2.getGraph();
			
			TestHighLevelOperation(graph1, graph2);
		
			double similarity = HighLevelOP.GetHighLevelOPSimilarity(graph1, graph2);
			System.out.println("time:"+(System.nanoTime()-start));
			
			DecimalFormat format = new DecimalFormat("0.00");

			System.out.printf("similarity:"+format.format(similarity));
		}catch(Exception e){
			System.out.println("Some Bugs!!!");
		}
	}
	
	//public static void test2(){
	public static void main2(String [] args){
		String foldername = System.getProperty("user.dir") + "\\models\\paper_experiment";
		File folder = new File(foldername);
		File[] listOfFiles = null;
		try {
			listOfFiles = folder.listFiles();
		} catch (Exception e) {
			System.out.println("Invalid input: folder " + foldername);
			return;
		}
		
		List<Graph> graphList = new ArrayList<Graph>(); 
		List<String> filenameList = new ArrayList<String>();
		List<Integer> distanceList = new ArrayList<Integer>();
		List<Double> similarityList = new ArrayList<Double>();
		for (int k = 0; k < listOfFiles.length; k++) {
			if (listOfFiles[k].isFile()) {
				String filepath = foldername + "\\" + listOfFiles[k].getName();
				String filename = listOfFiles[k].getName();
				filenameList.add(filename);
				try {						
					EPC epcmodel = EPC.loadEPML(filepath);
					epcmodel.cleanEPC();
					EPCHelper epc = new EPCHelper(epcmodel, filepath);
					Graph graph = epc.getGraph();
					graphList.add(graph);
				} catch (Throwable e) {
					System.out.println("Problem with model " + filepath + ".");
				}
			}
		}
		
		for(int i = 0; i < graphList.size(); i++){
			System.out.printf("%2s", i  + ": ");
			System.out.printf("%12s", filenameList.get(i));
			for(int j = 0; j < graphList.size(); j++){
				List<String> opList = HighLevelOP.GetHighLevelOPList(graphList.get(i), graphList.get(j));
				double similarity = HighLevelOP.GetHighLevelOPSimilarity(graphList.get(i), graphList.get(j));
				DecimalFormat df = new DecimalFormat("0.00");
				
				distanceList.add(opList.size());
				similarityList.add(similarity);
				
				System.out.printf("%9s", opList.size()+"/"+df.format(similarity));
				if(j == graphList.size()-1)
					System.out.println();
			}
		}
	}
	

}
