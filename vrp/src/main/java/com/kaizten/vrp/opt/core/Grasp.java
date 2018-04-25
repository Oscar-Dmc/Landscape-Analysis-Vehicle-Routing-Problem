package com.kaizten.vrp.opt.core;
import java.util.ArrayList;

public class Grasp {
	private ArrayList<Node> cl; 
	private ArrayList<Node> rcl;
	private int sizeRcl;
	private double tct; /* Valor de la solucion */
	private ArrayList<Vehicle> solution;
	private Vrp initialVrp; 
	
	public Grasp(int sizeRcl, Vrp vrp){
		this.sizeRcl = sizeRcl;
		this.cl = new ArrayList<Node>();
		this.rcl = new ArrayList<Node>();
		this.solution = new ArrayList<Vehicle>();
		this.initialVrp = vrp;
		for(int i = 1; i < vrp.getCustomers().size(); i++){
			cl.add(vrp.getCustomers().get(i));
		}
	}
	
	private void MakeRCL(int idVehicle){
		/* La solucion esta vacia, por lo tanto aceptamos a los clientes con menor distancia desde el deposito sin ser visitados aun */
		if(this.solution.isEmpty()){
			while(this.rcl.size() != this.sizeRcl){
				double minDistance = 99999.99;
				int indCustomer = 0;
				for (int i = 0; i < this.cl.size(); i++){
					if(this.initialVrp.getDistanceMatrix()[0][this.cl.get(i).getIndex()] > minDistance){
						indCustomer = i;
						minDistance = this.initialVrp.getDistanceMatrix()[0][this.cl.get(i).getIndex()];
					}
				}
				this.rcl.add(this.cl.get(indCustomer));
				this.cl.remove(indCustomer);
			}
		}
		else{ /* Ya tenemos a minimo un vehiculo satisfaciendo clientes */
			while(this.rcl.size() != this.sizeRcl){
				double minDistance = 99999.99;
				int idCustomer = 0;
				for (int i = 0;  i < this.cl.size(); i++){
					int lastCustomer = this.solution.get(idVehicle).getLastCustomerSatisfied().getIndex();
					if (this.initialVrp.getDistanceMatrix()[lastCustomer][i] > minDistance){
						idCustomer = i;
						minDistance  = this.initialVrp.getDistanceMatrix()[lastCustomer][i];
					}
				}
				this.rcl.add(this.cl.get(idCustomer));
				this.cl.remove(idCustomer);
			}
		}
	}
	
	private Node SelectElementAtRandom(){
		int randomIndex =  (int)(Math.random() * this.sizeRcl);
		Node randomCustomer = this.rcl.get(randomIndex);
		this.rcl.remove(randomIndex);
		return randomCustomer;
	}
	
	private ArrayList<Vehicle> ConstructGreedyRandomizedSolution(){
		ArrayList<Vehicle> solution = new ArrayList<Vehicle>();
		int indVehicle = 0;
		Vehicle currentVehicle = this.initialVrp.getVehicles().get(indVehicle);
		
		while (!this.initialVrp.AllCustomersSatisfied()){
			MakeRCL(indVehicle);
			Node customer = SelectElementAtRandom();
			if(solution.isEmpty()){
				currentVehicle.addCustomerToRoute(customer);
				this.initialVrp.getCustomers().get(customer.getIndex()).setSatisfied(true);
			}
			else {
				if(currentVehicle.getMaxCustomers() == currentVehicle.getRoute().size()){
					solution.add(currentVehicle);
					indVehicle++; /* ¿Falta contemplar que no se puedan satisfacer todos con esas condiciones? */ 
					currentVehicle =  this.initialVrp.getVehicles().get(indVehicle);
					
				}
				currentVehicle.addCustomerToRoute(customer);
				this.initialVrp.getCustomers().get(customer.getIndex()).setSatisfied(true);
			}
			
		}
		
		solution.add(currentVehicle);
		return solution;
	} 
	
	private void ClearList(){
		this.rcl.clear();
		this.cl.clear();
		this.tct = 0.0;
		for(int i = 1; i < this.initialVrp.getCustomers().size(); i++){
			this.initialVrp.getCustomers().get(i).setSatisfied(false);
			cl.add(this.initialVrp.getCustomers().get(i));
		}
	}
	
	public void ProcedureGrasp(int nMaxIter){
		ArrayList<Vehicle> bestSolution =  new ArrayList<Vehicle>();
		double BestTct = 99999999.99;
		for (int i = 0;  i < nMaxIter;  i++){
			ArrayList<Vehicle> currentSolution = ConstructGreedyRandomizedSolution();
			double currentTct = getTCT(currentSolution);
			
		}
		
	}
	
	private double getTCT(ArrayList<Vehicle> solution){
		double tct = 0;
		for(int i = 0;  i < solution.size(); i++){
			tct += solution.get(i).getTCT();
		}
		return tct;
	}
	
	public ArrayList<Vehicle> getSolution(){
		return solution;
	}
	
	public double getTct(){
		return tct;
	}
}
