package com.kaizten.vrp.opt.landscape;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.kaizten.opt.landscape.AbstractLandscapeIndicator;
import com.kaizten.opt.solution.RoutesSolution;
import com.kaizten.vrp.opt.core.Vrp;

public class AutocorrelationFunction extends AbstractLandscapeIndicator<RoutesSolution<Vrp>, Double> {
	
	private List<RoutesSolution<Vrp>> solutions; 
	private double valueMedium; 
	private double totalDistance; 
	private List<Double> diferenceMedium; 
	
	public AutocorrelationFunction() {
		this.valueMedium = 0; 
		this.diferenceMedium = new ArrayList<Double>();
	}
	
	@Override
	public void compute() {
		this.calcTotalDistance();
		double autocorrelation = this.totalDistance/ (this.solutions.size() * this.calcVariance()); 
		System.out.println("Autocorrelation function: " + autocorrelation);
		this.setValue(autocorrelation);
	}

	private void calcValueMedium() {
		double totalValue = 0;
		for(int i = 0; i < this.solutions.size(); i++) {
			totalValue += this.solutions.get(i).getObjectiveFunctionValue(0); 
		}
		this.valueMedium = totalValue / this.solutions.size();
	}
	
	private void calcTotalDistance() {
		this.calcValueMedium();
		this.calcDiferenceMedium();
		for(int i = 0; i < this.diferenceMedium.size(); i++) {
			for(int j = i + 1; j <this.diferenceMedium.size(); j++) {
				this.totalDistance += (this.diferenceMedium.get(i) * this.diferenceMedium.get(j)); 
			}
		}
	}
	
	private double calcVariance() {
		double variance = 0;
		for(int i = 0; i < this.diferenceMedium.size(); i++) {
			variance += Math.pow(this.diferenceMedium.get(i), 2);
		}
		
		return (variance / this.diferenceMedium.size()); 
	}
	
	private void calcDiferenceMedium() {
		for (int i = 0;  i < this.solutions.size(); i++) {
			double value1 = this.solutions.get(i).getObjectiveFunctionValue(0) - this.valueMedium;
			this.diferenceMedium.add(value1);
		}
	}
	
	public void setSolutions(Stream<RoutesSolution<Vrp>> stream) {
		this.solutions = stream.collect(Collectors.toList()); 
	}
	
	
}
