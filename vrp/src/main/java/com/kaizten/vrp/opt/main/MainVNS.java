package com.kaizten.vrp.opt.main;

import java.io.File;
import java.util.ArrayList;

import com.kaizten.opt.solution.RoutesSolution;
import com.kaizten.vrp.opt.core.Vrp;
import com.kaizten.vrp.opt.io.VrpSupplier;
import com.kaizten.vrp.opt.solver.Vns;

public class MainVNS {
	
	public static void main(String[] args) {
		System.out.println("Launch vns \n");
		File file =  new File(args[0]);
		VrpSupplier vrpSupplier = new VrpSupplier();
		vrpSupplier.setNVehicles(Integer.parseInt(args[1]));
		Vrp problem = vrpSupplier.get(file).findFirst().get();
		problem.setNMaxCustomers(Integer.parseInt(args[2])); 
		Vns vns =  new Vns(problem, Integer.parseInt(args[3]));
		ArrayList<Integer> neighborhoods = new ArrayList<Integer>();
		for(int i = 4; i < args.length;  i++) {
			neighborhoods.add(Integer.parseInt(args[i]) - 1);
		}
		vns.setNeighborhood(neighborhoods);
		long startTime = System.currentTimeMillis();
		RoutesSolution<Vrp> finalSolution =  vns.run();
		long endTime = System.currentTimeMillis(); 
		System.out.println(finalSolution.getObjectiveFunctionValues());
		System.out.println("Total execution time: " + (endTime - startTime) + " ms" ); 
	}
}
