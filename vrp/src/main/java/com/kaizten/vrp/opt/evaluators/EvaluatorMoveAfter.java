package com.kaizten.vrp.opt.evaluators;

import java.util.ArrayList;

import com.kaizten.opt.evaluator.EvaluatorObjectiveFunctionMovement;
import com.kaizten.opt.solution.RoutesSolution;
import com.kaizten.vrp.opt.core.Vrp;
import com.kaizten.vrp.opt.move.MoveRoutesSolutionMoveAfter;

public class EvaluatorMoveAfter extends EvaluatorObjectiveFunctionMovement<RoutesSolution<Vrp>, MoveRoutesSolutionMoveAfter>{

	@Override
	public double[] evaluate(RoutesSolution<Vrp> solution, MoveRoutesSolutionMoveAfter move) {
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
		RoutesSolution<Vrp> solutionMoveAfter =  solution.clone();
		solutionMoveAfter.addAfter(move.getElement0(), move.getElement1());
		
		for (int i = 0; i < indexRoutes.size();  i++) {
			if(solutionMoveAfter.getLengthRoute(indexRoutes.get(i)) <= solutionMoveAfter.getOptimizationProblem().getNMaxCustomers()) {
				if(solutionMoveAfter.getLengthRoute(indexRoutes.get(i)) > 0) {
					int indexCustomer =  solutionMoveAfter.getFirstInRoute(indexRoutes.get(i));
					tctRouteMod  += solutionMoveAfter.getOptimizationProblem().getDistanceMatrix()[0][indexCustomer + 1]; 
					while (solutionMoveAfter.getSuccessor(indexCustomer) != -1) {
						tctRouteMod +=  solutionMoveAfter.getOptimizationProblem().getDistanceMatrix()[indexCustomer + 1][solutionMoveAfter.getSuccessor(indexCustomer) + 1];
						indexCustomer = solutionMoveAfter.getSuccessor(indexCustomer);
					}
					tctRouteMod += solutionMoveAfter.getOptimizationProblem().getDistanceMatrix()[0][solutionMoveAfter.getLastInRoute(indexRoutes.get(i)) + 1];
					
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
