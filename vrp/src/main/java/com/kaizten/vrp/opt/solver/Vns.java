package com.kaizten.vrp.opt.solver;

import com.kaizten.opt.move.Move;
import com.kaizten.opt.move.MoveRoutesSolutionSwap;
import com.kaizten.opt.move.acceptor.MoveAcceptor;
import com.kaizten.opt.move.acceptor.MoveAcceptorFirstImprovement;
import com.kaizten.opt.move.applier.Applier;
import com.kaizten.opt.move.applier.MoveApplier;
import com.kaizten.opt.move.explorer.MoveExplorer;
import com.kaizten.opt.move.explorer.MoveExplorerBasic;
import com.kaizten.opt.move.manager.MoveManagerSequential;
import com.kaizten.opt.solution.RoutesSolution;
import com.kaizten.opt.solver.Solver;
import com.kaizten.vrp.opt.core.Vrp;
import com.kaizten.vrp.opt.move.MoveRoutesSolutionMoveAfter;
import com.kaizten.vrp.opt.move.MoveRoutesSolutionMoveBefore;
import com.kaizten.vrp.opt.move.applier.MoveApplierRoutesSolutionMoveAfter;
import com.kaizten.vrp.opt.move.applier.MoveApplierRoutesSolutionMoveBefore;
import com.kaizten.vrp.opt.move.applier.MoveApplierRoutesSolutionSwap;
import com.kaizten.vrp.opt.move.generator.MoveGeneratorRoutesSolutionMoveAfter;
import com.kaizten.vrp.opt.move.generator.MoveGeneratorRoutesSolutionMoveBefore;
import com.kaizten.vrp.opt.move.generator.MoveGeneratorRoutesSolutionSwap;

public class Vns implements Solver<RoutesSolution<Vrp>>{

	private RoutesSolution<Vrp> originalSolution;
	private RoutesSolution<Vrp> auxSolution;
	private MoveManagerSequential<RoutesSolution<Vrp>, ?> manager; 
	private MoveGeneratorRoutesSolutionSwap<RoutesSolution<Vrp>, MoveRoutesSolutionSwap> mGSwap; 
	private MoveGeneratorRoutesSolutionMoveAfter<RoutesSolution<Vrp>, MoveRoutesSolutionMoveAfter> mGAfter;
	private MoveGeneratorRoutesSolutionMoveBefore<RoutesSolution<Vrp>, MoveRoutesSolutionMoveBefore> mGBefore;
	private Applier<RoutesSolution<Vrp>> gApplier; 
	private MoveAcceptor acceptor; 
	private MoveExplorer explorer; 
	private double executionTime; 
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Vns(Vrp problem) {
		SequentialBuilder builder  = new SequentialBuilder(problem);
		this.originalSolution =  builder.run();
		
		this.manager = new MoveManagerSequential();
		this.manager.setSolution(this.originalSolution);
		this.mGSwap = new MoveGeneratorRoutesSolutionSwap<RoutesSolution<Vrp>, MoveRoutesSolutionSwap>();
		this.mGAfter = new MoveGeneratorRoutesSolutionMoveAfter<RoutesSolution<Vrp>, MoveRoutesSolutionMoveAfter>();
		this.mGBefore = new MoveGeneratorRoutesSolutionMoveBefore<RoutesSolution<Vrp>, MoveRoutesSolutionMoveBefore>();
		this.manager.addMoveGenerator(this.mGSwap);
		this.manager.addMoveGenerator(this.mGAfter);
		this.manager.addMoveGenerator(this.mGBefore);
		this.manager.init();
		
		this.gApplier =  new Applier<RoutesSolution<Vrp>>();
		MoveApplier applierSwap =  new MoveApplierRoutesSolutionSwap();
		MoveApplier applierMoveAfter = new MoveApplierRoutesSolutionMoveAfter();
		MoveApplier applierMoveBefore = new MoveApplierRoutesSolutionMoveBefore();
		this.gApplier.addMoveApplier(applierSwap);
		this.gApplier.addMoveApplier(applierMoveAfter);
		this.gApplier.addMoveApplier(applierMoveBefore);
		
		this.acceptor = new MoveAcceptorFirstImprovement();
		this.explorer = new MoveExplorerBasic();
		
		this.executionTime = 60;
	
	}
	
	@SuppressWarnings("unchecked")
	public RoutesSolution<Vrp> shake(int environment){
		this.manager.activeExclusively(environment);
		this.auxSolution =  this.originalSolution.clone();
		this.gApplier.setSolution(this.auxSolution);
		this.gApplier.setMove(this.manager.next());
		return this.gApplier.apply(); 
	}
	
	public RoutesSolution<Vrp> firstImprovement(RoutesSolution<Vrp> solution) {
		RoutesSolution<Vrp> bestSolution = solution; 
		this.explorer.setMoveManager(this.manager);
		this.explorer.setMoveAcceptor(this.acceptor);
		boolean improvement = true; 
		while(improvement) {
			improvement = false;
			this.explorer.setSolution(solution);
			Move acceptedMove = this.explorer.explore();
			if(acceptedMove != null) {
				this.gApplier.setSolution(solution);
				this.gApplier.setMove(acceptedMove);
				bestSolution =  this.gApplier.apply();
				improvement =  true; 
				this.acceptor.init();
			}
		}
		return bestSolution;
	}
	

	public void neighborhoodChange(RoutesSolution<Vrp> neighborSolution, int k){
		if (this.originalSolution.getObjectiveFunctionValue(0) >= neighborSolution.getObjectiveFunctionValue(0)) {	
			this.setOriginalSolution(neighborSolution);
			k = 0;
		}
		else {
			k++;
		}
	}
	
	public RoutesSolution<Vrp> basicVns(int kMax) {
		while (this.executionTime > 0) {

			long tInit =  System.currentTimeMillis();
			for(int k = 0;  k < kMax; k++) {
				this.neighborhoodChange(this.firstImprovement(this.shake(k)), k);;
			}

			long tEnd =  System.currentTimeMillis();
			this.executionTime  -= ((tEnd - tInit) * 0.001);
		}
		return this.originalSolution;
	}
	
	@Override
	public RoutesSolution<Vrp> run() {
		this.originalSolution.evaluate();
		
		return basicVns(this.manager.getNumberOfMoveGenerators());
	} 
	
	/* ------ Gets and sets ---- */ 
	public RoutesSolution<Vrp> getOriginalSolution() {
		return originalSolution;
	}

	public void setOriginalSolution(RoutesSolution<Vrp> solution) {
		this.originalSolution = solution;
		this.manager.removeMoveGenerator(this.mGSwap);
		this.manager.removeMoveGenerator(this.mGAfter);
		this.manager.removeMoveGenerator(this.mGBefore);
		this.manager.setSolution(this.originalSolution);
		this.manager.addMoveGenerator(this.mGSwap);
		this.manager.addMoveGenerator(this.mGAfter);
		this.manager.addMoveGenerator(this.mGBefore);
		this.mGSwap.init();
		this.mGAfter.init();
		this.mGBefore.init();
	}
	
	public void setExecutionTime(int tMax) {
		this.executionTime =  tMax; 
	}

	
}
