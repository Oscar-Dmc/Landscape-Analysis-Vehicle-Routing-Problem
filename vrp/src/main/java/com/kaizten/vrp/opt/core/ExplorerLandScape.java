package com.kaizten.vrp.opt.core;

import com.kaizten.opt.move.MoveRoutesSolutionInsertionAfter;
import com.kaizten.opt.move.MoveRoutesSolutionRemove;
import com.kaizten.opt.move.MoveRoutesSolutionSwap;
import com.kaizten.opt.move.manager.MoveManagerSequential;
import com.kaizten.opt.move.applier.Applier;
import com.kaizten.opt.move.applier.MoveApplier;
import com.kaizten.opt.move.applier.MoveApplierRoutesSolutionInsertionAfter;
import com.kaizten.opt.move.applier.MoveApplierRoutesSolutionRemove;
import com.kaizten.vrp.opt.move.applier.MoveApplierRoutesSolutionSwap;
import com.kaizten.opt.move.generator.MoveGeneratorRoutesSolutionInsertionAfter;
import com.kaizten.vrp.opt.move.generator.MoveGeneratorRoutesSolutionRemove;
import com.kaizten.vrp.opt.move.generator.MoveGeneratorRoutesSolutionSwap;

import com.kaizten.opt.solution.RoutesSolution;
import com.kaizten.vrp.opt.db.DBControl;

public class ExplorerLandScape {

	private RoutesSolution<Vrp> solution; 
	private RoutesSolution<Vrp> auxSolution; 
	private MoveManagerSequential<RoutesSolution<Vrp>, ?> MMSequential;
	private Applier<RoutesSolution<Vrp>> GApplier; 
	private MoveGeneratorRoutesSolutionSwap<RoutesSolution<Vrp>, MoveRoutesSolutionSwap> MGSwap;
	private MoveGeneratorRoutesSolutionRemove<RoutesSolution<Vrp>, MoveRoutesSolutionRemove> MGRemove; 
	private MoveGeneratorRoutesSolutionInsertionAfter<RoutesSolution<Vrp>, MoveRoutesSolutionInsertionAfter> MGInsertionAfter;
	private DBControl db; 
	private long idCurrentSolution; 
	
	/* ToDo 

	 * */
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void init() {
		this.MMSequential = new MoveManagerSequential();
		this.GApplier =  new Applier<RoutesSolution<Vrp>>();
		this.MGSwap = new MoveGeneratorRoutesSolutionSwap<RoutesSolution<Vrp>, MoveRoutesSolutionSwap>();
		this.MGRemove =  new MoveGeneratorRoutesSolutionRemove<RoutesSolution<Vrp>, MoveRoutesSolutionRemove>();
		this.MGInsertionAfter =  new MoveGeneratorRoutesSolutionInsertionAfter<RoutesSolution<Vrp>, MoveRoutesSolutionInsertionAfter>();
		/* Appliers */ 
		MoveApplier applierSwap =  new MoveApplierRoutesSolutionSwap();
		MoveApplier applierRemove =  new MoveApplierRoutesSolutionRemove();
		MoveApplier applierInsertionAfter =  new MoveApplierRoutesSolutionInsertionAfter();
		this.GApplier.addMoveApplier(applierSwap);
		this.GApplier.addMoveApplier(applierRemove);
		this.GApplier.addMoveApplier(applierInsertionAfter);
		
		/* Database */ 
		this.db = new DBControl();
		this.db.init();
	}
	
	public void explorer (RoutesSolution<Vrp> solution, int environment,  double executionTime) {
		this.MMSequential.setSolution(solution);
		this.setInitialSolution(solution, environment);  
		
		switch(environment) {
			case 0 : 
				this.MMSequential.addMoveGenerator(this.MGSwap);
				this.MMSequential.init();
				while(executionTime > 0) {
					executionTime -= runSwap(executionTime);
				}
				break;
			case 1:
				this.MMSequential.addMoveGenerator(this.MGRemove);
				this.MMSequential.init();
				while(executionTime > 0) { 
					executionTime -= runRemove(executionTime);
				}
				break;
			case 2:
				this.MMSequential.addMoveGenerator(this.MGInsertionAfter);
				this.MMSequential.init();
				while(executionTime > 0) {					
					executionTime -= runInsertionAfter(executionTime);
				}
				break;
		}	
	}
	
	@SuppressWarnings("unchecked")
	public double runSwap(double executionTime) {
		long time_start, time_end;
		time_start = System.currentTimeMillis();
		
		this.auxSolution =  this.solution.clone();
		this.GApplier.setSolution(this.auxSolution);
		this.GApplier.setMove(this.MMSequential.next());
		
		this.db.addSolutionMoveSwap(this.GApplier.apply());
		if(executionTime > 0) {
			if(!this.MMSequential.hasNext()) {
				this.setSolution(this.db.getSolution(this.idCurrentSolution + 1));
				this.MMSequential.removeMoveGenerator(this.MGSwap);
				this.MMSequential.setSolution(this.solution);
				this.MMSequential.addMoveGenerator(this.MGSwap);
				this.MGSwap.init();
			}
		}
		
		time_end = System.currentTimeMillis();
		return(( time_end - time_start ) * 0.001);
	}
	
	@SuppressWarnings("unchecked")
	public double runRemove(double executionTime) {
		long time_start, time_end;
		time_start = System.currentTimeMillis();
		
		this.auxSolution =  this.solution.clone();
		this.GApplier.setSolution(this.auxSolution);
		this.GApplier.setMove(this.MMSequential.next());
		
		//System.out.println(this.GApplier.apply());
		this.db.addSolutionMoveRemove(this.GApplier.apply()); 
		
		if(executionTime > 0) {
			if(!this.MMSequential.hasNext()) {
				//System.out.println("ID Actual " + this.idCurrentSolution);
				this.setSolution(this.db.getSolution(this.idCurrentSolution + 1));
				this.MMSequential.removeMoveGenerator(this.MGRemove);
				this.MMSequential.setSolution(this.solution);
				this.MMSequential.addMoveGenerator(this.MGRemove);
				this.MGRemove.init();
			}
		}
		
		time_end = System.currentTimeMillis();
		return(( time_end - time_start ) * 0.001);
	}
	
	@SuppressWarnings("unchecked")
	public double runInsertionAfter(double executionTime) {
		long time_start, time_end;
		time_start = System.currentTimeMillis();
		
		this.auxSolution =  this.solution.clone();
		this.GApplier.setSolution(this.auxSolution);
		this.GApplier.setMove(this.MMSequential.next());
		
		this.db.addSolutionMoveInsertionAfter(this.GApplier.apply());
		//System.out.println(this.GApplier.apply());
		if(executionTime > 0) {
			if(!this.MMSequential.hasNext()) {
				//System.out.println("Nueva solución" );
				this.setSolution(this.db.getSolution(this.idCurrentSolution + 1));
				//System.out.println(this.solution);
				this.MMSequential.removeMoveGenerator(MGInsertionAfter);
				this.MMSequential.setSolution(this.solution);
				this.MMSequential.addMoveGenerator(MGInsertionAfter);
				this.MGInsertionAfter.init();
				}
		}
		
		time_end = System.currentTimeMillis();
		return(( time_end - time_start ) * 0.001);
	}
	
	/* Set & Get */ 
	public void setSolution(RoutesSolution<Vrp> solution) {
		this.idCurrentSolution =  db.addSolution(solution);
		this.solution =  solution;		
	}
	
	public void setInitialSolution(RoutesSolution<Vrp> solution, int environment) {
		this.idCurrentSolution =  this.db.addInitialSolution(solution, environment);
		this.solution =  solution;
	}
	
	public DBControl getDBControl() {
		return this.db;
		}
	
	/* Main */ 
	public static void main(String[] args) {
		ExplorerLandScape explorer =  new ExplorerLandScape();
		RoutesSolution<Vrp> solution = null; 
		Vrp problemVrp = null;
		explorer.init();
		int option = 2;
		/* Mejorar la entrada del problema, esto es para pruebas */ 
		switch(option) {
			case 0:
				/* Generamos un problema aleatorio y solucion aleatoria */ 
				problemVrp = new Vrp(100, 100, 15, 5, 3);
				solution =  new LatencySolution(problemVrp, 3).getSolution();
				solution.evaluate();
				
				explorer.getDBControl().addProblem(problemVrp);
				explorer.getDBControl().setOriginalProblem(problemVrp);
				break;
			case 1: 
				/* Obtenemos problema de la base de datos y generamos solucion aleatiora */ 
				/* Mejorar la recogida del id del problema */
				problemVrp =  explorer.getDBControl().getProblem(1); 
				solution =  new LatencySolution(problemVrp, 3).getSolution();
				solution.evaluate();
				explorer.getDBControl().setOriginalProblem(problemVrp);
				break; 
			case 2: 
				/* Todo obtenido de la base de datos */ 
				problemVrp = explorer.getDBControl().getProblem(1);
				explorer.getDBControl().setOriginalProblem(problemVrp);
				solution = explorer.getDBControl().getSolution(3000080);
				solution.evaluate();
				break;
		}
		
		explorer.explorer(solution, 2, (3600 * 3));
		System.out.println("Fin de ejecución");
	}
	
}
