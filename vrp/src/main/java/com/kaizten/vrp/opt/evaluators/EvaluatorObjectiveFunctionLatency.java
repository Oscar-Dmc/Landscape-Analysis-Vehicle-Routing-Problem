package com.kaizten.vrp.opt.evaluators;

import com.kaizten.opt.evaluator.EvaluatorSingleObjectiveFunction;
import com.kaizten.opt.evaluator.ObjectiveFunctionType;
import com.kaizten.opt.solution.RoutesSolution;
import com.kaizten.vrp.opt.core.Vrp;

public class EvaluatorObjectiveFunctionLatency extends EvaluatorSingleObjectiveFunction<RoutesSolution<Vrp>> {
	
	enum namesObj {
		LATENCY
	}
	@SuppressWarnings("rawtypes")
	private Enum [] name; 
	private ObjectiveFunctionType[] type; 

	public EvaluatorObjectiveFunctionLatency() {
		this.name = new Enum[1];
		this.type = new ObjectiveFunctionType[1];
		
		this.name[0] = namesObj.LATENCY;
		this.type[0] = ObjectiveFunctionType.MINIMIZATION;
	}
	
	@Override
	public void evaluate(RoutesSolution<Vrp> solution) {
		double objective = 0.0;
 		for (int i = 0; i < solution.getNumberOfRoutes(); i++) {
			if(solution.getFirstInRoute(i) != -3) {
				objective += solution.getOptimizationProblem().getDistanceMatrix()[0][solution.getFirstInRoute(i) + 1];
				int indexCustomer = solution.getFirstInRoute(i);
				while (solution.getSuccessor(indexCustomer) != -1) {
					objective += solution.getOptimizationProblem().getDistanceMatrix()[indexCustomer + 1][solution.getSuccessor(indexCustomer) + 1];
					indexCustomer = solution.getSuccessor(indexCustomer);
				}
				objective += solution.getOptimizationProblem().getDistanceMatrix()[0][solution.getLastInRoute(i) + 1];
			}
		}
		super.setObjectiveFunctionValue(objective);
 		
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
