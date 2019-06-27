package com.kaizten.vrp.opt.solver;

import com.kaizten.opt.solution.RoutesSolution;
import com.kaizten.opt.solver.Solver;
import com.kaizten.vrp.opt.core.Vrp;

public class SequentialBuilder implements Solver<RoutesSolution<Vrp>> {
	
	private Vrp problem; 
	
	public SequentialBuilder(Vrp problem) {
		this.problem =  problem; 
	}

	@Override
	public RoutesSolution<Vrp> run() {
		int currentRoute = 0;
		RoutesSolution<Vrp> solution =  new RoutesSolution<Vrp>(this.problem, this.problem.getNCustomers(), this.problem.getNVehicles());
		for(int i = 0; i < this.problem.getNCustomers(); i++) {
			if(currentRoute >= solution.getNumberOfRoutes()) {
				currentRoute = 0; 
			}
			if (solution.isEmpty(currentRoute)) {
				solution.addAfterDepot(i, currentRoute);
			} else {
				solution.addAfter(i, solution.getLastInRoute(currentRoute));
			}
			currentRoute++;
		}
		solution.evaluate();
		return solution;
	}

}
