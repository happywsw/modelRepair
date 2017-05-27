package org.geneticrepair.similarity.graphconvert;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.geneticrepair.similarity.util.OPGraph;
import org.geneticrepair.similarity.util.OPLine;
import org.geneticrepair.similarity.util.OPNode;

import de.bpt.hpi.graph.*;

public class GraphConvert {
	
	private Graph graph;
	private OPGraph opgraph;
	
	public GraphConvert(Graph graph){
		this.graph = graph;
		this.opgraph = new OPGraph();
	
		long start = System.nanoTime();
		this.opgraph = convert();
		System.out.println("test:"+ (System.nanoTime() - start));
	}
	
	public Graph getGraph(){
		return this.graph;
	}
	
	public OPGraph getOPGraph(){
		return this.opgraph;
	}
	
	public void printGraph(){
		for(Integer v : graph.getVertices()){
			System.out.println(v+" "+graph.getLabel(v));
		}
		System.out.println();
	}
	public void printOPGraph(){
		for(int i=0; i<opgraph.nodes.size(); i++){
			System.out.println(opgraph.nodes.get(i).id+" "+opgraph.nodes.get(i).label);
		}
		System.out.println();
	}
	
	private OPGraph load(){		
		HashMap<String, OPNode> map = new HashMap<String, OPNode>();
		
		for(Integer v : graph.getVertices()){
			OPNode node = new OPNode();
			
			node.id = v.toString();
			node.label = graph.getLabel(v.intValue());
			
			opgraph.nodes.add(node);
			map.put(node.id, node);
		}
		
		for(Edge e : graph.getEdges()){
			OPLine line = new OPLine();
			OPNode nodep, nodec;
			
	
			line.setStart(e.getFirst().toString());
			line.setEnd(e.getSecond().toString());
			if(map.containsKey(line.getStart()) && map.containsKey(line.getEnd())){
				nodep = map.get(line.getStart());
				nodec = map.get(line.getEnd());
				
				nodep.addChildren(nodec);//从这里找到孩子节点
					
				nodec.addParents(nodep);//从这里找到父亲节点
			}
			opgraph.lines.add(line);
		}
		
		//printGraph();printOPGraph();
		return opgraph;
	}

	private OPGraph convert(){
		long start = System.nanoTime();
		OPGraph opgraph = load();
		System.out.println("load" + (System.nanoTime() - start));
		//遍历结点 删掉
		for(int i = opgraph.nodes.size() - 1;i >= 0 ;i--){
			OPNode node = opgraph.nodes.get(i);
	
			if(node.label.equalsIgnoreCase("xorsplit") || node.label.equalsIgnoreCase("andsplit") || node.label.equalsIgnoreCase("orsplit")){
				Operator(opgraph, node, true);
			}
			
			if(node.label.equalsIgnoreCase("xorjoin") || node.label.equalsIgnoreCase("andjoin") || node.label.equalsIgnoreCase("orjoin")){	
				Operator(opgraph, node, false);
			}
		}
//		
		return opgraph;
	} 
	
	private void Operator(OPGraph opgraph, OPNode node, boolean flag){
		List<OPNode> nodes;
		
		if(flag){ //xorsplit
			nodes = node.parents;
			
		}else{//xorjoin
			nodes = node.children;
		}
		
		for(int i = nodes.size() - 1;i >= 0; i--){
			OPNode opNode = nodes.get(i);
			
			opNode.addType(node.label);
							
			if(flag){
				deleteEdges(opgraph, node, true);
				addEdge(opgraph, opNode, node.children, true);
				opNode.children.remove(node);
				
			}else{
				deleteEdges(opgraph, node, false);
				addEdge(opgraph, opNode, node.parents, false);
				opNode.parents.remove(node);
			}	
		}
		
		opgraph.nodes.remove(node);
		
	}
	
	private void deleteEdges(OPGraph graph, OPNode node, boolean flag){		
		for(int i = graph.lines.size() - 1; i >= 0; i--){
			if(graph.lines.get(i).getStart().equals(node.id) || graph.lines.get(i).getEnd().equals(node.id)){
				graph.lines.remove(i);
			}
		}
	
		if(flag){
			for(int i = node.children.size()-1; i >= 0; i--){
				node.children.get(i).parents.remove(node);				
			}
			
		}else{
			for(int i = node.parents.size()-1; i >= 0; i--){
				node.parents.get(i).children.remove(node);				
			}
		}		
		
	}

	private void addEdge(OPGraph graph,OPNode node, List<OPNode> nodes, boolean flag){		
		for(OPNode cNode: nodes){
			OPLine line = new OPLine();
			if(flag){ // xorsplit, andsplit, orsplit
				line.setStart(node.id);
				line.setEnd(cNode.id);
				
				node.addChildren(cNode);
				cNode.addParents(node);
				
			}else{ //xorjoin, andjoin, orjoin
				line.setStart(cNode.id);
				line.setEnd(node.id);
				
				cNode.addChildren(node);
				node.addParents(cNode);
			}
			
			graph.lines.add(line);
		}
		
	}
	
}


