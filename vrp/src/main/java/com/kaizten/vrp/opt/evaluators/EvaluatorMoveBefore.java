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
		double[] deviation =  new double [solution.getNumberOfObjectives()];
		solution.evaluate();
		double[] tctRouteOriginal = new double [solution.getNumberOfObjectives()];
		double[] tctRouteMod = new double [solution.getNumberOfObjectives()];
		indexRoutes.add(solution.getRouteIndex(move.getElement0()));
		if(!indexRoutes.contains(solution.getRouteIndex(move.getElement1()))){
			indexRoutes.add(solution.getRouteIndex(move.getElement1()));
		}
		
		for (int i = 0; i < indexRoutes.size();  i++) {
			int indexCustomer =  solution.getFirstInRoute(indexRoutes.get(i));
			tctRouteOriginal[0] += solution.getOptimizationProblem().getDistanceMatrix()[0][indexCustomer + 1];
			tctRouteOriginal[1] += solution.getOptimizationProblem().getCustomers().get(indexCustomer + 1).get(2); 
			while (solution.getSuccessor(indexCustomer) != -1) {
				tctRouteOriginal[0] += solution.getOptimizationProblem().getDistanceMatrix()[indexCustomer + 1][solution.getSuccessor(indexCustomer) + 1];
				indexCustomer = solution.getSuccessor(indexCustomer);
				tctRouteOriginal[1] += solution.getOptimizationProblem().getCustomers().get(indexCustomer + 1).get(2);
			}
			tctRouteOriginal[0] += solution.getOptimizationProblem().getDistanceMatrix()[0][solution.getLastInRoute(indexRoutes.get(i)) + 1];
			tctRouteOriginal[1] += solution.getOptimizationProblem().getCustomers().get(solution.getLastInRoute(indexRoutes.get(i)) + 1).get(2);
		}
		
		@SuppressWarnings("unchecked")
		RoutesSolution<Vrp> solutionMoveBefore =  solution.clone();
		solutionMoveBefore.addBefore(move.getElement0(), move.getElement1());
		
		for (int i = 0; i < indexRoutes.size();  i++) {
			if(solutionMoveBefore.getLengthRoute(indexRoutes.get(i)) <= solutionMoveBefore.getOptimizationProblem().getNMaxCustomers()) {
				if(solutionMoveBefore.getLengthRoute(indexRoutes.get(i)) > 0) {
					int indexCustomer =  solutionMoveBefore.getFirstInRoute(indexRoutes.get(i));
					tctRouteMod[0] += solutionMoveBefore.getOptimizationProblem().getDistanceMatrix()[0][indexCustomer + 1];
					tctRouteMod[1] += solutionMoveBefore.getOptimizationProblem().getCustomers().get(indexCustomer + 1).get(2);
					
					while (solutionMoveBefore.getSuccessor(indexCustomer) != -1) {
						tctRouteMod[0] += solutionMoveBefore.getOptimizationProblem().getDistanceMatrix()[indexCustomer + 1][solutionMoveBefore.getSuccessor(indexCustomer) + 1];
						indexCustomer = solutionMoveBefore.getSuccessor(indexCustomer);
						tctRouteMod[1] += solutionMoveBefore.getOptimizationProblem().getCustomers().get(indexCustomer + 1).get(2);
					}
					tctRouteMod[0] += solutionMoveBefore.getOptimizationProblem().getDistanceMatrix()[0][solutionMoveBefore.getLastInRoute(indexRoutes.get(i)) + 1];
					tctRouteMod[1] += solutionMoveBefore.getOptimizationProblem().getCustomers().get(solutionMoveBefore.getLastInRoute(indexRoutes.get(i)) + 1).get(2);
					
				}
			} else {
				deviation[0] = Double.MAX_VALUE;
				deviation[1] = Double.MAX_VALUE;
				move.setDeviationObjectiveFunctionValue(0, deviation[0]);
				move.setDeviationObjectiveFunctionValue(1, deviation[1]);
				return deviation;
			}
		} 
		
		deviation[0] = tctRouteMod[0] - tctRouteOriginal[0];
		deviation[1] = tctRouteMod[1] - tctRouteOriginal[1];
		move.setDeviationObjectiveFunctionValue(0, deviation[0]);
		move.setDeviationObjectiveFunctionValue(1, deviation[1]);
		return deviation;
	}
}
