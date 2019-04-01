package com.kaizten.vrp.opt.main;

import com.kaizten.opt.solution.RoutesSolution;
import com.kaizten.vrp.opt.core.Vrp;
import com.kaizten.vrp.opt.solver.LNS;

public class MainLNS {

	public static void main(String[] args) {
		System.out.println("Launch LNS\n");
		Vrp problem =  new Vrp(2000, 2000, 100, 20, 7);
		
		//System.out.println(problem.toString());
		LNS lns =  new LNS(problem);
		RoutesSolution<Vrp> finalSolution = lns.run();
		
		System.out.println(finalSolution);
	}
}
