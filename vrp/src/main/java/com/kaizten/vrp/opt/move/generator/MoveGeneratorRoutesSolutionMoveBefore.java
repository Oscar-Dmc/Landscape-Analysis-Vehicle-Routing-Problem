package com.kaizten.vrp.opt.move.generator;

import com.kaizten.opt.move.MoveRoutesSolutionMoveBefore;
import com.kaizten.opt.move.generator.AbstractMoveGenerator;
import com.kaizten.opt.solution.RoutesSolution;


public class MoveGeneratorRoutesSolutionMoveBefore <S extends RoutesSolution<?>,  M extends MoveRoutesSolutionMoveBefore> extends AbstractMoveGenerator<S, M>{

	private int n;
	private int element0;
	private int element1;
	
	public MoveGeneratorRoutesSolutionMoveBefore() {
		this.n =  0;
		this.element0 = 0;
		this.element1 = 1;
	}
	
	public void init() {
		this.n =  super.getManager().getSolution().size();
		this.element0 = 0;
		this.element1 = 1;
	}

	
	@Override
	public boolean hasNext() {
		return this.element0 < this.n - 1;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public M next() {
		MoveRoutesSolutionMoveBefore move =  null; 
		if(this.hasNext()) {
			RoutesSolution solution =  super.getManager().getSolution();
			if(solution == null) {
				System.out.println("Null Solution");
				System.exit(0);
			}
			move =  new MoveRoutesSolutionMoveBefore(super.getManager().getSolution().getNumberOfObjectives());
			move.setElement0(this.element0);
			move.setElement1(this.element1);
			this.element1++;
			if(this.element1 == n) {
				this.element0++;
				this.element1 = this.element0 + 1; 
			}
		}
		return (M) move;
	}

}
