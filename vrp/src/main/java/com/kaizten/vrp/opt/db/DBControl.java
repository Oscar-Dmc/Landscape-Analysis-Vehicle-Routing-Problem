package com.kaizten.vrp.opt.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bson.Document;
import com.kaizten.opt.solution.RoutesSolution;
import com.kaizten.vrp.opt.core.Vrp;
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
	
	public void init() {
		try {
			Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
			mongoLogger.setLevel(Level.SEVERE);
			this.mongoClient =  new MongoClient(new MongoClientURI("mongodb://127.0.0.1:27017"));
			this.database =  this.mongoClient.getDatabase("Vrp");
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
			ArrayList<ArrayList<Double>> distanceMatrix = new ArrayList<ArrayList<Double>>(); 
			ArrayList<ArrayList<Integer>> customers =  new ArrayList<ArrayList<Integer>>();
			
			
			ArrayList<Double> distance =  new ArrayList<Double>();
			ArrayList<Integer> customer =  new ArrayList<Integer>();
			for(int i = 0; i < nCustomers + 1;  i++) {
				customer.add(problem.getCustomers().get(i).getX());
				customer.add(problem.getCustomers().get(i).getY());
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
					.append("nVehicles", nVehicles);
			
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
			//System.out.println(solutionDb.toString());
			this.collection.insertOne(solutionDb);
		}
		
		return id; 
		
	}
	
	public void addSolutionMove(RoutesSolution<Vrp> solution, String graph) {
		this.collection =  this.database.getCollection("solutionsTest");
		long id =  addSolution(solution);
		
		Document pair = new Document("idNode1", this.idOriginalSolution)
							 .append("idNode2", id);
		
		if(existPair(this.idOriginalSolution, id, graph)) {
			this.collection.insertOne(pair);
		}
	}
	
	public long addInitialSolution(RoutesSolution<Vrp> solution, int environment) {
		this.collection =  this.database.getCollection("solutionsTest");
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
		this.collection =  this.database.getCollection("solutionsTest");
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
	
	@SuppressWarnings("unchecked")
	public Vrp getProblem(long id) {
		this.collection =  this.database.getCollection("Problems");
		Document documentWithProblem = this.collection.find(eq("_id",id)).first();
		ArrayList<ArrayList<Integer>> customers =  (ArrayList<ArrayList<Integer>>) documentWithProblem.get("Customers");
		Integer nCustomers = documentWithProblem.getInteger("nCustomers");
		Integer nVehicles =  documentWithProblem.getInteger("nVehicles");
		
		return new Vrp(customers, nCustomers, nVehicles);
	}
	
	public void setIdOriginalSolution(long id) {
		this.idOriginalSolution =  id; 
	}
	
	public void setOriginalProblem(Vrp problem) {
		this.originalProblem = problem; 
	}
	
	public long getNSolutionsEnvironment(int environment) {
		this.collection = this.database.getCollection("solutionsTest");
		
		return this.collection.countDocuments(and(Filters.gte("_id", environment * RANGE_OF_SOLUTIONS),
										  Filters.lt("_id", (environment + 1) * RANGE_OF_SOLUTIONS)));
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<Double> getObjFunctionValue(long id){
		this.collection = this.database.getCollection("solutionsTest");
		return (ArrayList<Double>) this.collection.find(eq("_id", id)).first().get("ObjFunction");
	}
	
	public long exist(RoutesSolution<Vrp> solution) {
		this.collection = this.database.getCollection("solutionsTest");
		int nCustomers = solution.getOptimizationProblem().getNCustomers();
		ArrayList<Integer> predecessors = new ArrayList<Integer>();
		ArrayList<Integer> route = new ArrayList<Integer>();
		//Integer[] predecessors = new Integer [nCustomers];
		for(int i = 0; i < nCustomers; i++) {
			predecessors.add(solution.getPredecessor(i));
			route.add(solution.getRouteIndex(i));
			//predecessors[i] =  solution.getPredecessor(i);
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
			customer.add(problem.getCustomers().get(i).getX());
			customer.add(problem.getCustomers().get(i).getY());
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