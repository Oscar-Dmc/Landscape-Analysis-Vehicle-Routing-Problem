package com.kaizten.vrp.opt.move.applier;

import com.kaizten.opt.move.MoveRoutesSolutionMoveAfter;
import com.kaizten.opt.move.applier.MoveApplier;
import com.kaizten.opt.solution.RoutesSolution;


public class MoveApplierRoutesSolutionMoveAfter extends MoveApplier<RoutesSolution<?>, MoveRoutesSolutionMoveAfter> {

	@Override
	public void accept(RoutesSolution<?> solution, MoveRoutesSolutionMoveAfter move) {
		int indexRoute =  solution.getRouteIndex(move.getElement1());
		int nElements =  solution.getNumberOfIncluded() + solution.getNumberOfNonIncluded();
		int nMaxElementInRoute = (nElements/solution.getNumberOfRoutes()) + 1;
		if((solution.getLengthRoute(indexRoute) + 1) <= nMaxElementInRoute || indexRoute ==  solution.getRouteIndex(move.getElement0())){
			solution.addAfter(move.getElement0(), move.getElement1());			
		}
	}

}
