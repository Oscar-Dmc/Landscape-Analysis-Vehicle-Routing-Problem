package com.kaizten.vrp.opt.evaluators;

import java.util.ArrayList;

import com.kaizten.opt.evaluator.Evaluator;
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
		
		ArrayList<Integer> route = new ArrayList<Integer>();
		for(int i = 0;  i < solution.getRoute(move.getRoute()).length; i++) {
			route.add(solution.getRoute(move.getRoute())[i]);
		}
		
		if(route.isEmpty()) {
			tctRouteOriginal[0] = 0;
		} else {
			tctRouteOriginal[0] += solution.getOptimizationProblem().getDistanceMatrix()[0][route.get(0) + 1];
			for(int i = 1; i < route.size(); i++) {
				tctRouteOriginal[0] += solution.getOptimizationProblem().getDistanceMatrix()[route.get(i - 1) + 1][route.get(i) + 1];
			}
			tctRouteOriginal[0] += solution.getOptimizationProblem().getDistanceMatrix()[0][route.get(route.size() - 1) + 1];
		}
		
		route.add(route.indexOf(move.getAfter()) + 1, move.getToInsert());
		
		if(route.size() <= solution.getOptimizationProblem().getNMaxCustomers()) {
			tctRouteMod[0] += solution.getOptimizationProblem().getDistanceMatrix()[0][route.get(0) + 1];
			for(int i = 1; i < route.size(); i++) {
				tctRouteMod[0] += solution.getOptimizationProblem().getDistanceMatrix()[route.get(i - 1) + 1][route.get(i) + 1];
			}
			tctRouteMod[0] += solution.getOptimizationProblem().getDistanceMatrix()[0][route.get(route.size() - 1) + 1];
		} else {
			deviation[0] = -Evaluator.OBJECTIVE_INFEASIBLE;
			move.setDeviationObjectiveFunctionValue(0, deviation[0]);
			return deviation;
		}
		
		deviation[0] = tctRouteMod[0] - tctRouteOriginal[0];
		move.setDeviationObjectiveFunctionValue(0, deviation[0]);
		return deviation;
	}

}
