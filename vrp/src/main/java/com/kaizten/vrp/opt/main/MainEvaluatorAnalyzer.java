package com.kaizten.vrp.opt.main;

import com.kaizten.opt.evaluator.EvaluatorAnalyzer;
import com.kaizten.opt.builder.evaluator.EvaluatorBuilder;
import com.kaizten.opt.evaluator.Evaluator;
import com.kaizten.opt.solution.RoutesSolution;
import com.kaizten.opt.solution.Solution;
import com.kaizten.vpr.opt.io.VrpSolutionSupplier;
import com.kaizten.vpr.opt.io.VrpSupplier;
import com.kaizten.vrp.opt.core.Vrp;
import com.kaizten.vrp.opt.evaluators.EvaluatorObjectiveFunctionDistances;
import com.kaizten.vrp.opt.evaluators.VehicleRoutingProblemEvaluatorDistanceCplex;
import com.kaizten.vrp.opt.solver.RandomizedBuilder;

import ilog.concert.IloException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class MainEvaluatorAnalyzer {
    
    public static final String INSTANCE_FILE = "D:\\Documentos\\Repositorios\\TFG\\vrp\\instances\\C101_100.txt";
    public static final String SOLUTION_FILE = "D:\\Documentos\\Repositorios\\TFG\\vrp\\solutions\\Solution_C101_100.txt";
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(String[] args) throws IloException {
    	String fileProblem =  args[0];
    	Integer nSolutions =  Integer.parseInt(args[1]); 
        System.out.println("====================================================");
        System.out.println("Reading Vehicle Routing Problem:");
        System.out.println("====================================================");
        Vrp optimizationProblem = new VrpSupplier().get(new File(fileProblem))
                .findFirst()
                .get();
        System.out.println(optimizationProblem);
        System.out.println("====================================================");
        System.out.println("Creating Evaluator CPLEX:");
        System.out.println("====================================================");
        Evaluator<RoutesSolution<Vrp>> evaluatorCplex = EvaluatorBuilder.instance()
                .addEvaluatorObjectiveFunction(VehicleRoutingProblemEvaluatorDistanceCplex.class)
                .build();
        optimizationProblem.setEvaluator(evaluatorCplex);
        System.out.println(evaluatorCplex);
        System.out.println("====================================================");
        System.out.println("Creating Evaluator:");
        System.out.println("====================================================");
        Evaluator<RoutesSolution<Vrp>> evaluator = EvaluatorBuilder.instance()
                .addEvaluatorObjectiveFunction(EvaluatorObjectiveFunctionDistances.class)
                .build();
        optimizationProblem.setEvaluator(evaluator);
        System.out.println(evaluator);
        System.out.println("====================================================");
        System.out.println("Creating Solution:");
        System.out.println("====================================================");
        List<RoutesSolution<Vrp>> solutions = new  ArrayList<RoutesSolution<Vrp>>();
        for(int i = 0; i < nSolutions; i++) {
        	solutions.add(new RandomizedBuilder(optimizationProblem).run());
        }
         
        Stream<RoutesSolution<Vrp>> solutionAux =  Stream.of(solutions.get(0));
        
        /*RoutesSolution<Vrp> solution = new VrpSolutionSupplier()
                .get(optimizationProblem, new File(SOLUTION_FILE))
                .findFirst()
                .get();
        System.out.println(solution);*/
        System.out.println("====================================================");
        System.out.println("Analyzing Solutions:");
        System.out.println("====================================================");
        EvaluatorAnalyzer<Vrp> analyzer = new EvaluatorAnalyzer();
        analyzer.setOptimizationProblem(optimizationProblem);
        analyzer.addEvaluator(evaluator);
        analyzer.addEvaluator(evaluatorCplex);
        //analyzer.setSolutions(Stream.of(solution));
        //analyzer.setSolutions(solutions.stream());
        //analyzer.setSolutions(solutionAux);
        //analyzer.run();
        for(int i = 0; i < nSolutions; i++) {
        	analyzer.setSolutions(Stream.of(solutions.get(i)));
        	analyzer.run();
        }
    }
}