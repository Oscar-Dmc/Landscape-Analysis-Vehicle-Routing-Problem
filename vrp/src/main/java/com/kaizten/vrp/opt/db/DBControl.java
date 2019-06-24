package com.kaizten.vrp.opt.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bson.Document;

import com.kaizten.opt.evaluator.Evaluator;
import com.kaizten.opt.solution.RoutesSolution;
import com.kaizten.vrp.opt.core.Vrp;
import com.kaizten.vrp.opt.evaluators.EvaluatorMoveAfter;
import com.kaizten.vrp.opt.evaluators.EvaluatorMoveBefore;
import com.kaizten.vrp.opt.evaluators.EvaluatorMoveInsertionAfter;
import com.kaizten.vrp.opt.evaluators.EvaluatorMoveInsertionBefore;
import com.kaizten.vrp.opt.evaluators.EvaluatorMoveRemove;
import com.kaizten.vrp.opt.evaluators.EvaluatorMoveSwap;
import com.kaizten.vrp.opt.evaluators.EvaluatorObjectiveFunctionDistances;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;

import static com.mongodb.client.model.Filters.*;


public class DBControl {
	
	private MongoClient mongoClient; 
	private MongoDatabase database; 
	private MongoCollection<Document> collection; 
	private long idOriginalSolution;
	private int environment;
	private Vrp originalProblem; 
	public final long RANGE_OF_SOLUTIONS = 3000000;
	private String dbName; 
	
	public void init() {
		try {
			Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
			mongoLogger.setLevel(Level.SEVERE);
			this.mongoClient =  new MongoClient(new MongoClientURI("mongodb://127.0.0.1:27017"));
			this.database =  this.mongoClient.getDatabase(dbName);
		}
		catch(Exception e) {
			System.out.println(e);
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public long addProblem(Vrp problem) {
		long id = -1; 
		this.collection = database.getCollection("Problems");
		if(this.existProblem(problem) != -1) {
			return this.existProblem(problem);
		}
		else {
			Integer nCustomers = problem.getNCustomers();
			Integer nVehicles =  problem.getNVehicles();
			Integer nMaxCustomers = problem.getNMaxCustomers();
			ArrayList<ArrayList<Double>> distanceMatrix = new ArrayList<ArrayList<Double>>(); 
			ArrayList<ArrayList<Integer>> customers =  new ArrayList<ArrayList<Integer>>();
			ArrayList<Double> distance =  new ArrayList<Double>();
			ArrayList<Integer> customer =  new ArrayList<Integer>();
			for(int i = 0; i < nCustomers + 1;  i++) {
				customer =  problem.getCustomers().get(i);
				for(int j = 0;  j < nCustomers; j++) {
					distance.add(problem.getDistanceMatrix()[i][j]);
				}
				customers.add((ArrayList<Integer>) customer.clone());
				distanceMatrix.add((ArrayList<Double>) distance.clone());
				customer.clear();
				distance.clear();
			}
			
			id =  this.collection.countDocuments() + 1;
			
			Document problemDB =  new Document("_id", id)
					.append("DistanceMatrix", distanceMatrix)
					.append("Customers", customers)
					.append("nCustomers", nCustomers)
					.append("nVehicles", nVehicles)
					.append("nMaxCustomers", nMaxCustomers);
				
			
			this.collection.insertOne(problemDB);
		}
		return id; 
	}
	
	public long addSolution(RoutesSolution<Vrp> solution) {
		long id = -1;
		if(this.exist(solution) != -1) {
			id = this.exist(solution);
		}
		else {
			int nCustomers = solution.getOptimizationProblem().getNCustomers();
			int nRoutes =  solution.getNumberOfRoutes();
			Integer[] predecessors = new Integer [nCustomers];
			Integer[] successors = new Integer[nCustomers];
			Integer[] inRoute = new Integer[nCustomers];
			Boolean[] noIncluded = new Boolean[nCustomers];
			Integer[] first = new Integer[nRoutes];
			Integer[] last =  new Integer[nRoutes];
			Integer[] length = new Integer[nRoutes];
			Double[] objFunction =  new Double[solution.getNumberOfObjectives()];
			
			
			for(int i = 0; i < nCustomers; i++) {
				predecessors[i] =  solution.getPredecessor(i);
				successors[i] =  solution.getSuccessor(i);
				inRoute[i] = solution.getRouteIndex(i);
				noIncluded[i] =  solution.isRouted(i);
			}
			
			for(int i = 0; i < nRoutes; i++) {
				first[i] = solution.getFirstInRoute(i);
				last[i] =  solution.getLastInRoute(i);
				length[i] =  solution.getLengthRoute(i);
			}
			
			for(int i = 0; i < solution.getNumberOfObjectives();  i++) {
				objFunction[i] =  solution.getObjectiveFunctionValue(i);
			}
			
			/* Save the solutions in blocks */ 
			Document aux = this.collection.find(and(Filters.gte("_id", this.environment * RANGE_OF_SOLUTIONS),
													Filters.lt("_id", (this.environment + 1) * RANGE_OF_SOLUTIONS)))
										  .sort(Sorts.descending("_id")).first();
			
			if(aux != null) {
				id = aux.getLong("_id") + 1;
			}
			else {
				id = RANGE_OF_SOLUTIONS * this.environment; 
			}

			Document solutionDb =  new Document("_id",  id)
					.append("Predecessor", Arrays.asList(predecessors))
					.append("Successor", Arrays.asList(successors))
					.append("Route", Arrays.asList(inRoute))
					.append("No Included", Arrays.asList(noIncluded))
					.append("Routes", new Document("first", Arrays.asList(first))
							.append("last", Arrays.asList(last))
							.append("length", Arrays.asList(length)))
					.append("ObjFunction", Arrays.asList(objFunction));
			this.collection.insertOne(solutionDb);
		}
		
		return id; 
		
	}
	
	public long addSolutionGraph(RoutesSolution<Vrp> solution, String graph) {
		this.collection =  this.database.getCollection("solutions");
		long id =  addSolution(solution);
		
		Document pair = new Document("idNode1", this.idOriginalSolution)
							 .append("idNode2", id);
		
		if(existPair(this.idOriginalSolution, id, graph)) {
			this.collection.insertOne(pair);
		}
		return id;
	}
	
	public long addInitialSolution(RoutesSolution<Vrp> solution, int environment) {
		this.collection =  this.database.getCollection("solutions");
		this.environment =  environment; 
		long id = addSolution(solution);
		this.setIdOriginalSolution(id);
		return id;
	}
	
	public FindIterable<Document> getPairs(long id, String collection) {
		this.collection =  this.database.getCollection(collection);
		return this.collection.find(eq("idNode1", id));
	}

	/* Get a solution in database */ 
	@SuppressWarnings("unchecked")
	public RoutesSolution<Vrp> getSolution(long id){
		this.collection =  this.database.getCollection("solutions");
		if(this.originalProblem == null ) {
			System.out.println("The problem which belong this solution can't be null.");
			return null; 
		}
		Document documentWithSolution =  this.collection.find(eq("_id", id)).first();
		RoutesSolution<Vrp> solution = new RoutesSolution<Vrp>(this.originalProblem, this.originalProblem.getNCustomers(), this.originalProblem.getNVehicles());
		
		ArrayList<Integer> successor = new ArrayList<Integer>();
		ArrayList<Integer> firstInRoute = new ArrayList<Integer>(); 
		ArrayList<Double> objFunctionValue = new ArrayList<Double>();
		successor = (ArrayList<Integer>) documentWithSolution.get("Successor");
		firstInRoute = (ArrayList<Integer>) ((Document) documentWithSolution.get("Routes")).get("first");
		objFunctionValue = (ArrayList<Double>) documentWithSolution.get("ObjFunction");

		int index = 0; 
		for(int i = 0; i < firstInRoute.size(); i++) {
			if(firstInRoute.get(i) != -3) {
				index =  firstInRoute.get(i);
				solution.addAfterDepot(index, i);
				while(successor.get(index) != -1) {
					solution.addAfter(successor.get(index), index);
					index = successor.get(index); 
				}
			}
		}
		
		for(int i = 0; i < objFunctionValue.size(); i++) {
			solution.setObjectiveFunctionValue(i, objFunctionValue.get(i));
		}
		
		return solution;
	}
	
	public RoutesSolution<Vrp> getFirstSolution(){
		this.collection =  this.database.getCollection("solutions");
		if(this.originalProblem == null ) {
			System.out.println("The problem which belong this solution can't be null.");
			return null; 
		}
		long id  =  this.collection.find(gte("_id", -1)).first().getLong("_id");
		
		return this.getSolution(id); 
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Vrp getProblem(long id) {
		this.collection =  this.database.getCollection("Problems");
		Document documentWithProblem = this.collection.find(eq("_id",id)).first();
		ArrayList<ArrayList<Integer>> customers =  (ArrayList<ArrayList<Integer>>) documentWithProblem.get("Customers");
		Integer nCustomers = documentWithProblem.getInteger("nCustomers");
		Integer nVehicles =  documentWithProblem.getInteger("nVehicles");
		Integer nMaxCustomers =  documentWithProblem.getInteger("nMaxCustomers");
		
		Vrp problem =  new Vrp();
		/* init Vrp */ 
		problem.setCustomers(customers);
		problem.setNCustomers(nCustomers);
		problem.setNVehicles(nVehicles);
		problem.setNMaxCustomers(nMaxCustomers);
		problem.fillDistanceMatrix();
		
		/* add evaluators */ 
		Evaluator evaluator = new Evaluator(); 
		EvaluatorObjectiveFunctionDistances evaluatorLatency = new EvaluatorObjectiveFunctionDistances();
		
		evaluator.addEvaluatorObjectiveFunction(evaluatorLatency, evaluatorLatency.getName(), evaluatorLatency.getType());
		evaluator.addEvaluatorObjectiveFunctionMovement(new EvaluatorMoveSwap(1), 0);
		evaluator.addEvaluatorObjectiveFunctionMovement(new EvaluatorMoveAfter(), 0);
		evaluator.addEvaluatorObjectiveFunctionMovement(new EvaluatorMoveBefore(), 0);
		evaluator.addEvaluatorObjectiveFunctionMovement(new EvaluatorMoveRemove(1), 0);
		evaluator.addEvaluatorObjectiveFunctionMovement(new EvaluatorMoveInsertionAfter(), 0);
		evaluator.addEvaluatorObjectiveFunctionMovement(new EvaluatorMoveInsertionBefore(), 0);
		
		problem.setEvaluator(evaluator);
		
		return problem;
	}
	
	public void setIdOriginalSolution(long id) {
		this.idOriginalSolution =  id; 
	}
	
	public void setOriginalProblem(Vrp problem) {
		this.originalProblem = problem; 
	}
	
	public long getNSolutionsEnvironment(int environment) {
		this.collection = this.database.getCollection("solutions");
		
		return this.collection.countDocuments(and(Filters.gte("_id", environment * RANGE_OF_SOLUTIONS),
										  Filters.lt("_id", (environment + 1) * RANGE_OF_SOLUTIONS)));
	}
	
	public List<RoutesSolution<Vrp>> getSolutionsOfGraph(String collection){
		this.collection = this.database.getCollection(collection);
		List<RoutesSolution<Vrp>> solutions = new ArrayList<RoutesSolution<Vrp>>();
		ArrayList<Long> ids = new ArrayList<Long>();
		FindIterable<Document> pairsOfDB = this.collection.find(); 
		ids.add(pairsOfDB.first().getLong("idNode1"));
		solutions.add(this.getSolution(pairsOfDB.first().getLong("idNode1")));
		for(Document doc : pairsOfDB) {
			if(!ids.contains(doc.getLong("idNode2"))) { 
				ids.add(doc.getLong("idNode2"));
				solutions.add(this.getSolution(doc.getLong("idNode2")));
			} 
		}
		
		return solutions; 
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<Double> getObjFunctionValue(long id){
		this.collection = this.database.getCollection("solutions");
		return (ArrayList<Double>) this.collection.find(eq("_id", id)).first().get("ObjFunction");
	}
	
	public void setDBName(String name) {
		this.dbName = name;
	}
	
	public long getNSolutions() {
		this.collection = this.database.getCollection("solutions");
		return this.collection.countDocuments();
	}
	
	public long exist(RoutesSolution<Vrp> solution) {
		this.collection = this.database.getCollection("solutions");
		int nCustomers = solution.getOptimizationProblem().getNCustomers();
		ArrayList<Integer> predecessors = new ArrayList<Integer>();
		ArrayList<Integer> route = new ArrayList<Integer>();
		for(int i = 0; i < nCustomers; i++) {
			predecessors.add(solution.getPredecessor(i));
			route.add(solution.getRouteIndex(i));
		}
		
		if(this.collection.find(and(eq("Predecessor", predecessors), eq("Route", route))).first() !=  null) {
			return this.collection.find(and(eq("Predecessor", predecessors), eq("Route", route))).first().getLong("_id");
		}
		
		return -1;
	}
	
	@SuppressWarnings("unchecked")
	public long existProblem(Vrp problem) {
		this.collection = database.getCollection("Problems");
		ArrayList<ArrayList<Integer>> customers =  new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> customer =  new ArrayList<Integer>();
		for(int i = 0; i < problem.getNCustomers() + 1; i++) {
			customer = (ArrayList<Integer>) problem.getCustomers().get(i).clone();
			customers.add((ArrayList<Integer>) customer.clone());
			customer.clear();
		}
		
		if(this.collection.find(eq("Customers", customers)).first() !=  null) {
			return this.collection.find(eq("Customers", customers)).first().getLong("_id");
		}
		return -1;
	}
	
	public boolean existPair(long id1, long id2, String collection) {
		this.collection =  this.database.getCollection(collection);
		return (this.collection.find(and(eq("idNode1", id1), eq("idNode2", id2))).first() == null && 
				this.collection.find(and(eq("idNode1", id2), eq("idNode2",id1))).first() == null);
	}

}
