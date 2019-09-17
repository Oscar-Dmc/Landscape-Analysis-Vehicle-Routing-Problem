package com.kaizten.vrp.opt.landscape;

import java.util.ArrayList;

import com.kaizten.opt.solution.RoutesSolution;
import com.kaizten.vrp.opt.core.Vrp;

public class Distances {
	
	public int distanceSwap(RoutesSolution<Vrp> solution1, RoutesSolution<Vrp> solution2) {
		ArrayList<Integer> sol1 = new ArrayList<Integer>();
		ArrayList<Integer> sol2 = new ArrayList<Integer>();
		
		for(int i = 0; i < solution1.getNumberOfRoutes(); i++) {
			for(int j = 0; j < solution1.getRoute(i).length; j++) {
				sol1.add(solution1.getRoute(i)[j]);
				sol2.add(solution2.getRoute(i)[j]);
			}
		}
		
		int distance = 0; 
		int i = 0;
		
		while(!sol1.equals(sol2)) {
			if(sol1.get(i) != sol2.get(i)) {
				Integer ogElement = sol1.get(i);
				int indexElemOg =  sol2.indexOf(ogElement);
				Integer elemChange = sol2.get(i);
				sol2.set(i, ogElement);
				sol2.set(indexElemOg, elemChange);
				distance++;
			}
			i++;
		}
		
		return distance; 
		
	}
	
	public int distanceMove(RoutesSolution<Vrp> solution1, RoutesSolution<Vrp> solution2) {
		int distance = 0;
		ArrayList<ArrayList<Integer>> routes1 = new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayList<Integer>> routes2 = new ArrayList<ArrayList<Integer>>();
		
		for(int i = 0; i < solution1.getNumberOfRoutes(); i++) {
			ArrayList<Integer> route1 = new ArrayList<Integer>();
			ArrayList<Integer> route2 = new ArrayList<Integer>();
			
			for(int j = 0; j < solution1.getRoute(i).length; j++) {
				route1.add(solution1.getRoute(i)[j]);
			}
			
			for(int j = 0; j < solution2.getRoute(i).length; j++) {
				route2.add(solution2.getRoute(i)[j]);
			}
			
			routes1.add(route1);
			routes2.add(route2);
		}
		
		for(int i = 0; i < routes1.size(); i++) {
			for(int j = 0; j < routes2.get(i).size(); j++) {
				Integer element = routes2.get(i).get(j);
				if(!routes1.get(i).contains(element)) {
					routes2.get(i).remove(element);
					j--;
				}
			}
			
			if(routes2.get(i).isEmpty()) {
				for(int j = 0; j < routes1.get(i).size(); j++) {
					routes2.get(i).add(routes1.get(i).get(j));
					distance++;
				}
			} else {
				for(int j = 0; j < routes1.get(i).size(); j++) {
					if(j == routes2.get(i).size()) {
						routes2.get(i).add(routes1.get(i).get(j));
						distance++; 
					}else if(routes1.get(i).get(j) != routes2.get(i).get(j)){
						routes2.get(i).remove(routes1.get(i).get(j));
						routes2.get(i).add(j, routes1.get(i).get(j));
						distance++; 
					}
				}
			}
		}
		
		return distance; 
	}

	public int distanceInsRem(RoutesSolution<Vrp> solution1, RoutesSolution<Vrp> solution2) {
		return Math.abs(solution1.getNumberOfIncluded() - solution2.getNumberOfIncluded()); 
	}

	
}
