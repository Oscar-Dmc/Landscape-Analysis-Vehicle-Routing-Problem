package com.kaizten.vrp.opt.move.generator;

import java.util.ArrayList;
import java.util.Random;

import com.kaizten.opt.move.MoveRoutesSolutionSwap;
import com.kaizten.opt.move.generator.AbstractMoveGenerator;
import com.kaizten.opt.solution.RoutesSolution;

public class MoveGeneratorRoutesSolutionSwap <S extends RoutesSolution<?>, M extends MoveRoutesSolutionSwap> extends AbstractMoveGenerator<S,M>{
	
	private int n;
	/*private int element0;
	private int element1;*/
	private ArrayList<Integer[]> availableMoves; 
	
	public MoveGeneratorRoutesSolutionSwap() {
		this.availableMoves = new ArrayList<Integer[]>();
		/*this.element0 = 0;
		this.element1 = 1; */
	}
	
	public void init() {
		/*this.element0 = 0;
		this.element1 = 1;*/
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
		//return null;
	}
	
	@Override
	public boolean hasNext() {
		return !this.availableMoves.isEmpty();
		/*return (this.element0 < this.n) && (this.element1 < this.n) ;*/
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public M next() {
		MoveRoutesSolutionSwap move =  null;
		if(this.hasNext()) {
			RoutesSolution solution =  super.getManager().getSolution();
			if(solution == null) {
				System.out.println("Null Solution");
				System.exit(0);
			}
			move = new MoveRoutesSolutionSwap(super.getManager().getSolution().getNumberOfObjectives());
			Integer [] pair =  this.getRandomPair(); 
			System.out.println("[" + pair[0] + "][" + pair[1] + "]");
			move.setElement0(pair[0]);
			move.setElement1(pair[1]);
			/*move.setElement0(element0);
			move.setElement1(element1);
			this.element1++;
			if(this.element1 == this.n) {
				this.element0++;
				this.element1 = this.element0 + 1;
			}*/
		}
		return (M) move;
	}

}
