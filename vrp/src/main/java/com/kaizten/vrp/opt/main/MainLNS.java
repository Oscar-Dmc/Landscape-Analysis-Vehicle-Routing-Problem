package com.kaizten.vrp.opt.main;

import com.kaizten.opt.solution.RoutesSolution;
import com.kaizten.vrp.opt.core.LNS;
import com.kaizten.vrp.opt.core.Vrp;

public class MainLNS {

	public static void main(String[] args) {
		System.out.println("Launch LNS\n");
		Vrp problem =  new Vrp(2000, 2000, 100, 20, 7);
		
		for(int i = 0;  i < problem.getCustomers().size();  i++) {
			//for(int j = 0; j < 3; j++) {
				System.out.println(problem.getCustomers().get(i));
			//}
		}
		//System.out.println(problem.toString());
		LNS lns =  new LNS(problem);
		RoutesSolution<Vrp> finalSolution =  lns.sequentialSolutionConstruct()/*lns.run()*/;
		
		System.out.println(finalSolution);
	}
}
