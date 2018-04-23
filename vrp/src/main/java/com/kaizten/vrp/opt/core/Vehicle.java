package com.kaizten.vrp.opt.core;

import java.util.ArrayList;

public class Vehicle {
	private int nMaxCustomers; 
	private ArrayList<Node> route;
	
	public Vehicle(int nMaxCustomers){
		this.nMaxCustomers = nMaxCustomers;
	}
	
	public void addCustomerToRoute(Node customer){
		this.route.add(customer);
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