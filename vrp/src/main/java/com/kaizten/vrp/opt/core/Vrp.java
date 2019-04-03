package com.kaizten.vrp.opt.core;

import com.kaizten.opt.evaluator.Evaluator;
import com.kaizten.opt.io.KaiztenOptimizationProblemFileSupplier;
import com.kaizten.opt.problem.OptimizationProblem;
import com.kaizten.utils.algorithm.GraphUtils;
import com.kaizten.vrp.opt.evaluators.EvaluatorMoveAfter;
import com.kaizten.vrp.opt.evaluators.EvaluatorMoveBefore;
import com.kaizten.vrp.opt.evaluators.EvaluatorMoveInsertionAfter;
import com.kaizten.vrp.opt.evaluators.EvaluatorMoveInsertionBefore;
import com.kaizten.vrp.opt.evaluators.EvaluatorMoveRemove;
import com.kaizten.vrp.opt.evaluators.EvaluatorMoveSwap;
import com.kaizten.vrp.opt.evaluators.EvaluatorObjectiveFunctionDistances;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Stream;

public class Vrp extends OptimizationProblem {

	private double[][] distanceMatrix;
	ArrayList<ArrayList<Integer>> customers; 
	/*private ArrayList<Node> customers;
	private Node depot;*/
	private int nVehicles;
	private int nCustomers;
	private int nMaxCustomers;

	@SuppressWarnings({ "unchecked" })
	public Vrp(int width, int height, int nCustomers, int nVehicles, int nMaxCustomers) {
		this.setName("VPR");
		
		/* Evaluators */ 
		@SuppressWarnings("rawtypes")
		Evaluator evaluator = new Evaluator();
		evaluator.addEvaluatorObjectiveFunction(new EvaluatorObjectiveFunctionDistances());
		evaluator.addEvaluatorObjectiveFunctionMovement(new EvaluatorMoveRemove(1), 0);
		evaluator.addEvaluatorObjectiveFunctionMovement(new EvaluatorMoveSwap(1), 0);
		evaluator.addEvaluatorObjectiveFunctionMovement(new EvaluatorMoveInsertionAfter(), 0);
		evaluator.addEvaluatorObjectiveFunctionMovement(new EvaluatorMoveInsertionBefore(), 0);
		evaluator.addEvaluatorObjectiveFunctionMovement(new EvaluatorMoveAfter(), 0);
		evaluator.addEvaluatorObjectiveFunctionMovement(new EvaluatorMoveBefore(), 0);
		this.setEvaluator(evaluator);
		
		/* Init ArrayList */
		this.nCustomers = nCustomers;
		this.nVehicles = nVehicles;
		this.nMaxCustomers = nMaxCustomers;
		this.customers = new ArrayList<ArrayList<Integer>>();
		//this.customers = new ArrayList<Node>();

		/* Create depot node */
		ArrayList<Integer> customer = new ArrayList<Integer>();
		customer.add(width / 2); /* Coordinated x for depot */
		customer.add(height / 2); /* Coordinated y for depot */
		customer.add(0); /* Demand of depot */ 
		this.customers.add((ArrayList<Integer>) customer.clone());
		customer.clear();
		
		/* Create nCustomer with random location */
		Random randomGenerator = new Random();
		for (int i = 0; i < nCustomers; i++) {
			int x = randomGenerator.nextInt(width + 1);
			int y = randomGenerator.nextInt(height + 1);
			customer.add(x);
			customer.add(y);
			customer.add(randomGenerator.nextInt(50) + 1);
			this.customers.add((ArrayList<Integer>) customer.clone());
			customer.clear();
		}

		this.fillDistanceMatrix();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Vrp(ArrayList<ArrayList<Integer>> customers, Integer nCustomers, Integer nVehicles, Integer nMaxCustomers) {
		this.setName("VRP");
		
		Evaluator evaluator = new Evaluator();
		evaluator.addEvaluatorObjectiveFunction(new EvaluatorObjectiveFunctionDistances());
		evaluator.addEvaluatorObjectiveFunctionMovement(new EvaluatorMoveRemove(1), 0);
		evaluator.addEvaluatorObjectiveFunctionMovement(new EvaluatorMoveSwap(1), 0);
		evaluator.addEvaluatorObjectiveFunctionMovement(new EvaluatorMoveInsertionAfter(), 0);
		evaluator.addEvaluatorObjectiveFunctionMovement(new EvaluatorMoveInsertionBefore(), 0);
		evaluator.addEvaluatorObjectiveFunctionMovement(new EvaluatorMoveAfter(), 0);
		evaluator.addEvaluatorObjectiveFunctionMovement(new EvaluatorMoveBefore(), 0);
		this.setEvaluator(evaluator);
		
		this.customers = (ArrayList<ArrayList<Integer>>) customers.clone();
		this.nCustomers =  nCustomers;
		this.nVehicles = nVehicles;
		this.nMaxCustomers = nMaxCustomers;
		
		this.fillDistanceMatrix();
		
	}
	
	public Vrp() {}
	
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
	
}
