package com.kaizten.vrp.opt.evaluators;

import com.kaizten.opt.solution.RoutesSolution;
import com.kaizten.vrp.opt.core.Vrp;

public class LatencySolution {
	private Vrp problem; /* For more problem? */ 
	private RoutesSolution solutionVrp;  /* Array? */ 

	public LatencySolution(Vrp problem) {
		this.problem =  problem;
		this.solutionVrp = new RoutesSolution<Vrp>(problem, problem.getNCustomers(), problem.getNVehicles());
	}
	
	public void ConstructGreedyRandomizedSolution() {
		int currentRoute = 0;
		while(!this.problem.AllCustomersSatisfied()) {
			MakeRCL(currentRoute);
			int indexCustomer =  SelectElementAtRandom();
		}
	}
	
	private void MakeRCL(int route) {
		
	}
	
	private void SelectElementAtRandom() {
		
	}
}
