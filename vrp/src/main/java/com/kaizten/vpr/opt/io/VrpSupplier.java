package com.kaizten.vpr.opt.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Stream;

import com.kaizten.opt.evaluator.Evaluator;
import com.kaizten.opt.io.KaiztenOptimizationProblemFileSupplier;
import com.kaizten.vrp.opt.core.Vrp;
import com.kaizten.vrp.opt.evaluators.EvaluatorMoveAfter;
import com.kaizten.vrp.opt.evaluators.EvaluatorMoveBefore;
import com.kaizten.vrp.opt.evaluators.EvaluatorMoveInsertionAfter;
import com.kaizten.vrp.opt.evaluators.EvaluatorMoveInsertionBefore;
import com.kaizten.vrp.opt.evaluators.EvaluatorMoveRemove;
import com.kaizten.vrp.opt.evaluators.EvaluatorMoveSwap;
import com.kaizten.vrp.opt.evaluators.EvaluatorObjectiveFunctionDistances;

public class VrpSupplier implements KaiztenOptimizationProblemFileSupplier<Vrp>{
	
	private int nVehicles;

	@SuppressWarnings({ "unused", "unchecked" })
	@Override
	public Stream<Vrp> get(File file) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = reader.readLine(); 
			int nCustomers =  Integer.parseInt(reader.readLine());
			line = reader.readLine(); /* Skip lines with only characters */ 
			int vehicleCapacity = Integer.parseInt(reader.readLine());
			line = reader.readLine();
			int tMax = Integer.parseInt(reader.readLine()); 
			line = reader.readLine();
			line = reader.readLine();
			ArrayList<ArrayList<Integer>> customers = new ArrayList<ArrayList<Integer>>();
			while(line != null) {
				String[] lineSplit =  line.split("\\s+");
				if(lineSplit.length > 0) {
					ArrayList<Integer> customer =  new ArrayList<Integer>();
					for(int i = 1; i < lineSplit.length;  i++ ) {
						customer.add(Integer.parseInt(lineSplit[i]));
					} 
					customers.add(customer);
				}
				line =  reader.readLine();
			}
			reader.close();
			
			int nMaxCustomers = ((nCustomers/this.nVehicles) + 1); /* get this value for parameters */ 
			//Vrp problem =  new Vrp(customers, nCustomers, this.nVehicles, nCustomers); /* Establish fixed of number of vehicles until new version of instances */ 
			Vrp problem =  new Vrp();
			/* init Vrp */ 
			problem.setCustomers(customers);
			problem.setNCustomers(nCustomers);
			problem.setNVehicles(this.nVehicles);
			problem.setNMaxCustomers(nMaxCustomers);
			problem.fillDistanceMatrix();
			
			/* Add evaluators */ 
			@SuppressWarnings("rawtypes")
			Evaluator evaluator = new Evaluator(); 
			EvaluatorObjectiveFunctionDistances evaluatorLatency = new EvaluatorObjectiveFunctionDistances();
			
			evaluator.addEvaluatorObjectiveFunction(evaluatorLatency, evaluatorLatency.getName(), evaluatorLatency.getType());
			evaluator.addEvaluatorObjectiveFunctionMovement(new EvaluatorMoveRemove(1), 0);
			evaluator.addEvaluatorObjectiveFunctionMovement(new EvaluatorMoveSwap(1), 0);
			evaluator.addEvaluatorObjectiveFunctionMovement(new EvaluatorMoveInsertionAfter(), 0);
			evaluator.addEvaluatorObjectiveFunctionMovement(new EvaluatorMoveInsertionBefore(), 0);
			evaluator.addEvaluatorObjectiveFunctionMovement(new EvaluatorMoveAfter(), 0);
			evaluator.addEvaluatorObjectiveFunctionMovement(new EvaluatorMoveBefore(), 0);
			
			problem.setEvaluator(evaluator);
			
			
			return Stream.of(problem); 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null; 
		} catch (IOException e) {
			e.printStackTrace();
			return null; 
		}
	}
	
	public void setNVehicles(int nVehicles) {
		this.nVehicles =  nVehicles; 
	}

}
