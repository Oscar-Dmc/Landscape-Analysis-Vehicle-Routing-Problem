package com.kaizten.vrp.opt.landscape;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.kaizten.opt.landscape.AbstractLandscapeIndicator;
import com.kaizten.opt.solution.RoutesSolution;
import com.kaizten.vrp.opt.core.Vrp;

public class Lmm extends AbstractLandscapeIndicator<RoutesSolution<Vrp>, Double>{

	private double totalLength;
	private List<RoutesSolution<Vrp>> solutions; 
	
	public Lmm() {
		this.totalLength = 0;
	}
	@Override
	public void compute() {
		this.calcTotalLength();
		double lmm = this.totalLength / this.solutions.size();
		System.out.println("Length of the walks: " + lmm);
		this.setValue(lmm);
	}
	
	public void calcTotalLength() {
		this.totalLength = 0; 
		for(int i = 0; i < this.solutions.size(); i++) {
			double value = this.solutions.get(i).getObjectiveFunctionValue(0);
			int nextSolution = i + 1; 
			while(nextSolution < this.solutions.size() && value > this.solutions.get(nextSolution).getObjectiveFunctionValue(0)) {
				value = this.solutions.get(nextSolution).getObjectiveFunctionValue(0);
				this.totalLength++;
				nextSolution++;
			}
		}
	}
	
	public void setSolutions(Stream<RoutesSolution<Vrp>> stream) {
		this.solutions = stream.collect(Collectors.toList()); 
	}

}
