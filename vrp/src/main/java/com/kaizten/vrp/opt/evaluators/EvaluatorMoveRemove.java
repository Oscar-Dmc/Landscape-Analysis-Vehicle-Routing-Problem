package com.kaizten.vrp.opt.evaluators;

import com.kaizten.opt.evaluator.EvaluatorObjectiveFunctionMovement;
import com.kaizten.opt.move.MoveRoutesSolutionRemove;
import com.kaizten.opt.solution.RoutesSolution;
import com.kaizten.vrp.opt.core.Vrp;

public class EvaluatorMoveRemove extends EvaluatorObjectiveFunctionMovement<RoutesSolution<Vrp>, MoveRoutesSolutionRemove> {
	
	public EvaluatorMoveRemove() {
		super();
	}
	
	public EvaluatorMoveRemove(int objectives) {
		super(objectives);
	}

	@Override
	public double[] evaluate(RoutesSolution<Vrp> solution, MoveRoutesSolutionRemove move) {
		int indexRoute = solution.getRouteIndex(move.getToRemove());
		double[] deviation = new double [solution.getNumberOfObjectives()];
		double[] tctRouteOriginal = new double [solution.getNumberOfObjectives()];
		double[] tctRouteMod = new double [solution.getNumberOfObjectives()]; 
		
		int indexCustomer = solution.getFirstInRoute(indexRoute);
		tctRouteOriginal[0] += solution.getOptimizationProblem().getDistanceMatrix()[0][indexCustomer + 1];
		while (solution.getSuccessor(indexCustomer) != -1) {
			tctRouteOriginal[0] += solution.getOptimizationProblem().getDistanceMatrix()[indexCustomer + 1][solution.getSuccessor(indexCustomer) + 1];
			indexCustomer = solution.getSuccessor(indexCustomer);
		}
		tctRouteOriginal[0] += solution.getOptimizationProblem().getDistanceMatrix()[0][indexCustomer + 1];
		
		@SuppressWarnings("unchecked")
		RoutesSolution<Vrp> solutionRemove = solution.clone();
		if (solutionRemove.getLengthRoute(indexRoute) == 1) {
			indexCustomer = solutionRemove.getFirstInRoute(indexRoute);
			tctRouteMod[0] += solutionRemove.getOptimizationProblem().getDistanceMatrix()[0][indexCustomer + 1];
			solutionRemove.remove(move.getToRemove());
			
		} else {
			solutionRemove.remove(move.getToRemove());
			indexCustomer = solutionRemove.getFirstInRoute(indexRoute);
			tctRouteMod[0] += solutionRemove.getOptimizationProblem().getDistanceMatrix()[0][indexCustomer + 1]; 
			
			while (solutionRemove.getSuccessor(indexCustomer) != -1) {
				tctRouteMod[0] += solutionRemove.getOptimizationProblem().getDistanceMatrix()[indexCustomer + 1][solution.getSuccessor(indexCustomer) + 1];
				indexCustomer = solutionRemove.getSuccessor(indexCustomer);
			}
			tctRouteMod[0] += solutionRemove.getOptimizationProblem().getDistanceMatrix()[0][indexCustomer + 1];
		}
		
		deviation[0] = tctRouteMod[0] - tctRouteOriginal[0];
		move.setDeviationObjectiveFunctionValue(0, deviation[0]);
		
		return deviation;
	}


}
