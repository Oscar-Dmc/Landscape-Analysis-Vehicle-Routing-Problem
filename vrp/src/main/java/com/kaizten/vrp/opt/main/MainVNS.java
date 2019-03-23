package com.kaizten.vrp.opt.main;

import com.kaizten.opt.solution.RoutesSolution;
import com.kaizten.vrp.opt.core.Vns;
import com.kaizten.vrp.opt.core.Vrp;

public class MainVNS {
	
	public static void main(String[] args) {
		System.out.println("Launch vns \n");
		Vrp problem = new Vrp(100, 100, 10, 5, 4);
		Vns vns =  new Vns(problem);
		RoutesSolution<Vrp> finalSolution =  vns.run();
		
		System.out.println(finalSolution);
	}
}
