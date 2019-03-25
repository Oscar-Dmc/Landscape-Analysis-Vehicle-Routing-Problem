package com.kaizten.vrp.opt.evaluators;

import com.kaizten.opt.evaluator.EvaluatorObjectiveFunctionMovement;
import com.kaizten.opt.move.MoveRoutesSolutionInsertionBefore;
import com.kaizten.opt.solution.RoutesSolution;
import com.kaizten.vrp.opt.core.Vrp;

public class EvaluatorMoveInsertionBefore extends EvaluatorObjectiveFunctionMovement<RoutesSolution<Vrp>, MoveRoutesSolutionInsertionBefore>{

	@Override
	public double[] evaluate(RoutesSolution<Vrp> solution, MoveRoutesSolutionInsertionBefore move) {
		double[] deviation = new double [solution.getNumberOfObjectives()]; 
		double[] tctRouteOriginal = new double [solution.getNumberOfObjectives()];
		double[] tctRouteMod = new double [solution.getNumberOfObjectives()]; 
		
		int indexCustomer = solution.getFirstInRoute(move.getRoute());
		tctRouteOriginal[0] += solution.getOptimizationProblem().getDistanceMatrix()[0][indexCustomer + 1]; 
		tctRouteOriginal[1] += solution.getOptimizationProblem().getCustomers().get(indexCustomer + 1).get(2);
		while (solution.getSuccessor(indexCustomer) != -1) {
			tctRouteOriginal[0] += solution.getOptimizationProblem().getDistanceMatrix()[indexCustomer + 1][solution.getSuccessor(indexCustomer) + 1];
			indexCustomer = solution.getSuccessor(indexCustomer);
			tctRouteOriginal[1] += solution.getOptimizationProblem().getCustomers().get(indexCustomer + 1).get(2);
		}
		tctRouteOriginal[0] += solution.getOptimizationProblem().getDistanceMatrix()[0][indexCustomer + 1];
		tctRouteOriginal[1] += solution.getOptimizationProblem().getCustomers().get(indexCustomer +1).get(2);
		
		@SuppressWarnings("unchecked")
		RoutesSolution<Vrp> solutionInsertionBefore =  solution.clone();
		if(move.getBefore() == -1) {
			solutionInsertionBefore.addAfterDepot(move.getToInsert(), move.getRoute());
		}
		else {			
			solutionInsertionBefore.addAfter(move.getToInsert(), move.getBefore());
		}
		
		if(solutionInsertionBefore.getLengthRoute(move.getRoute()) <= solutionInsertionBefore.getOptimizationProblem().getNMaxCustomers()) {
			indexCustomer = solutionInsertionBefore.getFirstInRoute(move.getRoute());
			tctRouteMod[0] += solutionInsertionBefore.getOptimizationProblem().getDistanceMatrix()[0][indexCustomer + 1]; 
			tctRouteMod[1] += solutionInsertionBefore.getOptimizationProblem().getCustomers().get(indexCustomer + 1).get(2);
			while (solutionInsertionBefore.getSuccessor(indexCustomer) != -1) {
				tctRouteMod[0] += solutionInsertionBefore.getOptimizationProblem().getDistanceMatrix()[indexCustomer + 1][solutionInsertionBefore.getSuccessor(indexCustomer) + 1];
				indexCustomer = solutionInsertionBefore.getSuccessor(indexCustomer);
				tctRouteMod[1] += solutionInsertionBefore.getOptimizationProblem().getCustomers().get(indexCustomer + 1).get(2);
			}
			tctRouteMod[0] += solutionInsertionBefore.getOptimizationProblem().getDistanceMatrix()[0][indexCustomer + 1];
			tctRouteMod[1] += solutionInsertionBefore.getOptimizationProblem().getCustomers().get(indexCustomer + 1).get(2);
		} else {
			deviation[0] = Double.MAX_VALUE;
			deviation[1] = Double.MAX_VALUE;
			move.setDeviationObjectiveFunctionValue(0, deviation[0]);
			move.setDeviationObjectiveFunctionValue(1, deviation[1]);
			return deviation;
		}
		
		deviation[0] = tctRouteMod[0] - tctRouteOriginal[0];
		deviation[1] = tctRouteMod[1] - tctRouteOriginal[1];
		move.setDeviationObjectiveFunctionValue(0, deviation[0]);
		move.setDeviationObjectiveFunctionValue(1, deviation[1]);
		return deviation;
	}

}
