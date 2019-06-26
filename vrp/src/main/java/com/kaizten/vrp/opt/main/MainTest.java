package com.kaizten.vrp.opt.main;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.kaizten.opt.move.Move;
import com.kaizten.opt.move.MoveRoutesSolutionInsertionAfter;
import com.kaizten.opt.move.MoveRoutesSolutionInsertionBefore;
import com.kaizten.opt.move.MoveRoutesSolutionMoveAfter;
import com.kaizten.opt.move.MoveRoutesSolutionMoveBefore;
import com.kaizten.opt.move.MoveRoutesSolutionRemove;
import com.kaizten.opt.move.MoveRoutesSolutionSwap;
import com.kaizten.opt.move.applier.MoveApplier;
import com.kaizten.opt.move.applier.MoveApplierRoutesSolutionInsertionAfter;
import com.kaizten.opt.move.applier.MoveApplierRoutesSolutionInsertionBefore;
import com.kaizten.opt.move.applier.MoveApplierRoutesSolutionRemove;
import com.kaizten.opt.move.applier.manager.MoveApplierManager;
import com.kaizten.opt.move.generator.MoveGeneratorRoutesSolutionInsertionAfter;
import com.kaizten.opt.move.generator.MoveGeneratorRoutesSolutionInsertionBefore;
import com.kaizten.opt.move.generator.MoveGeneratorRoutesSolutionMoveAfter;
import com.kaizten.opt.move.generator.MoveGeneratorRoutesSolutionMoveBefore;
import com.kaizten.opt.move.generator.MoveGeneratorRoutesSolutionSwap;
import com.kaizten.opt.move.generator.manager.SequentialMoveGeneratorManager;
import com.kaizten.opt.solution.RoutesSolution;
import com.kaizten.vrp.opt.core.Vrp;
import com.kaizten.vrp.opt.io.VrpSolutionCustomer;
import com.kaizten.vrp.opt.io.VrpSolutionSupplier;
import com.kaizten.vrp.opt.io.VrpSupplier;
import com.kaizten.vrp.opt.landscape.Amp;
import com.kaizten.vrp.opt.landscape.AutocorrelationFunction;
import com.kaizten.vrp.opt.landscape.DistanceMedium;
import com.kaizten.vrp.opt.landscape.Entropy;
import com.kaizten.vrp.opt.landscape.FitnessDistanceCorrelation;
import com.kaizten.vrp.opt.landscape.Lmm;
import com.kaizten.vrp.opt.move.applier.MoveApplierRoutesSolutionMoveAfter;
import com.kaizten.vrp.opt.move.applier.MoveApplierRoutesSolutionMoveBefore;
import com.kaizten.vrp.opt.move.applier.MoveApplierRoutesSolutionSwap;
import com.kaizten.vrp.opt.move.generator.MoveGeneratorRoutesSolutionRemove;
import com.kaizten.vrp.opt.solver.RandomizedBuilder;
import com.kaizten.vrp.opt.solver.RandomizedRCLBuilder;

public class MainTest {
	
	public int[] getIndexElement(ArrayList<ArrayList<Integer>> solution, Integer element) {
		int[] indexs = {-1, -1};
		for(int i = 0; i < solution.size();  i++) {
			if(solution.get(i).indexOf(element) != -1) {
				indexs[0] = i;
				indexs[1] = solution.get(i).indexOf(element);
				return indexs; 
			}
		}
		return indexs; 
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String[] args) {
		MainTest tester = new MainTest();
		//String routeFileIn = args[0];
		File file =  new File(args[0]);
		//File file = new File(routeFileIn);
		VrpSupplier vrpSupplier = new VrpSupplier();
		vrpSupplier.setNVehicles(Integer.parseInt(args[1]));
		Vrp problem = vrpSupplier.get(file).findFirst().get();
		//VrpSolutionCustomer vrpSolutionCustomer = new VrpSolutionCustomer();
		problem.setNMaxCustomers(10);
		RoutesSolution<Vrp> solution = new RandomizedBuilder(problem).run();
		/* Chivato de la matriz de distancia  
		for(int j = 0;  j < solution.getOptimizationProblem().getDistanceMatrix()[0].length; j++) {
			System.out.print("\t|" + (j -1) + "|");
		}
		System.out.println();
		for(int i = 0;  i < solution.getOptimizationProblem().getDistanceMatrix()[0].length; i++) {
			System.out.print("|" + (i-1) + "|");
			for(int j = 0;  j < solution.getOptimizationProblem().getDistanceMatrix()[i].length; j++) {
				System.out.print("\t" + solution.getOptimizationProblem().getDistanceMatrix()[i][j]);
			}
			System.out.println();
		}*/
		System.out.println("Solución inicial\n" + solution);
		/*String nameFileSolution = "D:\\Documentos\\Repositorios\\TFG\\vrp\\solutions\\Solution_" + file.getName();
		File fileSolution = new File(nameFileSolution);
		vrpSolutionCustomer.accept(solution, fileSolution);
		
		VrpSolutionSupplier vrpSolutionSupplier =  new VrpSolutionSupplier();
		File fileSS = new File("D:\\Documentos\\Repositorios\\TFG\\vrp\\solutions\\Solution_C101_100.txt");
		RoutesSolution<Vrp> solutionIo = vrpSolutionSupplier.get(problem, fileSS).findFirst().get();
		System.out.println(solutionIo.toString()); */
		
		RoutesSolution<Vrp> solutionIncomplete = solution.clone();
		Random rand = new Random();
		for(int i = 0; i < 5;  i++) {
			int customerToRemove = rand.nextInt(solutionIncomplete.size());
			solutionIncomplete.remove(customerToRemove);
		}
		System.out.println("Solution incomplete\n" + solutionIncomplete);
		
		SequentialMoveGeneratorManager<RoutesSolution<Vrp>, ?> MMSequential = new SequentialMoveGeneratorManager();
		MoveApplierManager<RoutesSolution<Vrp>> GApplier = new MoveApplierManager<RoutesSolution<Vrp>>();
		
		//MoveGeneratorRoutesSolutionMoveBefore<RoutesSolution<Vrp>, MoveRoutesSolutionMoveBefore> MGMoveBefore = new MoveGeneratorRoutesSolutionMoveBefore<RoutesSolution<Vrp>, MoveRoutesSolutionMoveBefore>();
		//MoveGeneratorRoutesSolutionMoveAfter<RoutesSolution<Vrp>, MoveRoutesSolutionMoveAfter> MGMoveAfter = new MoveGeneratorRoutesSolutionMoveAfter<RoutesSolution<Vrp>, MoveRoutesSolutionMoveAfter>();
		MoveGeneratorRoutesSolutionSwap<RoutesSolution<Vrp>, MoveRoutesSolutionSwap> MGSwap = new MoveGeneratorRoutesSolutionSwap<RoutesSolution<Vrp>, MoveRoutesSolutionSwap>();
		//MoveGeneratorRoutesSolutionRemove<RoutesSolution<Vrp>, MoveRoutesSolutionRemove> MGRemove =  new MoveGeneratorRoutesSolutionRemove<RoutesSolution<Vrp>, MoveRoutesSolutionRemove>();
		//MoveGeneratorRoutesSolutionInsertionAfter<RoutesSolution<Vrp>, MoveRoutesSolutionInsertionAfter> MGInsertionAfter = new MoveGeneratorRoutesSolutionInsertionAfter<RoutesSolution<Vrp>, MoveRoutesSolutionInsertionAfter>();
		//MoveGeneratorRoutesSolutionInsertionBefore<RoutesSolution<Vrp>, MoveRoutesSolutionInsertionBefore> MGInsertionBefore = new MoveGeneratorRoutesSolutionInsertionBefore<RoutesSolution<Vrp>, MoveRoutesSolutionInsertionBefore>();
		
		//MoveApplier applierMoveBefore =  new MoveApplierRoutesSolutionMoveBefore();
		//MoveApplier applierMoveAfter =  new MoveApplierRoutesSolutionMoveAfter();
		MoveApplier applierSwap =  new MoveApplierRoutesSolutionSwap();
		//MoveApplier applierRemove = new MoveApplierRoutesSolutionRemove();
		//MoveApplier applierInsertionAfter =  new MoveApplierRoutesSolutionInsertionAfter();
		//MoveApplier applierInsertionBefore = new MoveApplierRoutesSolutionInsertionBefore();
		//GApplier.addMoveApplier(applierMoveBefore);
		//GApplier.addMoveApplier(applierMoveAfter);
		GApplier.addMoveApplier(applierSwap);
		//GApplier.addMoveApplier(applierRemove);
		//GApplier.addMoveApplier(applierInsertionAfter);
		//GApplier.addMoveApplier(applierInsertionBefore);
		
		MMSequential.setSolution(solution);
		//MMSequential.setSolution(solutionIncomplete);
		
		//MMSequential.addMoveGenerator(MGMoveBefore);
		//MMSequential.addMoveGenerator(MGMoveAfter);
		MMSequential.addMoveGenerator(MGSwap);
		//MMSequential.addMoveGenerator(MGRemove);
		//MMSequential.addMoveGenerator(MGInsertionAfter);
		//MMSequential.addMoveGenerator(MGInsertionBefore);
		
		MMSequential.init();
		
		GApplier.setSolution(solution);
		//GApplier.setSolution(solutionIncomplete);
		RoutesSolution<Vrp> aux = solution.clone();
		List<RoutesSolution<Vrp>> landscape = new ArrayList<RoutesSolution<Vrp>>();
		for(int i = 0; i < 25; i++) {
			GApplier.setSolution(aux);
			Move move =  MMSequential.next(); 
			GApplier.setMove(move);
			GApplier.apply();
			landscape.add(aux.clone());
			//System.out.println("---------------------------------------------------------" );
		}

		/*while(MMSequential.hasNext()) {	
			RoutesSolution<Vrp> aux = solution.clone();
			GApplier.setSolution(aux);
			Move move =  MMSequential.next(); 
			System.out.println(move.getTotalDeviation());
			GApplier.setMove(move);
			GApplier.apply();
			System.out.println(aux);
			/*if(move.getDeviationObjectiveFunctionValue(0) < 0) {
				GApplier.setMove(move);
				GApplier.apply();
				//MMSequential.removeMoveGenerator(MGMoveBefore);
				MMSequential.removeMoveGenerator(MGMoveAfter);
				//MMSequential.removeMoveGenerator(MGSwap);
				//MMSequential.removeMoveGenerator(MGRemove);
				System.out.println("Antes del evaluate: " + solution.getObjectiveFunctionValue(0));
				solution.evaluate();
				System.out.println("Después del evaluate: " + solution.getObjectiveFunctionValue(0));
				MMSequential.setSolution(solution);
				//MMSequential.addMoveGenerator(MGMoveBefore);
				MMSequential.addMoveGenerator(MGMoveAfter);
				//MMSequential.addMoveGenerator(MGSwap);
				//MMSequential.addMoveGenerator(MGRemove);
				MGMoveAfter.init();
				//MGMoveBefore.init();
				//MGSwap.init();
				//MGRemove.init();
			}
			//System.out.println(MMSequential.next());
		}
		/*MMSequential.init();
		System.out.println(MMSequential.hasNext());
		System.out.println(MMSequential.next());
		System.out.println(solution);
		System.out.println("Se han generado " + MMSequential.getNumberOfGeneratedMovements() + " movimientos ");*/
		
		/* Prueba de las métricas */ 
		DistanceMedium dmm = new DistanceMedium();
		dmm.setSolutions(landscape.stream(), 0);
		dmm.compute();
		
		Amp amp = new Amp();
		amp.setSolutions(landscape.stream());
		amp.compute();
		
		Lmm lmm = new Lmm();
		lmm.setSolutions(landscape.stream());
		lmm.compute();
		
		AutocorrelationFunction  aCor = new AutocorrelationFunction();
		aCor.setSolutions(landscape.stream());
		aCor.compute();
		
		FitnessDistanceCorrelation fdc = new FitnessDistanceCorrelation();
		fdc.setSolutions(landscape.stream(), 0);
		fdc.compute();
		
		Entropy entropy = new Entropy();
		entropy.setSolutions(landscape.stream());
		entropy.compute();
		
	}

}
