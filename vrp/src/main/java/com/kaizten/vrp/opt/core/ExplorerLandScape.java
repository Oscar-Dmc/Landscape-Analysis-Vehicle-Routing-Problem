package com.kaizten.vrp.opt.core;

import java.util.ArrayList;

import org.bson.Document;
import com.kaizten.opt.move.MoveRoutesSolutionInsertionAfter;
import com.kaizten.opt.move.MoveRoutesSolutionInsertionBefore;
import com.kaizten.opt.move.MoveRoutesSolutionRemove;
import com.kaizten.opt.move.MoveRoutesSolutionSwap;
import com.kaizten.opt.move.manager.MoveManagerSequential;
import com.kaizten.opt.move.applier.Applier;
import com.kaizten.opt.move.applier.MoveApplier;
import com.kaizten.opt.move.applier.MoveApplierRoutesSolutionInsertionAfter;
import com.kaizten.opt.move.applier.MoveApplierRoutesSolutionRemove;
import com.kaizten.vrp.opt.move.MoveRoutesSolutionMoveAfter;
import com.kaizten.vrp.opt.move.applier.MoveApplierRoutesSolutionInsertionBefore;
import com.kaizten.vrp.opt.move.applier.MoveApplierRoutesSolutionMoveAfter;
import com.kaizten.vrp.opt.move.applier.MoveApplierRoutesSolutionSwap;
import com.kaizten.opt.move.generator.MoveGeneratorRoutesSolutionInsertionAfter;
import com.kaizten.vrp.opt.move.generator.MoveGeneratorRoutesSolutionInsertionBefore;
import com.kaizten.vrp.opt.move.generator.MoveGeneratorRoutesSolutionMoveAfter;
import com.kaizten.vrp.opt.move.generator.MoveGeneratorRoutesSolutionRemove;
import com.kaizten.vrp.opt.move.generator.MoveGeneratorRoutesSolutionSwap;
import com.mongodb.client.FindIterable;
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
	private MoveGeneratorRoutesSolutionInsertionBefore<RoutesSolution<Vrp>, MoveRoutesSolutionInsertionBefore> MGInsertionBefore; 
	private MoveGeneratorRoutesSolutionMoveAfter<RoutesSolution<Vrp>, MoveRoutesSolutionMoveAfter> MGMoveAfter; 
	private DBControl db; 
	private long idCurrentSolution;
	private int environment; 
	private ArrayList<Long> nextSolutions; 
	
	/* ToDo 

	 * */
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void init() {
		this.nextSolutions = new ArrayList<Long>();
		this.MMSequential = new MoveManagerSequential();
		this.GApplier =  new Applier<RoutesSolution<Vrp>>();
		this.MGSwap = new MoveGeneratorRoutesSolutionSwap<RoutesSolution<Vrp>, MoveRoutesSolutionSwap>();
		this.MGRemove =  new MoveGeneratorRoutesSolutionRemove<RoutesSolution<Vrp>, MoveRoutesSolutionRemove>();
		this.MGInsertionAfter =  new MoveGeneratorRoutesSolutionInsertionAfter<RoutesSolution<Vrp>, MoveRoutesSolutionInsertionAfter>();
		this.MGInsertionBefore =  new MoveGeneratorRoutesSolutionInsertionBefore<RoutesSolution<Vrp>, MoveRoutesSolutionInsertionBefore>();
		this.MGMoveAfter = new MoveGeneratorRoutesSolutionMoveAfter<RoutesSolution<Vrp>, MoveRoutesSolutionMoveAfter>();
		
		/* Appliers */ 
		MoveApplier applierSwap =  new MoveApplierRoutesSolutionSwap();
		MoveApplier applierRemove =  new MoveApplierRoutesSolutionRemove();
		MoveApplier applierInsertionAfter =  new MoveApplierRoutesSolutionInsertionAfter();
		MoveApplier applierInsertionBefore =  new MoveApplierRoutesSolutionInsertionBefore();
		MoveApplier applierMoveAfter = new MoveApplierRoutesSolutionMoveAfter();
		this.GApplier.addMoveApplier(applierSwap);
		this.GApplier.addMoveApplier(applierRemove);
		this.GApplier.addMoveApplier(applierInsertionAfter);
		this.GApplier.addMoveApplier(applierInsertionBefore);
		this.GApplier.addMoveApplier(applierMoveAfter);
		
		/* Database */ 
		this.db = new DBControl();
		this.db.init();
	}
	
	public void explorer (RoutesSolution<Vrp> solution, int environment,  double executionTime) {
		this.MMSequential.setSolution(solution);
		this.environment = environment; 
		this.setInitialSolution(solution, this.environment);  
		
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
			case 3:
				this.MMSequential.addMoveGenerator(this.MGInsertionBefore);
				this.MMSequential.init();
				while(executionTime > 0) {
					executionTime -= runInsertionBefore(executionTime);
				}
				break;
			case 4:
				this.MMSequential.addMoveGenerator(this.MGMoveAfter);
				this.MMSequential.init();
				while(executionTime > 0) {
					executionTime -= runMoveAfter(executionTime);
				}
				break;
		}	
	}
	
	public double runSwap(double executionTime) {
		long time_start_function, time_end_function;
		time_start_function = System.currentTimeMillis();
		if(this.MMSequential.hasNext()) {
			this.reset();
			
			this.db.addSolutionMoveSwap(this.GApplier.apply());
			if(executionTime > 0) {
				long time_start, time_end;
				while(!this.MMSequential.hasNext() && executionTime > 0) {
					long id =  nextSolutionToExplore("swapGraph");
					
					time_start = System.currentTimeMillis();
					if(id != -1) {
						this.setSolution(this.db.getSolution(id));
						this.db.setIdOriginalSolution(id);
						this.MMSequential.removeMoveGenerator(this.MGSwap);
						this.MMSequential.setSolution(this.solution);
						this.MMSequential.addMoveGenerator(this.MGSwap);
						this.MGSwap.init();
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
	
	public double runRemove(double executionTime) {
		long time_start_function, time_end_function;
		time_start_function = System.currentTimeMillis();
		if(this.MMSequential.hasNext()) {
			this.reset();
			
			this.db.addSolutionMoveRemove(this.GApplier.apply());
			if(executionTime > 0) {
				long time_start, time_end;
				while(!this.MMSequential.hasNext() && executionTime > 0) {
					long id =  nextSolutionToExplore("removeGraph");
					
					time_start = System.currentTimeMillis();
					if(id != -1) {
						this.setSolution(this.db.getSolution(id));
						this.db.setIdOriginalSolution(id);
						this.MMSequential.removeMoveGenerator(this.MGRemove);
						this.MMSequential.setSolution(this.solution);
						this.MMSequential.addMoveGenerator(this.MGRemove);
						this.MGRemove.init();
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
	
	public double runInsertionAfter(double executionTime) {
		long time_start_function, time_end_function;
		time_start_function = System.currentTimeMillis();
		if(this.MMSequential.hasNext()) {
			this.reset();
			
			this.db.addSolutionMoveInsertionAfter(this.GApplier.apply());
			if(executionTime > 0) {
				long time_start, time_end;
				while(!this.MMSequential.hasNext() && executionTime > 0) {
					long id =  nextSolutionToExplore("insertionAfterGraph");
					
					time_start = System.currentTimeMillis();
					if(id != -1) {
						this.setSolution(this.db.getSolution(id));
						this.db.setIdOriginalSolution(id);
						this.MMSequential.removeMoveGenerator(this.MGInsertionAfter);
						this.MMSequential.setSolution(this.solution);
						this.MMSequential.addMoveGenerator(this.MGInsertionAfter);
						this.MGInsertionAfter.init();
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
	
	public double runInsertionBefore(double executionTime) {
		long time_start_function, time_end_function;
		time_start_function = System.currentTimeMillis();
		if(this.MMSequential.hasNext()) {
			this.reset();
			
			this.db.addSolutionMoveInsertionBefore(this.GApplier.apply());
			if(executionTime > 0) {
				long time_start, time_end;
				while(!this.MMSequential.hasNext() && executionTime > 0) {
					long id =  nextSolutionToExplore("insertionBeforeGraph");
					
					time_start = System.currentTimeMillis();
					if(id != -1) {
						this.setSolution(this.db.getSolution(id));
						this.db.setIdOriginalSolution(id);
						this.MMSequential.removeMoveGenerator(this.MGInsertionBefore);
						this.MMSequential.setSolution(this.solution);
						this.MMSequential.addMoveGenerator(this.MGInsertionBefore);
						this.MGInsertionBefore.init();
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
	
	public double runMoveAfter(double executionTime) {
		long time_start_function, time_end_function;
		time_start_function = System.currentTimeMillis();
		if(this.MMSequential.hasNext()) {
			this.reset();
			this.db.addSolutionMoveAfter(this.GApplier.apply());
			if(executionTime > 0) {
				long time_start, time_end;
				while(!this.MMSequential.hasNext() && executionTime > 0) {
					long id =  nextSolutionToExplore("moveAfterGraph");
					
					time_start = System.currentTimeMillis();
					if(id != -1) {
						this.setSolution(this.db.getSolution(id));
						this.db.setIdOriginalSolution(id);
						this.MMSequential.removeMoveGenerator(this.MGMoveAfter);
						this.MMSequential.setSolution(this.solution);
						this.MMSequential.addMoveGenerator(this.MGMoveAfter);
						this.MGMoveAfter.init();
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
	
	@SuppressWarnings("unchecked")
	public void reset() {
		this.auxSolution = this.solution.clone();
		this.GApplier.setSolution(this.auxSolution);
		this.GApplier.setMove(this.MMSequential.next());
	}
	
	public long nextSolutionToExplore(String collection) {
		if(this.nextSolutions.isEmpty()) {
			FindIterable<Document> pairsDB = this.db.getPairs(this.idCurrentSolution, collection);
			for(Document doc : pairsDB) {
				if (this.nextSolutions.indexOf(doc.getLong("idNode2")) == -1) {
					this.nextSolutions.add(doc.getLong("idNode2"));
				}
			}
			return this.nextSolutions.get(0);
		} else {
			for(int i = 0; i < this.nextSolutions.size(); i++) {
				FindIterable<Document> pairsDB = this.db.getPairs(this.nextSolutions.get(0), collection);
				for(Document doc : pairsDB) {
					if (this.nextSolutions.indexOf(doc.getLong("idNode2")) == -1) {
						this.nextSolutions.add(doc.getLong("idNode2"));
					}
				}
			}
			this.nextSolutions.remove(0);
			if(this.nextSolutions.isEmpty()) {
				return -1;
			} else {
				return this.nextSolutions.get(0);
			}
		}
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
/*				solution.remove(5);
				solution.remove(10);
				solution.remove(1);*/
				explorer.getDBControl().setOriginalProblem(problemVrp);
				break; 
			case 2: 
				/* Todo obtenido de la base de datos */ 
				problemVrp = explorer.getDBControl().getProblem(1);
				explorer.getDBControl().setOriginalProblem(problemVrp);
				solution = explorer.getDBControl().getSolution(100);
				solution.evaluate();
				break;
		}
		System.out.println("Solución inicial\n" + solution );
		explorer.explorer(solution, 4, 120);
		System.out.println("Fin de ejecución");
	}
	
}
