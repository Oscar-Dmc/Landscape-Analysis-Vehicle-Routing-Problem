package com.kaizten.vrp.opt.landscape;

import java.util.ArrayList;

import com.kaizten.opt.solution.RoutesSolution;
import com.kaizten.vrp.opt.core.Vrp;

public class Distances {
	
	public int distanceSwap(RoutesSolution<Vrp> solution1, RoutesSolution<Vrp> solution2) {
		ArrayList<ArrayList<Integer>> routes1 = new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayList<Integer>> routes2 = new ArrayList<ArrayList<Integer>>();
		
		for(int i = 0; i < solution1.getNumberOfRoutes(); i++) {
			ArrayList<Integer> route1 = new ArrayList<Integer>();
			ArrayList<Integer> route2 = new ArrayList<Integer>();
			
			for(int j = 0; j < solution1.getRoute(i).length; j++) {
				route1.add(solution1.getRoute(i)[j]);
				route2.add(solution2.getRoute(i)[j]);
			}
			
			routes1.add(route1);
			routes2.add(route2);
		}
		
		int distance = 0; 
		for(int i = 0; i < routes1.size(); i++) {
			for(int j = 0; j < routes1.get(i).size(); j++) {
				if(routes1.get(i).get(j) != routes2.get(i).get(j)) {
					for(int k = i; k < routes2.size(); k++) {
						Integer ogElement =  routes1.get(i).get(j);
						int indexEleOg = routes2.get(k).indexOf(ogElement);
						if(indexEleOg != -1) {
							Integer elemChange = routes2.get(i).get(j);
							routes2.get(i).set(j, routes2.get(k).get(indexEleOg));
							routes2.get(k).set(indexEleOg, elemChange);
							distance++;
						}
					}
				}
			}
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
