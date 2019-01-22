package com.kaizten.vrp.opt.move.applier;

import com.kaizten.opt.move.applier.MoveApplier;
import com.kaizten.opt.solution.RoutesSolution;
import com.kaizten.vrp.opt.move.MoveRoutesSolutionMoveBefore;

public class MoveApplierRoutesSolutionMoveBefore extends MoveApplier<RoutesSolution<?>, MoveRoutesSolutionMoveBefore> {

	@Override
	public void accept(RoutesSolution<?> solution, MoveRoutesSolutionMoveBefore move) {
		solution.addBefore(move.getElement0(), move.getElement1());
	}

}
