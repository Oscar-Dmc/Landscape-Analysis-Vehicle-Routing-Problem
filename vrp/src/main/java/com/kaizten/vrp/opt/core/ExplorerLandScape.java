package com.kaizten.vrp.opt.core;

import java.util.ArrayList;

import com.kaizten.opt.move.MoveRoutesSolutionInsertionAfter;
import com.kaizten.opt.move.MoveRoutesSolutionInsertionBefore;
import com.kaizten.opt.move.MoveRoutesSolutionMoveAfter;
import com.kaizten.opt.move.MoveRoutesSolutionMoveBefore;
import com.kaizten.opt.move.MoveRoutesSolutionRemove;
import com.kaizten.opt.move.MoveRoutesSolutionSwap;
import com.kaizten.opt.move.applier.MoveApplier;
import com.kaizten.opt.move.applier.MoveApplierRoutesSolutionInsertionAfter;
import com.kaizten.opt.move.applier.MoveApplierRoutesSolutionInsertionBefore;
import com.kaizten.opt.move.applier.MoveApplierRoutesSolutionRemove;
import com.kaizten.opt.move.applier.manager.MoveApplierManager;
import com.kaizten.vrp.opt.move.applier.MoveApplierRoutesSolutionMoveAfter;
import com.kaizten.vrp.opt.move.applier.MoveApplierRoutesSolutionMoveBefore;
import com.kaizten.vrp.opt.move.applier.MoveApplierRoutesSolutionSwap;
import com.kaizten.opt.move.generator.MoveGeneratorRoutesSolutionInsertionAfter;
import com.kaizten.opt.move.generator.MoveGeneratorRoutesSolutionInsertionBefore;
import com.kaizten.opt.move.generator.MoveGeneratorRoutesSolutionMoveAfter;
import com.kaizten.opt.move.generator.MoveGeneratorRoutesSolutionMoveBefore;
import com.kaizten.opt.move.generator.MoveGeneratorRoutesSolutionSwap;
import com.kaizten.opt.move.generator.manager.SequentialMoveGeneratorManager;
import com.kaizten.vrp.opt.move.generator.MoveGeneratorRoutesSolutionRemove;
import com.kaizten.opt.solution.RoutesSolution;
import com.kaizten.vrp.opt.db.DBControl;

public class ExplorerLandScape {
	private final String graphs[] = {"swapGraph", "moveAfterGraph", "moveBeforeGraph", "removeGraph", "insertionAfterGraph", "insertionBeforeGraph"}; 
	private RoutesSolution<Vrp> solution; 
	private RoutesSolution<Vrp> auxSolution; 
	private SequentialMoveGeneratorManager<RoutesSolution<Vrp>, ?> MMSequential;
	private MoveApplierManager<RoutesSolution<Vrp>> GApplier; 
	private DBControl db; 
	private String dbName; 
	private int environment; 
	private ArrayList<Long> nextSolutions;
	private ArrayList<Long> exploredSolutions; 
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void init() {
		this.nextSolutions = new ArrayList<Long>();
		this.exploredSolutions = new ArrayList<Long>();
		this.MMSequential = new SequentialMoveGeneratorManager();
		this.GApplier =  new MoveApplierManager<RoutesSolution<Vrp>>();
		
		this.MMSequential.addMoveGenerator(new MoveGeneratorRoutesSolutionSwap<RoutesSolution<Vrp>, MoveRoutesSolutionSwap>());
		this.MMSequential.addMoveGenerator(new MoveGeneratorRoutesSolutionMoveAfter<RoutesSolution<Vrp>, MoveRoutesSolutionMoveAfter>());
		this.MMSequential.addMoveGenerator(new MoveGeneratorRoutesSolutionMoveBefore<RoutesSolution<Vrp>, MoveRoutesSolutionMoveBefore>());
		this.MMSequential.addMoveGenerator(new MoveGeneratorRoutesSolutionRemove<RoutesSolution<Vrp>, MoveRoutesSolutionRemove>());
		this.MMSequential.addMoveGenerator(new MoveGeneratorRoutesSolutionInsertionAfter<RoutesSolution<Vrp>, MoveRoutesSolutionInsertionAfter>());
		this.MMSequential.addMoveGenerator(new MoveGeneratorRoutesSolutionInsertionBefore<RoutesSolution<Vrp>, MoveRoutesSolutionInsertionBefore>());
		
		/* Appliers */ 
		MoveApplier applierSwap =  new MoveApplierRoutesSolutionSwap();
		MoveApplier applierMoveAfter = new MoveApplierRoutesSolutionMoveAfter();
		MoveApplier applierMoveBefore = new MoveApplierRoutesSolutionMoveBefore();
		MoveApplier applierRemove =  new MoveApplierRoutesSolutionRemove();
		MoveApplier applierInsertionAfter =  new MoveApplierRoutesSolutionInsertionAfter();
		MoveApplier applierInsertionBefore =  new MoveApplierRoutesSolutionInsertionBefore();
		this.GApplier.addMoveApplier(applierSwap);
		this.GApplier.addMoveApplier(applierMoveAfter);
		this.GApplier.addMoveApplier(applierMoveBefore);
		this.GApplier.addMoveApplier(applierRemove);
		this.GApplier.addMoveApplier(applierInsertionAfter);
		this.GApplier.addMoveApplier(applierInsertionBefore);
		
		/* Database */ 
		this.db = new DBControl();
		this.db.setDBName(this.dbName);
		this.db.init();
	}
	
	public void explorer (RoutesSolution<Vrp> solution, int environment,  double executionTime) {
		this.MMSequential.setSolution(solution);
		this.environment = environment - 1; 
		this.setInitialSolution(solution, this.environment);  
		
		this.MMSequential.activeExclusively(this.environment);
		this.MMSequential.init();
		while(executionTime > 0) {
			executionTime -= this.run(executionTime, graphs[this.environment]);
		}
		
	}
	
	public double run(double executionTime, String graph) {
		long time_start_function, time_end_function;
		time_start_function = System.currentTimeMillis();
		if(this.MMSequential.hasNext()) {
			
			this.auxSolution = this.solution.clone();
			this.GApplier.setSolution(this.auxSolution);
			this.GApplier.setMove(this.MMSequential.next());
			
			Long idLastSolution = this.db.addSolutionGraph(this.GApplier.apply(), graph);
			this.addNextSolution(idLastSolution);
			if(executionTime > 0) {
				long time_start, time_end;
				while(!this.MMSequential.hasNext() && executionTime > 0) {
					long id =  this.nextSolutionToExplore();
					
					time_start = System.currentTimeMillis();
					if(id != -1) {
						this.setSolution(this.db.getSolution(id));
						this.db.setIdOriginalSolution(id);
						this.MMSequential.setSolution(this.solution);
						this.MMSequential.init();
					} else {
						return executionTime;
					}
					time_end = System.currentTimeMillis();
					executionTime -= (( time_end - time_start ) * 0.001);
				}
			}
		}
		
		time_end_function = System.currentTimeMillis();
		return(( time_end_function - time_start_function ) * 0.001);
	}
	
	public void addNextSolution(long id) {
		if(this.exploredSolutions.isEmpty()) {
			this.nextSolutions.add(id);
		} else {
			if(!this.exploredSolutions.contains(id)) {
				this.nextSolutions.add(id);
			}
		}
	}
	
	public long nextSolutionToExplore() {
		long idSolution =  this.nextSolutions.get(0);
		this.exploredSolutions.add(idSolution);
		this.nextSolutions.remove(0);
		return idSolution; 
	}
	
	/* Set & Get */ 
	public void setSolution(RoutesSolution<Vrp> solution) {
		this.solution =  solution;		
	}
	
	public void setInitialSolution(RoutesSolution<Vrp> solution, int environment) {
		this.db.addInitialSolution(solution, environment);
		this.solution =  solution;
	}
	
	
	public DBControl getDBControl() {
		return this.db;
	}
	
	public void setDBName(String name) {
		this.dbName = name; 
	}
		
}
