package com.kaizten.vrp.opt.core;

public class Node {

	private int x; 
	private int y; 
	private String id; 
	/*private static int demand;*/
	
	public Node(int x, int y, String id){
		this.x = x;
		this.y = y;
		this.id = id;
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
	
}
