package com.kaizten.vrp.opt.move.generator;

import com.kaizten.opt.move.MoveRoutesSolutionInsertionBefore;
import com.kaizten.opt.move.generator.AbstractMoveGenerator;
import com.kaizten.opt.solution.RoutesSolution;

public class MoveGeneratorRoutesSolutionInsertionBefore <S extends RoutesSolution<?>, M extends MoveRoutesSolutionInsertionBefore> extends AbstractMoveGenerator<S, M>{

	private int before;
	private int route;
	private int toInsert; 
	
	public MoveGeneratorRoutesSolutionInsertionBefore() {
		this.route = 0; 
		this.before = 0; 
		this.toInsert = 0;
	}
	
	@Override
	public boolean hasNext() {
		return this.route < super.getManager().getSolution().getNumberOfRoutes();
	}

	public void init() {
		S solution =  super.getManager().getSolution();
		if(!solution.isFull()) {
			this.route = 0;
			this.before =  RoutesSolution.DEFAULT_VALUE;
			this.toInsert = (int) solution.getIndexNextNonRouted().get();
		} else {
			this.route = solution.getNumberOfRoutes();
		}
	}
	@SuppressWarnings("unchecked")
	@Override
	public M next() {
		S solution = super.getManager().getSolution();
		
		MoveRoutesSolutionInsertionBefore move = new MoveRoutesSolutionInsertionBefore(solution.getNumberOfObjectives());
		move.setToInsert(this.toInsert);
		move.setBefore(this.before);
		move.setRoute(this.route);
		
		this.toInsert = (int) solution.getIndexNextNonRouted(this.toInsert + 1).orElse(-1);
		
		if(this.toInsert == -1) {
			if(this.before == RoutesSolution.DEFAULT_VALUE) {
				if(solution.isEmpty(this.route)) {
					this.route++;
				} else {
					this.before =  solution.getFirstInRoute(this.route);
				}
			} else if(solution.isFirstInRoute(this.before)) {
				this.route++;
				this.before = RoutesSolution.DEFAULT_VALUE;
			} else {
				this.before = solution.getSuccessor(this.before);
			}
			this.toInsert = (int) solution.getIndexNextNonRouted().orElse(-1);
		}
		return (M) move;
	}

}
