package com.kaizten.vrp.opt.move;

import com.kaizten.opt.move.Move;

public class MoveRoutesSolutionMoveAfter extends Move {
	private int element0;
	private int element1;
	
	public MoveRoutesSolutionMoveAfter(int objectives) {
		super(objectives);
		this.element0 = -1;
		this.element1 = -1;
	}
	
	public int getElement0() {
		return this.element0;
	}
	
	public int getElement1() {
		return this.element1;
	}
	
	public void setElement0(int element0) {
		this.element0 = element0;
	}
	
	public void setElement1(int element1) {
		this.element1 = element1;
	}
	
	@Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("RoutesSolutionMoveAfter");
        builder.append(super.toString());
        return builder.toString();
    }
}
