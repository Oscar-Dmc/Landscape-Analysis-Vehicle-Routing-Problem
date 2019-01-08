package com.kaizten.vrp.opt.db;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bson.Document;
import com.kaizten.opt.solution.RoutesSolution;
import com.kaizten.vrp.opt.core.Vrp;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class DBControl {
	
	private MongoClient mongoClient; 
	private MongoDatabase database; 
	private MongoCredential credential;
	private MongoCollection<Document> collection; 
	
	public DBControl() {
		//this.idSolution = 0; /* improve only for test */ 
	}
	
	public void init() {
		try {
			Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
			mongoLogger.setLevel(Level.SEVERE);
			this.mongoClient =  new MongoClient(new MongoClientURI("mongodb://127.0.0.1:27017"));
			this.credential = MongoCredential.createCredential("sampleUser", "Vrp", "password".toCharArray());			
			this.database =  mongoClient.getDatabase("Vrp");
		}
		catch(Exception e) {
			System.out.println(e);
		}

		
	}
	
	public void addSolution(RoutesSolution<Vrp> solution,  long id) {		
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
	
	public void addSolutionMoveRemove(RoutesSolution<Vrp> solution/*, MoveRoutesSolutionRemove move*/) {
		this.collection =  database.getCollection("solutionsTest");
		/* improve  
		if(this.idSolution == 0) {
			addSolution(solution);
		} */
		long id =  this.collection.countDocuments() + 1;
		/*@SuppressWarnings("unchecked")
		RoutesSolution<Vrp> solutionRemove =  solution.clone();
		MoveApplierRoutesSolutionRemove applierRemove =  new MoveApplierRoutesSolutionRemove();
		applierRemove.accept(solutionRemove, move);*/
		
		addSolution(solution, id);
	}
	
	public void addSolutionMoveSwap(RoutesSolution<Vrp> solution) {
		this.collection =  database.getCollection("solutionsTest");
		long id =  this.collection.countDocuments() + 1;
		addSolution(solution, id);
		
		
	}
	
	
}
