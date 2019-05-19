package com.kaizten.vrp.opt.evaluators;

import java.util.ArrayList;

import com.kaizten.opt.evaluator.EvaluatorObjectiveFunctionMovement;
import com.kaizten.opt.move.MoveRoutesSolutionMoveBefore;
import com.kaizten.opt.solution.RoutesSolution;
import com.kaizten.vrp.opt.core.Vrp;


public class EvaluatorMoveBefore extends EvaluatorObjectiveFunctionMovement<RoutesSolution<Vrp>, MoveRoutesSolutionMoveBefore>{
	@Override
	public double[] evaluate(RoutesSolution<Vrp> solution, MoveRoutesSolutionMoveBefore move) {
		
		ArrayList<Integer> indexRoutes =  new ArrayList<Integer>();
		double[] deviation =  new double [solution.getNumberOfObjectives()];
		double[] tctRouteOriginal = new double [solution.getNumberOfObjectives()];
		double[] tctRouteMod = new double [solution.getNumberOfObjectives()];
		int[] indexElement0 = new int [2]; /* 0 if route in ArrayList, and 1 is the index */
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
		/* Chivatos, parte no relevante del código
		System.out.println("Rutas Afectadas: " + indexRoutes.size());
		for(int i = 0; i < indexRoutes.size(); i++) {
			for(int j = 0;  j < routes.get(i).size(); j ++) {
				System.out.print(" [ " + routes.get(i).get(j) + " ] ");
			}
			System.out.println();
		}
		/*System.out.println("Elemento 0 -> Array: " + indexElement0[0] + " Posición: " + indexElement0[1]);
		System.out.println("Elemento 1 -> Array: " + indexElement1[0] + " Posición: " + indexElement1[1]);*/
		
		/*---------------------------------------------*/
		for(int i = 0; i < indexRoutes.size(); i++) {
			tctRouteOriginal[0] += solution.getOptimizationProblem().getDistanceMatrix()[0][routes.get(i).get(0) + 1]; 
			for(int j = 1;  j < routes.get(i).size(); j ++) {
				tctRouteOriginal[0] += solution.getOptimizationProblem().getDistanceMatrix()[routes.get(i).get(j - 1) + 1][routes.get(i).get(j) + 1];
			}
			tctRouteOriginal[0] += solution.getOptimizationProblem().getDistanceMatrix()[0][routes.get(i).get(routes.get(i).size() - 1) + 1];
		}
	  /* Método viejo  
		for (int i = 0; i < indexRoutes.size();  i++) {
			int indexCustomer =  solution.getFirstInRoute(indexRoutes.get(i));
			tctRouteOriginal[0] += solution.getOptimizationProblem().getDistanceMatrix()[0][indexCustomer + 1];
			while (solution.getSuccessor(indexCustomer) != -1) {
				tctRouteOriginal[0] += solution.getOptimizationProblem().getDistanceMatrix()[indexCustomer + 1][solution.getSuccessor(indexCustomer) + 1];
				indexCustomer = solution.getSuccessor(indexCustomer);
			}
			tctRouteOriginal[0] += solution.getOptimizationProblem().getDistanceMatrix()[0][solution.getLastInRoute(indexRoutes.get(i)) + 1];
		}*/
		
		/* Mas Chivatos 
		System.out.println("Método nuevo: " + tctRouteOriginalAux[0] + " Método viejo " + tctRouteOriginal[0]);*/
		
		routes.get(indexElement0[0]).remove(indexElement0[1]);
		routes.get(indexElement1[0]).add(routes.get(indexElement1[0]).indexOf(move.getElement1()), move.getElement0());
		
		/* Ruta después del movimiento 
		System.out.println("-----------------------------" );
		for(int i = 0; i < indexRoutes.size(); i++) {
			for(int j = 0;  j < routes.get(i).size(); j ++) {
				System.out.print(" [ " + routes.get(i).get(j) + " ] ");
			}
			System.out.println();
		} */
		
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
				deviation[0] = Double.MAX_VALUE;
				move.setDeviationObjectiveFunctionValue(0, deviation[0]);
				return deviation;
			}
		}
		
		/*@SuppressWarnings("unchecked")
		RoutesSolution<Vrp> solutionMoveBefore =  solution.clone();
		solutionMoveBefore.addBefore(move.getElement0(), move.getElement1());
		
		for (int i = 0; i < indexRoutes.size();  i++) {
			if(solutionMoveBefore.getLengthRoute(indexRoutes.get(i)) <= solutionMoveBefore.getOptimizationProblem().getNMaxCustomers()) {
				if(solutionMoveBefore.getLengthRoute(indexRoutes.get(i)) > 0) {
					int indexCustomer =  solutionMoveBefore.getFirstInRoute(indexRoutes.get(i));
					tctRouteMod[0] += solutionMoveBefore.getOptimizationProblem().getDistanceMatrix()[0][indexCustomer + 1];
					
					while (solutionMoveBefore.getSuccessor(indexCustomer) != -1) {
						tctRouteMod[0] += solutionMoveBefore.getOptimizationProblem().getDistanceMatrix()[indexCustomer + 1][solutionMoveBefore.getSuccessor(indexCustomer) + 1];
						indexCustomer = solutionMoveBefore.getSuccessor(indexCustomer);
					}
					tctRouteMod[0] += solutionMoveBefore.getOptimizationProblem().getDistanceMatrix()[0][solutionMoveBefore.getLastInRoute(indexRoutes.get(i)) + 1];
					
				}
			} else {
				deviation[0] = Double.MAX_VALUE;
				move.setDeviationObjectiveFunctionValue(0, deviation[0]);
				return deviation;
			}
		} 
		
		System.out.println("Nuevo método mod: " + tctRouteModAux[0] + " Método Viejo: " + tctRouteMod[0]); */
		
		deviation[0] = tctRouteMod[0] - tctRouteOriginal[0];
		move.setDeviationObjectiveFunctionValue(0, deviation[0]);
		return deviation;
	}
}
