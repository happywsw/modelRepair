package mtree.index;

import nl.tue.tm.is.epc.EPC;
import de.bpt.hpi.graph.Graph;
import graph.EPCHelper;

public class FragmentProcess {
	
	public String filepath = null;
	public String filename = null;
	public Fragment fragment = new Fragment();
	
	public FragmentProcess(String filepath){
		this.filepath = filepath;
		this.filename = filepath.substring(filepath.lastIndexOf("\\") + 1);
		this.fragment = convertFragment(filepath);
	}
	
	public Fragment getFragment(){
		return this.fragment;
	}
	
	public Fragment convertFragment(String filepath){
	
		EPC epcmodel = EPC.loadEPML(filepath);
		epcmodel.cleanEPC();
		
		EPCHelper epc = new EPCHelper(epcmodel, filepath);
		Graph graph = epc.getGraph();
		
		fragment.setGraph(graph);
		fragment.modelfiles.add(filename);
		
		return fragment;
	}

}
