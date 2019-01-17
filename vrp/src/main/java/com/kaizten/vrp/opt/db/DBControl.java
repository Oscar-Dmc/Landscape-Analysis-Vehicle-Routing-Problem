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
//import com.mongodb.MongoCredential;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Filters.*;


public class DBControl {
	
	private MongoClient mongoClient; 
	private MongoDatabase database; 
	//private MongoCredential credential;
	private MongoCollection<Document> collection; 
	private long idOriginalSolution; 
	private Vrp originalProblem; 
	
	public DBControl() {
		//this.idSolution = 0; /* improve only for test */ 
	}
	
	public void init() {
		try {
			Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
			mongoLogger.setLevel(Level.SEVERE);
			this.mongoClient =  new MongoClient(new MongoClientURI("mongodb://127.0.0.1:27017"));
			//this.credential = MongoCredential.createCredential("sampleUser", "Vrp", "password".toCharArray());			
			this.database =  mongoClient.getDatabase("Vrp");
			//this.originalProblem =  problem; 
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

			id = this.collection.countDocuments() + 1;
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
	
	public void addSolutionMoveRemove(RoutesSolution<Vrp> solution/*, MoveRoutesSolutionRemove move*/) {
		this.collection =  database.getCollection("solutionsTest");
		/* improve  
		if(this.idSolution == 0) {
			addSolution(solution);
		} */
		//long id =  this.collection.countDocuments() + 1;
		/*@SuppressWarnings("unchecked")
		RoutesSolution<Vrp> solutionRemove =  solution.clone();
		MoveApplierRoutesSolutionRemove applierRemove =  new MoveApplierRoutesSolutionRemove();
		applierRemove.accept(solutionRemove, move);*/
		
		addSolution(solution);
	}
	
	public void addSolutionMoveSwap(RoutesSolution<Vrp> solution) {
		this.collection =  database.getCollection("solutionsTest");
		long id =  addSolution(solution);		
		
		Document pair =  new Document("idNode1", this.idOriginalSolution)
									.append("idNode2", id);
		
		this.collection =  database.getCollection("swapGraph");
		
		if(this.collection.find(and(eq("idNode1", this.idOriginalSolution), eq("idNode2", id))).first() == null && 
		   this.collection.find(and(eq("idNode1", id), eq("idNode2", this.idOriginalSolution))).first() == null) {
				
			this.collection.insertOne(pair);
		}
		
		
	}
	
	public long addInitialSolution(RoutesSolution<Vrp> solution) {
		this.collection =  database.getCollection("solutionsTest");
		long id = addSolution(solution);
		setIdOriginalSolution(id);
		return id;
	}
	
	/* Testing method */ 
	public boolean getPair(long id1, long id2) {
		this.collection =  database.getCollection("swapGraph");
		System.out.println(this.collection.find(and(eq("idNode1", id1), eq("idNode2", id2))).first());
		return (this.collection.find(and(eq("idNode1", id1), eq("idNode2", id2))).first() != null);
	}
	
	/* Get a solution in database */ 
	@SuppressWarnings("unchecked")
	public RoutesSolution<Vrp> getSolution(long id){
		this.collection =  database.getCollection("solutionsTest");
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
			index =  firstInRoute.get(i);
			solution.addAfterDepot(index, i);
			while(successor.get(index) != -1) {
				solution.addAfter(successor.get(index), index);
				index = successor.get(index); 
			}
		}
		
		for(int i = 0; i < objFunctionValue.size(); i++) {
			solution.setObjectiveFunctionValue(i, objFunctionValue.get(i));
		}
		
		return solution;
	}
	
	@SuppressWarnings("unchecked")
	public Vrp getProblem(long id) {
		this.collection =  database.getCollection("Problems");
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
	
	public long exist(RoutesSolution<Vrp> solution) {
		this.collection = database.getCollection("solutionsTest");
		int nCustomers = solution.getOptimizationProblem().getNCustomers();
		Integer[] predecessors = new Integer [nCustomers];
		for(int i = 0; i < nCustomers; i++) {
			predecessors[i] =  solution.getPredecessor(i);
		}
		
		if(this.collection.find(eq("Predecessor", Arrays.asList(predecessors))).first() !=  null) {
			return this.collection.find(eq("Predecessor", Arrays.asList(predecessors))).first().getLong("_id");
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
}
