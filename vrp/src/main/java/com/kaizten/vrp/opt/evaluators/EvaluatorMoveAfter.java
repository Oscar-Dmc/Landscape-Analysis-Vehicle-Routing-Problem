package com.kaizten.vrp.opt.evaluators;

import java.util.ArrayList;

import com.kaizten.opt.evaluator.Evaluator;
import com.kaizten.opt.evaluator.EvaluatorObjectiveFunctionMovement;
import com.kaizten.opt.move.MoveRoutesSolutionMoveAfter;
import com.kaizten.opt.solution.RoutesSolution;
import com.kaizten.vrp.opt.core.Vrp;

public class EvaluatorMoveAfter extends EvaluatorObjectiveFunctionMovement<RoutesSolution<Vrp>, MoveRoutesSolutionMoveAfter>{

	@Override
	public double[] evaluate(RoutesSolution<Vrp> solution, MoveRoutesSolutionMoveAfter move) {
		ArrayList<Integer> indexRoutes =  new ArrayList<Integer>();
		double[] deviation =  new double [solution.getNumberOfObjectives()]; 
		double[] tctRouteOriginal = new double [solution.getNumberOfObjectives()];
		double[] tctRouteMod = new double [solution.getNumberOfObjectives()]; 
		int[] indexElement0 = new int [2]; /* 0 is route in ArrayList, and 1 is the index */
		int[] indexElement1 = new int [2];
		ArrayList<ArrayList<Integer>> routes = new ArrayList<ArrayList<Integer>>();
		
		indexRoutes.add(solution.getRouteIndex(move.getElement0()));
		if(!indexRoutes.contains(solution.getRouteIndex(move.getElement1()))){
			indexRoutes.add(solution.getRouteIndex(move.getElement1()));
		}
		
		for(int i = 0; i < indexRoutes.size(); i++) {
			ArrayList<Integer> route =  new ArrayList<Integer>();
			int [] routeInSolution = solution.getRoute(indexRoutes.get(i));
			for(int j = 0; j < routeInSolution.length; j++) {
				if(routeInSolution[j] == move.getElement0()) {
					indexElement0[0] = i;
					indexElement0[1] = j;
				} else if (routeInSolution[j] == move.getElement1()) {
					indexElement1[0] = i;
					indexElement1[1] = j;
				}
				route.add(routeInSolution[j]);
			}
			routes.add(route);
		}
		
		for(int i = 0; i < indexRoutes.size(); i++) {
			tctRouteOriginal[0] += solution.getOptimizationProblem().getDistanceMatrix()[0][routes.get(i).get(0) + 1]; 
			for(int j = 1;  j < routes.get(i).size(); j ++) {
				tctRouteOriginal[0] += solution.getOptimizationProblem().getDistanceMatrix()[routes.get(i).get(j - 1) + 1][routes.get(i).get(j) + 1];
			}
			tctRouteOriginal[0] += solution.getOptimizationProblem().getDistanceMatrix()[0][routes.get(i).get(routes.get(i).size() - 1) + 1];
		}
		
		routes.get(indexElement0[0]).remove(indexElement0[1]);
		routes.get(indexElement1[0]).add(routes.get(indexElement1[0]).indexOf(move.getElement1()) + 1, move.getElement0());
		
		for(int i = 0; i < indexRoutes.size(); i++) {
			if(routes.get(i).size() <= solution.getOptimizationProblem().getNMaxCustomers()) {
				if(!routes.get(i).isEmpty()) {
					tctRouteMod[0] += solution.getOptimizationProblem().getDistanceMatrix()[0][routes.get(i).get(0) + 1]; 
					for(int j = 1;  j < routes.get(i).size(); j ++) {
						tctRouteMod[0] += solution.getOptimizationProblem().getDistanceMatrix()[routes.get(i).get(j - 1) + 1][routes.get(i).get(j) + 1];
					}
					tctRouteMod[0] += solution.getOptimizationProblem().getDistanceMatrix()[0][routes.get(i).get(routes.get(i).size() - 1) + 1];
				}
			} else {
				deviation[0] = -Evaluator.OBJECTIVE_INFEASIBLE;
				move.setDeviationObjectiveFunctionValue(0, deviation[0]);
				return deviation;
			}
		}
		
		deviation[0] = tctRouteMod[0] - tctRouteOriginal[0];
		move.setDeviationObjectiveFunctionValue(0, deviation[0]);
		return deviation;
	}

}
