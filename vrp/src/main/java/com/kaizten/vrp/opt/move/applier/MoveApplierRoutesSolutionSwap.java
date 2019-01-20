package com.kaizten.vrp.opt.move.applier;

import com.kaizten.opt.move.MoveRoutesSolutionSwap;
import com.kaizten.opt.move.applier.MoveApplier;
import com.kaizten.opt.solution.RoutesSolution;


public class MoveApplierRoutesSolutionSwap extends MoveApplier<RoutesSolution<?>, MoveRoutesSolutionSwap>{

	@Override
	public void accept(RoutesSolution<?> solution, MoveRoutesSolutionSwap move) {
		if(solution.getRouteIndex(move.getElement0()) == solution.getRouteIndex(move.getElement1())) {
			this.swapInSameRoute(solution, move);
		}
		else {
			if(solution.getPredecessor(move.getElement0()) != -1) {
				int pred0 =  solution.getPredecessor(move.getElement0());
				solution.addAfter(move.getElement0(), move.getElement1());
				solution.addAfter(move.getElement1(), pred0);

			}
			else {
				int succ0 =  solution.getSuccessor(move.getElement0());
				solution.addAfter(move.getElement0(), move.getElement1());
				solution.addBefore(move.getElement1(), succ0);

			}
		}
	}
	
	public void swapInSameRoute(RoutesSolution<?> solution, MoveRoutesSolutionSwap move) {
		if(solution.getPredecessor(move.getElement0()) == move.getElement1()) {
			solution.addBefore(move.getElement0(), move.getElement1());
		}
		else if (solution.getPredecessor(move.getElement1()) == move.getElement0()) {
			solution.addBefore(move.getElement1(), move.getElement0());
		}
		else {
			int pred0 =  solution.getPredecessor(move.getElement0());
			int pred1 =  solution.getPredecessor(move.getElement1());
			if(pred0 == -1) {
				solution.addBefore(move.getElement1(), move.getElement0());
				solution.addAfter(move.getElement0(), pred1);
			}
			else if(pred1 == -1) {
				solution.addBefore(move.getElement0(), move.getElement1());
				solution.addAfter(move.getElement1(), pred0);
			}
			else {
				solution.addAfter(move.getElement1(), pred0);
				solution.addAfter(move.getElement0(), pred1);
			}
		}
		
	}

}
