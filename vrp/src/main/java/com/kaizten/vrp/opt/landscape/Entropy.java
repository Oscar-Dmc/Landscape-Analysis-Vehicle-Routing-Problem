package com.kaizten.vrp.opt.landscape;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.kaizten.opt.landscape.AbstractLandscapeIndicator;
import com.kaizten.opt.solution.RoutesSolution;
import com.kaizten.vrp.opt.core.Vrp;

public class Entropy extends AbstractLandscapeIndicator<RoutesSolution<Vrp>, Double>{
	private List<RoutesSolution<Vrp>> solutions;
	
	@Override
	public void compute() {
		double entropy = 0;
		entropy = (-1 / (this.solutions.size() * Math.log(this.solutions.size()))) * this.getSumatory(); 
		System.out.println("Entropy: " + entropy);
		this.setValue(entropy);
		
	}

	private double getSumatory() {
		double value = 0; 
		for (int i = 0; i < this.solutions.size(); i++) {
			double objFunction = this.solutions.get(i).getObjectiveFunctionValue(0);
			double nRepeat = 0; 
			for(int j = 0; j < this.solutions.size(); j++) {
				if(objFunction == this.solutions.get(j).getObjectiveFunctionValue(0)) {
					nRepeat++; 
				}
			}
			
			value += ((nRepeat/this.solutions.size()) * Math.log(nRepeat / this.solutions.size()));
		}
		
		return value;
	}
	
	public void setSolutions(Stream<RoutesSolution<Vrp>> stream) {
		this.solutions = stream.collect(Collectors.toList()); 
	}
}
