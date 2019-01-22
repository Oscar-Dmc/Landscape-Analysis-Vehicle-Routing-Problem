package com.kaizten.vrp.opt.move.applier;

import com.kaizten.opt.move.applier.MoveApplier;
import com.kaizten.opt.solution.RoutesSolution;
import com.kaizten.vrp.opt.move.MoveRoutesSolutionMoveAfter;

public class MoveApplierRoutesSolutionMoveAfter extends MoveApplier<RoutesSolution<?>, MoveRoutesSolutionMoveAfter> {

	@Override
	public void accept(RoutesSolution<?> solution, MoveRoutesSolutionMoveAfter move) {
		solution.addAfter(move.getElement0(), move.getElement1());
	}

}
