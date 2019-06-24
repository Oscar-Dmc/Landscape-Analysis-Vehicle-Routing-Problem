package com.kaizten.vrp.opt.landscape;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.kaizten.opt.landscape.AbstractLandscapeIndicator;
import com.kaizten.opt.solution.RoutesSolution;
import com.kaizten.vrp.opt.core.Vrp;

public class DistanceMedium  extends AbstractLandscapeIndicator<RoutesSolution<Vrp>, Double>{

	private List<RoutesSolution<Vrp>> solutions;
	private int availableMove; 
	private Distances calcDistances; 
	private double distanceMax; 
	private Double dmm; 
	
	public DistanceMedium() {
		this.calcDistances = new Distances();
		this.distanceMax = 0;
	}
	
	@Override
	public void compute() {
		double denominator = this.solutions.size() * (this.solutions.size() - 1); 
		this.dmm = new Double((this.totalDistance() / denominator)); 
		System.out.println("Distribution in the search space: " + this.dmm);
		double Dmm = this.dmm / this.distanceMax;
		System.out.println("Dmm Normalized: " + Dmm);
		this.setValue(this.dmm);
	}
	
	public double totalDistance() {
		double distance = 0; 
		double currentDistance = 0; 
		if(this.availableMove == 0) {
			for(int i = 0; i < this.solutions.size(); i++) {
				for(int j = i + 1; j < this.solutions.size(); j++) {
					currentDistance = this.calcDistances.distanceSwap(this.solutions.get(i), this.solutions.get(j));
					distance += currentDistance; 
					if(currentDistance >  distanceMax) {
						this.distanceMax = currentDistance; 
					}
				}
			}
		} else if (this.availableMove == 1 || this.availableMove == 2) {
			for(int i = 0; i < this.solutions.size(); i++) {
				for(int j = i + 1; j < this.solutions.size(); j++) {					
					currentDistance = this.calcDistances.distanceMove(this.solutions.get(i), this.solutions.get(j));
					distance += currentDistance; 
					if(currentDistance >  distanceMax) {
						this.distanceMax = currentDistance; 
					}
				}
			}
		} else if (this.availableMove > 2 && this.availableMove < 6) {
			for(int i = 0; i < this.solutions.size(); i++) {
				for(int j = i + 1; j < this.solutions.size(); j++) {					
					currentDistance = this.calcDistances.distanceInsRem(this.solutions.get(i), this.solutions.get(j));
					distance += currentDistance; 
					if(currentDistance >  distanceMax) {
						this.distanceMax = currentDistance; 
					}
				}
			}
		}
		return distance; 
	}
	
	public void setSolutions(Stream<RoutesSolution<Vrp>> stream, int availableMove) {
		this.solutions = stream.collect(Collectors.toList()); 
		this.availableMove = availableMove; 
	}

	
}
