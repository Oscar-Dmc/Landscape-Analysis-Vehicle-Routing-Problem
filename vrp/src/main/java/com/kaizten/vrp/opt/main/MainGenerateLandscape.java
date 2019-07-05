package com.kaizten.vrp.opt.main;

import java.io.File;

import com.kaizten.opt.solution.RoutesSolution;
import com.kaizten.vrp.opt.core.ExplorerLandScape;
import com.kaizten.vrp.opt.core.Vrp;
import com.kaizten.vrp.opt.io.VrpSupplier;
import com.kaizten.vrp.opt.solver.RandomizedBuilder;

public class MainGenerateLandscape {
	public static void main(String[] args) {
		System.out.println("Starting to generate the landscape ");
		File file =  new File(args[0]);
		VrpSupplier vrpSupplier = new VrpSupplier();
		vrpSupplier.setNVehicles(Integer.parseInt(args[1]));
		Vrp problem = vrpSupplier.get(file).findFirst().get();
		problem.setNMaxCustomers(16);

		ExplorerLandScape explorer =  new ExplorerLandScape();
		explorer.setDBName(file.getName().split("\\.")[0]);
		explorer.init();
		explorer.getDBControl().setOriginalProblem(problem);
		RoutesSolution<Vrp> solution = null; 
		if(args.length >= 5) {
			try{
				solution = explorer.getDBControl().getSolution(Long.parseLong(args[4]));
			} catch (Exception e) {
				System.out.println("Database doesn't contain the specific solution");
				System.exit(0);
			}
		} else if (explorer.getDBControl().getNSolutions() == 0 || explorer.getDBControl().getFirstSolutionOfGraph(explorer.graphs[Integer.parseInt(args[2]) - 1]) == null) {
			solution = new RandomizedBuilder(problem).run();
		} else {
			solution = explorer.getDBControl().getFirstSolutionOfGraph(explorer.graphs[Integer.parseInt(args[2]) - 1]); 
		}
		
		if(args.length < 4 || 
		   Integer.parseInt(args[2]) < 0 || Integer.parseInt(args[2]) > 5 ||
		   Integer.parseInt(args[3]) < 0) {
			
			System.out.println("This problem requires 4 arguments");
			System.out.println("1: Path of the file who contain the initial problem. ");
			System.out.println("2: Number of vehicles available to solve that problem. ");
			System.out.println("3: Environment\n\t1 = Swap\n\t2 = Move After\n\t3 = Move Before\n\t4 = Remove\n\t5 = Insertion After\n\t6 = Insertion Before ");
			System.out.println("4: Exectution time");
		} else {
			explorer.explorer(solution, Integer.parseInt(args[2]), Integer.parseInt(args[3]));
		}

	}
	
}
