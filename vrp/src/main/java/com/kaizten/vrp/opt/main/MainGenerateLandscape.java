package com.kaizten.vrp.opt.main;

import java.io.File;

import com.kaizten.opt.solution.RoutesSolution;
import com.kaizten.vrp.opt.core.ExplorerLandScape;
import com.kaizten.vrp.opt.core.Vrp;
import com.kaizten.vrp.opt.core.VrpSupplier;
import com.kaizten.vrp.opt.solver.RandomizedBuilder;

public class MainGenerateLandscape {
	public static void main(String[] args) {
		System.out.println("Starting to generate the landscape ");
		File file =  new File(args[0]);
		VrpSupplier vrpSupplier = new VrpSupplier();
		vrpSupplier.setNVehicles(Integer.parseInt(args[1]));
		Vrp problem = vrpSupplier.get(file).findFirst().get();

		ExplorerLandScape explorer =  new ExplorerLandScape();
		explorer.setDBName(file.getName().split("\\.")[0]);
		explorer.init();
		explorer.getDBControl().setOriginalProblem(problem);
		RoutesSolution<Vrp> solution = null; 
		if(explorer.getDBControl().getNSolutions() == 0) {
			solution =  new RandomizedBuilder(problem).run();
		} else {
			solution = explorer.getDBControl().getSolution(0); 
		}
		
		if(args.length < 4 || 
		   Integer.parseInt(args[2]) < 0 || Integer.parseInt(args[2]) > 5 ||
		   Integer.parseInt(args[3]) < 0) {
			
			System.out.println("This problem requires 4 arguments");
			System.out.println("1: Path of the file who contain the initial problem. ");
			System.out.println("2: Number of vehicles available to solve that problem. ");
			System.out.println("3: Environment\n\t0 = Swap\n\t1 = Move After\n\t2 = Move Before\n\t3 = Remove\n\t4 = Insertion After\n\t5 = Insertion Before ");
			System.out.println("4: Exectution time");
		} else {
			explorer.explorer(solution, Integer.parseInt(args[2]), Integer.parseInt(args[3]));
		}

	}
	
}
