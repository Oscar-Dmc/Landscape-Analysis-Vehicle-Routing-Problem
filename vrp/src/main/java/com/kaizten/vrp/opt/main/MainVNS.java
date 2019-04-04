package com.kaizten.vrp.opt.main;

import java.io.File;

import com.kaizten.opt.solution.RoutesSolution;
import com.kaizten.vrp.opt.core.Vrp;
import com.kaizten.vrp.opt.core.VrpSupplier;
import com.kaizten.vrp.opt.solver.Vns;

public class MainVNS {
	
	public static void main(String[] args) {
		System.out.println("Launch vns \n");
		File file =  new File(args[0]);
		VrpSupplier vrpSupplier = new VrpSupplier();
		vrpSupplier.setNVehicles(Integer.parseInt(args[1]));
		Vrp problem = vrpSupplier.get(file).findFirst().get();
		Vns vns =  new Vns(problem);
		RoutesSolution<Vrp> finalSolution =  vns.run();
		
		System.out.println(finalSolution);
	}
}
