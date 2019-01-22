package com.kaizten.vrp.opt.move.generator;

import com.kaizten.opt.move.generator.AbstractMoveGenerator;
import com.kaizten.opt.solution.RoutesSolution;
import com.kaizten.vrp.opt.move.MoveRoutesSolutionMoveAfter;

public class MoveGeneratorRoutesSolutionMoveAfter<S extends RoutesSolution<?>,  M extends MoveRoutesSolutionMoveAfter> extends AbstractMoveGenerator<S, M> {
	
	private int n;
	private int element0;
	private int element1; 
	
	public MoveGeneratorRoutesSolutionMoveAfter() {
		this.element0 = 0;
		this.element1 = 1;
	}
	
	public void init() {
		this.element0 = 0;
		this.element1 = 1;
		this.n =  super.getManager().getSolution().size();
	}
	
	@Override
	public boolean hasNext() {
		return (this.element0 < this.n) && (this.element1 < this.n);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public M next() {
		MoveRoutesSolutionMoveAfter move =  null; 
		if(this.hasNext()) {
			RoutesSolution solution =  super.getManager().getSolution();
			if(solution == null) {
				System.out.println("Null Solution");
				System.exit(0);
			}
			move =  new MoveRoutesSolutionMoveAfter(super.getManager().getSolution().getNumberOfObjectives());
			move.setElement0(element0);
			move.setElement1(element1);
			this.element1++;
			if(this.element1 == this.n) {
				this.element0++;
				this.element1 =  this.element0 + 1;
			}
		}
		return (M) move;
	}

}
