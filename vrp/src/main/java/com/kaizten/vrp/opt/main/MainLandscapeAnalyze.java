package com.kaizten.vrp.opt.main;

import java.io.File;
import java.util.List;
import com.kaizten.opt.solution.RoutesSolution;
import com.kaizten.vrp.opt.core.Vrp;
import com.kaizten.vrp.opt.db.DBControl;
import com.kaizten.vrp.opt.io.VrpSupplier;
import com.kaizten.vrp.opt.landscape.Amp;
import com.kaizten.vrp.opt.landscape.AutocorrelationFunction;
import com.kaizten.vrp.opt.landscape.DistanceMedium;
import com.kaizten.vrp.opt.landscape.Entropy;
import com.kaizten.vrp.opt.landscape.FitnessDistanceCorrelation;
import com.kaizten.vrp.opt.landscape.Lmm;

public class MainLandscapeAnalyze {
	private final static String graphs[] = {"swapGraph", "moveAfterGraph", "moveBeforeGraph", "removeGraph", "insertionAfterGraph", "insertionBeforeGraph"};

	public static void main(String[] args) {
		File file =  new File(args[0]);
		VrpSupplier vrpSupplier = new VrpSupplier();
		vrpSupplier.setNVehicles(Integer.parseInt(args[1]));
		Vrp problem = vrpSupplier.get(file).findFirst().get();
		problem.setNMaxCustomers(5);
		
		DBControl db = new DBControl();
		db.setDBName(file.getName().split("\\.")[0]);
		db.setOriginalProblem(problem);
		db.init();
		List<RoutesSolution<Vrp>> solutions = db.getSolutionsOfGraph(graphs[Integer.parseInt(args[2]) - 1]);
		int environment = Integer.parseInt(args[2]) - 1;
		System.out.println("Number of solutions: " + solutions.size());
		
		/* Measures */ 
		DistanceMedium dmm = new DistanceMedium();
		dmm.setSolutions(solutions.stream(), environment);
		dmm.compute();
		
		Entropy entropy = new Entropy();
		entropy.setSolutions(solutions.stream());
		entropy.compute();
		
		Amp amp = new Amp();
		amp.setSolutions(solutions.stream());
		amp.compute();
		
		Lmm lmm = new Lmm();
		lmm.setSolutions(solutions.stream());
		lmm.compute();
		
		AutocorrelationFunction  aCor = new AutocorrelationFunction();
		aCor.setSolutions(solutions.stream());
		aCor.compute();
		
		FitnessDistanceCorrelation fdc = new FitnessDistanceCorrelation();
		fdc.setSolutions(solutions.stream(), environment);
		fdc.compute();
		
	}
}
