package com.kaizten.vrp.opt.core;

import com.kaizten.opt.move.MoveRoutesSolutionSwap;
import com.kaizten.opt.move.applier.Applier;
import com.kaizten.opt.move.applier.MoveApplier;
import com.kaizten.opt.move.manager.MoveManagerSequential;
import com.kaizten.opt.solution.RoutesSolution;
import com.kaizten.vrp.opt.db.DBControl;
import com.kaizten.vrp.opt.move.applier.MoveApplierRoutesSolutionSwap;
import com.kaizten.vrp.opt.move.generator.MoveGeneratorRoutesSolutionSwap;

public class ExplorerLandScape {

	private RoutesSolution<Vrp> solution; 
	private RoutesSolution<Vrp> auxSolution; 
	private MoveManagerSequential<RoutesSolution<Vrp>, ?> MMSequential;
	private Applier<RoutesSolution<Vrp>> GApplier; 
	private MoveGeneratorRoutesSolutionSwap<RoutesSolution<Vrp>, MoveRoutesSolutionSwap> MGSwap;
	private DBControl db; 
	private long idCurrentSolution; 
	
	/* ToDo 
	 * 1- Donde almacenamos la solucion inicial
	 * 2- Si no almacena nada imprimir por pantalla un warning.
	 * 3- Creamos el main aqui para controlar la ejecución del problema. 
	 * */
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void init() {
		this.MMSequential = new MoveManagerSequential();
		this.GApplier =  new Applier<RoutesSolution<Vrp>>();
		this.MGSwap = new MoveGeneratorRoutesSolutionSwap<RoutesSolution<Vrp>, MoveRoutesSolutionSwap>();
		/* Appliers */ 
		MoveApplier applierSwap =  new MoveApplierRoutesSolutionSwap();
		this.GApplier.addMoveApplier(applierSwap);
		
		/* Database */ 
		this.db = new DBControl();
		this.db.init();
		//this.db.addProblem(this.problem);
	}
	
	public void explorer (RoutesSolution<Vrp> solution, int environment,  double executionTime) {
		this.MMSequential.setSolution(solution);
		this.setInitialSolution(solution);
		
		//MoveGeneratorRoutesSolutionSwap<RoutesSolution<Vrp>, MoveRoutesSolutionSwap> MGSwap =  new MoveGeneratorRoutesSolutionSwap<RoutesSolution<Vrp>, MoveRoutesSolutionSwap>();
		
		switch(environment) {
			case 0 : 
				this.MMSequential.addMoveGenerator(this.MGSwap);
				this.MMSequential.init();
				while(executionTime > 0) {
					executionTime -= runSwap(executionTime);
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
		
		this.db.addSolutionMoveSwap(GApplier.apply());
		if(executionTime > 0) {
			if(!this.MMSequential.hasNext()) {
				this.setSolution(db.getSolution(idCurrentSolution + 1));
				this.MMSequential.removeMoveGenerator(this.MGSwap);
				this.MMSequential.setSolution(this.solution);
				this.MMSequential.addMoveGenerator(this.MGSwap);
				this.MGSwap.init();
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
	
	public void setInitialSolution(RoutesSolution<Vrp> solution) {
		this.idCurrentSolution =  this.db.addInitialSolution(solution);
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
				explorer.setInitialSolution(solution);
				break; 
			case 2: 
				/* Todo obtenido de la base de datos */ 
				problemVrp = explorer.getDBControl().getProblem(1);
				explorer.getDBControl().setOriginalProblem(problemVrp);
				solution = explorer.getDBControl().getSolution(1);
				solution.evaluate();
				explorer.setInitialSolution(solution);
				break;
		}
		
		explorer.explorer(solution, 0, (3600 * 3));
		System.out.println("Fin de ejecución");
	}
	
}
