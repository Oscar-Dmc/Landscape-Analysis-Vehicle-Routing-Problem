package com.kaizten.vrp.opt.move.applier;

import com.kaizten.opt.move.MoveRoutesSolutionInsertionBefore;
import com.kaizten.opt.move.applier.MoveApplier;
import com.kaizten.opt.solution.RoutesSolution;

public class MoveApplierRoutesSolutionInsertionBefore extends MoveApplier<RoutesSolution<?>, MoveRoutesSolutionInsertionBefore>{

	@Override
	public void accept(RoutesSolution<?> solution, MoveRoutesSolutionInsertionBefore move) {
		if(move.getBefore() == -1) {
			solution.addBeforeDepot(move.getToInsert(), move.getRoute());
		} else {
			solution.addBefore(move.getToInsert(), move.getBefore());
		}
		
	}

}
