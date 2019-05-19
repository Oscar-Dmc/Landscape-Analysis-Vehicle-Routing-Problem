package com.kaizten.vrp.opt.evaluators;

import java.util.ArrayList;

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

		if(route.indexOf(move.getBefore()) == -1) {
			route.add(route.size(), move.getToInsert());
		} else {
			route.add(route.indexOf(move.getBefore()), move.getToInsert());			
		}
		
		if(route.size() <= solution.getOptimizationProblem().getNMaxCustomers()) {
			tctRouteMod[0] += solution.getOptimizationProblem().getDistanceMatrix()[0][route.get(0) + 1];
			for(int i = 1; i < route.size(); i++) {
				tctRouteMod[0] += solution.getOptimizationProblem().getDistanceMatrix()[route.get(i - 1) + 1][route.get(i) + 1];
			}
			tctRouteMod[0] += solution.getOptimizationProblem().getDistanceMatrix()[0][route.get(route.size() - 1) + 1];
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
