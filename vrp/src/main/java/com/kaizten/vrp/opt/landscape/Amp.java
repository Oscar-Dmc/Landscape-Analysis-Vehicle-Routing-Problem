package com.kaizten.vrp.opt.landscape;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.kaizten.opt.landscape.AbstractLandscapeIndicator;
import com.kaizten.opt.solution.RoutesSolution;
import com.kaizten.vrp.opt.core.Vrp;

public class Amp extends AbstractLandscapeIndicator<RoutesSolution<Vrp>, Double>{

	private double bestValue;
	private double worstValue; 
	private double totalValue;
	private List<RoutesSolution<Vrp>> solutions; 
	
	public Amp() {
		this.bestValue = Double.MAX_VALUE; 
		this.worstValue = 0; 
		this.totalValue = 0;
	}
	@Override
	public void compute() {
		this.findBestWorstValues();
		double amp =  (this.solutions.size() * (this.worstValue - this.bestValue))/this.totalValue;
		System.out.println("Distribution in the objective space: " + amp);
		this.setValue(amp);
	}

	public void findBestWorstValues() {
		for(int i = 0; i < this.solutions.size(); i++) {
			this.totalValue += this.solutions.get(i).getObjectiveFunctionValue(0);
			if(this.solutions.get(i).getObjectiveFunctionValue(0) < this.bestValue) {
				this.bestValue = this.solutions.get(i).getObjectiveFunctionValue(0);
			}
			
			if(this.solutions.get(i).getObjectiveFunctionValue(0) > this.worstValue) {
				this.worstValue = this.solutions.get(i).getObjectiveFunctionValue(0); 
			}
		}
	}
	
	public void setSolutions(Stream<RoutesSolution<Vrp>> stream) {
		this.solutions = stream.collect(Collectors.toList()); 
	}
}
