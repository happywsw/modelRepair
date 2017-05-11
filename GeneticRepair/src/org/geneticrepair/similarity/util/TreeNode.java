package org.geneticrepair.similarity.util;

import java.util.ArrayList;
import java.util.List;

import nl.tue.tm.is.epc.Node;

public class TreeNode {
	private Node node;
	private int key = 0;
	private List<TreeNode> childList;
	
	public TreeNode(Node root){
		this.node = root;
		childList = new ArrayList<TreeNode>();
	}
	
	public TreeNode(int key,Node root){
		this.key = key;
		this.node = root;
	}
	
	public TreeNode(Node root,List<TreeNode> childList){
		this.node = root;
		this.childList = childList;
	}
	 public Node getNode() {  
	        return this.node;  
	    }  
	  
	    public void setNode(Node data) {  
	        this.node = data;  
	    }  
	  
	    public List<TreeNode> getChildList() {  
	        return childList;  
	    }  
	  
	    public void setChildList(List<TreeNode> childList) {  
	        this.childList = childList;  
	    }
	    public String getName(){
	    	return node.getName();
	    }
	    public String getId(){
	    	return node.getId();
	    }
}
