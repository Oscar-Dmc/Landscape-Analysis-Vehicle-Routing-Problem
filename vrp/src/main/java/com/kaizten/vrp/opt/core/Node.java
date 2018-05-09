package com.kaizten.vrp.opt.core;

public class Node {

	private int x; 
	private int y; 
	private String id;
	private int index;
	private boolean satisfied;
	/*private static int demand;*/
	
	public Node(int x, int y, String id, int index){
		this.x = x;
		this.y = y;
		this.id = id;
		this.index = index;
		this.satisfied = false;
	}
	
	public Node(Node value) {
		this.x =  value.getX();
		this.y =  value.getY();
		this.id = value.getId();
		this.index = value.getIndex();
		this.satisfied = value.getSatisfied();
	}
	
	public int getX(){
		return this.x;
	}
	
	public int getY(){
		return this.y;
	}
	
	public String getId(){
		return this.id; 
	} 
	
	public boolean getSatisfied(){
		return this.satisfied;
	}
	
	public void setSatisfied(boolean value){
		this.satisfied = value;
	}
	public int getIndex(){
		return this.index;
	}
	
}
