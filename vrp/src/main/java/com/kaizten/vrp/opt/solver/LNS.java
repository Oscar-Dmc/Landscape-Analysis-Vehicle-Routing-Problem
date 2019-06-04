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
	private MoveGeneratorRoutesSolutionSwap<RoutesSolution<Vrp>, MoveRoutesSolutionSwap> mGSwap; 
	private MoveGeneratorRoutesSolutionMoveAfter<RoutesSolution<Vrp>, MoveRoutesSolutionMoveAfter> mGAfter;
	private MoveGeneratorRoutesSolutionMoveBefore<RoutesSolution<Vrp>, MoveRoutesSolutionMoveBefore> mGBefore;
	private MoveGeneratorRoutesSolutionRemove<RoutesSolution<Vrp>, MoveRoutesSolutionRemove> mGRemove; 
	private MoveGeneratorRoutesSolutionInsertionAfter<RoutesSolution<Vrp>, MoveRoutesSolutionInsertionAfter> mGInsertionAfter;
	private MoveGeneratorRoutesSolutionInsertionBefore<RoutesSolution<Vrp>, MoveRoutesSolutionInsertionBefore> mGInserionBefore; 
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
		
		System.out.println("Initial Solution \n" + this.originalSolution);
		
		this.manager = new MoveManagerSequential();
		this.manager.setSolution(this.originalSolution);
		this.mGSwap = new MoveGeneratorRoutesSolutionSwap<RoutesSolution<Vrp>, MoveRoutesSolutionSwap>();
		this.mGAfter = new MoveGeneratorRoutesSolutionMoveAfter<RoutesSolution<Vrp>, MoveRoutesSolutionMoveAfter>();
		this.mGBefore = new MoveGeneratorRoutesSolutionMoveBefore<RoutesSolution<Vrp>, MoveRoutesSolutionMoveBefore>();
		this.mGRemove = new  MoveGeneratorRoutesSolutionRemove<RoutesSolution<Vrp>, MoveRoutesSolutionRemove>();
		this.mGInsertionAfter = new MoveGeneratorRoutesSolutionInsertionAfter<RoutesSolution<Vrp>, MoveRoutesSolutionInsertionAfter>();
		this.mGInserionBefore = new MoveGeneratorRoutesSolutionInsertionBefore<RoutesSolution<Vrp>, MoveRoutesSolutionInsertionBefore>();
		this.manager.addMoveGenerator(this.mGSwap);
		this.manager.addMoveGenerator(this.mGAfter);
		this.manager.addMoveGenerator(this.mGBefore);
		this.manager.addMoveGenerator(this.mGRemove);
		this.manager.addMoveGenerator(this.mGInsertionAfter);
		this.manager.addMoveGenerator(this.mGInserionBefore);
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
	
	
	public RoutesSolution<Vrp> localSearch(RoutesSolution<Vrp> solution){
		RoutesSolution<Vrp> explorerSolution = solution.clone();
		for(int i = 0; i < this.neighborhoods.size(); i++) {
			this.applier.accept(explorerSolution, this.getBestMove(explorerSolution, this.neighborhoods.get(i)));
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
