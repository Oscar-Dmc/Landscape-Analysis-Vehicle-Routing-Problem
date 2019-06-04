package com.kaizten.vrp.opt.move.applier;

import com.kaizten.opt.evaluator.Evaluator;
import com.kaizten.opt.move.MoveRoutesSolutionMoveBefore;
import com.kaizten.opt.move.applier.MoveApplier;
import com.kaizten.opt.solution.RoutesSolution;

public class MoveApplierRoutesSolutionMoveBefore extends MoveApplier<RoutesSolution<?>, MoveRoutesSolutionMoveBefore> {

	@Override
	public void accept(RoutesSolution<?> solution, MoveRoutesSolutionMoveBefore move) {
		if(move.getDeviationObjectiveFunctionValue(0) != -Evaluator.OBJECTIVE_INFEASIBLE && solution.isRouted(move.getElement1())) {			
			solution.addBefore(move.getElement0(), move.getElement1());
		}
	}

}
