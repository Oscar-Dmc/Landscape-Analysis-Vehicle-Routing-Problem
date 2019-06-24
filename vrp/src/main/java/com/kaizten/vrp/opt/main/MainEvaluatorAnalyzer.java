package com.kaizten.vrp.opt.main;

import com.kaizten.opt.evaluator.EvaluatorAnalyzer;
import com.kaizten.opt.move.MoveRoutesSolutionMoveAfter;
import com.kaizten.opt.move.MoveRoutesSolutionMoveBefore;
import com.kaizten.opt.move.MoveRoutesSolutionSwap;
import com.kaizten.opt.move.applier.Applier;
import com.kaizten.opt.move.applier.MoveApplier;
import com.kaizten.opt.move.generator.MoveGeneratorRoutesSolutionMoveAfter;
import com.kaizten.opt.move.generator.MoveGeneratorRoutesSolutionMoveBefore;
import com.kaizten.opt.move.generator.MoveGeneratorRoutesSolutionSwap;
import com.kaizten.opt.move.manager.MoveManagerSequential;
import com.kaizten.opt.builder.evaluator.EvaluatorBuilder;
import com.kaizten.opt.evaluator.Evaluator;
import com.kaizten.opt.solution.RoutesSolution;
import com.kaizten.opt.solution.Solution;
import com.kaizten.vrp.opt.core.Vrp;
import com.kaizten.vrp.opt.evaluators.EvaluatorMoveAfter;
import com.kaizten.vrp.opt.evaluators.EvaluatorMoveBefore;
import com.kaizten.vrp.opt.evaluators.EvaluatorMoveSwap;
import com.kaizten.vrp.opt.evaluators.EvaluatorObjectiveFunctionDistances;
import com.kaizten.vrp.opt.evaluators.VehicleRoutingProblemEvaluatorDistanceCplex;
import com.kaizten.vrp.opt.io.VrpSupplier;
import com.kaizten.vrp.opt.move.applier.MoveApplierRoutesSolutionMoveAfter;
import com.kaizten.vrp.opt.move.applier.MoveApplierRoutesSolutionMoveBefore;
import com.kaizten.vrp.opt.move.applier.MoveApplierRoutesSolutionSwap;
import com.kaizten.vrp.opt.solver.RandomizedRCLBuilder;

import ilog.concert.IloException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class MainEvaluatorAnalyzer {
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(String[] args) throws IloException {
    	String fileProblem =  args[0];
    	Integer nSolutions =  Integer.parseInt(args[1]); 
        System.out.println("====================================================");
        System.out.println("Reading Vehicle Routing Problem:");
        System.out.println("====================================================");
        VrpSupplier supplierProblem =  new VrpSupplier();
        supplierProblem.setNVehicles(10);
        Vrp optimizationProblem = supplierProblem.get(new File(fileProblem))
                .findFirst()
                .get();
        optimizationProblem.setNMaxCustomers(100);
        System.out.println(optimizationProblem);
        System.out.println("====================================================");
        System.out.println("Creating Evaluator CPLEX:");
        System.out.println("====================================================");
        Evaluator<RoutesSolution<Vrp>> evaluatorCplex = EvaluatorBuilder.instance()
                .addEvaluatorObjectiveFunction(VehicleRoutingProblemEvaluatorDistanceCplex.class)
                .addEvaluatorObjectiveFunctionMovement(new EvaluatorMoveSwap(), 0)
                .addEvaluatorObjectiveFunctionMovement(new EvaluatorMoveAfter(), 0)
                .addEvaluatorObjectiveFunctionMovement(new EvaluatorMoveBefore(), 0)
                .build();
        optimizationProblem.setEvaluator(evaluatorCplex);
        System.out.println(evaluatorCplex);
        System.out.println("====================================================");
        System.out.println("Creating Evaluator:");
        System.out.println("====================================================");
        Evaluator<RoutesSolution<Vrp>> evaluator = EvaluatorBuilder.instance()
                .addEvaluatorObjectiveFunction(EvaluatorObjectiveFunctionDistances.class)
                .addEvaluatorObjectiveFunctionMovement(new EvaluatorMoveSwap(), 0)
                .addEvaluatorObjectiveFunctionMovement(new EvaluatorMoveAfter(), 0)
                .addEvaluatorObjectiveFunctionMovement(new EvaluatorMoveBefore(), 0)
                .build();
        optimizationProblem.setEvaluator(evaluator);
        System.out.println(evaluator);
        System.out.println("====================================================");
        System.out.println("Creating Solution:");
        System.out.println("====================================================");
        List<Solution<Vrp>> solutions = new  ArrayList<>();
        for(int i = 0; i < nSolutions; i++) {
        	solutions.add(new RandomizedRCLBuilder(optimizationProblem).run());
        }
         
        System.out.println("====================================================");
        System.out.println("Analyzing Solutions:");
        System.out.println("====================================================");
        EvaluatorAnalyzer<Vrp> analyzer = new EvaluatorAnalyzer();
        analyzer.setOptimizationProblem(optimizationProblem);
        analyzer.addEvaluator(evaluator);
        analyzer.addEvaluator(evaluatorCplex);
        analyzer.setSolutions(solutions.stream());
        
        /* Compare only firsts four decimals */ 
        BiFunction<double[], double[], Boolean> comparator = (objValue1, objValue2) -> {
        	int value1 = (int) (objValue1[0] * 10000);
        	int value2 = (int) (objValue2[0] * 10000);
        	return (value1 == value2); 
        };
        analyzer.setComparator(comparator);
        
        /* Compare movements */ 
        
        MoveManagerSequential manager = new MoveManagerSequential();
        manager.addMoveGenerator(new MoveGeneratorRoutesSolutionSwap<RoutesSolution<Vrp>, MoveRoutesSolutionSwap>());
		manager.addMoveGenerator(new MoveGeneratorRoutesSolutionMoveAfter<RoutesSolution<Vrp>, MoveRoutesSolutionMoveAfter>());
		manager.addMoveGenerator(new MoveGeneratorRoutesSolutionMoveBefore<RoutesSolution<Vrp>, MoveRoutesSolutionMoveBefore>());
		
		Applier applier =  new Applier<RoutesSolution<Vrp>>();
		MoveApplier applierSwap =  new MoveApplierRoutesSolutionSwap();
		MoveApplier applierMoveAfter = new MoveApplierRoutesSolutionMoveAfter();
		MoveApplier applierMoveBefore = new MoveApplierRoutesSolutionMoveBefore();
		applier.addMoveApplier(applierSwap);
		applier.addMoveApplier(applierMoveAfter);
		applier.addMoveApplier(applierMoveBefore);
		
		analyzer.setMoveManager(manager);
		analyzer.setApplier(applier);
        
        analyzer.run();
    }
}