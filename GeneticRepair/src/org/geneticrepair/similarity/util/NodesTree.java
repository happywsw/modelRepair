package org.geneticrepair.similarity.util;


import java.util.ArrayList;
import java.util.List;

import org.wsmo.wsml.compiler.node.TTOntology;

import gui.action.NewAction;
import nl.tue.tm.is.epc.Node;


public class NodesTree{
	TreeNode root ;
	
	public NodesTree(Node node){
		root = new TreeNode(node);
	}
	
	public TreeNode addChild(TreeNode node,Node child){
		List<TreeNode> childs = node.getChildList();
		if(childs.size() == 0){
			childs = new ArrayList<TreeNode>();
		}
		TreeNode ch = new TreeNode(child);
		childs.add(ch);
		node.setChildList(childs);
		return node;
	}
	
	public NodesTree createTree(Node ro,List<Node> nodes){
		if((nodes == null) || (nodes.size() < 0)){
			return null;
		}
		NodesTree tree = new NodesTree(ro);
		TreeNode parent = new TreeNode(ro);
		TreeNode pa = parent;
		
		for(Node n:nodes){
			if(!n.equals(pa.getNode())){
				tree.addChild(pa, n);
				pa = new TreeNode(n);
			}
			if(n.getName().equals(pa.getName())){
				tree.addChild(parent, n);
				parent = pa;
			}
		}
		return tree;
	}
	
	public static void main(String[]args){
		Node node = new Node();
		node.setId("0");
		node.setName("A");
		NodesTree tree = new NodesTree(node);
		TreeNode root = new TreeNode(node);
		Node node1 = new Node("1","B");
		Node node2 = new Node("2","B");
		root = tree.addChild(root, node1);
		//List<Node> nodes = new ArrayList<Node>();
		//nodes.add(node1);
		//nodes.add(node2);
		System.out.println(tree);
		
		
		
	}
}