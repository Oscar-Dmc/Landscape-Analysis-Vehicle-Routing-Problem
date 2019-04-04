package com.kaizten.vrp.opt.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Stream;

import com.kaizten.opt.io.KaiztenOptimizationProblemFileSupplier;

public class VrpSupplier implements KaiztenOptimizationProblemFileSupplier<Vrp>{
	
	private int nVehicles;

	@SuppressWarnings("unused")
	@Override
	public Stream<Vrp> get(File file) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = reader.readLine(); 
			int nCustomers =  Integer.parseInt(reader.readLine());
			line = reader.readLine(); /* Skip lines with only characters */ 
			int vehicleCapacity = Integer.parseInt(reader.readLine());
			line = reader.readLine();
			int tMax = Integer.parseInt(reader.readLine()); 
			line = reader.readLine();
			line = reader.readLine();
			ArrayList<ArrayList<Integer>> customers = new ArrayList<ArrayList<Integer>>();
			while(line != null) {
				String[] lineSplit =  line.split("\\s+");
				if(lineSplit.length > 0) {
					ArrayList<Integer> customer =  new ArrayList<Integer>();
					for(int i = 1; i < lineSplit.length;  i++ ) {
						customer.add(Integer.parseInt(lineSplit[i]));
					} 
					customers.add(customer);
				}
				line =  reader.readLine();
			}
			reader.close();
			
			int nMaxCustomers = ((nCustomers/this.nVehicles) + 1); /* get this value for parameters */ 
			Vrp problem =  new Vrp(customers, nCustomers, this.nVehicles, nCustomers); /* Establish fixed of number of vehicles until new version of instances */ 
			
			return Stream.of(problem); 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null; 
		} catch (IOException e) {
			e.printStackTrace();
			return null; 
		}
	}
	
	public void setNVehicles(int nVehicles) {
		this.nVehicles =  nVehicles; 
	}

}
