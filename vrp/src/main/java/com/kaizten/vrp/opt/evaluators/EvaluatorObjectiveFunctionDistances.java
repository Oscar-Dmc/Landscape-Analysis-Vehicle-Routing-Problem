package com.kaizten.vrp.opt.evaluators;

import com.kaizten.opt.evaluator.EvaluatorObjectiveFunction;
import com.kaizten.opt.solution.RoutesSolution;
import com.kaizten.vrp.opt.core.Vrp;

public class EvaluatorObjectiveFunctionDistances extends EvaluatorObjectiveFunction<RoutesSolution<Vrp>> {

	public EvaluatorObjectiveFunctionDistances() {
		super(2);
	}
	
	@Override
	public void evaluate(RoutesSolution<Vrp> solution) {
		double [] objectives = new double[2];
 		for (int i = 0; i < solution.getNumberOfRoutes(); i++) {
			if(solution.getFirstInRoute(i) != -3) {
				objectives[0] += solution.getOptimizationProblem().getDistanceMatrix()[0][solution.getFirstInRoute(i) + 1];
				objectives[1] += solution.getOptimizationProblem().getCustomers().get(solution.getFirstInRoute(i) + 1).get(2);
				int indexCustomer = solution.getFirstInRoute(i);
				while (solution.getSuccessor(indexCustomer) != -1) {
					objectives[0] += solution.getOptimizationProblem().getDistanceMatrix()[indexCustomer + 1][solution.getSuccessor(indexCustomer) + 1];
					objectives[1] += solution.getOptimizationProblem().getCustomers().get(indexCustomer + 1).get(2);
					indexCustomer = solution.getSuccessor(indexCustomer);
				}
				objectives[0] += solution.getOptimizationProblem().getDistanceMatrix()[0][solution.getLastInRoute(i) + 1];
				objectives[1] += solution.getOptimizationProblem().getCustomers().get(solution.getLastInRoute(i) + 1).get(2);
			}
		}
		super.objectiveFunctionValue = objectives;	
	}

	@Override
	public void fillSolution(RoutesSolution<Vrp> solution) {}

}
