package com.kaizten.vrp.opt.main;

import java.io.File;
import java.util.ArrayList;

import com.kaizten.opt.solution.RoutesSolution;
import com.kaizten.vrp.opt.core.Vrp;
import com.kaizten.vrp.opt.io.VrpSupplier;
import com.kaizten.vrp.opt.solver.LNS;

public class MainLNS {

	public static void main(String[] args) {
		System.out.println("Launch LNS\n");
		File file =  new File(args[0]);
		VrpSupplier vrpSupplier = new VrpSupplier();
		vrpSupplier.setNVehicles(Integer.parseInt(args[1]));
		Vrp problem = vrpSupplier.get(file).findFirst().get();
		problem.setNMaxCustomers(Integer.parseInt(args[2]));
		if(((problem.getNMaxCustomers() * problem.getNVehicles()) / problem.getNCustomers()) > 0) {
			double percent = (Double.parseDouble(args[4]) / 100);  
			int itMax = Integer.parseInt(args[5]);
			ArrayList<Integer> neighborhoods = new ArrayList<Integer>();
			for(int i = 6; i < args.length;  i++) {
				neighborhoods.add(Integer.parseInt(args[i]) - 1);
			}
			
			LNS lns =  new LNS(problem, Integer.parseInt(args[3]));
			lns.setNeighborhood(neighborhoods);
			lns.setPercent(percent);
			lns.setIterationsMax(itMax);
			RoutesSolution<Vrp> finalSolution = lns.run();
			System.out.println("Final solution\n" + finalSolution);
			System.out.println();
		} else {
			System.out.println("Can't resolve this problem with that settings.");
			System.out.println("Check the capacity and number of vehicles and number of routes, and try again.");
		}
		

	}
}
