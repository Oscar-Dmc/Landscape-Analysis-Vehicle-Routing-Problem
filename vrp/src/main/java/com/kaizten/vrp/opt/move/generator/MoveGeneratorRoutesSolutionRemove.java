package com.kaizten.vrp.opt.move.generator;

import com.kaizten.opt.move.MoveRoutesSolutionRemove;
import com.kaizten.opt.move.generator.AbstractMoveGenerator;
import com.kaizten.opt.solution.RoutesSolution;

public class MoveGeneratorRoutesSolutionRemove <S extends RoutesSolution<?>, M extends MoveRoutesSolutionRemove> extends AbstractMoveGenerator<S,M> {
	
	private int n;
    private int next;
    private int generated;
    
    public MoveGeneratorRoutesSolutionRemove() {
        this.next = 0;
        this.generated = 0;
    }
    
    @SuppressWarnings("rawtypes")
	@Override
    public void init() {
        this.next = 0;
        RoutesSolution solution = super.getManager().getSolution();
        this.n = solution.getNumberOfIncluded();
        this.generated = 0;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
    public M next() {
        MoveRoutesSolutionRemove move = null;
        if (this.hasNext()) {
            RoutesSolution solution = super.getManager().getSolution();
            while (!solution.isRouted(this.next)) {
                this.next++;
            }
            move = new MoveRoutesSolutionRemove(super.getManager().getSolution().getNumberOfObjectives());
            move.setToRemove(this.next);
            this.next++;
            this.generated++;
        }
        return (M) move;
    }

    @Override
    public boolean hasNext() {
        return (this.generated < this.n);
    }
}
