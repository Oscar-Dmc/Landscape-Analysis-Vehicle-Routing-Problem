package com.kaizten.vrp.opt.move.applier;

import com.kaizten.opt.move.MoveRoutesSolutionMoveBefore;
import com.kaizten.opt.move.applier.MoveApplier;
import com.kaizten.opt.solution.RoutesSolution;
import com.kaizten.vrp.opt.core.Vrp;


public class MoveApplierRoutesSolutionMoveBefore extends MoveApplier<RoutesSolution<Vrp>, MoveRoutesSolutionMoveBefore> {

	@Override
	public void accept(RoutesSolution<Vrp> solution, MoveRoutesSolutionMoveBefore move) {
		int indexRoute = solution.getRouteIndex(move.getElement1());
		if((solution.getLengthRoute(indexRoute) + 1) <= solution.getOptimizationProblem().getNMaxCustomers() || indexRoute ==  solution.getRouteIndex(move.getElement0())) {			
			solution.addBefore(move.getElement0(), move.getElement1());
		}
	}

}
