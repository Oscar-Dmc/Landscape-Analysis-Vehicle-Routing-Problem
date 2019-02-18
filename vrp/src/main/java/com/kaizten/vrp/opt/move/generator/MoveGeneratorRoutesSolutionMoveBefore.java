package com.kaizten.vrp.opt.move.generator;

import java.util.ArrayList;
import java.util.Random;

import com.kaizten.opt.move.generator.AbstractMoveGenerator;
import com.kaizten.opt.solution.RoutesSolution;
import com.kaizten.vrp.opt.move.MoveRoutesSolutionMoveBefore;

public class MoveGeneratorRoutesSolutionMoveBefore <S extends RoutesSolution<?>,  M extends MoveRoutesSolutionMoveBefore> extends AbstractMoveGenerator<S, M>{

	private int n;
	private ArrayList<Integer[]> availableMoves; 
	
	public MoveGeneratorRoutesSolutionMoveBefore() {
		this.availableMoves = new ArrayList<Integer[]>();
	}
	
	public void init() {
		this.n =  super.getManager().getSolution().size();
		for(int i = 0;  i < this.n; i++) {
			for(int j = i + 1; j < this.n;  j++) {
				Integer[] pair = new Integer[2];
				pair[0] = i;
				pair[1] = j;
				this.availableMoves.add(pair);
			}
		}
	}
	
	private Integer[] getRandomPair() {
		Random rand = new Random();
		int index = rand.nextInt(this.availableMoves.size());
		Integer[] pair = this.availableMoves.get(index);
		this.availableMoves.remove(index);
		return pair; 
	}
	
	@Override
	public boolean hasNext() {
		return !this.availableMoves.isEmpty();
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
			Integer [] pair =  this.getRandomPair(); 
			move.setElement0(pair[0]);
			move.setElement1(pair[1]);
		}
		return (M) move;
	}

}
