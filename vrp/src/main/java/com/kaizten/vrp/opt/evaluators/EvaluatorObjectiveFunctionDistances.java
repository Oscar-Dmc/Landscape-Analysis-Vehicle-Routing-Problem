package com.kaizten.vrp.opt.evaluators;

import com.kaizten.opt.evaluator.EvaluatorObjectiveFunction;
import com.kaizten.opt.evaluator.ObjectiveFunctionType;
import com.kaizten.opt.solution.RoutesSolution;
import com.kaizten.vrp.opt.core.Vrp;

public class EvaluatorObjectiveFunctionDistances extends EvaluatorObjectiveFunction<RoutesSolution<Vrp>> {
	
	enum namesObj {
		LATENCY
	}
	@SuppressWarnings("rawtypes")
	private Enum [] name; 
	private ObjectiveFunctionType[] type; 

	public EvaluatorObjectiveFunctionDistances() {
		super(1);
		this.name = new Enum[1];
		this.type = new ObjectiveFunctionType[1];
		
		this.name[0] = namesObj.LATENCY;
		this.type[0] = ObjectiveFunctionType.MINIMIZATION;
	}
	
	@Override
	public void evaluate(RoutesSolution<Vrp> solution) {
		double [] objectives = new double[1];
 		for (int i = 0; i < solution.getNumberOfRoutes(); i++) {
			if(solution.getFirstInRoute(i) != -3) {
				objectives[0] += solution.getOptimizationProblem().getDistanceMatrix()[0][solution.getFirstInRoute(i) + 1];
				int indexCustomer = solution.getFirstInRoute(i);
				while (solution.getSuccessor(indexCustomer) != -1) {
					objectives[0] += solution.getOptimizationProblem().getDistanceMatrix()[indexCustomer + 1][solution.getSuccessor(indexCustomer) + 1];
					indexCustomer = solution.getSuccessor(indexCustomer);
				}
				objectives[0] += solution.getOptimizationProblem().getDistanceMatrix()[0][solution.getLastInRoute(i) + 1];
			}
		}
		super.objectiveFunctionValue = objectives;	
	}

	@Override
	public void fillSolution(RoutesSolution<Vrp> solution) {}
	
	
	@SuppressWarnings("rawtypes")
	public Enum[] getName() {
		return this.name;
	}
	
	public ObjectiveFunctionType[] getType() {
		return type;
	}

}
