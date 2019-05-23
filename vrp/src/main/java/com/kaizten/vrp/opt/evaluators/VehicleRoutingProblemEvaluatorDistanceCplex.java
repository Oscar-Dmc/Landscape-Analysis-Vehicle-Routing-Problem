package com.kaizten.vrp.opt.evaluators;

import com.kaizten.opt.evaluator.EvaluatorSingleObjectiveFunction;
import com.kaizten.opt.solution.RoutesSolution;
import com.kaizten.vrp.opt.core.Vrp;
import com.kaizten.vrp.opt.solver.OptimizationModel;
import ilog.concert.IloException;
import ilog.cplex.IloCplex;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Christopher Expósito-Izquierdo (christopherexpositoizquierdo@gmail.com)
 */
public class VehicleRoutingProblemEvaluatorDistanceCplex extends EvaluatorSingleObjectiveFunction<RoutesSolution<Vrp>> {

    @Override
    public void evaluate(RoutesSolution<Vrp> solution) {
        Vrp optimizationProblem = solution.getOptimizationProblem();
        super.objectiveFunctionValue[0] = 0.0;
        IloCplex cplex;
        try {
            cplex = new IloCplex();
            cplex.setOut(null);
            OptimizationModel solver = new OptimizationModel(optimizationProblem, cplex);
            //System.out.print("Setting solution.................................... ");
            solver.setInitialSolution(solution);
            //System.out.println("Done");
            //System.out.print("Creating decision variables......................... ");
            solver.createVariables();
            //System.out.println("Done");
            solver.initializeVariables();
            //System.out.print("Creating constraints 1.............................. ");
            solver.createConstraints1();
            //System.out.println("Done");
            //System.out.print("Creating constraints 2.............................. ");
            solver.createConstraints2();
            //System.out.println("Done");
            //System.out.print("Creating constraints 3.............................. ");
            solver.createConstraints3();
            //System.out.println("Done");
            //System.out.print("Creating constraints 4.............................. ");
            solver.createConstraints4();
            //System.out.println("Done");
            //System.out.print("Creating constraints 5.............................. ");
            solver.createConstraints5();
            //.out.println("Done");
            //System.out.print("Creating objective function......................... ");
            solver.createObjectiveFunction();
            //System.out.println("Done");
            solver.run();
            //solver.printResults();
            super.objectiveFunctionValue[0] = cplex.getObjValue();
            cplex.end();
        } catch (IloException ex) {
            Logger.getLogger(VehicleRoutingProblemEvaluatorDistanceCplex.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void fillSolution(RoutesSolution<Vrp> solution) {
    }
}
