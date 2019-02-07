package com.kaizten.vrp.opt.core;

import java.util.Random;

import com.kaizten.opt.solution.RoutesSolution;
import com.kaizten.vrp.opt.db.DBControl;

public class Vns {

	private RoutesSolution<Vrp> originalSolution;
	private DBControl dbControl; 
	
	public Vns(RoutesSolution<Vrp> solution) {
		this.originalSolution =  solution;
		this.dbControl = new DBControl();
		this.dbControl.init();
		this.dbControl.setOriginalProblem(this.originalSolution.getOptimizationProblem());
	}
	
	public RoutesSolution<Vrp> shake(int environment){
		Random rand =  new Random();
		long idSolution = (long)(rand.nextFloat() * this.dbControl.getNSolutionsEnvironment(environment)) + (this.dbControl.RANGE_OF_SOLUTIONS * environment); 
		return this.dbControl.getSolution(idSolution); 
	}
	
	public RoutesSolution<Vrp> bestImprovement(RoutesSolution<Vrp> x) {
		Double auxObjFunctionValue = x.getObjectiveFunctionValue(0);
		Long initialId = this.dbControl.exist(x);
		long i = initialId ,j = initialId;
		try {
			while(this.dbControl.getSolution(i).getObjectiveFunctionValue(0) >= auxObjFunctionValue) {
				i++; 
			}
		}catch (Exception e) {
			i =  initialId;
		}
		try {
			while(this.dbControl.getSolution(j).getObjectiveFunctionValue(0) >= auxObjFunctionValue) {
				j--; 
			}
		}catch (Exception e) {
			j =  initialId;
		}
		
		if(this.dbControl.getSolution(i).getObjectiveFunctionValue(0) <= this.dbControl.getSolution(j).getObjectiveFunctionValue(0)) {
			return this.dbControl.getSolution(i);
		}
		else {
			return this.dbControl.getSolution(j);
		}
	}
	

	public void neighborhoodChange(RoutesSolution<Vrp> neighborSolution, int k){
		if (this.originalSolution.getObjectiveFunctionValue(0) > neighborSolution.getObjectiveFunctionValue(0)) {	
			this.setOriginalSolution(neighborSolution);
			k = 0;
		}
		else {
			k++;
		}
	}
	
	public RoutesSolution<Vrp> basicVns(int kMax,  int tMax) {
		double t = 0;
		while (t < tMax) {

			long tInit =  System.currentTimeMillis();
			for(int k = 0;  k < kMax; k++) {
				this.neighborhoodChange(this.bestImprovement(this.shake(k)), k);;				
			}

			long tEnd =  System.currentTimeMillis();
			t  += ((tEnd - tInit) * 0.001);
		}
		return this.originalSolution;
	}
	
	/* ------ Gets and sets ---- */ 
	public RoutesSolution<Vrp> getOriginalSolution() {
		return originalSolution;
	}

	public void setOriginalSolution(RoutesSolution<Vrp> solution) {
		this.originalSolution = solution;
	} 
}
