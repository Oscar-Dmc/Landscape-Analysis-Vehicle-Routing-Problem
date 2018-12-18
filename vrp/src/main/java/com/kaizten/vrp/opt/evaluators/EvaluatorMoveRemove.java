package com.kaizten.vrp.opt.evaluators;

import com.kaizten.opt.evaluator.EvaluatorObjectiveFunctionMovement;
import com.kaizten.opt.move.MoveRemove;
import com.kaizten.opt.solution.RoutesSolution;
import com.kaizten.vrp.opt.core.Vrp;

public class EvaluatorMoveRemove extends EvaluatorObjectiveFunctionMovement<RoutesSolution<Vrp>, MoveRemove> {
	
	public EvaluatorMoveRemove() {
		super();
	}
	
	public EvaluatorMoveRemove(int objectives) {
		super(objectives);
	}

	@Override
	public double[] evaluate(RoutesSolution<Vrp> solution, MoveRemove move) {
		int indexRoute =  solution.getRouteIndex(move.getElement());
		System.out.println("Numero de objetivos " + solution.getNumberOfObjectives());
		double[] desviation =  new double [solution.getNumberOfObjectives()]; 
		double tctRouteOriginal = 0.0;
		double tctRouteMod = 0.0; 
		
		int indexCustomer =  solution.getFirstInRoute(indexRoute);
		tctRouteOriginal  += solution.getOptimizationProblem().getDistanceMatrix()[0][indexCustomer + 1]; 
		while (solution.getSuccessor(indexCustomer) != -1) {
			tctRouteOriginal +=  solution.getOptimizationProblem().getDistanceMatrix()[indexCustomer + 1][solution.getSuccessor(indexCustomer) + 1];
			indexCustomer = solution.getSuccessor(indexCustomer);
		}
		tctRouteOriginal += solution.getOptimizationProblem().getDistanceMatrix()[0][indexCustomer + 1];
		
		@SuppressWarnings("unchecked")
		RoutesSolution<Vrp> solutionRemove =  solution.clone();
		solutionRemove.remove(move.getElement());
		
		indexCustomer =  solutionRemove.getFirstInRoute(indexRoute);
		tctRouteMod  += solutionRemove.getOptimizationProblem().getDistanceMatrix()[0][indexCustomer + 1]; 
		while (solutionRemove.getSuccessor(indexCustomer) != -1) {
			tctRouteMod +=  solutionRemove.getOptimizationProblem().getDistanceMatrix()[indexCustomer + 1][solution.getSuccessor(indexCustomer) + 1];
			indexCustomer = solutionRemove.getSuccessor(indexCustomer);
		}
		tctRouteMod += solutionRemove.getOptimizationProblem().getDistanceMatrix()[0][indexCustomer + 1];
		
		
		desviation[0] = tctRouteOriginal - tctRouteMod;
		
		return desviation;
	}


}
