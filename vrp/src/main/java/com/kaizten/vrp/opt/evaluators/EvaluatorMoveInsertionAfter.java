package com.kaizten.vrp.opt.evaluators;

import com.kaizten.opt.evaluator.EvaluatorObjectiveFunctionMovement;
import com.kaizten.opt.move.MoveRoutesSolutionInsertionAfter;
import com.kaizten.opt.solution.RoutesSolution;
import com.kaizten.vrp.opt.core.Vrp;

public class EvaluatorMoveInsertionAfter extends EvaluatorObjectiveFunctionMovement<RoutesSolution<Vrp>, MoveRoutesSolutionInsertionAfter> {

	@Override
	public double[] evaluate(RoutesSolution<Vrp> solution, MoveRoutesSolutionInsertionAfter move) {
		double[] deviation =  new double [solution.getNumberOfObjectives()]; 
		double[] tctRouteOriginal = new double [solution.getNumberOfObjectives()];
		double[] tctRouteMod = new double [solution.getNumberOfObjectives()]; 
		int indexCustomer; 
		
		if(solution.getLengthRoute(move.getRoute()) > 0) {			
			indexCustomer =  solution.getFirstInRoute(move.getRoute());
			tctRouteOriginal[0] += solution.getOptimizationProblem().getDistanceMatrix()[0][indexCustomer + 1]; 
			while (solution.getSuccessor(indexCustomer) != -1) {
				tctRouteOriginal[0] += solution.getOptimizationProblem().getDistanceMatrix()[indexCustomer + 1][solution.getSuccessor(indexCustomer) + 1];
				indexCustomer = solution.getSuccessor(indexCustomer);
			}
			tctRouteOriginal[0] += solution.getOptimizationProblem().getDistanceMatrix()[0][indexCustomer + 1];
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
			tctRouteMod[0] += solutionInsertionAfter.getOptimizationProblem().getDistanceMatrix()[0][indexCustomer + 1];
			while (solutionInsertionAfter.getSuccessor(indexCustomer) != -1) {
				tctRouteMod[0] += solutionInsertionAfter.getOptimizationProblem().getDistanceMatrix()[indexCustomer + 1][solutionInsertionAfter.getSuccessor(indexCustomer) + 1];
				indexCustomer = solutionInsertionAfter.getSuccessor(indexCustomer);
			}
			tctRouteMod[0] += solutionInsertionAfter.getOptimizationProblem().getDistanceMatrix()[0][indexCustomer + 1];
		} else {
			deviation[0] = Double.MAX_VALUE;
			move.setDeviationObjectiveFunctionValue(0, deviation[0]);
			return deviation;
		}
		
		deviation[0] = tctRouteMod[0] - tctRouteOriginal[0];
		move.setDeviationObjectiveFunctionValue(0, deviation[0]);
		return deviation;
	}

}
