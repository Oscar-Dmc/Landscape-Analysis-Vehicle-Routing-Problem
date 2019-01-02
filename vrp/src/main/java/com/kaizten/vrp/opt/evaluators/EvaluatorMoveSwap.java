package com.kaizten.vrp.opt.evaluators;

import com.kaizten.opt.evaluator.EvaluatorObjectiveFunctionMovement;
import com.kaizten.opt.move.MoveRoutesSolutionSwap;
import com.kaizten.opt.solution.RoutesSolution;
import com.kaizten.vrp.opt.core.Vrp;

public class EvaluatorMoveSwap extends EvaluatorObjectiveFunctionMovement<RoutesSolution<Vrp>, MoveRoutesSolutionSwap>{
	
	public EvaluatorMoveSwap() {
		super();
	}
	
	public EvaluatorMoveSwap(int objectives) {
		super(objectives);
	}

	@Override
	public double[] evaluate(RoutesSolution<Vrp> solution, MoveRoutesSolutionSwap move) {
		int[] indexRoutes =  new int[2]; 
		double[] desviation =  new double [solution.getNumberOfObjectives()]; 
		double tctRouteOriginal = 0.0;
		double tctRouteMod = 0.0; 
		indexRoutes[0] = solution.getRouteIndex(move.getElement0());
		indexRoutes[1] = solution.getRouteIndex(move.getElement1());
		
		for (int i = 0; i < indexRoutes.length;  i++) {
			int indexCustomer =  solution.getFirstInRoute(indexRoutes[i]);
			tctRouteOriginal  += solution.getOptimizationProblem().getDistanceMatrix()[0][indexCustomer + 1]; 
			while (solution.getSuccessor(indexCustomer) != -1) {
				tctRouteOriginal +=  solution.getOptimizationProblem().getDistanceMatrix()[indexCustomer + 1][solution.getSuccessor(indexCustomer) + 1];
				indexCustomer = solution.getSuccessor(indexCustomer);
			}
			tctRouteOriginal += solution.getOptimizationProblem().getDistanceMatrix()[0][indexCustomer + 1];
		}
		
		@SuppressWarnings("unchecked")
		RoutesSolution<Vrp> solutionSwap =  solution.clone();
		solutionSwap.swap(move.getElement0(), move.getElement1());
		
		for (int i = 0; i < indexRoutes.length;  i++) {
			int indexCustomer =  solutionSwap.getFirstInRoute(indexRoutes[i]);
			tctRouteOriginal  += solutionSwap.getOptimizationProblem().getDistanceMatrix()[0][indexCustomer + 1]; 
			while (solutionSwap.getSuccessor(indexCustomer) != -1) {
				tctRouteOriginal +=  solutionSwap.getOptimizationProblem().getDistanceMatrix()[indexCustomer + 1][solutionSwap.getSuccessor(indexCustomer) + 1];
				indexCustomer = solutionSwap.getSuccessor(indexCustomer);
			}
			tctRouteMod += solutionSwap.getOptimizationProblem().getDistanceMatrix()[0][indexCustomer + 1];
		}
		
		desviation[0] = tctRouteOriginal - tctRouteMod;
		return desviation;
	}

}
