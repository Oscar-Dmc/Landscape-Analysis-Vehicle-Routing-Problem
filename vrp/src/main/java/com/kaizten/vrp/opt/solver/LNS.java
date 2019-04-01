package com.kaizten.vrp.opt.solver;

import java.util.ArrayList;
import java.util.Random;


import com.kaizten.opt.move.MoveRoutesSolutionInsertionAfter;
import com.kaizten.opt.move.MoveRoutesSolutionSwap;
import com.kaizten.opt.move.acceptor.MoveAcceptor;
import com.kaizten.opt.move.acceptor.MoveAcceptorBestImprovement;
import com.kaizten.opt.move.applier.Applier;
import com.kaizten.opt.move.applier.MoveApplier;
import com.kaizten.opt.move.applier.MoveApplierRoutesSolutionInsertionAfter;
import com.kaizten.opt.move.manager.BaseMoveManager;
import com.kaizten.opt.move.manager.MoveManagerSequential;
import com.kaizten.opt.solution.RoutesSolution;
import com.kaizten.opt.solver.LocalSearch;
import com.kaizten.opt.solver.Solver;
import com.kaizten.vrp.opt.core.Vrp;
import com.kaizten.vrp.opt.evaluators.EvaluatorMoveInsertionAfter;
import com.kaizten.vrp.opt.move.MoveRoutesSolutionMoveAfter;
import com.kaizten.vrp.opt.move.MoveRoutesSolutionMoveBefore;
import com.kaizten.vrp.opt.move.applier.MoveApplierRoutesSolutionMoveAfter;
import com.kaizten.vrp.opt.move.applier.MoveApplierRoutesSolutionMoveBefore;
import com.kaizten.vrp.opt.move.applier.MoveApplierRoutesSolutionSwap;
import com.kaizten.vrp.opt.move.generator.MoveGeneratorRoutesSolutionMoveAfter;
import com.kaizten.vrp.opt.move.generator.MoveGeneratorRoutesSolutionMoveBefore;
import com.kaizten.vrp.opt.move.generator.MoveGeneratorRoutesSolutionSwap;

public class LNS implements Solver<RoutesSolution<Vrp>>{
	private RoutesSolution<Vrp> originalSolution; 
	private double executionTime; 
	private double percent; 
	
	/* LocalSearch */ 
	private BaseMoveManager<RoutesSolution<Vrp>, ?> manager; 
	private MoveGeneratorRoutesSolutionSwap<RoutesSolution<Vrp>, MoveRoutesSolutionSwap> mGSwap; 
	private MoveGeneratorRoutesSolutionMoveAfter<RoutesSolution<Vrp>, MoveRoutesSolutionMoveAfter> mGAfter;
	private MoveGeneratorRoutesSolutionMoveBefore<RoutesSolution<Vrp>, MoveRoutesSolutionMoveBefore> mGBefore;
	private Applier<RoutesSolution<Vrp>> applier; 
	private MoveAcceptor acceptor; 
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public LNS(Vrp problem) {
		SequentialBuilder builder  = new SequentialBuilder(problem);
		this.originalSolution =  builder.run();
		this.executionTime = 60;
		this.percent = 0.05; 
		
		this.manager = new MoveManagerSequential();
		this.mGSwap = new MoveGeneratorRoutesSolutionSwap<RoutesSolution<Vrp>, MoveRoutesSolutionSwap>();
		this.mGAfter = new MoveGeneratorRoutesSolutionMoveAfter<RoutesSolution<Vrp>, MoveRoutesSolutionMoveAfter>();
		this.mGBefore = new MoveGeneratorRoutesSolutionMoveBefore<RoutesSolution<Vrp>, MoveRoutesSolutionMoveBefore>();
		this.manager.addMoveGenerator(this.mGSwap);
		this.manager.addMoveGenerator(this.mGAfter);
		this.manager.addMoveGenerator(this.mGBefore);
		
		this.applier =  new Applier<RoutesSolution<Vrp>>();
		MoveApplier applierSwap =  new MoveApplierRoutesSolutionSwap();
		MoveApplier applierMoveAfter = new MoveApplierRoutesSolutionMoveAfter();
		MoveApplier applierMoveBefore = new MoveApplierRoutesSolutionMoveBefore();
		this.applier.addMoveApplier(applierSwap);
		this.applier.addMoveApplier(applierMoveAfter);
		this.applier.addMoveApplier(applierMoveBefore);
		
		this.acceptor = new MoveAcceptorBestImprovement();
	}
	
	@SuppressWarnings("unchecked")
	public RoutesSolution<Vrp> ALNS() {
		RoutesSolution<Vrp> bestSolution = this.originalSolution.clone(); 
		long time_start, time_end;
		while(this.executionTime > 0 ) {
			time_start = System.currentTimeMillis();
			RoutesSolution<Vrp> temporalSolution = this.randomDestroyer(this.originalSolution, this.percent);
			temporalSolution.evaluate();
			
			temporalSolution = localSearch(this.bestRepair(temporalSolution));
			setOriginalSolution(temporalSolution);
			if(temporalSolution.getObjectiveFunctionValue(0) < bestSolution.getObjectiveFunctionValue(0)) {
				bestSolution = temporalSolution.clone();
			}
			/*System.out.println(temporalSolution);
			System.out.println("---------------------------------------------------------------------------------------------");
			temporalSolution =  this.localSearch(temporalSolution);
			System.out.println(temporalSolution);
			System.out.println("---------------------------------------------------------------------------------------------");*/
			time_end = System.currentTimeMillis();
			this.executionTime -= (( time_end - time_start ) * 0.001);
		}
		return bestSolution;
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
	
	public RoutesSolution<Vrp> localSearch(RoutesSolution<Vrp> solution){
		LocalSearch<RoutesSolution<Vrp>> local = new LocalSearch<RoutesSolution<Vrp>>();
		local.setMoveAcceptor(this.acceptor);
		local.setApplier(this.applier);
		local.setMoveManager(this.manager);
		local.setSolution(solution);
		
		return local.run(); 
	}

	@Override
	public RoutesSolution<Vrp> run() {
		this.originalSolution.evaluate();
		
		return ALNS();
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
	
	public void setPercent(double percent) {
		this.percent = percent; 
	}

}
