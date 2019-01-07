package com.kaizten.vrp.opt.move.applier;

import com.kaizten.opt.move.MoveRoutesSolutionSwap;
import com.kaizten.opt.move.applier.MoveApplier;
import com.kaizten.opt.solution.RoutesSolution;


public class MoveApplierRoutesSolutionSwap extends MoveApplier<RoutesSolution<?>, MoveRoutesSolutionSwap>{

	@Override
	public void accept(RoutesSolution<?> solution, MoveRoutesSolutionSwap move) {
		if(solution.getPredecessor(move.getElement0()) != -1) {
			int pred0 =  solution.getPredecessor(move.getElement0());
			solution.remove(move.getElement0());
			solution.addAfter(move.getElement0(), move.getElement1());
			solution.remove(move.getElement1());
			solution.addAfter(move.getElement1(), pred0);
		}
		else {
			int succ0 =  solution.getSuccessor(move.getElement0());
			solution.remove(move.getElement0());
			solution.addAfter(move.getElement0(), move.getElement1());
			solution.remove(move.getElement1());
			solution.addBefore(move.getElement1(), succ0);
		}
	}

}
