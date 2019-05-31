package com.kaizten.vrp.opt.core;

import com.kaizten.opt.problem.OptimizationProblem;
import com.kaizten.utils.algorithm.GraphUtils;
import java.util.ArrayList;

public class Vrp extends OptimizationProblem {

	private double[][] distanceMatrix;
	ArrayList<ArrayList<Integer>> customers; 
	
	private int nVehicles;
	private int nCustomers;
	private int nMaxCustomers;
	
	public Vrp() {
		this.setName("VRP");
		this.customers = new ArrayList<ArrayList<Integer>>();
		this.nCustomers = 0;
		this.nVehicles = 0;
		this.nMaxCustomers = 0; 
	}
	
	public void fillDistanceMatrix() {		
		this.distanceMatrix = new double[this.nCustomers + 1][this.nCustomers + 1]; /* Plus 1 for depot */
		for (int i = 0; i < this.customers.size(); i++) {
			this.distanceMatrix[i][i] = 0;
			for (int j = i + 1; j < this.customers.size(); j++) {
				double distance = GraphUtils.getEuclideanDistance(this.customers.get(i).get(0),
						this.customers.get(i).get(1), this.customers.get(j).get(0), this.customers.get(i).get(1));
				this.distanceMatrix[i][j] = distance;
				this.distanceMatrix[j][i] = distance;
			}
		}
	}

	/* Get's & Set's */
	public ArrayList<ArrayList<Integer>> getCustomers() {
		return customers;
	}

	public double[][] getDistanceMatrix() {
		return distanceMatrix;
	}

	public int getNCustomers() {
		return this.nCustomers;
	}

	public int getNVehicles() {
		return this.nVehicles;
	}

	public int getNMaxCustomers() {
		return this.nMaxCustomers;
	}
	
	public void setNMaxCustomers(int nMaxCustomers) {
		this.nMaxCustomers = nMaxCustomers; 
	}
	
	@SuppressWarnings("unchecked")
	public void setCustomers(ArrayList<ArrayList<Integer>> customers) {
		this.customers = (ArrayList<ArrayList<Integer>>) customers.clone();
	}
	
	public void setNVehicles(int nVehicles) {
		this.nVehicles = nVehicles;
	}
	
	public void setNCustomers(int nCustomers) {
		this.nCustomers = nCustomers; 
	}
	
}
