package com.kaizten.vrp.opt.move.applier;

import com.kaizten.opt.evaluator.Evaluator;
import com.kaizten.opt.move.MoveRoutesSolutionMoveAfter;
import com.kaizten.opt.move.applier.MoveApplier;
import com.kaizten.opt.solution.RoutesSolution;


public class MoveApplierRoutesSolutionMoveAfter extends MoveApplier<RoutesSolution<?>, MoveRoutesSolutionMoveAfter> {

	@Override
	public void accept(RoutesSolution<?> solution, MoveRoutesSolutionMoveAfter move) {
		if(move.getDeviationObjectiveFunctionValue(0) != -Evaluator.OBJECTIVE_INFEASIBLE && solution.isRouted(move.getElement1())) {
			solution.addAfter(move.getElement0(), move.getElement1());
		}
	}

}
