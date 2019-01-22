package com.kaizten.vrp.opt.evaluators;

import com.kaizten.opt.evaluator.EvaluatorObjectiveFunctionMovement;
import com.kaizten.opt.solution.RoutesSolution;
import com.kaizten.vrp.opt.core.Vrp;

import com.kaizten.vrp.opt.move.MoveRoutesSolutionMoveBefore;

public class EvaluatorMoveBefore extends EvaluatorObjectiveFunctionMovement<RoutesSolution<Vrp>, MoveRoutesSolutionMoveBefore>{
	@Override
	public double[] evaluate(RoutesSolution<Vrp> solution, MoveRoutesSolutionMoveBefore move) {
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
		RoutesSolution<Vrp> solutionMoveAfter =  solution.clone();
		solutionMoveAfter.addAfter(move.getElement0(), move.getElement1());
		for (int i = 0; i < indexRoutes.length;  i++) {
			int indexCustomer =  solutionMoveAfter.getFirstInRoute(indexRoutes[i]);
			tctRouteMod  += solutionMoveAfter.getOptimizationProblem().getDistanceMatrix()[0][indexCustomer + 1]; 
			while (solutionMoveAfter.getSuccessor(indexCustomer) != -1) {
				tctRouteMod +=  solutionMoveAfter.getOptimizationProblem().getDistanceMatrix()[indexCustomer + 1][solutionMoveAfter.getSuccessor(indexCustomer) + 1];
				indexCustomer = solutionMoveAfter.getSuccessor(indexCustomer);
			}
			tctRouteMod += solutionMoveAfter.getOptimizationProblem().getDistanceMatrix()[0][indexCustomer + 1];
		}
		
		desviation[0] = tctRouteOriginal - tctRouteMod;
		return desviation;
	}
}
