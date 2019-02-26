package com.kaizten.vrp.opt.core;

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
import com.kaizten.vrp.opt.move.MoveRoutesSolutionMoveAfter;
import com.kaizten.vrp.opt.move.MoveRoutesSolutionMoveBefore;
import com.kaizten.vrp.opt.move.applier.MoveApplierRoutesSolutionMoveAfter;
import com.kaizten.vrp.opt.move.applier.MoveApplierRoutesSolutionMoveBefore;
import com.kaizten.vrp.opt.move.applier.MoveApplierRoutesSolutionSwap;
import com.kaizten.vrp.opt.move.generator.MoveGeneratorRoutesSolutionMoveAfter;
import com.kaizten.vrp.opt.move.generator.MoveGeneratorRoutesSolutionMoveBefore;
import com.kaizten.vrp.opt.move.generator.MoveGeneratorRoutesSolutionSwap;

public class Vns {

	private RoutesSolution<Vrp> originalSolution;
	private RoutesSolution<Vrp> auxSolution;
	private MoveManagerSequential<RoutesSolution<Vrp>, ?> manager; 
	private MoveGeneratorRoutesSolutionSwap<RoutesSolution<Vrp>, MoveRoutesSolutionSwap> mGSwap; 
	private MoveGeneratorRoutesSolutionMoveAfter<RoutesSolution<Vrp>, MoveRoutesSolutionMoveAfter> mGAfter;
	private MoveGeneratorRoutesSolutionMoveBefore<RoutesSolution<Vrp>, MoveRoutesSolutionMoveBefore> mGBefore;
	private Applier<RoutesSolution<Vrp>> gApplier; 
	private MoveAcceptor acceptor; 
	private MoveExplorer explorer; 
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Vns(RoutesSolution<Vrp> solution) {
		this.originalSolution =  solution;
		
		this.manager = new MoveManagerSequential();
		this.manager.setSolution(solution);
		this.mGSwap = new MoveGeneratorRoutesSolutionSwap<RoutesSolution<Vrp>, MoveRoutesSolutionSwap>();
		this.mGAfter = new MoveGeneratorRoutesSolutionMoveAfter<RoutesSolution<Vrp>, MoveRoutesSolutionMoveAfter>();
		this.mGBefore = new MoveGeneratorRoutesSolutionMoveBefore<RoutesSolution<Vrp>, MoveRoutesSolutionMoveBefore>();
		this.manager.addMoveGenerator(this.mGSwap);
		this.manager.addMoveGenerator(this.mGAfter);
		this.manager.addMoveGenerator(mGBefore);
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
	
	public RoutesSolution<Vrp> basicVns(int kMax,  int tMax) {
		double t = 0;
		while (t < tMax) {

			long tInit =  System.currentTimeMillis();
			for(int k = 0;  k < kMax; k++) {
				this.neighborhoodChange(this.firstImprovement(this.shake(k)), k);;
			}

			long tEnd =  System.currentTimeMillis();
			t  += ((tEnd - tInit) * 0.001);
		}
		return this.originalSolution;
	}
	
	
	public static void main(String[] args) {
		System.out.println("Start VNS");
		
		Vrp problem = new Vrp(100, 100, 10, 5, 4);
		RoutesSolution<Vrp> solution =   new LatencySolution(problem, 3).getSolution();
		solution.evaluate();
		System.out.println( "Initial solution: \n" + solution.toString());

		Vns vns =  new Vns(solution);
		System.out.println("Final solution: \n" + vns.basicVns(3, 600));
		
		
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
}
