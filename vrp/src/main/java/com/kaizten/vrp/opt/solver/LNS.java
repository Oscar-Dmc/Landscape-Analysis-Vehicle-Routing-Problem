package com.kaizten.vrp.opt.solver;

import java.util.ArrayList;
import java.util.Random;
import com.kaizten.opt.evaluator.Evaluator;
import com.kaizten.opt.move.Move;
import com.kaizten.opt.move.MoveRoutesSolutionInsertionAfter;
import com.kaizten.opt.move.MoveRoutesSolutionInsertionBefore;
import com.kaizten.opt.move.MoveRoutesSolutionMoveAfter;
import com.kaizten.opt.move.MoveRoutesSolutionMoveBefore;
import com.kaizten.opt.move.MoveRoutesSolutionRemove;
import com.kaizten.opt.move.MoveRoutesSolutionSwap;
import com.kaizten.opt.move.applier.Applier;
import com.kaizten.opt.move.applier.MoveApplier;
import com.kaizten.opt.move.applier.MoveApplierRoutesSolutionInsertionAfter;
import com.kaizten.opt.move.applier.MoveApplierRoutesSolutionInsertionBefore;
import com.kaizten.opt.move.applier.MoveApplierRoutesSolutionRemove;
import com.kaizten.opt.move.generator.MoveGeneratorRoutesSolutionInsertionAfter;
import com.kaizten.opt.move.generator.MoveGeneratorRoutesSolutionInsertionBefore;
import com.kaizten.opt.move.generator.MoveGeneratorRoutesSolutionMoveAfter;
import com.kaizten.opt.move.generator.MoveGeneratorRoutesSolutionMoveBefore;
import com.kaizten.opt.move.generator.MoveGeneratorRoutesSolutionSwap;
import com.kaizten.opt.move.manager.BaseMoveManager;
import com.kaizten.opt.move.manager.MoveManagerSequential;
import com.kaizten.opt.solution.RoutesSolution;
import com.kaizten.opt.solver.Solver;
import com.kaizten.vrp.opt.core.Vrp;
import com.kaizten.vrp.opt.move.applier.MoveApplierRoutesSolutionMoveAfter;
import com.kaizten.vrp.opt.move.applier.MoveApplierRoutesSolutionMoveBefore;
import com.kaizten.vrp.opt.move.applier.MoveApplierRoutesSolutionSwap;
import com.kaizten.vrp.opt.move.generator.MoveGeneratorRoutesSolutionRemove;

public class LNS implements Solver<RoutesSolution<Vrp>>{
	private RoutesSolution<Vrp> originalSolution; 
	private int itMax; 
	private double percent; 
	private ArrayList<Integer> neighborhoods; 
	
	/* LocalSearch */ 
	private BaseMoveManager<RoutesSolution<Vrp>, ?> manager; 
	private Applier<RoutesSolution<Vrp>> applier; 
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public LNS(Vrp problem, int construct) {
		if(construct == 1) {
			SequentialBuilder builder = new SequentialBuilder(problem);
			this.originalSolution = builder.run();
		} else if (construct == 2) {
			RandomizedBuilder builder = new RandomizedBuilder(problem);
			this.originalSolution = builder.run();
		} else {
			RandomizedRCLBuilder builder = new RandomizedRCLBuilder(problem);
			this.originalSolution = builder.run();
		}
		
		//System.out.println("Initial Solution \n" + this.originalSolution);
		
		this.manager = new MoveManagerSequential();
		this.manager.setSolution(this.originalSolution);
		this.manager.addMoveGenerator(new MoveGeneratorRoutesSolutionSwap<RoutesSolution<Vrp>, MoveRoutesSolutionSwap>());
		this.manager.addMoveGenerator(new MoveGeneratorRoutesSolutionMoveAfter<RoutesSolution<Vrp>, MoveRoutesSolutionMoveAfter>());
		this.manager.addMoveGenerator(new MoveGeneratorRoutesSolutionMoveBefore<RoutesSolution<Vrp>, MoveRoutesSolutionMoveBefore>());
		this.manager.addMoveGenerator(new MoveGeneratorRoutesSolutionRemove<RoutesSolution<Vrp>, MoveRoutesSolutionRemove>());
		this.manager.addMoveGenerator(new MoveGeneratorRoutesSolutionInsertionAfter<RoutesSolution<Vrp>, MoveRoutesSolutionInsertionAfter>());
		this.manager.addMoveGenerator(new MoveGeneratorRoutesSolutionInsertionBefore<RoutesSolution<Vrp>, MoveRoutesSolutionInsertionBefore>());
		this.manager.init();
		
		this.applier =  new Applier<RoutesSolution<Vrp>>();
		MoveApplier applierSwap =  new MoveApplierRoutesSolutionSwap();
		MoveApplier applierMoveAfter = new MoveApplierRoutesSolutionMoveAfter();
		MoveApplier applierMoveBefore = new MoveApplierRoutesSolutionMoveBefore();
		MoveApplier applierRemove = new MoveApplierRoutesSolutionRemove();
		MoveApplier applierInsertionAfter = new MoveApplierRoutesSolutionInsertionAfter();
		MoveApplier applierInsertionBefore = new MoveApplierRoutesSolutionInsertionBefore();
		this.applier.addMoveApplier(applierSwap);
		this.applier.addMoveApplier(applierMoveAfter);
		this.applier.addMoveApplier(applierMoveBefore);
		this.applier.addMoveApplier(applierRemove);
		this.applier.addMoveApplier(applierInsertionAfter);
		this.applier.addMoveApplier(applierInsertionBefore);
		
	}
	
	public RoutesSolution<Vrp> ALNS() {
		RoutesSolution<Vrp> bestSolution = this.originalSolution.clone(); 
		
		while(this.itMax > 0 ) {
			RoutesSolution<Vrp> temporalSolution = this.randomDestroyer(this.originalSolution, this.percent);
			temporalSolution.evaluate();
			
			temporalSolution = localSearch(this.bestRepair(temporalSolution));
			this.setOriginalSolution(temporalSolution);
			
			if(bestSolution.getObjectiveFunctionValue(0) > temporalSolution.getObjectiveFunctionValue(0)) {
				bestSolution = temporalSolution.clone();
			}
			
			this.itMax--;
		}
		return bestSolution;
	} 
	
	public RoutesSolution<Vrp> randomDestroyer(RoutesSolution<Vrp> solution, double percent ) {
		RoutesSolution<Vrp> partialSolution = solution.clone();
		int nCustomersToRemove = (int) (partialSolution.getOptimizationProblem().getNCustomers() * this.percent); 
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
		while(!solution.isFull()) {		
			this.applier.accept(solution, this.getBestMove(solution, 4));
		}
		return solution; 
	}
	
	
	@SuppressWarnings("unchecked")
	public RoutesSolution<Vrp> localSearch(RoutesSolution<Vrp> solution){
		RoutesSolution<Vrp> explorerSolution = solution.clone();
		ArrayList<Integer> currentNeighborhoods = (ArrayList<Integer>) this.neighborhoods.clone();
		
		if(currentNeighborhoods.contains(3)) {
			RoutesSolution<Vrp> incompleteSolution = solution.clone();
			int indexRemove = currentNeighborhoods.indexOf(3);
			this.applier.accept(incompleteSolution, this.getBestMove(incompleteSolution, currentNeighborhoods.get(indexRemove)));
			currentNeighborhoods.remove(indexRemove);
			Move insertionAfter = new MoveRoutesSolutionInsertionAfter(1);
			Move insertionBefore = new MoveRoutesSolutionInsertionBefore(1);
			insertionAfter.setDeviationObjectiveFunctionValue(0, -Evaluator.OBJECTIVE_INFEASIBLE);
			insertionBefore.setDeviationObjectiveFunctionValue(0, -Evaluator.OBJECTIVE_INFEASIBLE);
			if(currentNeighborhoods.contains(4)) {
				int indexAfter = currentNeighborhoods.indexOf(4);
				insertionAfter = this.getBestMove(incompleteSolution, currentNeighborhoods.get(indexAfter));
				currentNeighborhoods.remove(indexAfter);
			} 
			if (currentNeighborhoods.contains(5)) {
				int indexBefore = currentNeighborhoods.indexOf(5);
				insertionBefore = this.getBestMove(incompleteSolution, currentNeighborhoods.get(indexBefore));
				currentNeighborhoods.remove(indexBefore);
			}					
			
			if(insertionAfter.getDeviationObjectiveFunctionValue(0) < insertionBefore.getDeviationObjectiveFunctionValue(0)) {
				this.applier.accept(incompleteSolution, insertionAfter);
			} else {
				this.applier.accept(incompleteSolution, insertionBefore);
			} 
		}
		if(currentNeighborhoods.size() > 0) {
			Move bestMove = this.getBestMove(explorerSolution, currentNeighborhoods.get(0)); 
			for(int i = 1; i < currentNeighborhoods.size(); i++) {
				Move localBestMove = this.getBestMove(explorerSolution, currentNeighborhoods.get(i));
				if(bestMove.getDeviationObjectiveFunctionValue(0) > localBestMove.getDeviationObjectiveFunctionValue(0)) {
					bestMove =  localBestMove; 
				}
			}
			this.applier.accept(explorerSolution, bestMove);
		}
		return explorerSolution; 
	}
	
	public Move getBestMove(RoutesSolution<Vrp> solution, int neighborhood) {
		this.manager.activeExclusively(neighborhood);
		this.manager.setSolution(solution);
		this.manager.init();
		
		ArrayList<Move> availableMoves =  new ArrayList<Move>();
		while(this.manager.hasNext()) {
			Move move = this.manager.next(); 
			if(move.getDeviationObjectiveFunctionValue(0) != -Evaluator.OBJECTIVE_INFEASIBLE) {
				availableMoves.add(move);
			}
		}
		
		int indexBestMove = 0;
		for(int i = 1; i < availableMoves.size(); i++) {
			if(availableMoves.get(indexBestMove).getDeviationObjectiveFunctionValue(0) > availableMoves.get(i).getDeviationObjectiveFunctionValue(0)) {
				indexBestMove = i;
			}
		}
		
		return availableMoves.get(indexBestMove);
	}

	@Override
	public RoutesSolution<Vrp> run() {
		this.originalSolution.evaluate();
		
		return ALNS();
	}
	
	public void setOriginalSolution(RoutesSolution<Vrp> solution) {
		this.originalSolution = solution.clone();
	}
	
	public RoutesSolution<Vrp> getOriginalSolution() {
		return this.originalSolution;
	}
	
	public void setIterationsMax(int itMax) {
		this.itMax = itMax; 
	}
	
	public void setPercent(double percent) {
		this.percent = percent; 
	}
	
	public void setNeighborhood(ArrayList<Integer> neighborhoods) {
		this.neighborhoods = neighborhoods; 
	}

}
