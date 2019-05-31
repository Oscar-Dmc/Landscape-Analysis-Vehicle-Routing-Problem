package com.kaizten.vrp.opt.solver;

import java.util.ArrayList;

import com.kaizten.opt.solution.RoutesSolution;
import com.kaizten.opt.solver.Solver;
import com.kaizten.vrp.opt.core.Vrp;

public class RandomizedBuilder implements Solver<RoutesSolution<Vrp>>{

	private Vrp problem; 
	private RoutesSolution<Vrp> solution; 
	private ArrayList<Integer> customers; 
	private ArrayList<Integer> routes; 
	
	public RandomizedBuilder (Vrp problem) {
		this.problem = problem; 
		for(int i = 0; i < problem.getNCustomers(); i++) {
			this.customers.add(i);
		}
		for(int i = 0; i < problem.getNVehicles(); i++){
			this.routes.add(i);
		}
		this.solution =  new RoutesSolution<Vrp>(problem, problem.getNCustomers(), problem.getNVehicles());
	}
	@Override
	public RoutesSolution<Vrp> run() {
		int indexCustomer = -1;
		int indexRoute = -1;
		while(solution.getNumberOfNonIncluded() != 0) {
			indexCustomer =  (int) (Math.random() * this.customers.size());
			indexRoute = (int) (Math.random() * this.routes.size());
			this.solution.addInRoute(indexRoute, indexCustomer);
			this.customers.remove(indexCustomer);
			if(this.solution.getLengthRoute(indexRoute) == this.problem.getNMaxCustomers()) {
				this.routes.remove(indexRoute);
			}
		}
		return this.solution;
	}

}
