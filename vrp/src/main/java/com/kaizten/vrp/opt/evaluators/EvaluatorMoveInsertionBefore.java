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
		RoutesSolution<Vrp> solutionInsertionAfter =  solution.clone();
		//System.out.println("Desglose de momvimiento insertionBefore -> Ruta: " + move.getRoute() + " Get To insert: " + move.getToInsert() + " Before: " + move.getBefore());
		if(move.getBefore() == -1) {
			solutionInsertionAfter.addBeforeDepot(move.getToInsert(), move.getRoute());
		}
		else {			
			solutionInsertionAfter.addBefore(move.getToInsert(), move.getBefore());
		}
		
		indexCustomer =  solutionInsertionAfter.getFirstInRoute(move.getRoute());
		tctRouteMod  += solutionInsertionAfter.getOptimizationProblem().getDistanceMatrix()[0][indexCustomer + 1]; 
		while (solutionInsertionAfter.getSuccessor(indexCustomer) != -1) {
			tctRouteMod +=  solutionInsertionAfter.getOptimizationProblem().getDistanceMatrix()[indexCustomer + 1][solution.getSuccessor(indexCustomer) + 1];
			indexCustomer = solutionInsertionAfter.getSuccessor(indexCustomer);
		}
		tctRouteMod += solutionInsertionAfter.getOptimizationProblem().getDistanceMatrix()[0][indexCustomer + 1];
		
		
		desviation[0] = tctRouteOriginal - tctRouteMod;
		
		return desviation;
	}

}
