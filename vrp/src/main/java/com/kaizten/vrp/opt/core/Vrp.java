package com.kaizten.vrp.opt.core;

import com.kaizten.opt.evaluator.Evaluator;
import com.kaizten.opt.move.MoveRoutesSolutionSwap;
import com.kaizten.opt.move.manager.MoveManagerSequential;
import com.kaizten.opt.problem.OptimizationProblem;
import com.kaizten.opt.solution.RoutesSolution;
import com.kaizten.utils.algorithm.GraphUtils;
import com.kaizten.vrp.opt.db.DBControl;
import com.kaizten.vrp.opt.evaluators.EvaluatorMoveRemove;
import com.kaizten.vrp.opt.evaluators.EvaluatorMoveSwap;
import com.kaizten.vrp.opt.evaluators.EvaluatorObjectiveFunctionDistances;
import com.kaizten.vrp.opt.move.generator.MoveGeneratorRoutesSolutionSwap;

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
		this.setEvaluator(evaluator);
		
		/* Init ArrayList */
		this.nCustomers = nCustomers;
		this.nVehicles = nVehicles;
		this.nMaxCustomers = nMaxCustomers;
		this.customers = new ArrayList<Node>();

		/* Create depot node */
		this.depot = new Node((width / 2), (height / 2), "DP", -1);
		depot.setSatisfied(true); /* Because the depot is a main node and always is satisfied. */
		this.customers.add(depot);
		
		/* Create nCustomer with random location */
		Random randomGenerator = new Random();
		for (int i = 0; i < nCustomers; i++) {
			int x = randomGenerator.nextInt(width + 1);
			int y = randomGenerator.nextInt(height + 1);
			int index = i;
			String id = "CU" + index;
			Node customer = new Node(x, y, id, index);
			this.customers.add(customer);
		}

		/* Fill distance matrix */
		this.distanceMatrix = new double[nCustomers + 1][nCustomers + 1]; /* Plus 1 for depot */
		for (int i = 0; i < customers.size(); i++) {
			this.distanceMatrix[i][i] = 0;
			for (int j = i + 1; j < customers.size(); j++) {
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

	public static void main(String[] args) {
		Vrp problem = new Vrp(100, 100, 15, 5, 3);
		//Vrp problem = new Vrp(5000, 5000, 500, 100, 5);
		System.out.print("\n\n\tDistance Matrix\n");
		for (int i = 0; i < problem.getCustomers().size(); i++) { 
			System.out.print(i + "\t| ");
			for (int j = 0; j < problem.getCustomers().size(); j++) {
				
				System.out.print("\t" + problem.getDistanceMatrix()[i][j]);
			}
			System.out.println();
		}		
		
		LatencySolution solution = new LatencySolution(problem, 3);
		solution.getSolution().evaluate();
		System.out.println(solution.getSolution().toString());
		
		/* Move manager sequential test */
		@SuppressWarnings({ "rawtypes", "unchecked" })
		MoveManagerSequential<RoutesSolution<Vrp>, ?> MMSequential =  new MoveManagerSequential();
		MMSequential.setSolution(solution.getSolution());
		
		/* Add some move generators */
		//MoveGeneratorRoutesSolutionInsertionAfter<RoutesSolution<Vrp>, MoveRoutesSolutionInsertionAfter> MGIna = new MoveGeneratorRoutesSolutionInsertionAfter<RoutesSolution<Vrp>, MoveRoutesSolutionInsertionAfter>();
		//MoveGeneratorRoutesSolutionRemove<RoutesSolution<Vrp>, MoveRemove> MGRem =  new MoveGeneratorRoutesSolutionRemove<RoutesSolution<Vrp>, MoveRemove>();
		MoveGeneratorRoutesSolutionSwap<RoutesSolution<Vrp>, MoveRoutesSolutionSwap> MGSwap =  new MoveGeneratorRoutesSolutionSwap<RoutesSolution<Vrp>, MoveRoutesSolutionSwap>();
		//MMSequential.addMoveGenerator(MGIna);
		//MMSequential.addMoveGenerator(MGRem);
		MMSequential.addMoveGenerator(MGSwap);
		
		//@SuppressWarnings("rawtypes")
		//Applier<RoutesSolution> MApplier =  new Applier<RoutesSolution>();
		//MoveApplierRoutesSolutionRemove applierRemove =  new MoveApplierRoutesSolutionRemove();
		//applierRemove.setApplier(MApplier);
		//MApplier.addMoveApplier(applierRemove);
		
		/* Initialization of Move Manager */
		MMSequential.init();
		/* Connection to DB */
		DBControl db =  new DBControl(); 
		db.init();
		//db.addSolutionMoveRemove(solution.getSolution());
		
		while(MMSequential.hasNext()) {
			System.out.println("\n-----------------------------------------------------------");
			//System.out.println(MMSequential.getNumberOfMoveGenerators() + " has next? " + MMSequential.hasNext());
			System.out.println("Movimiento= " + MMSequential.next());
			//@SuppressWarnings("unchecked")
			//RoutesSolution<Vrp> auxSolution =  solution.getSolution().clone();
			/*MApplier.setSolution(solutionRemove);
			MApplier.setMove(MMSequential.next());*/
			//applierRemove.accept(solutionRemove, (MoveRoutesSolutionRemove) MMSequential.next());
			//db.addSolutionMoveRemove(auxSolution);
		}
		
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
