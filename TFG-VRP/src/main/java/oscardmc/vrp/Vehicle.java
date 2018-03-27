package main.java.oscardmc.vrp;

import java.util.ArrayList;

public class Vehicle {
	
	public static final int MAX_NODES = 5; 
	private ArrayList<Node> route;
	
	public ArrayList<Node> getRoute(){
		return this.route;
	}
	
	public void setRoute(ArrayList<Node> route){
		this.route =  route;
	}
	
}
