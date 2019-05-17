package com.kaizten.vrp.opt.move.applier;

import com.kaizten.opt.move.MoveRoutesSolutionMoveAfter;
import com.kaizten.opt.move.applier.MoveApplier;
import com.kaizten.opt.solution.RoutesSolution;
import com.kaizten.vrp.opt.core.Vrp;


public class MoveApplierRoutesSolutionMoveAfter extends MoveApplier<RoutesSolution<Vrp>, MoveRoutesSolutionMoveAfter> {

	@Override
	public void accept(RoutesSolution<Vrp> solution, MoveRoutesSolutionMoveAfter move) {
		int indexRoute =  solution.getRouteIndex(move.getElement1());
		if((solution.getLengthRoute(indexRoute) + 1) <= solution.getOptimizationProblem().getNMaxCustomers() || indexRoute ==  solution.getRouteIndex(move.getElement0())){
			solution.addAfter(move.getElement0(), move.getElement1());			
		}
	}

}
