package com.kaizten.vrp.opt.evaluators;

import com.kaizten.opt.evaluator.EvaluatorObjectiveFunction;
import com.kaizten.opt.solution.RoutesSolution;
import com.kaizten.vrp.opt.core.Vrp;

public class EvaluatorObjectiveFunctionDistances extends EvaluatorObjectiveFunction<RoutesSolution<Vrp>> {
	
	public double CalculateTCT(RoutesSolution<Vrp> solution) {
		double tct = 0; 
		for(int i = 0;  i < solution.getNumberOfRoutes(); i++) {
			tct += solution.getOptimizationProblem().getDistanceMatrix()[0][solution.getFirstInRoute(i)+1];
			int indexCustomer =  solution.getFirstInRoute(i);
			while(solution.getSuccessor(indexCustomer) != -1) {
				tct += solution.getOptimizationProblem().getDistanceMatrix()[indexCustomer + 1][solution.getSuccessor(indexCustomer) + 1];
				indexCustomer =solution.getSuccessor(indexCustomer);
			}
			tct += solution.getOptimizationProblem().getDistanceMatrix()[0][solution.getLastInRoute(i)+1];
		}
		
		return tct; 
	}
	

	@Override
	public void evaluate(RoutesSolution<Vrp> solution) {
		double tct =  CalculateTCT(solution);
		this.objectiveFunctionValue[0] = tct;		
	}

	@Override
	public void fillSolution(RoutesSolution<Vrp> solution) {
		// TODO Auto-generated method stub
		
	}

}
