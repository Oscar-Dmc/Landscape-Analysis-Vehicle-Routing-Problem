package com.kaizten.vpr.opt.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Stream;

import com.kaizten.opt.io.KaiztenSolutionFileSupplier;
import com.kaizten.opt.solution.RoutesSolution;
import com.kaizten.vrp.opt.core.Vrp;

public class VrpSolutionSupplier  implements KaiztenSolutionFileSupplier<Vrp, RoutesSolution<Vrp>>{

	@SuppressWarnings("resource")
	@Override
	public Stream<RoutesSolution<Vrp>> get(Vrp optimizationProblem, File file) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			ArrayList<Integer> successors = new ArrayList<Integer>();
			ArrayList<Integer> firstInRoute = new ArrayList<Integer>();
			
			for(int i = 0; i < 12 ; i++) { 
				if(i == 2) {
					successors = getNumbers(reader.readLine().split("\\s+"));
				} else if (i == 9) {
					firstInRoute = getNumbers(reader.readLine().split("\\s+"));
				} else {
					reader.readLine();					
				}
			}
			
			String[] line = reader.readLine().split("\\s+");			
			Double objFunction = Double.parseDouble(line[line.length - 1].substring(1, line[line.length - 1].length() - 1));
			
			
			RoutesSolution<Vrp> solution = new RoutesSolution<Vrp>(optimizationProblem, successors.size(), firstInRoute.size());
			
			for(int i = 0; i < firstInRoute.size(); i++) {
				if(firstInRoute.get(i) != -1) {
					int index =  firstInRoute.get(i);
					solution.addAfterDepot(index, i);
					while(successors.get(index) != -1) {
						solution.addAfter(successors.get(index), index);
						index = successors.get(index); 
					}
				}
			}
			
			solution.setObjectiveFunctionValue(0, objFunction);
			
			return Stream.of(solution);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public ArrayList<Integer> getNumbers(String[] line){
		ArrayList<Integer> numbers = new ArrayList<Integer>();
		for(int i = 0; i < line.length; i++) {
			if(line[i].matches("\\d+")) {
				numbers.add(Integer.parseInt(line[i]));
			} else if (line[i].matches("-")) {
				numbers.add(-1);
			} 
		}
		return numbers; 
	}

}
