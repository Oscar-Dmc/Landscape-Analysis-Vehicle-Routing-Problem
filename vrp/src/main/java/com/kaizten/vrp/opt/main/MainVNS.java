package com.kaizten.vrp.opt.main;

import java.io.File;

import com.kaizten.opt.solution.RoutesSolution;
import com.kaizten.vrp.opt.core.Vrp;
import com.kaizten.vrp.opt.core.VrpSupplier;
import com.kaizten.vrp.opt.solver.Vns;

public class MainVNS {
	
	public static void main(String[] args) {
		System.out.println("Launch vns \n");
		//Vrp problem = new Vrp(100, 100, 10, 5, 4);
		File file =  new File("D:\\Documentos\\Repositorios\\TFG\\vrp\\instances\\C101_25.txt");
		VrpSupplier vrpSupplier = new VrpSupplier();
		Vrp problem = vrpSupplier.get(file).findFirst().get();
		Vns vns =  new Vns(problem);
		RoutesSolution<Vrp> finalSolution =  vns.run();
		
		System.out.println(finalSolution);
	}
}
