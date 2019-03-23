package com.kaizten.vrp.opt.evaluators;

import com.kaizten.opt.evaluator.EvaluatorObjectiveFunctionMovement;
import com.kaizten.opt.move.MoveRoutesSolutionInsertionAfter;
import com.kaizten.opt.solution.RoutesSolution;
import com.kaizten.vrp.opt.core.Vrp;

public class EvaluatorMoveInsertionAfter extends EvaluatorObjectiveFunctionMovement<RoutesSolution<Vrp>, MoveRoutesSolutionInsertionAfter> {

	@Override
	public double[] evaluate(RoutesSolution<Vrp> solution, MoveRoutesSolutionInsertionAfter move) {
		double[] desviation =  new double [solution.getNumberOfObjectives()]; 
		double tctRouteOriginal = 0.0;
		double tctRouteMod = 0.0; 
		int indexCustomer; 
		
		if(solution.getLengthRoute(move.getRoute()) > 0) {			
			indexCustomer =  solution.getFirstInRoute(move.getRoute());
			tctRouteOriginal  += solution.getOptimizationProblem().getDistanceMatrix()[0][indexCustomer + 1]; 
			while (solution.getSuccessor(indexCustomer) != -1) {
				tctRouteOriginal +=  solution.getOptimizationProblem().getDistanceMatrix()[indexCustomer + 1][solution.getSuccessor(indexCustomer) + 1];
				indexCustomer = solution.getSuccessor(indexCustomer);
			}
			tctRouteOriginal += solution.getOptimizationProblem().getDistanceMatrix()[0][indexCustomer + 1];
		}
		
		@SuppressWarnings("unchecked")
		RoutesSolution<Vrp> solutionInsertionAfter =  solution.clone();
		if(move.getAfter() == -1) {
			solutionInsertionAfter.addAfterDepot(move.getToInsert(), move.getRoute());
		}
		else {			
			solutionInsertionAfter.addAfter(move.getToInsert(), move.getAfter());
		}
		
		if(solutionInsertionAfter.getLengthRoute(move.getRoute()) <= solutionInsertionAfter.getOptimizationProblem().getNMaxCustomers()) {
			indexCustomer =  solutionInsertionAfter.getFirstInRoute(move.getRoute());
			tctRouteMod  += solutionInsertionAfter.getOptimizationProblem().getDistanceMatrix()[0][indexCustomer + 1];
			while (solutionInsertionAfter.getSuccessor(indexCustomer) != -1) {
				tctRouteMod +=  solutionInsertionAfter.getOptimizationProblem().getDistanceMatrix()[indexCustomer + 1][solutionInsertionAfter.getSuccessor(indexCustomer) + 1];
				indexCustomer = solutionInsertionAfter.getSuccessor(indexCustomer);
			}
			tctRouteMod += solutionInsertionAfter.getOptimizationProblem().getDistanceMatrix()[0][indexCustomer + 1];
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
