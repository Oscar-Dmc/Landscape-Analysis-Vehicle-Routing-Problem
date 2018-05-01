package com.kaizten.vrp.opt.core;
import com.kaizten.utils.algorithm.GraphUtils;

import java.util.ArrayList;

public class Vehicle {
	private Node depot; 
	private int nMaxCustomers; 
	private ArrayList<Node> route;
	
	public Vehicle(int nMaxCustomers, Node depot){
		this.nMaxCustomers = nMaxCustomers;
		this.route = new ArrayList<Node>();
		this.depot = depot;
	}
	
	public Vehicle(Vehicle other) {
		this.depot =  other.depot;
		this.nMaxCustomers =  other.nMaxCustomers;
		this.route = new ArrayList<Node>(other.route);
	}
	
	public void addCustomerToRoute(Node customer){
		this.route.add(customer);
	}
	
	public double getTCT(){
		double tct = 0;
		ArrayList<Double> distances = new ArrayList<Double>();
		double currentDistance = GraphUtils.getEuclideanDistance(this.depot.getX(), this.depot.getY(), this.route.get(0).getX(), this.route.get(0).getY());
		distances.add(currentDistance);
		for(int i = 0;  i < this.route.size();  i++){
			if(i+1 < this.route.size() - 1){
				currentDistance = GraphUtils.getEuclideanDistance(this.route.get(i).getX(), this.route.get(i).getY(), this.route.get(i+1).getX(), this.route.get(i+1).getY());
				distances.add(currentDistance);
			}
		}
		currentDistance = GraphUtils.getEuclideanDistance(this.route.get(this.route.size() - 1).getX(), this.route.get(this.route.size() - 1).getY(), 
																		 this.depot.getX(), this.depot.getY());
		distances.add(currentDistance);
		
		for(int i = 0;  i < distances.size(); i++){
			tct += distances.get(i);
		}
		
		return tct;
	}
	
	public void resetRoute(){
		this.route.clear();
	}
	
	public Node getLastCustomerSatisfied(){
		return this.route.get(route.size() - 1);
	}
	
	public ArrayList<Node> getRoute(){
		return this.route;
	}
	
	public void setRoute(ArrayList<Node> route){
		this.route =  route;
	}
	
	public int getMaxCustomers(){
		return this.nMaxCustomers;
	}
	
}
