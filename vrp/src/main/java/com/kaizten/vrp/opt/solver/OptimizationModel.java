package com.kaizten.vrp.opt.solver;

import com.kaizten.opt.solution.RoutesSolution;
import com.kaizten.opt.solver.Solver;
import com.kaizten.utils.datastructure.KaiztenArray;
import com.kaizten.utils.exception.KaiztenException;
import com.kaizten.vrp.opt.core.Vrp;
import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloNumExpr;
import ilog.cplex.IloCplex;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Christopher Expósito-Izquierdo (christopherexpositoizquierdo@gmail.com)
 */
public class OptimizationModel implements Solver<RoutesSolution<Vrp>> {

    /**
     *
     */
    private final Vrp optimizationProblem;
    /**
     *
     */
    private RoutesSolution<Vrp> initialSolution;
    /**
     *
     */
    private final IloCplex cplex;
    /**
     *
     */
    private IloIntVar[][][] X;
    /**
     *
     */
    private int m;
    /**
     *
     */
    private int n;

    /**
     *
     * @param problem
     * @param cplex
     */
    public OptimizationModel(Vrp problem, IloCplex cplex) {
        this.optimizationProblem = problem;
        this.cplex = cplex;
    }

    /**
     *
     * @param solution
     * @throws IloException
     */
    public void setInitialSolution(RoutesSolution<Vrp> solution) throws IloException {
        this.initialSolution = solution;
    }

    /**
     *
     * @throws IloException
     */
    public void createVariables() throws IloException {
        this.m = this.initialSolution.getNumberOfRoutes();
        this.n = this.optimizationProblem.getNCustomers();
        this.X = new IloIntVar[this.n + 2][this.n + 2][this.m];
        for (int i = 0; i <= this.n; i++) {
            for (int j = 0; j <= this.n; j++) {
                for (int k = 1; k <= this.m; k++) {
                    final String name = "X(" + i + ")(" + j + ")(" + k + ")";
                    this.X[i][j][k - 1] = this.cplex.boolVar(name);
                }
            }
        }
    }

    /**
     *
     * @throws IloException
     */
    public void createConstraints1() throws IloException {
        for (int j = 1; j <= this.n; j++) {
            final String name = "constraint1(j" + j + ")";
            IloNumExpr expr = this.cplex.numExpr();
            for (int k = 1; k <= this.m; k++) {
                for (int i = 0; i <= this.n; i++) {
                    if (j != i) {
                        expr = this.cplex.sum(expr, this.X[i][j][k - 1]);
                    }
                }
            }
            this.cplex.addEq(expr, 1, name);
        }
    }

    /**
     *
     * @throws IloException
     */
    public void createConstraints2() throws IloException {
        for (int j = 1; j <= this.n; j++) {
            final String name = "constraint2(j" + j + ")";
            IloNumExpr expr = this.cplex.numExpr();
            for (int k = 1; k <= this.m; k++) {
                for (int i = 0; i <= this.n; i++) {
                    if (j != i) {
                        expr = this.cplex.sum(expr, this.X[j][i][k - 1]);
                    }
                }
            }
            this.cplex.addEq(expr, 1, name);
        }
    }

    /**
     *
     * @throws IloException
     */
    public void createConstraints3() throws IloException {
        for (int j = 1; j <= this.n; j++) {
            for (int k = 1; k <= this.m; k++) {
                final String name = "constraint3(j" + j + ", k" + k + ")";
                IloNumExpr exprLeft = this.cplex.numExpr();
                for (int i = 0; i <= this.n; i++) {
                    if (i != j) {
                        exprLeft = this.cplex.sum(exprLeft, this.X[i][j][k - 1]);
                    }
                }
                IloNumExpr exprRight = this.cplex.numExpr();
                for (int i = 0; i <= this.n; i++) {
                    if (i != j) {
                        exprRight = this.cplex.sum(exprRight, this.X[j][i][k - 1]);
                    }
                }
                this.cplex.addEq(this.cplex.diff(exprLeft, exprRight), 0, name);
            }
        }
    }

    /**
     *
     * @throws IloException
     */
    public void createConstraints4() throws IloException {
        for (int k = 1; k <= this.m; k++) {
            final String name = "constraint4(k" + k + ")";
            IloNumExpr expr = this.cplex.numExpr();
            for (int i = 0; i <= this.n; i++) {
                expr = this.cplex.sum(expr, this.X[0][i][k - 1]);
            }
            this.cplex.addEq(expr, 1, name);
        }
    }

    /**
     *
     * @throws IloException
     */
    public void createConstraints5() throws IloException {
        for (int k = 1; k <= this.m; k++) {
            final String name = "constraint5(k" + k + ")";
            IloNumExpr expr = this.cplex.numExpr();
            for (int i = 0; i <= this.n; i++) {
                expr = this.cplex.sum(expr, this.X[i][0][k - 1]);
            }
            this.cplex.addEq(expr, 1, name);
        }
    }

    /**
     *
     * @throws IloException
     */
    public void createObjectiveFunction() throws IloException {
        IloNumExpr expr = this.cplex.numExpr();
        for (int k = 1; k <= this.m; k++) {
            for (int i = 0; i <= this.n; i++) {
                for (int j = 0; j <= this.n; j++) {
                    //expr = this.cplex.sum(expr, this.cplex.prod(this.optimizationProblem.getDistance(i, j), this.X[i][j][k - 1]));
                    expr = this.cplex.sum(expr, this.cplex.prod(this.optimizationProblem.getDistanceMatrix()[i][j], this.X[i][j][k - 1]));
                }
            }
        }
        this.cplex.addMinimize(expr, "Objective Function");
    }

    /**
     *
     * @return
     */
    @Override
    public RoutesSolution<Vrp> run() {
        RoutesSolution<Vrp> solution = null;
        try {
            this.cplex.setParam(IloCplex.DoubleParam.WorkMem, 128);
            //this.cplex.setParam(IloCplex.StringParam.WorkDir, "");
            this.cplex.setParam(IloCplex.IntParam.Threads, 1);
            this.cplex.setParam(IloCplex.IntParam.NodeFileInd, 2);
            //this.cplex.setParam(IloCplex.DoubleParam.TreLim, 128);
            this.cplex.solve();
        } catch (IloException ex) {
            Logger.getLogger(OptimizationModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return solution;
    }

    /**
     *
     * @param output
     * @throws IOException
     */
    public void exportModel(String output) throws IOException {
        try {
            this.cplex.exportModel(output);
        } catch (IloException exception) {
            throw new KaiztenException("ERROR. File " + output + " not found.", exception);
        }
    }

    /**
     *
     * @throws IloException
     */
    public void initializeVariables() throws IloException {
        for (int i = 0; i <= this.n; i++) {
            for (int j = 0; j <= this.n; j++) {
                for (int k = 1; k <= this.m; k++) {
                    this.X[i][j][k - 1].setLB(0.0);
                    this.X[i][j][k - 1].setUB(0.0);
                }
            }
        }
        for (int k = 0; k < this.initialSolution.getNumberOfRoutes(); k++) {
            int[] route = this.initialSolution.getRoute(k);
            route = KaiztenArray.join(-1, route);
            route = KaiztenArray.add(route, -1);
            // System.out.println("Route " + k + ": " + KaiztenString.print(route));
            double length = 0.0;
            for (int j = 0; j < route.length - 1; j++) {
                int node1 = route[j];
                //int node2 = route[j + 1];
                int node2 = (j == route.length - 1) ? -1 : route[j + 1];
                //System.out.println("\t" + node1 + "\t" + node2 + " -> " + (node1 + 1) + "\t" + (node2 + 1));
                this.X[node1 + 1][node2 + 1][k].setLB(1.0);
                this.X[node1 + 1][node2 + 1][k].setUB(1.0);
            }
        }
    }

    /**
     *
     * @throws IloException
     */
    public void printObjectiveFunctionValue() throws IloException {
        System.out.println("Objective Function Value: " + this.cplex.getObjValue());
    }

    /**
     *
     * @throws IloException
     */
    public void printResults() throws IloException {
        System.out.println("Status:                   " + this.cplex.getStatus());
        //if (!((this.cplex.getStatus() == Status.Optimal) || (this.cplex.getStatus() == Status.Feasible))) {
        //return;
        //}
        System.out.println("Objective Function Value: " + this.cplex.getObjValue());
        System.out.println();
        // Variables
        //this.showVariablesState();
        //        
        String[] strings = new String[this.optimizationProblem.getNCustomers()];
        strings[0] = "Customer";
        for (int i = 1; i < this.optimizationProblem.getNCustomers(); i++) {
            strings[i] = "" + i;
        }
        /*System.out.println(VRPTimeWindowsUtilities.getFormat(strings, OptimizationModel.COLUMN_WIDTH));
        System.out.println("Routes:");
        strings[0] = "Predecesor";
        for (int i = 1; i < this.problem.getNodes(); i++) {
            strings[i] = "" + this.getPredecessor(i);
        }
        System.out.println(VRPTimeWindowsUtilities.getFormat(strings, OptimizationModel.COLUMN_WIDTH));
        strings[0] = "Successor";
        for (int i = 1; i < this.problem.getNodes(); i++) {
            strings[i] = "" + this.getSuccessor(i);
        }
        System.out.println(VRPTimeWindowsUtilities.getFormat(strings, OptimizationModel.COLUMN_WIDTH));
        strings[0] = "q";
        for (int i = 1; i < this.problem.getNodes(); i++) {
            strings[i] = "" + this.q[i];
        }
        System.out.println(VRPTimeWindowsUtilities.getFormat(strings, OptimizationModel.COLUMN_WIDTH));
        strings[0] = "a";
        for (int i = 1; i < this.problem.getNodes(); i++) {
            strings[i] = "" + this.a[i];
        }
        System.out.println("Time Windows:");
        System.out.println(VRPTimeWindowsUtilities.getFormat(strings, OptimizationModel.COLUMN_WIDTH));
        strings[0] = "b";
        for (int i = 1; i < this.problem.getNodes(); i++) {
            strings[i] = "" + this.b[i];
        }
        System.out.println(VRPTimeWindowsUtilities.getFormat(strings, OptimizationModel.COLUMN_WIDTH));
        strings[0] = "vehicle";
        for (int i = 1; i < this.problem.getNodes(); i++) {
            strings[i] = "" + this.getVehicle(i);
        }
        System.out.println(VRPTimeWindowsUtilities.getFormat(strings, OptimizationModel.COLUMN_WIDTH));
        strings[0] = "arrival";
        for (int i = 1; i < this.problem.getNodes(); i++) {
            strings[i] = "" + this.getArrivalTime(i);
        }
        System.out.println(VRPTimeWindowsUtilities.getFormat(strings, OptimizationModel.COLUMN_WIDTH));
        ArrayList<Integer> depotPredecesors = this.getPredecessors(this.n + 1);
        ArrayList<Integer> depotSuccessors = this.getSuccessors(0);
        System.out.print("Depot predecesors: ");
        for (Integer predecesor : depotPredecesors) {
            System.out.print(predecesor + "\t");
        }
        System.out.println();
        System.out.print("Depot successors:  ");
        for (Integer successor : depotSuccessors) {
            System.out.print(successor + "\t");
        }
        System.out.println();
        for (int k = 0; k < this.m; k++) {
            strings[0] = "w(" + k + ")";
            for (int i = 1; i < this.optimizationProblem.getNodes(); i++) {
                strings[i] = "" + this.cplex.getValue(this.W[i][k]);
            }
            System.out.println(VRPTimeWindowsUtilities.getFormat(strings, OptimizationModel.COLUMN_WIDTH));
        }
        System.out.println();
        //
        ArrayList<Integer>[] routes = new ArrayList[this.m];
        for (int k = 1; k <= this.m; k++) {
            System.out.println("Route " + (k - 1) + ":");
            for (int i = 0; i <= this.n + 1; i++) {
                for (int j = 0; j <= this.n + 1; j++) {
                    try {
                        if (this.cplex.getValue(this.X[i][j][k - 1]) >= 0.95) {
                            System.out.println("\t" + this.X[i][j][k - 1]);
                        }
                    } catch (Exception e) {
                        //System.out.println(this.X[i - 1][j - 1][k - 1]);
                    }
                }
            }
            System.out.println();
            //
            ArrayList<Integer> route = new ArrayList<>();
            route.add(0);
            int currentNode = 0;
            int nextNode = -1;
            while ((nextNode != 0) && (nextNode != (this.n + 1))) {
                nextNode = this.getSuccessor(currentNode, k - 1);
                currentNode = nextNode;
                route.add(currentNode);
            }
            System.out.println(VRPTimeWindowsUtilities.getFormat(route) + "\n");
            System.out.println(this.showRoute(route));
            routes[k - 1] = route;
        }
        System.out.println();
        System.out.println("Length Solution: " + VRPTimeWindowsUtilities.getFormat(this.optimizationProblem.getDistance(routes), 3));
         */
    }

    public String showVariablesState() {
        String text = "";
        for (int i = 0; i <= this.n + 1; i++) {
            for (int j = 0; j <= this.n + 1; j++) {
                for (int k = 1; k <= this.m; k++) {
                    double value = -1;
                    try {
                        value = this.cplex.getValue(this.X[i][j][k - 1]);
                        if (value > 0.5) {
                            text += this.X[i][j][k - 1] + " --> " + value + "\n";
                        }
                    } catch (Exception e) {
                    }
                }
            }
        }
        System.out.println();
        return text;
    }

    /*private String showRoute(ArrayList<Integer> route) throws IloException {
        String string = "";
        String[] strings = new String[]{"Index", "Node", "Distance", "Cumulat.", "Open", "Close", "Arrival", "Departure", "Waiting", "TimeLeft", "Load", "Cum. Load", "Diff", "Diff*"};
        string += VRPTimeWindowsUtilities.getFormat(strings, OptimizationModel.COLUMN_WIDTH) + "\n";
        double cumulative = 0.0;
        double cumulativeLoad = 0.0;
        double average = 0.0;
        for (int i = 0; i < route.size(); i++) {
            int node = route.get(i);
            double distance = (i > 0) ? this.optimizationProblem.getDistance(route.get(i - 1), node) : 0.0;
            double waitingTime = (node != 0) ? 0 : 0;//this.getArrivalTime(node) - this.e[node] : 0.0;
            double timeLeft = (node != 0) ? 0 : 0;//this.l[node] - this.getDepartureTime(node) : 0.0;
            cumulative += distance;
            cumulativeLoad += this.q[node];
            int index = 0;
            strings[index++] = "" + i;
            strings[index++] = "" + node;
            strings[index++] = "" + distance;
            strings[index++] = "" + cumulative;
            strings[index++] = (node != 0) ? "" + this.optimizationProblem.getReadyTime(node) : "-";
            strings[index++] = (node != 0) ? "" + this.optimizationProblem.getDueDate(node) : "-";
            strings[index++] = ((node != 0) && (node != (this.n + 1))) ? "" + this.getArrivalTime(node) : "";
            strings[index++] = ((node != 0) && (node != (this.n + 1))) ? "" + this.getDepartureTime(node) : "";
            strings[index++] = (node != 0 && waitingTime >= 0) ? "" + waitingTime : "-";
            strings[index++] = (node != 0 && timeLeft >= 0) ? "" + timeLeft : "-";
            strings[index++] = (node != 0) ? "" + this.q[node] : "-";
            strings[index++] = (node != 0) ? "" + cumulativeLoad : "-";
            strings[index++] = ((node != 0) && (node != (this.n + 1))) ? "" + (this.getArrivalTime(node) - this.a[node]) : "";
            strings[index++] = ((node != 0) && (node != (this.n + 1))) ? "" + (this.getArrivalTime(node) - this.a[node]) / (this.b[node] - this.a[node]) : "";
            string += VRPTimeWindowsUtilities.getFormat(strings, OptimizationModel.COLUMN_WIDTH) + "\n";
            average += ((node != 0) && (node != (this.n + 1))) ? (this.getArrivalTime(node) - this.a[node]) / (this.b[node] - this.a[node]) : 0.0;
        }
        double length = this.problem.getDistance(route);
        Arrays.fill(strings, "");
        strings[0] = "Length:";
        strings[1] = "" + length;
        string += VRPTimeWindowsUtilities.getFormat(strings, OptimizationModel.COLUMN_WIDTH) + "\n";
        strings[0] = "Averages WT:";
        strings[1] = "" + average;
        string += VRPTimeWindowsUtilities.getFormat(strings, OptimizationModel.COLUMN_WIDTH) + "\n";
        strings[0] = "Averages WT*:";
        strings[1] = "" + (double) (average / this.n);
        string += VRPTimeWindowsUtilities.getFormat(strings, OptimizationModel.COLUMN_WIDTH) + "\n";
        return string;
    }*/
    private int getPredecessor(int node) {
        for (int j = 0; j <= this.n; j++) {
            for (int k = 0; k < this.m; k++) {
                try {
                    if (this.cplex.getValue(this.X[j][node][k]) >= 0.95) {
                        return j;
                    }
                } catch (Exception e) {
                }
            }
        }
        System.out.println("ERROR. Finding predecessor of node " + node);
        System.exit(0);
        return -1;
    }

    private ArrayList<Integer> getPredecessors(int node) {
        ArrayList<Integer> predecessors = new ArrayList<>();
        for (int j = 0; j <= this.n + 1; j++) {
            for (int k = 0; k < this.m; k++) {
                try {
                    if (this.cplex.getValue(this.X[j][node][k]) >= 0.95) {
                        predecessors.add(j);
                    }
                } catch (Exception e) {
                }
            }
        }
        return predecessors;
    }

    private int getSuccessor(int node) {
        for (int j = 0; j <= this.n + 1; j++) {
            for (int k = 0; k < this.m; k++) {
                try {
                    if (this.cplex.getValue(this.X[node][j][k]) >= 0.95) {
                        return j;
                    }
                } catch (Exception e) {
                }
            }
        }
        System.out.println("ERROR. Finding successor of node " + node);
        System.exit(0);
        return -1;
    }

    private ArrayList<Integer> getSuccessors(int node) {
        ArrayList<Integer> successors = new ArrayList<>();
        for (int j = 0; j <= this.n + 1; j++) {
            for (int k = 0; k < this.m; k++) {
                try {
                    if (this.cplex.getValue(this.X[node][j][k]) >= 0.95) {
                        successors.add(j);
                    }
                } catch (Exception e) {
                }
            }
        }
        return successors;
    }

    private int getSuccessor(int node, int vehicle) {
        for (int j = 0; j <= this.n + 1; j++) {
            try {
                if (this.cplex.getValue(this.X[node][j][vehicle]) >= 0.95) {
                    return j;
                }
            } catch (Exception e) {
            }
        }
        System.out.println("ERROR. Finding successor of node " + node + " when using vehicle " + vehicle);
        System.exit(0);
        return -1;
    }

    private int getVehicle(int node) {
        for (int j = 0; j <= this.n + 1; j++) {
            for (int k = 0; k < this.m; k++) {
                try {
                    if (this.cplex.getValue(this.X[node][j][k]) >= 0.95) {
                        return k;
                    }
                } catch (Exception e) {
                }
            }
        }
        System.out.println("ERROR. Determining the vehicle that visits node " + node);
        System.exit(0);
        return -1;
    }

    @Override
    public String toString() {
        String text = "Vehicles:  " + this.m + "\n";
        text += "Customers: " + this.n + "\n";
        /*String[] strings = new String[this.optimizationProblem.getNodes()];
        strings[0] = "a";
        for (int i = 1; i < this.optimizationProblem.getNodes(); i++) {
            strings[i] = "" + this.a[i];
        }
        text += VRPTimeWindowsUtilities.getFormat(strings, OptimizationModel.COLUMN_WIDTH) + "\n";
        strings[0] = "b";
        for (int i = 1; i < this.optimizationProblem.getNodes(); i++) {
            strings[i] = "" + this.b[i];
        }
        text += VRPTimeWindowsUtilities.getFormat(strings, OptimizationModel.COLUMN_WIDTH) + "\n";
        strings[0] = "s";
        for (int i = 1; i < this.optimizationProblem.getNodes(); i++) {
            strings[i] = "" + this.s[i];
        }
        text += VRPTimeWindowsUtilities.getFormat(strings, OptimizationModel.COLUMN_WIDTH) + "\n";
        strings[0] = "q";
        for (int i = 1; i < this.optimizationProblem.getNodes(); i++) {
            strings[i] = "" + this.q[i];
        }
        text += VRPTimeWindowsUtilities.getFormat(strings, OptimizationModel.COLUMN_WIDTH) + "\n";*/
        /*for (int i = 0; i <= this.n + 1; i++) {
         strings[0] = "M_" + i;
         for (int j = 0; j <= this.n + 1; j++) {
         strings[i + 1] = "" + this.M[i][j];
         text += VRPTimeWindowsUtilities.getFormat(strings, OptimizationModel.COLUMN_WIDTH) + "\n";
         }
         }*/
        return text;
    }
}
