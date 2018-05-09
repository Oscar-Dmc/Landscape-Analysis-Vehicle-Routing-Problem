package com.kaizten.vrp.opt.core;

import java.util.ArrayList;

public class Gvsn {

	private ArrayList<Vehicle> initialSolution;
	private ArrayList<ArrayList<Vehicle>> localSolutions;
	
	public Gvsn(ArrayList<Vehicle> solution) {
		this.initialSolution = new ArrayList<Vehicle>(solution); 
		this.localSolutions = new ArrayList<ArrayList<Vehicle>>();
	}
	
	// Private? 
	public ArrayList<Vehicle> SwapInRoute(ArrayList<Vehicle> solution) {
		ArrayList<Vehicle> local = new ArrayList<Vehicle>(solution);
		for(int i = 0; i < local.size(); i++) {
			if(local.get(i).getRoute().size() < 2) {
				
			}
			else if(local.get(i).getRoute().size() == 2) {
				Node aux =  new Node(local.get(i).getRoute().get(0));
				local.get(0).getRoute().set(0, new Node(local.get(1).getRoute().get(1)));
				local.get(0).getRoute().set(1, aux);
			}
			else {
				int indexA = (int)(Math.random() * local.get(i).getRoute().size());
				int indexB = (int)(Math.random() * local.get(i).getRoute().size());
				while(indexA == indexB) {
					indexB = (int)(Math.random() * local.get(i).getRoute().size());
				}
				Node aux =  new Node(local.get(i).getRoute().get(indexA));
				local.get(i).getRoute().set(indexA, new Node(local.get(i).getRoute().get(indexB)));
				local.get(i).getRoute().set(indexB, aux);
			}
		}
		
		return local; 
	}
	
	//Borrar solo para las pruebas. 
	
	public void Pruebas() {
		ArrayList<Vehicle> solucionLocal =  new ArrayList<Vehicle>(SwapInRoute(this.initialSolution));
		System.out.println("\nSolucion inicial");
		System.out.println("Las rutas de los vehículos son: ");
		for(int i = 0; i < this.initialSolution.size(); i++) {
			System.out.println("\nEl vehículo " + (i+1) + " :");
			System.out.print("\t DP -> ");
			for(int j = 0; j < this.initialSolution.get(i).getRoute().size(); j++){
				System.out.print(this.initialSolution.get(i).getRoute().get(j).getId() + " -> ");
			}
			System.out.print(" DP");
		}
		
		System.out.println("\nSolucion Local");
		System.out.println("Las rutas de los vehículos son: ");
		for(int i = 0; i < solucionLocal.size(); i++) {
			System.out.println("\nEl vehículo " + (i+1) + " :");
			System.out.print("\t DP -> ");
			for(int j = 0; j < solucionLocal.get(i).getRoute().size(); j++){
				System.out.print(solucionLocal.get(i).getRoute().get(j).getId() + " -> ");
			}
			System.out.print(" DP");
		}

		
	}
}
