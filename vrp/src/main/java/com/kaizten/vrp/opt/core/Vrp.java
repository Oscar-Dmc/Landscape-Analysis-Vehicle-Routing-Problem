package com.kaizten.vrp.opt.core;

import com.kaizten.opt.evaluator.Evaluator;
import com.kaizten.opt.problem.OptimizationProblem;
import com.kaizten.utils.algorithm.GraphUtils;
import com.kaizten.vrp.opt.evaluators.EvaluatorMoveInsertionAfter;
import com.kaizten.vrp.opt.evaluators.EvaluatorMoveInsertionBefore;
import com.kaizten.vrp.opt.evaluators.EvaluatorMoveRemove;
import com.kaizten.vrp.opt.evaluators.EvaluatorMoveSwap;
import com.kaizten.vrp.opt.evaluators.EvaluatorObjectiveFunctionDistances;
import java.util.ArrayList;
import java.util.Random;

public class Vrp extends OptimizationProblem {

	private double[][] distanceMatrix;
	private ArrayList<Node> customers;
	private Node depot;
	private int nVehicles;
	private int nCustomers;
	private int nMaxCustomers;

	@SuppressWarnings({ "unchecked" })
	public Vrp(int width, int height, int nCustomers, int nVehicles, int nMaxCustomers) {
		this.setName("VPR");
		
		/* Evaluators */ 
		@SuppressWarnings("rawtypes")
		Evaluator evaluator = new Evaluator(1);
		evaluator.addEvaluatorObjectiveFunction(new EvaluatorObjectiveFunctionDistances());
		evaluator.addEvaluatorObjectiveFunctionMovement(new EvaluatorMoveRemove(1), 0);
		evaluator.addEvaluatorObjectiveFunctionMovement(new EvaluatorMoveSwap(1), 0);
		evaluator.addEvaluatorObjectiveFunctionMovement(new EvaluatorMoveInsertionAfter(), 0);
		evaluator.addEvaluatorObjectiveFunctionMovement(new EvaluatorMoveInsertionBefore(), 0);
		this.setEvaluator(evaluator);
		
		/* Init ArrayList */
		this.nCustomers = nCustomers;
		this.nVehicles = nVehicles;
		this.nMaxCustomers = nMaxCustomers;
		this.customers = new ArrayList<Node>();

		/* Create depot node */
		this.depot = new Node((width / 2), (height / 2), "DP", -1);
		this.depot.setSatisfied(true); /* Because the depot is a main node and always is satisfied. */
		this.customers.add(this.depot);
		
		/* Create nCustomer with random location */
		Random randomGenerator = new Random();
		for (int i = 0; i < nCustomers; i++) {
			int x = randomGenerator.nextInt(width + 1);
			int y = randomGenerator.nextInt(height + 1);
			String id = "CU" + i;
			Node customer = new Node(x, y, id, i);
			this.customers.add(customer);
		}

		this.fillDistanceMatrix();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Vrp(ArrayList<ArrayList<Integer>> customers, Integer nCustomers, Integer nVehicles) {
		this.setName("VRP");
		
		Evaluator evaluator = new Evaluator(1);
		evaluator.addEvaluatorObjectiveFunction(new EvaluatorObjectiveFunctionDistances());
		evaluator.addEvaluatorObjectiveFunctionMovement(new EvaluatorMoveRemove(1), 0);
		evaluator.addEvaluatorObjectiveFunctionMovement(new EvaluatorMoveSwap(1), 0);
		evaluator.addEvaluatorObjectiveFunctionMovement(new EvaluatorMoveInsertionAfter(), 0);
		evaluator.addEvaluatorObjectiveFunctionMovement(new EvaluatorMoveInsertionBefore(), 0);
		this.setEvaluator(evaluator);
		
		this.customers = new ArrayList<Node>();
		this.nCustomers =  nCustomers;
		this.nVehicles = nVehicles;
		this.nMaxCustomers = 3; 
		
		this.depot =  new Node(customers.get(0).get(0), customers.get(0).get(1), "DP", -1);
		this.depot.setSatisfied(true);
		this.customers.add(this.depot);
		
		for(int i = 1; i < customers.size(); i++) {
			String id = "CU" + i;
			Node customer =  new Node(customers.get(i).get(0), customers.get(i).get(1), id, i);
			this.customers.add(customer); 
		}
		
		this.fillDistanceMatrix();
		
	}
	
	public void fillDistanceMatrix() {
		this.distanceMatrix = new double[this.nCustomers + 1][this.nCustomers + 1]; /* Plus 1 for depot */
		for (int i = 0; i < this.customers.size(); i++) {
			this.distanceMatrix[i][i] = 0;
			for (int j = i + 1; j < this.customers.size(); j++) {
				double distance = GraphUtils.getEuclideanDistance(this.customers.get(i).getX(),
						this.customers.get(i).getY(), this.customers.get(j).getX(), this.customers.get(i).getY());
				this.distanceMatrix[i][j] = distance;
				this.distanceMatrix[j][i] = distance;
			}
		}
	}

	/* Check if all customers has been satisfied */
	public boolean AllCustomersSatisfied() {
		int nSatisfied = 0;
		for (int i = 0; i < this.customers.size(); i++) {
			if (this.customers.get(i).getSatisfied()) {
				nSatisfied++; 
			}
		}
		return (nSatisfied == this.customers.size());
	}

	/* Get's & Set's */
	public ArrayList<Node> getCustomers() {
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
}
