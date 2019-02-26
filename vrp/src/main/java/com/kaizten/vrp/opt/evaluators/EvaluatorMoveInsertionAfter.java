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
		
		int indexCustomer =  solution.getFirstInRoute(move.getRoute());
		tctRouteOriginal  += solution.getOptimizationProblem().getDistanceMatrix()[0][indexCustomer + 1]; 
		while (solution.getSuccessor(indexCustomer) != -1) {
			tctRouteOriginal +=  solution.getOptimizationProblem().getDistanceMatrix()[indexCustomer + 1][solution.getSuccessor(indexCustomer) + 1];
			indexCustomer = solution.getSuccessor(indexCustomer);
		}
		tctRouteOriginal += solution.getOptimizationProblem().getDistanceMatrix()[0][indexCustomer + 1];
		
		@SuppressWarnings("unchecked")
		RoutesSolution<Vrp> solutionInsertionAfter =  solution.clone();
		//System.out.println("Desglose de momvimiento insertionAfter -> Ruta: " + move.getRoute() + " Get To insert: " + move.getToInsert() + " After: " + move.getAfter());
		if(move.getAfter() == -1) {
			solutionInsertionAfter.addAfterDepot(move.getToInsert(), move.getRoute());
		}
		else {			
			solutionInsertionAfter.addAfter(move.getToInsert(), move.getAfter());
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
