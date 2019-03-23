package com.kaizten.vrp.opt.core;

import com.kaizten.opt.solution.RoutesSolution;


import java.util.ArrayList;
import java.util.Arrays;

public class LatencySolution {
	private Vrp problem; 
	private RoutesSolution<Vrp> solutionVrp; 
	private boolean[] cl;
	private ArrayList<Integer> rcl;
	private int rclSize;

	public LatencySolution(Vrp problem, int rclSize) {
		this.problem = problem;
		this.solutionVrp = new RoutesSolution<Vrp>(problem, problem.getNCustomers(), problem.getNVehicles());
		this.cl = new boolean[problem.getNCustomers()];
		this.rcl = new ArrayList<Integer>();
		this.rclSize = rclSize;
		Arrays.fill(this.cl, true);
		sequientialConstructor();
	}

	public void init() {
		this.solutionVrp.evaluate();
	}
	
	public void constructGreedyRandomizedSolution() {
		int currentRoute = 0;
		while (!this.solutionVrp.isFull()) {
			MakeRCL(currentRoute);
			int indexCustomer = SelectElementAtRandom();

			if (this.solutionVrp.getLengthRoute(currentRoute) == this.problem.getNMaxCustomers()) {
				currentRoute++;
			}

			if (this.solutionVrp.isEmpty(currentRoute)) {
				this.solutionVrp.addAfterDepot(indexCustomer, currentRoute);
			} else {
				this.solutionVrp.addAfter(indexCustomer, this.solutionVrp.getLastInRoute(currentRoute));
			}
		}
	}

	private void MakeRCL(int route) {
		if (this.solutionVrp.isEmpty(route)) {
			while (this.rcl.size() < this.rclSize && CandidatesAvailable()) {
				double minDistance = Double.MAX_VALUE;
				int indCustomer = -1;
				for (int i = 0; i < this.cl.length; i++) {
					if (this.problem.getDistanceMatrix()[0][i + 1] < minDistance
							&& cl[i]) { /* In the distance matrix 0 is for the depot */
						indCustomer = i;
						minDistance = this.problem.getDistanceMatrix()[0][i + 1];
					}
				}
				this.rcl.add(indCustomer);
				this.cl[indCustomer] = false;
			}
		} else {
			while (this.rcl.size() < this.rclSize && CandidatesAvailable()) {
				double minDistance = Double.MAX_VALUE;
				int indCustomer = -1;
				for (int i = 0; i < this.cl.length; i++) {
					int lastCustomer = solutionVrp.getLastInRoute(route);
					if (this.problem.getDistanceMatrix()[lastCustomer + 1][i + 1] < minDistance && cl[i]) {
						indCustomer = i;
						minDistance = this.problem.getDistanceMatrix()[lastCustomer + 1][i + 1];
					}
				}
				this.rcl.add(indCustomer);
				this.cl[indCustomer] = false;
			}
		}
	}

	private int SelectElementAtRandom() {
		int randomCustomer = -1;
		if (this.rcl.size() == 1) {
			randomCustomer = this.rcl.get(0);
			this.rcl.remove(0);
		} else {
			int randomIndex = (int) (Math.random() * this.rcl.size());
			randomCustomer = this.rcl.get(randomIndex);
			this.rcl.remove(randomIndex);
		}
		return randomCustomer;
	}

	private boolean CandidatesAvailable() {
		for (int i = 0; i < this.cl.length; i++) {
			if (this.cl[i]) {
				return true;
			}
		}
		return false;
	}
	
	public void sequientialConstructor () {
		int currentRoute = 0;
		for(int i = 0; i < this.solutionVrp.getOptimizationProblem().getNCustomers(); i++) {
			if(currentRoute >= this.solutionVrp.getNumberOfRoutes()) {
				currentRoute = 0; 
			}
			if (this.solutionVrp.isEmpty(currentRoute)) {
				this.solutionVrp.addAfterDepot(i, currentRoute);
			} else {
				this.solutionVrp.addAfter(i, this.solutionVrp.getLastInRoute(currentRoute));
			}
			currentRoute++;
		}
	}
	

	public RoutesSolution<Vrp> getSolution() {
		return this.solutionVrp;
	}	

}
