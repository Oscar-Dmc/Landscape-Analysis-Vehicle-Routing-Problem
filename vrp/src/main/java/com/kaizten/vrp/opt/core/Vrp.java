package com.kaizten.vrp.opt.core;

import com.kaizten.opt.problem.OptimizationProblem;
import com.kaizten.utils.algorithm.GraphUtils;
import com.kaizten.vrp.opt.evaluators.LatencySolution;
import java.util.ArrayList;
import java.util.Random;



public class Vrp extends OptimizationProblem{

	private double [][] distanceMatrix;
	private ArrayList<Node> customers;
	private Node depot; 
	//private ArrayList<Vehicle> vehicles; Check if it will be necessary
	private int nVehicles;
	private int nCustomers; 
	private int nMaxCustomers;
	
	public Vrp(int width, int height, int nCustomers, int nVehicles, int nMaxCustomers) {
		this.setName("VPR");
		//this.setDescription(description);
		/* Init ArrayList */
		this.nCustomers =  nCustomers;
		this.nVehicles =  nVehicles;
		this.nMaxCustomers =  nMaxCustomers; 
		this.customers = new ArrayList<Node>();
		
		//this.vehicles = new ArrayList<Vehicle>();
		
		/* Create depot node */ 
		this.depot = new Node((width / 2), (height / 2), "DP", -1);
		depot.setSatisfied(true); /* Because the depot is a main node and always is satisfied. */ 
		
		/* Create nCustomer with random location */
		Random randomGenerator = new Random();
		for(int i = 0; i < nCustomers; i++){
			int x = randomGenerator.nextInt(width + 1);
			int y = randomGenerator.nextInt(height + 1);
			int index =  i;
			String id = "CU" + index;
			Node customer = new Node(x, y, id, index);
			this.customers.add(customer);
		}
		
		/* Fill distance matrix */
		this.distanceMatrix = new double[nCustomers + 1][nCustomers + 1]; /* Plus 1 for depot */
		for(int i = 0; i < customers.size();  i++){
			this.distanceMatrix[i][i] = 0; 
			for(int j = i+1; j < customers.size(); j++){
				double distance = GraphUtils.getEuclideanDistance(this.customers.get(i).getX(), this.customers.get(i).getY(), this.customers.get(j).getX(), this.customers.get(i).getY());
				this.distanceMatrix[i][j] = distance;
				this.distanceMatrix[j][i] = distance;
 			}
		}
		
		/* Create vehicles  
		for(int i = 0; i < nVehicles;  i++){
			Vehicle vehicle = new Vehicle(nMaxCustomers, depot);
			this.vehicles.add(vehicle);
		} */
	}
	
	/* Check if all customers has been satisfied */ 
	public boolean AllCustomersSatisfied(){
		int nSatisfied = 0;
		for(int i = 0;  i < this.customers.size(); i++){
			if(this.customers.get(i).getSatisfied()){
				nSatisfied++;
			}
		}
		return (nSatisfied == this.customers.size());
	}
	
	public static void main( String[] args){
		Vrp problem = new Vrp(100, 100, 12, 3, 4);
		LatencySolution solution = new LatencySolution(problem, 3);
		solution.getSolution().toString();
		//Grasp methodGrasp =  new Grasp(3, problem);
		
		for(int i = 0; i < problem.getCustomers().size(); i++){
			for(int j = 0;  j < problem.getCustomers().size(); j++){
				System.out.print("\t" + problem.getDistanceMatrix()[i][j]);
			}
			System.out.println();
		}
		
		//methodGrasp.ProcedureGrasp(100);
		//methodGrasp.PrintSolutionConsole();
		
		//Gvsn methodGvsn = new Gvsn(methodGrasp.getSolution());
		//methodGvsn.Pruebas();
		
	}
	
	/* Get's & Set's */
	public ArrayList<Node> getCustomers() {
		return customers;
	}
	
	/*public ArrayList<Vehicle> getVehicles() {
		return vehicles;
	}*/
	
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
}
