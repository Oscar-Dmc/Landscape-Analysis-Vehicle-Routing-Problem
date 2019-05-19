package com.kaizten.vrp.opt.evaluators;

import java.util.ArrayList;

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
		
		ArrayList<Integer> route = new ArrayList<Integer>();
		for(int i = 0;  i < solution.getRoute(indexRoute).length; i++) {
			route.add(solution.getRoute(indexRoute)[i]);
		}
		
		tctRouteOriginal[0] += solution.getOptimizationProblem().getDistanceMatrix()[0][route.get(0) + 1];
		for(int i = 1; i < route.size(); i++) {
			tctRouteOriginal[0] += solution.getOptimizationProblem().getDistanceMatrix()[route.get(i - 1) + 1][route.get(i) + 1];
		}
		tctRouteOriginal[0] += solution.getOptimizationProblem().getDistanceMatrix()[0][route.get(route.size() - 1) + 1];
		
		Integer toRemove =  move.getToRemove();
		route.remove(toRemove); 
		
		if(route.isEmpty()) {
			tctRouteMod[0] = 0;
		}
		else{
			tctRouteMod[0] += solution.getOptimizationProblem().getDistanceMatrix()[0][route.get(0) + 1];
			for(int i = 1; i < route.size(); i++) {
				tctRouteMod[0] += solution.getOptimizationProblem().getDistanceMatrix()[route.get(i - 1) + 1][route.get(i) + 1];
			}
			tctRouteMod[0] += solution.getOptimizationProblem().getDistanceMatrix()[0][route.get(route.size() - 1) + 1];
		}
		
		
		deviation[0] = tctRouteMod[0] - tctRouteOriginal[0];
		move.setDeviationObjectiveFunctionValue(0, deviation[0]);
		
		return deviation;
	}


}
