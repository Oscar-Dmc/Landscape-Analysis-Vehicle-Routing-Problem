package com.kaizten.vrp.opt.evaluators;

import java.util.ArrayList;

import com.kaizten.opt.evaluator.EvaluatorObjectiveFunctionMovement;
import com.kaizten.opt.solution.RoutesSolution;
import com.kaizten.vrp.opt.core.Vrp;

import com.kaizten.vrp.opt.move.MoveRoutesSolutionMoveBefore;

public class EvaluatorMoveBefore extends EvaluatorObjectiveFunctionMovement<RoutesSolution<Vrp>, MoveRoutesSolutionMoveBefore>{
	@Override
	public double[] evaluate(RoutesSolution<Vrp> solution, MoveRoutesSolutionMoveBefore move) {
		ArrayList<Integer> indexRoutes =  new ArrayList<Integer>();
		double[] desviation =  new double [solution.getNumberOfObjectives()]; 
		solution.evaluate();
		double tctRouteOriginal = 0.0;
		double tctRouteMod = 0.0; 
		indexRoutes.add(solution.getRouteIndex(move.getElement0()));
		if(!indexRoutes.contains(solution.getRouteIndex(move.getElement1()))){
			indexRoutes.add(solution.getRouteIndex(move.getElement1()));
		}
		
		for (int i = 0; i < indexRoutes.size();  i++) {
			int indexCustomer =  solution.getFirstInRoute(indexRoutes.get(i));
			tctRouteOriginal  += solution.getOptimizationProblem().getDistanceMatrix()[0][indexCustomer + 1]; 
			while (solution.getSuccessor(indexCustomer) != -1) {
				tctRouteOriginal +=  solution.getOptimizationProblem().getDistanceMatrix()[indexCustomer + 1][solution.getSuccessor(indexCustomer) + 1];
				indexCustomer = solution.getSuccessor(indexCustomer);
			}
			tctRouteOriginal += solution.getOptimizationProblem().getDistanceMatrix()[0][solution.getLastInRoute(indexRoutes.get(i)) + 1];
		}
		
		@SuppressWarnings("unchecked")
		RoutesSolution<Vrp> solutionMoveBefore =  solution.clone();
		solutionMoveBefore.addBefore(move.getElement0(), move.getElement1());
		
		for (int i = 0; i < indexRoutes.size();  i++) {
			if(solutionMoveBefore.getLengthRoute(indexRoutes.get(i)) <= solutionMoveBefore.getOptimizationProblem().getNMaxCustomers()) {
				if(solutionMoveBefore.getLengthRoute(indexRoutes.get(i)) > 0) {
					int indexCustomer =  solutionMoveBefore.getFirstInRoute(indexRoutes.get(i));
					tctRouteMod  += solutionMoveBefore.getOptimizationProblem().getDistanceMatrix()[0][indexCustomer + 1]; 
					while (solutionMoveBefore.getSuccessor(indexCustomer) != -1) {
						tctRouteMod +=  solutionMoveBefore.getOptimizationProblem().getDistanceMatrix()[indexCustomer + 1][solutionMoveBefore.getSuccessor(indexCustomer) + 1];
						indexCustomer = solutionMoveBefore.getSuccessor(indexCustomer);
					}
					tctRouteMod += solutionMoveBefore.getOptimizationProblem().getDistanceMatrix()[0][solutionMoveBefore.getLastInRoute(indexRoutes.get(i)) + 1];
					
				}
			} else {
				desviation[0] = Double.MAX_VALUE;
				move.setDeviationObjectiveFunctionValue(0, desviation[0]);
				return desviation;
			}
		} 
		
		desviation[0] = tctRouteMod - tctRouteOriginal;
		move.setDeviationObjectiveFunctionValue(0, desviation[0]);
		return desviation;
	}
}
