package com.kaizten.vrp.opt.evaluators;

import com.kaizten.opt.evaluator.EvaluatorObjectiveFunctionMovement;
import com.kaizten.opt.move.MoveRoutesSolutionInsertionBefore;
import com.kaizten.opt.solution.RoutesSolution;
import com.kaizten.vrp.opt.core.Vrp;

public class EvaluatorMoveInsertionBefore extends EvaluatorObjectiveFunctionMovement<RoutesSolution<Vrp>, MoveRoutesSolutionInsertionBefore>{

	@Override
	public double[] evaluate(RoutesSolution<Vrp> solution, MoveRoutesSolutionInsertionBefore move) {
		double[] desviation =  new double [solution.getNumberOfObjectives()]; 
		double tctRouteOriginal = 0.0;
		double tctRouteMod = 0.0; 
		
		int indexCustomer =  solution.getFirstInRoute(move.getRoute());
		tctRouteOriginal  += solution.getOptimizationProblem().getDistanceMatrix()[0][indexCustomer + 1]; 
		while (solution.getSuccessor(indexCustomer) != -1) {
			tctRouteOriginal +=  solution.getOptimizationProblem().getDistanceMatrix()[indexCustomer + 1][solution.getSuccessor(indexCustomer) + 1];
			indexCustomer = solution.getSuccessor(indexCustomer);
		}
		tctRouteOriginal += solution.getOptimizationProblem().getDistanceMatrix()[0][indexCustomer + 1];
		
		@SuppressWarnings("unchecked")
		RoutesSolution<Vrp> solutionInsertionBefore =  solution.clone();
		if(move.getBefore() == -1) {
			solutionInsertionBefore.addAfterDepot(move.getToInsert(), move.getRoute());
		}
		else {			
			solutionInsertionBefore.addAfter(move.getToInsert(), move.getBefore());
		}
		
		if(solutionInsertionBefore.getLengthRoute(move.getRoute()) <= solutionInsertionBefore.getOptimizationProblem().getNMaxCustomers()) {
			indexCustomer =  solutionInsertionBefore.getFirstInRoute(move.getRoute());
			tctRouteMod  += solutionInsertionBefore.getOptimizationProblem().getDistanceMatrix()[0][indexCustomer + 1]; 
			while (solutionInsertionBefore.getSuccessor(indexCustomer) != -1) {
				tctRouteMod +=  solutionInsertionBefore.getOptimizationProblem().getDistanceMatrix()[indexCustomer + 1][solutionInsertionBefore.getSuccessor(indexCustomer) + 1];
				indexCustomer = solutionInsertionBefore.getSuccessor(indexCustomer);
			}
			tctRouteMod += solutionInsertionBefore.getOptimizationProblem().getDistanceMatrix()[0][indexCustomer + 1];
		} else {
			desviation[0] = Double.MAX_VALUE;
			move.setDeviationObjectiveFunctionValue(0, desviation[0]);
			return desviation;
		}
		
		desviation[0] = tctRouteMod - tctRouteOriginal;
		move.setDeviationObjectiveFunctionValue(0, desviation[0]);
		return desviation;
	}

}
