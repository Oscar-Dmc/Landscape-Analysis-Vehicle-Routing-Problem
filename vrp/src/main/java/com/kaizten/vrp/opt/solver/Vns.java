package com.kaizten.vrp.opt.solver;

import java.util.ArrayList;
import java.util.Random;
import com.kaizten.opt.evaluator.Evaluator;
import com.kaizten.opt.move.Move;
import com.kaizten.opt.move.MoveRoutesSolutionInsertionAfter;
import com.kaizten.opt.move.MoveRoutesSolutionInsertionBefore;
import com.kaizten.opt.move.MoveRoutesSolutionMoveAfter;
import com.kaizten.opt.move.MoveRoutesSolutionMoveBefore;
import com.kaizten.opt.move.MoveRoutesSolutionSwap;
import com.kaizten.opt.move.MoveRoutesSolutionRemove;
import com.kaizten.opt.move.acceptor.MoveAcceptor;
import com.kaizten.opt.move.acceptor.MoveAcceptorBestImprovement;
import com.kaizten.opt.move.applier.Applier;
import com.kaizten.opt.move.applier.MoveApplier;
import com.kaizten.opt.move.applier.MoveApplierRoutesSolutionInsertionAfter;
import com.kaizten.opt.move.applier.MoveApplierRoutesSolutionInsertionBefore;
import com.kaizten.opt.move.applier.MoveApplierRoutesSolutionRemove;
import com.kaizten.opt.move.explorer.MoveExplorer;
import com.kaizten.opt.move.explorer.MoveExplorerBasic;
import com.kaizten.opt.move.generator.MoveGeneratorRoutesSolutionInsertionAfter;
import com.kaizten.opt.move.generator.MoveGeneratorRoutesSolutionInsertionBefore;
import com.kaizten.opt.move.generator.MoveGeneratorRoutesSolutionMoveAfter;
import com.kaizten.opt.move.generator.MoveGeneratorRoutesSolutionMoveBefore;
import com.kaizten.opt.move.generator.MoveGeneratorRoutesSolutionSwap;
import com.kaizten.opt.move.manager.MoveManagerSequential;
import com.kaizten.opt.solution.RoutesSolution;
import com.kaizten.opt.solver.Solver;
import com.kaizten.vrp.opt.core.Vrp;
import com.kaizten.vrp.opt.move.applier.MoveApplierRoutesSolutionMoveAfter;
import com.kaizten.vrp.opt.move.applier.MoveApplierRoutesSolutionMoveBefore;
import com.kaizten.vrp.opt.move.applier.MoveApplierRoutesSolutionSwap;
import com.kaizten.vrp.opt.move.generator.MoveGeneratorRoutesSolutionRemove;

public class Vns implements Solver<RoutesSolution<Vrp>>{

	private RoutesSolution<Vrp> originalSolution;
	private RoutesSolution<Vrp> auxSolution;
	private RoutesSolution<Vrp> incompleteSolution; 
	private Random randomGenerator; 
	private MoveManagerSequential<RoutesSolution<Vrp>, ?> manager; 
	private Applier<RoutesSolution<Vrp>> gApplier; 
	private MoveAcceptor acceptor;
	private MoveExplorer explorer;
	private ArrayList<Integer> neighborhoods; 
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Vns(Vrp problem, int construct) {
		if(construct == 1) {
			SequentialBuilder builder  = new SequentialBuilder(problem);
			this.originalSolution = builder.run();
		} else if(construct == 2) {
			RandomizedBuilder builder =  new RandomizedBuilder(problem); 
			this.originalSolution = builder.run();
		} else {
			RandomizedRCLBuilder builder =  new RandomizedRCLBuilder(problem); 
			this.originalSolution =  builder.run();
		}
		this.randomGenerator = new Random();
		
		System.out.println("Initial solution\n" + this.originalSolution);
		
		this.manager = new MoveManagerSequential();
		this.manager.setSolution(this.originalSolution);
		this.manager.addMoveGenerator(new MoveGeneratorRoutesSolutionSwap<RoutesSolution<Vrp>, MoveRoutesSolutionSwap>());
		this.manager.addMoveGenerator(new MoveGeneratorRoutesSolutionMoveAfter<RoutesSolution<Vrp>, MoveRoutesSolutionMoveAfter>());
		this.manager.addMoveGenerator(new MoveGeneratorRoutesSolutionMoveBefore<RoutesSolution<Vrp>, MoveRoutesSolutionMoveBefore>());
		this.manager.addMoveGenerator(new MoveGeneratorRoutesSolutionRemove<RoutesSolution<Vrp>, MoveRoutesSolutionRemove>());
		this.manager.addMoveGenerator(new MoveGeneratorRoutesSolutionInsertionAfter<RoutesSolution<Vrp>, MoveRoutesSolutionInsertionAfter>());
		this.manager.addMoveGenerator(new MoveGeneratorRoutesSolutionInsertionBefore<RoutesSolution<Vrp>, MoveRoutesSolutionInsertionBefore>());
		this.manager.init();
		
		this.gApplier =  new Applier<RoutesSolution<Vrp>>();
		MoveApplier applierSwap =  new MoveApplierRoutesSolutionSwap();
		MoveApplier applierMoveAfter = new MoveApplierRoutesSolutionMoveAfter();
		MoveApplier applierMoveBefore = new MoveApplierRoutesSolutionMoveBefore();
		MoveApplier applierRemove = new MoveApplierRoutesSolutionRemove();
		MoveApplier applierInsertionAfter = new MoveApplierRoutesSolutionInsertionAfter();
		MoveApplier applierInsertionBefore = new MoveApplierRoutesSolutionInsertionBefore();
		this.gApplier.addMoveApplier(applierSwap);
		this.gApplier.addMoveApplier(applierMoveAfter);
		this.gApplier.addMoveApplier(applierMoveBefore);
		this.gApplier.addMoveApplier(applierRemove);
		this.gApplier.addMoveApplier(applierInsertionAfter);
		this.gApplier.addMoveApplier(applierInsertionBefore);
		
		this.acceptor = new MoveAcceptorBestImprovement();
		this.explorer = new MoveExplorerBasic();
		
	}
	
	public RoutesSolution<Vrp> shake(int environment){
		this.manager.activeExclusively(this.neighborhoods.get(environment));
		
		if(this.neighborhoods.get(environment) > 3) {
			this.auxSolution = this.incompleteSolution.clone();
		} else {			
			this.auxSolution =  this.originalSolution.clone();
		}
		ArrayList<Move> availableMoves =  this.getAvailableMoves();

		int indexRandom = this.randomGenerator.nextInt(availableMoves.size());
		Move move = availableMoves.get(indexRandom);
		
		this.gApplier.setSolution(this.auxSolution);
		this.gApplier.setMove(move);
		this.gApplier.apply();
	
		return this.auxSolution; 
	}
	
	public RoutesSolution<Vrp> improvement(RoutesSolution<Vrp> solution) {
		RoutesSolution<Vrp> bestSolution = solution.clone(); 
		this.explorer.setMoveManager(this.manager);
		this.explorer.setMoveAcceptor(this.acceptor);
		this.explorer.setSolution(bestSolution);
		Move acceptedMove =  this.explorer.explore();
		if(acceptedMove != null) {
			this.gApplier.setSolution(bestSolution);
			this.gApplier.setMove(acceptedMove);
			bestSolution =  this.gApplier.apply();
		}
		return bestSolution;
	}
	

	public int neighborhoodChange(RoutesSolution<Vrp> neighborSolution, int k){
		if(neighborSolution.getNumberOfNonIncluded() == 0) {
			if (this.originalSolution.getObjectiveFunctionValue(0) > neighborSolution.getObjectiveFunctionValue(0)) {
				this.setOriginalSolution(neighborSolution);
				k = 0;
			} else {
				k++;
			}
		}
		else{
			if(this.neighborhoods.get(k) == 3) {
				this.setIncompleteSolution(neighborSolution);
				k++;
			} else if (this.originalSolution.getObjectiveFunctionValue(0) > neighborSolution.getObjectiveFunctionValue(0)) {
				this.setIncompleteSolution(neighborSolution);
			} else {
				this.manager.init();
			}
		}
		return k; 
	}
	
	public RoutesSolution<Vrp> basicVns(int kMax) {
		int k = 0;
		while (k < kMax) {
			RoutesSolution<Vrp> rShake = this.shake(k);
			RoutesSolution<Vrp> rImprovement = this.improvement(rShake);
			k = this.neighborhoodChange(rImprovement, k);
		}
	
		return this.originalSolution;
	}
	
	@Override
	public RoutesSolution<Vrp> run() {
		this.originalSolution.evaluate();
		
		return basicVns(this.neighborhoods.size());
	} 
	
	/* ------ Gets and sets ---- */ 
	public RoutesSolution<Vrp> getOriginalSolution() {
		return originalSolution;
	}
	
	public ArrayList<Move> getAvailableMoves(){
		ArrayList<Move> availableMoves =  new ArrayList<Move>();
		while(this.manager.hasNext()) {
			Move move =  this.manager.next();
			if(move.getDeviationObjectiveFunctionValue(0) != -Evaluator.OBJECTIVE_INFEASIBLE) {
				availableMoves.add(move);
			}
		}
		this.manager.init();
		return availableMoves; 
	}

	public void setOriginalSolution(RoutesSolution<Vrp> solution) {
		this.originalSolution = solution.clone();
		this.manager.setSolution(this.originalSolution);
		this.manager.init();
	}
	
	public void setIncompleteSolution(RoutesSolution<Vrp> solution) {
		this.incompleteSolution = solution.clone();
		this.manager.setSolution(this.incompleteSolution);
		this.manager.init();
	}
	
	public void setNeighborhood(ArrayList<Integer> neighborhoods) {
		this.neighborhoods = neighborhoods; 
	}
	
}
