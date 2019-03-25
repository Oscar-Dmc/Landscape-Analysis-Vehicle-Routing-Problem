package com.kaizten.vrp.opt.core;

import java.util.ArrayList;
import java.util.Random;

import com.kaizten.opt.move.MoveRoutesSolutionInsertionAfter;
import com.kaizten.opt.move.applier.MoveApplierRoutesSolutionInsertionAfter;
import com.kaizten.opt.solution.RoutesSolution;
import com.kaizten.opt.solver.Solver;
import com.kaizten.vrp.opt.evaluators.EvaluatorMoveInsertionAfter;

public class LNS implements Solver<RoutesSolution<Vrp>>{
	private Vrp problem;
	private RoutesSolution<Vrp> originalSolution; 
	private double executionTime; 
	
	public LNS(Vrp problem) {
		this.problem =  problem; 
		this.originalSolution =  sequentialSolutionConstruct();
		this.executionTime = 60;
	}
	
	/*@SuppressWarnings("unchecked")
	public RoutesSolution<Vrp> ALNS() {
		RoutesSolution<Vrp> bestSolution = this.originalSolution.clone(); 
		double percent = 0.01;
		long time_start, time_end;
		while(this.executionTime > 0 ) {
			time_start = System.currentTimeMillis();
			RoutesSolution<Vrp> temporalSolution = this.partialDestroyer(this.originalSolution, percent);
			temporalSolution.evaluate();
			this.repairSolution(temporalSolution);
			//System.out.println(temporalSolution);
			//if(initialSolution.getObjectiveFunctionValue(0) > temporalSolution.getObjectiveFunctionValue(0)) {
			setOriginalSolution(temporalSolution);
			//}
			if(bestSolution.getObjectiveFunctionValue(0) > temporalSolution.getObjectiveFunctionValue(0)) {
				bestSolution = temporalSolution.clone();
				percent = adjustPercents(1, percent);
			} else if (this.originalSolution.getObjectiveFunctionValue(0) > temporalSolution.getObjectiveFunctionValue(0)) {
				percent = adjustPercents(2, percent);
			} else {
				percent = adjustPercents(3, percent);
				System.out.println("hola");
			} 
			System.out.println(percent);
			time_end = System.currentTimeMillis();
			this.executionTime -= (( time_end - time_start ) * 0.001);
		}
		return bestSolution;
	} */
	
	public double adjustPercents(int weight, double percent) {
		return (0.9 * percent) + ((1 - 0.9) * weight);  
	}
	

	
	@SuppressWarnings("unchecked")
	public RoutesSolution<Vrp> randomDestroyer(RoutesSolution<Vrp> solution, double percent ) {
		RoutesSolution<Vrp> partialSolution = solution.clone();
		int nCustomersToRemove = (int) (partialSolution.getOptimizationProblem().getNCustomers() * percent); 
		Random rand = new Random();
		ArrayList<Integer> indexToRemove = new ArrayList<Integer>();
		while(indexToRemove.size() < nCustomersToRemove) {
			int index = rand.nextInt(partialSolution.getOptimizationProblem().getNCustomers());
			if(!indexToRemove.contains(index)) {
				indexToRemove.add(index);
			}
		}
		for(int i = 0; i < nCustomersToRemove; i++) {
			partialSolution.remove(indexToRemove.get(i));
		}
		return partialSolution;
	}
	
	public RoutesSolution<Vrp> randomRepair(RoutesSolution<Vrp> solution){
		return null; 
	}
	
	public RoutesSolution<Vrp> bestRepair(RoutesSolution<Vrp> solution){
		EvaluatorMoveInsertionAfter evaluator = new EvaluatorMoveInsertionAfter();
		MoveApplierRoutesSolutionInsertionAfter applier = new MoveApplierRoutesSolutionInsertionAfter();
		
		while(!solution.getNonIncluded(0, solution.getOptimizationProblem().getNCustomers() - 1).isEmpty()) {
			MoveRoutesSolutionInsertionAfter currentMove = new MoveRoutesSolutionInsertionAfter(2);
			MoveRoutesSolutionInsertionAfter bestMove = new MoveRoutesSolutionInsertionAfter(2);
			for(int i = 0; i < solution.getNonIncluded(0, solution.getOptimizationProblem().getNCustomers() - 1).size(); i++) {
				currentMove.setToInsert(solution.getNonIncluded(0, solution.getOptimizationProblem().getNCustomers() - 1).get(i));
				for(int j = 0; j < solution.getNumberOfRoutes(); j++) {
					/* Try to insert in first position in that route */
					int indexAfter = -1; 
					currentMove.setRoute(j);
					currentMove.setAfter(indexAfter);
					evaluator.evaluate(solution, currentMove);
					if(currentMove.getDeviationObjectiveFunctionValue(0) < bestMove.getDeviationObjectiveFunctionValue(0)) {
						bestMove =  this.copyMoveRoutesSolutionInsertionAfter(currentMove);
					}
					if(solution.getLengthRoute(j) > 0) {
						/* Try to insert after k elements in that route */
						indexAfter = solution.getFirstInRoute(j); 
						
						while(solution.getSuccessor(indexAfter) != -1) {
							currentMove.setAfter(indexAfter);
							evaluator.evaluate(solution, currentMove);
							if(currentMove.getDeviationObjectiveFunctionValue(0) < bestMove.getDeviationObjectiveFunctionValue(0)) {
								bestMove =  this.copyMoveRoutesSolutionInsertionAfter(currentMove);
							}
							indexAfter = solution.getSuccessor(indexAfter);
						}
					} 
				}
			}
			applier.accept(solution, bestMove);
			for (int i = 0; i < solution.getNumberOfObjectives(); i++) {
				double newObjectiveFunctionValue = solution.getObjectiveFunctionValue(i) + bestMove.getDeviationObjectiveFunctionValue(i);
				solution.setObjectiveFunctionValue(i, newObjectiveFunctionValue);
			}
		}
		return solution; 
	}
	
	public MoveRoutesSolutionInsertionAfter copyMoveRoutesSolutionInsertionAfter(MoveRoutesSolutionInsertionAfter moveOriginal) {
		MoveRoutesSolutionInsertionAfter moveCopy =  new MoveRoutesSolutionInsertionAfter(moveOriginal.getObjectives());
		moveCopy.setAfter(moveOriginal.getAfter());
		moveCopy.setRoute(moveOriginal.getRoute());
		moveCopy.setToInsert(moveOriginal.getToInsert());
		for(int i = 0; i < moveOriginal.getObjectives(); i++) {
			moveCopy.setDeviationObjectiveFunctionValue(i, moveOriginal.getDeviationObjectiveFunctionValue(i));
		}
		
		return moveCopy; 
	}
	
	
	public RoutesSolution<Vrp> sequentialSolutionConstruct() {
		int currentRoute = 0;
		RoutesSolution<Vrp> solution =  new RoutesSolution<Vrp>(this.problem, this.problem.getNCustomers(), this.problem.getNVehicles());
		for(int i = 0; i < this.problem.getNCustomers(); i++) {
			if(currentRoute >= solution.getNumberOfRoutes()) {
				currentRoute = 0; 
			}
			if (solution.isEmpty(currentRoute)) {
				solution.addAfterDepot(i, currentRoute);
			} else {
				solution.addAfter(i, solution.getLastInRoute(currentRoute));
			}
			currentRoute++;
		}
		solution.evaluate();
		return solution;
	}
	

	@Override
	public RoutesSolution<Vrp> run() {
		this.originalSolution.evaluate();
		
		return null/*ALNS()*/;
	}
	
	@SuppressWarnings("unchecked")
	public void setOriginalSolution(RoutesSolution<Vrp> solution) {
		this.originalSolution = solution.clone();
	}
	
	public RoutesSolution<Vrp> getOriginalSolution() {
		return this.originalSolution;
	}
	
	public void setExecutionTime(double tMax) {
		this.executionTime =  tMax; 
	}

}
