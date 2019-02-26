package com.kaizten.vrp.opt.evaluators;

import java.util.ArrayList;

import com.kaizten.opt.evaluator.EvaluatorObjectiveFunctionMovement;
import com.kaizten.opt.move.MoveRoutesSolutionSwap;
import com.kaizten.opt.solution.RoutesSolution;
import com.kaizten.vrp.opt.core.Vrp;

public class EvaluatorMoveSwap extends EvaluatorObjectiveFunctionMovement<RoutesSolution<Vrp>, MoveRoutesSolutionSwap>{
	
	public EvaluatorMoveSwap() {
		super();
	}
	
	public EvaluatorMoveSwap(int objectives) {
		super(objectives);
	}
	
	public void SolutionSwap (RoutesSolution<Vrp> solution, MoveRoutesSolutionSwap move) {		
		if(solution.inSameRoute(move.getElement0(), move.getElement1())) {
			this.swapInSameRoute(solution, move);
		}
		else if(solution.getLengthRoute(solution.getRouteIndex(move.getElement0())) == 1 ||
				solution.getLengthRoute(solution.getRouteIndex(move.getElement1())) == 1) {
			if(solution.getLengthRoute(solution.getRouteIndex(move.getElement0())) == 1 ) {
				int indexRoute =  solution.getRouteIndex(move.getElement0());
				solution.addAfter(move.getElement0(), move.getElement1());
				solution.remove(move.getElement1());
				solution.addBeforeDepot(move.getElement1(), indexRoute);
			} else {
				int indexRoute =  solution.getRouteIndex(move.getElement1());
				solution.addAfter(move.getElement1(), move.getElement0());
				solution.remove(move.getElement0());
				solution.addBeforeDepot(move.getElement0(), indexRoute);
			}
		} else {
			if(solution.getPredecessor(move.getElement0()) != -1) {
				int pred0 =  solution.getPredecessor(move.getElement0());
				solution.addAfter(move.getElement0(), move.getElement1());
				solution.addAfter(move.getElement1(), pred0);

			} else {
				int succ0 =  solution.getSuccessor(move.getElement0());
				solution.addAfter(move.getElement0(), move.getElement1());
				solution.addBefore(move.getElement1(), succ0);

			}
		}
	}

	public void swapInSameRoute(RoutesSolution<Vrp> solution, MoveRoutesSolutionSwap move) {
		if(solution.getPredecessor(move.getElement0()) == move.getElement1()) {
			solution.addBefore(move.getElement0(), move.getElement1());
		}
		else if (solution.getPredecessor(move.getElement1()) == move.getElement0()) {
			solution.addBefore(move.getElement1(), move.getElement0());
		}
		else {
			int pred0 =  solution.getPredecessor(move.getElement0());
			int pred1 =  solution.getPredecessor(move.getElement1());
			if(pred0 == -1) {
				solution.addBefore(move.getElement1(), move.getElement0());
				solution.addAfter(move.getElement0(), pred1);
			}
			else if(pred1 == -1) {
				solution.addBefore(move.getElement0(), move.getElement1());
				solution.addAfter(move.getElement1(), pred0);
			}
			else {
				solution.addAfter(move.getElement1(), pred0);
				solution.addAfter(move.getElement0(), pred1);
			}
		}
		
	}
	@Override
	public double[] evaluate(RoutesSolution<Vrp> solution, MoveRoutesSolutionSwap move) {
		ArrayList<Integer> indexRoutes =  new ArrayList<Integer>(); 
		double[] desviation =  new double [solution.getNumberOfObjectives()]; 
		double tctRouteOriginal = 0.0;
		double tctRouteMod = 0.0; 
		indexRoutes.add(solution.getRouteIndex(move.getElement0()));
		if(!indexRoutes.contains(solution.getRouteIndex(move.getElement1()))){
			indexRoutes.add(solution.getRouteIndex(move.getElement1()));
		}
		for (int i = 0; i < indexRoutes.size();  i++) {
			int indexCustomer =  solution.getFirstInRoute(indexRoutes.get(i));
			tctRouteOriginal  += solution.getOptimizationProblem().getDistanceMatrix()[0][indexCustomer + 1]; 
			while (solution.getSuccessor(indexCustomer) != -1) {
				tctRouteOriginal +=  solution.getOptimizationProblem().getDistanceMatrix()[indexCustomer + 1][solution.getSuccessor(indexCustomer) + 1];
				indexCustomer = solution.getSuccessor(indexCustomer);
			}
			tctRouteOriginal += solution.getOptimizationProblem().getDistanceMatrix()[0][solution.getLastInRoute(indexRoutes.get(i)) + 1];
		}
		
		@SuppressWarnings("unchecked")
		RoutesSolution<Vrp> solutionSwap =  solution.clone();
		this.SolutionSwap(solutionSwap, move);		
		for (int i = 0; i < indexRoutes.size();  i++) {
			int indexCustomer =  solutionSwap.getFirstInRoute(indexRoutes.get(i));
			tctRouteMod  += solutionSwap.getOptimizationProblem().getDistanceMatrix()[0][indexCustomer + 1]; 
			while (solutionSwap.getSuccessor(indexCustomer) != -1) {
				tctRouteMod +=  solutionSwap.getOptimizationProblem().getDistanceMatrix()[indexCustomer + 1][solutionSwap.getSuccessor(indexCustomer) + 1];
				indexCustomer = solutionSwap.getSuccessor(indexCustomer);
			}
			tctRouteMod += solutionSwap.getOptimizationProblem().getDistanceMatrix()[0][solutionSwap.getLastInRoute(indexRoutes.get(i)) + 1];
		} 
		desviation[0] = tctRouteMod - tctRouteOriginal;
		move.setDeviationObjectiveFunctionValue(0, desviation[0]);
		return desviation;
	}

}
