package com.kaizten.vrp.opt.landscape;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.kaizten.opt.landscape.AbstractLandscapeIndicator;
import com.kaizten.opt.solution.RoutesSolution;
import com.kaizten.vrp.opt.core.Vrp;

public class FitnessDistanceCorrelation extends AbstractLandscapeIndicator<RoutesSolution<Vrp>, Double>{
	private List<RoutesSolution<Vrp>> solutions; 
	private int availableMove;
	private List<Double> distances; 
	private int indexOptima; 
	private Distances calcDistances; 
	private double distaceMedium;
	private double valueMedium; 
	
	public FitnessDistanceCorrelation () {
		this.availableMove = -1; 
		this.distances = new ArrayList<Double>();
		this.indexOptima = 0; 
		this.calcDistances = new Distances();
		this.distaceMedium = 0;
		this.valueMedium = 0; 
	}

	@Override
	public void compute() {
		double cov = this.calcCOV(); 
		double desviationDistance = this.calcDesviationDistance();
		double desviationValue = this.calcDesviationValue();
		double r = cov / (desviationDistance * desviationValue); 
		System.out.println("Fitness distance correlation: " + r);
		this.setValue(r);
	}
	
	private void getIndexOptima() {
		double bestValue = this.solutions.get(0).getObjectiveFunctionValue(0);
		for(int i = 0; i < this.solutions.size(); i++) {
			if(bestValue > this.solutions.get(i).getObjectiveFunctionValue(0)) {
				this.indexOptima = i;
				bestValue = this.solutions.get(i).getObjectiveFunctionValue(0);
			}
		}
	}
	
	private void getDistances() {
		double currentDistance = 0;
		this.getIndexOptima();
		if(this.availableMove == 0) {
			for(int i  = 0; i < this.solutions.size(); i++) {
				currentDistance = this.calcDistances.distanceSwap(this.solutions.get(i), this.solutions.get(this.indexOptima));
				this.distances.add(currentDistance);
			}
		} else if(this.availableMove == 1 || this.availableMove == 2) {
			for(int i  = 0; i < this.solutions.size(); i++) {
				currentDistance = this.calcDistances.distanceMove(this.solutions.get(i), this.solutions.get(this.indexOptima));
				this.distances.add(currentDistance);
			}
		}else if(this.availableMove > 2 && this.availableMove < 6) {
			for(int i  = 0; i < this.solutions.size(); i++) {
				currentDistance = this.calcDistances.distanceInsRem(this.solutions.get(i), this.solutions.get(this.indexOptima));
				this.distances.add(currentDistance);
			}
		}
	}
	
	private void calcDistanceMedium() {
		double distanceMedium = 0;
		this.getDistances();
		for(int i = 0; i < this.distances.size(); i++) {
			distanceMedium += this.distances.get(i);
		}
		
		this.distaceMedium = (distanceMedium / this.distances.size()); 
	}
	
	private void calcValueMedium() {
		double valueMedium = 0;
		for(int i = 0; i < this.solutions.size(); i++) {
			valueMedium += this.solutions.get(i).getObjectiveFunctionValue(0);
		}
		
		this.valueMedium = (valueMedium / this.solutions.size());
	}
	
	private double calcCOV() {
		double cov = 0;
		this.calcDistanceMedium();
		this.calcValueMedium();
		for (int i = 0; i < this.solutions.size(); i++) {
			cov += ((this.solutions.get(i).getObjectiveFunctionValue(0) - this.valueMedium) * (this.distances.get(i) - this.distaceMedium));
		}
		
		return (cov / this.solutions.size());
	}
	
	private double calcDesviationDistance() {
		double desviation = 0; 
		for(int i = 0;  i < this.solutions.size(); i++) {
			desviation += Math.pow((this.distances.get(i) - this.distaceMedium), 2); 
		}
		
		desviation = (desviation / this.solutions.size());
		return Math.sqrt(desviation);
	}
	
	private double calcDesviationValue() {
		double desviation = 0; 
		for(int i = 0; i < this.solutions.size(); i++) {
			desviation += Math.pow(this.solutions.get(i).getObjectiveFunctionValue(0) - this.valueMedium, 2);
		}
		desviation = (desviation / this.solutions.size());
		
		return Math.sqrt(desviation);
	}
	
	public void setSolutions(Stream<RoutesSolution<Vrp>> stream, int availableMove) {
		this.solutions = stream.collect(Collectors.toList()); 
		this.availableMove = availableMove; 
	}
	
}
