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
import com.kaizten.vrp.opt.move.MoveRoutesSolutionMoveBefore;
import com.kaizten.vrp.opt.move.applier.MoveApplierRoutesSolutionInsertionBefore;
import com.kaizten.vrp.opt.move.applier.MoveApplierRoutesSolutionMoveAfter;
import com.kaizten.vrp.opt.move.applier.MoveApplierRoutesSolutionMoveBefore;
import com.kaizten.vrp.opt.move.applier.MoveApplierRoutesSolutionSwap;
import com.kaizten.opt.move.generator.MoveGeneratorRoutesSolutionInsertionAfter;
import com.kaizten.vrp.opt.move.generator.MoveGeneratorRoutesSolutionInsertionBefore;
import com.kaizten.vrp.opt.move.generator.MoveGeneratorRoutesSolutionMoveAfter;
import com.kaizten.vrp.opt.move.generator.MoveGeneratorRoutesSolutionMoveBefore;
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
	private MoveGeneratorRoutesSolutionMoveBefore<RoutesSolution<Vrp>, MoveRoutesSolutionMoveBefore> MGMoveBefore;
	private DBControl db; 
	private String dbName; 
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
		this.MGMoveBefore =  new MoveGeneratorRoutesSolutionMoveBefore<RoutesSolution<Vrp>, MoveRoutesSolutionMoveBefore>();
		
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
				this.MMSequential.addMoveGenerator(this.MGMoveAfter);
				this.MMSequential.init();
				while(executionTime > 0) {
					executionTime -= runMoveAfter(executionTime);
				}
				break;
			case 2:
				this.MMSequential.addMoveGenerator(this.MGMoveBefore);
				this.MMSequential.init();
				while(executionTime > 0) {
					executionTime -= runMoveBefore(executionTime);
				}
				break;
			case 3:
				this.MMSequential.addMoveGenerator(this.MGRemove);
				this.MMSequential.init();
				while(executionTime > 0) { 
					executionTime -= runRemove(executionTime);
				}
				break;
			case 4:
				this.MMSequential.addMoveGenerator(this.MGInsertionAfter);
				this.MMSequential.init();
				while(executionTime > 0) {					
					executionTime -= runInsertionAfter(executionTime);
				}
				break;
			case 5:
				this.MMSequential.addMoveGenerator(this.MGInsertionBefore);
				this.MMSequential.init();
				while(executionTime > 0) {
					executionTime -= runInsertionBefore(executionTime);
				}
				break;
		}	
	}
	
	public double runSwap(double executionTime) {
		long time_start_function, time_end_function;
		time_start_function = System.currentTimeMillis();
		if(this.MMSequential.hasNext()) {
			this.reset();
			
			this.db.addSolutionMove(this.GApplier.apply(), "swapGraph");
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
			this.db.addSolutionMove(this.GApplier.apply(), "removeGraph");
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
			
			this.db.addSolutionMove(this.GApplier.apply(), "insertionAfterGraph");
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
			
			this.db.addSolutionMove(this.GApplier.apply(), "insertionBeforeGraph");
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
			this.db.addSolutionMove(this.GApplier.apply(), "moveAfterGraph");
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
	
	public double runMoveBefore(double executionTime) {
		long time_start_function, time_end_function;
		time_start_function = System.currentTimeMillis();
		if(this.MMSequential.hasNext()) {
			this.reset();
			this.db.addSolutionMove(this.GApplier.apply(), "moveBeforeGraph");
			if(executionTime > 0) {
				long time_start, time_end;
				while(!this.MMSequential.hasNext() && executionTime > 0) {
					long id =  nextSolutionToExplore("moveBeforeGraph");
					
					time_start = System.currentTimeMillis();
					if(id != -1) {
						this.setSolution(this.db.getSolution(id));
						this.db.setIdOriginalSolution(id);
						this.MMSequential.removeMoveGenerator(this.MGMoveBefore);
						this.MMSequential.setSolution(this.solution);
						this.MMSequential.addMoveGenerator(this.MGMoveBefore);
						this.MGMoveBefore.init();
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
	
	public void setDBName(String name) {
		this.dbName = name; 
	}
		
}
