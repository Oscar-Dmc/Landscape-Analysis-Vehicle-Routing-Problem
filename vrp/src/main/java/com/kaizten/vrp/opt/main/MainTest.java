package com.kaizten.vrp.opt.main;

import java.io.File;

import com.kaizten.opt.move.Move;
import com.kaizten.opt.move.MoveRoutesSolutionInsertionAfter;
import com.kaizten.opt.move.MoveRoutesSolutionInsertionBefore;
import com.kaizten.opt.move.MoveRoutesSolutionMoveAfter;
import com.kaizten.opt.move.MoveRoutesSolutionMoveBefore;
import com.kaizten.opt.move.MoveRoutesSolutionRemove;
import com.kaizten.opt.move.MoveRoutesSolutionSwap;
import com.kaizten.opt.move.applier.Applier;
import com.kaizten.opt.move.applier.MoveApplier;
import com.kaizten.opt.move.applier.MoveApplierRoutesSolutionInsertionAfter;
import com.kaizten.opt.move.applier.MoveApplierRoutesSolutionInsertionBefore;
import com.kaizten.opt.move.applier.MoveApplierRoutesSolutionRemove;
import com.kaizten.opt.move.generator.MoveGeneratorRoutesSolutionInsertionAfter;
import com.kaizten.opt.move.generator.MoveGeneratorRoutesSolutionInsertionBefore;
import com.kaizten.opt.move.generator.MoveGeneratorRoutesSolutionMoveAfter;
import com.kaizten.opt.move.generator.MoveGeneratorRoutesSolutionMoveBefore;
import com.kaizten.opt.move.generator.MoveGeneratorRoutesSolutionSwap;
import com.kaizten.opt.move.manager.MoveManagerSequential;
import com.kaizten.opt.solution.RoutesSolution;
import com.kaizten.vrp.opt.core.Vrp;
import com.kaizten.vrp.opt.core.VrpSupplier;
import com.kaizten.vrp.opt.move.applier.MoveApplierRoutesSolutionMoveAfter;
import com.kaizten.vrp.opt.move.applier.MoveApplierRoutesSolutionMoveBefore;
import com.kaizten.vrp.opt.move.applier.MoveApplierRoutesSolutionSwap;
import com.kaizten.vrp.opt.move.generator.MoveGeneratorRoutesSolutionRemove;
import com.kaizten.vrp.opt.solver.RandomizedBuilder;

public class MainTest {
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String[] args) {
		File file =  new File(args[0]);
		VrpSupplier vrpSupplier = new VrpSupplier();
		vrpSupplier.setNVehicles(Integer.parseInt(args[1]));
		Vrp problem = vrpSupplier.get(file).findFirst().get();
	
		RoutesSolution<Vrp> solution = new RandomizedBuilder(problem).run();
		/* Chivato de la matriz de distancia */ 
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
		}
		System.out.println(solution);
		
		RoutesSolution<Vrp> solutionIncomplete = solution.clone();
		solutionIncomplete.remove(4);
		solutionIncomplete.remove(7);
		solutionIncomplete.remove(13);
		solutionIncomplete.remove(19);
		solutionIncomplete.remove(17);
		solutionIncomplete.evaluate();
		System.out.println(solutionIncomplete);
		
		MoveManagerSequential<RoutesSolution<Vrp>, ?> MMSequential = new MoveManagerSequential();
		Applier<RoutesSolution<Vrp>> GApplier = new Applier<RoutesSolution<Vrp>>();
		
		MoveGeneratorRoutesSolutionMoveBefore<RoutesSolution<Vrp>, MoveRoutesSolutionMoveBefore> MGMoveBefore = new MoveGeneratorRoutesSolutionMoveBefore<RoutesSolution<Vrp>, MoveRoutesSolutionMoveBefore>();
		MoveGeneratorRoutesSolutionMoveAfter<RoutesSolution<Vrp>, MoveRoutesSolutionMoveAfter> MGMoveAfter = new MoveGeneratorRoutesSolutionMoveAfter<RoutesSolution<Vrp>, MoveRoutesSolutionMoveAfter>();
		MoveGeneratorRoutesSolutionSwap<RoutesSolution<Vrp>, MoveRoutesSolutionSwap> MGSwap = new MoveGeneratorRoutesSolutionSwap<RoutesSolution<Vrp>, MoveRoutesSolutionSwap>();
		MoveGeneratorRoutesSolutionRemove<RoutesSolution<Vrp>, MoveRoutesSolutionRemove> MGRemove =  new MoveGeneratorRoutesSolutionRemove<RoutesSolution<Vrp>, MoveRoutesSolutionRemove>();
		MoveGeneratorRoutesSolutionInsertionAfter<RoutesSolution<Vrp>, MoveRoutesSolutionInsertionAfter> MGInsertionAfter = new MoveGeneratorRoutesSolutionInsertionAfter<RoutesSolution<Vrp>, MoveRoutesSolutionInsertionAfter>();
		MoveGeneratorRoutesSolutionInsertionBefore<RoutesSolution<Vrp>, MoveRoutesSolutionInsertionBefore> MGInsertionBefore = new MoveGeneratorRoutesSolutionInsertionBefore<RoutesSolution<Vrp>, MoveRoutesSolutionInsertionBefore>();
		
		MoveApplier applierMoveBefore =  new MoveApplierRoutesSolutionMoveBefore();
		MoveApplier applierMoveAfter =  new MoveApplierRoutesSolutionMoveAfter();
		MoveApplier applierSwap =  new MoveApplierRoutesSolutionSwap();
		MoveApplier applierRemove = new MoveApplierRoutesSolutionRemove();
		MoveApplier applierInsertionAfter =  new MoveApplierRoutesSolutionInsertionAfter();
		MoveApplier applierInsertionBefore = new MoveApplierRoutesSolutionInsertionBefore();
		GApplier.addMoveApplier(applierMoveBefore);
		GApplier.addMoveApplier(applierMoveAfter);
		GApplier.addMoveApplier(applierSwap);
		GApplier.addMoveApplier(applierRemove);
		GApplier.addMoveApplier(applierInsertionAfter);
		GApplier.addMoveApplier(applierInsertionBefore);
		
		//MMSequential.setSolution(solution);
		MMSequential.setSolution(solutionIncomplete);
		
		//MMSequential.addMoveGenerator(MGMoveBefore);
		//MMSequential.addMoveGenerator(MGMoveAfter);
		//MMSequential.addMoveGenerator(MGSwap);
		//MMSequential.addMoveGenerator(MGRemove);
		//MMSequential.addMoveGenerator(MGInsertionAfter);
		MMSequential.addMoveGenerator(MGInsertionBefore);
		
		MMSequential.init();
		
		//GApplier.setSolution(solution);
		GApplier.setSolution(solutionIncomplete);
		
		while(MMSequential.hasNext()) {	
			Move move =  MMSequential.next(); 
			System.out.println(move);
			GApplier.setMove(move);
			GApplier.apply();
			/*if(move.getDeviationObjectiveFunctionValue(0) < 0) {
				GApplier.setMove(move);
				GApplier.apply();
				//MMSequential.removeMoveGenerator(MGMoveAfter);
				//MMSequential.removeMoveGenerator(MGMoveBefore);
				//MMSequential.removeMoveGenerator(MGSwap);
				//MMSequential.removeMoveGenerator(MGRemove);
				System.out.println("Antes del evaluate: " + solution.getObjectiveFunctionValue(0));
				solution.evaluate();
				System.out.println("Después del evaluate: " + solution.getObjectiveFunctionValue(0));
				//MMSequential.setSolution(solution);
				//MMSequential.addMoveGenerator(MGMoveAfter);
				//MMSequential.addMoveGenerator(MGMoveBefore);
				//MMSequential.addMoveGenerator(MGSwap);
				//MMSequential.addMoveGenerator(MGRemove);
				//MGMoveAfter.init();
				//MGMoveBefore.init();
				//MGSwap.init();
				//MGRemove.init();
			}
			//System.out.println(MMSequential.next());*/
		}
		
		System.out.println(solution);
		System.out.println("Se han generado " + MMSequential.getNumberOfGeneratedMovements() + " movimientos ");
	}

}
