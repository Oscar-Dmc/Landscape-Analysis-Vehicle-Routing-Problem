# Vehicle Routing Problem (VRP)

## Neighborhoods 

In this project we use 6 environment structure for exploring the neighborhoods. 
The neighborhoods avaliable are: 
1. Swap 
    * Swap the positions of two elements. 
1. Move After
    * Move the element  1 in the position after element 2 if the route isn't full. 
2. Move Before
    * Move the element  1 in the position before element 2 if the route isn't full.  
3. Remove 
    * Remove one element of the current solution. In our case this solutions are incomplete and infeasible but we can mix this environment with other. 
4. Insertion After
    * We need to start in an incomplete solution. Insert one element who is out of solution after one which is in solution if that route isn't full. 
5. Insertion Before
    * We need to start in an incomplete solution. Insert one element who is out of solution before one which is in solution if that route isn't full. 
When you pass the argument for the environment, you must do in order that you want to execute it. 


## Builders
Most of techniques need an initial solution. For provide that solutions we have some builders, Sequential, Random and Random CL.  
1. **Sequential builder**:  It take the first customer and insert it in the first route. It continue to take the second element and insert in the second route. It repeat that steps untill reach the number maximun of routes, then it will insert next customer in the first route and repeat this steps untill the last customer is in solution. 

2. **Random**: It select a random customer and insert it in random route. When a route is full, we lock this route for next iteration, and prevent select it.  

3. **Random with RCL**:  In this builder we have a candidate list and restict candidate list (RCL) whit size number maximun of customers per vehicle. It will fill that RCL whit the best canditates to be insert in this iteration. Then the builder select one at random and insert it in the first route. It add new element to RCL if still have customers out of solution and pick another at random of RCL and instert it untill the rute are full. It repeat this steps utill the last customer are in solution. 

## How to start? 

1. Clone the repository or executables folder in your computer. 

2. Go to folder "executables"

3. Follow the instruction below for each .jar 

### VNS
* Launch  `java -jar .\VNS.jar [file path] [nVehicles] [nMaxCustomers] [builder] [neighborhoods] `
    * File path: Route of file who contain problem instance.
    * nVehicles: Number of vehicles
    * nMaxCustomer: Number maximum of customers that each vehicle can visit. 
    * builder: Select between random builder or sequential builder. Check the section Builders below for more info.
    * neighborhoods: Insert in order each environment structure for the launch of the Vns separated by spaces. Check section Neighborhoods. 

Example
> java -jar .\VNS.jar \home\documents\instancies\example1.txt 7 10 1 4 5 1 

If you want to execute the same settings for a various files, you can use 'multipleRunsVns'. The sintax is similar, you must pass the path folder which contain the instances instead of file path. 

Example
> .\multipleRunsVns \home\documents\instancies 7 10 1 4 5 1 

### LNS 
* Launch `java -jar .\LNS.jar [file path] [nVehicles] [nMaxCustomers] [builder] [percent] [iterations] [neighborhoods]`
    * File path: Route of file who contain problem instance.
    * nVehicles: Number of vehicles
    * nMaxCustomer: Number maximum of customers that each vehicle can visit. 
    * builder: Select between random builder or sequential builder. Check the section Builders below for more info.
    * percent: Percent of destruction. This percent will be use on destruction method for remove n customers of solution. You have to insert the percent with integer, for example if you wanna set 10%, you must to insert 10 in command line 
    * iterations: Number of iteration maximum. 
    * neighborhoods: Insert in order each environment structure for the launch of the Vns separated by spaces. Check section Neighborhoods. 

Example 
> java -jar .\LNS.jar \home\documents\instance\example\example1.txt 7 10 2 10 2500 4 5 1

If you want to execute the same settings for a various files, you can use 'multipleRunsLns'. The sintax is similar, you must pass the path folder which contain the instances instead of file path.

Example
> .\multipleRunsLns \home\documents\instancies 7 10 2 10 2500 4 5 1

### EvaluatorAnalyzer
This .jar compare all evaluator used in the other classes with the mathematical model for an accuracy of 4 decimals.
Before we launch this .jar we must be intalled ILOG CPLEX on your computer otherwise this run will be fail. 

* Launch `java - Djava.library.path=[cplex bin path] -jar .\EvaluatorAnalyzer.jar [file path] [nSolutions]`
    * cplex bin path: This is the path where you have cplex bin. 
    * file path: Route of the file who contain problem instance. 
    * nSolutions: Number of solutios that we want to test on each evaluator. 

Example for windows
> java '-Djava.library.path=D:\Programs\IBM\ILOG\CPLEX_Studio129\cplex\bin\x64_win64' -jar .\EvaluatorAnalyzer.jar "D:\Documents\instances\R101_100.txt" 10000


### Landscape 
This .jar files use MongoDB for generate and analyzer the landscape so we need to install it. When we have alreadty installed MongoDB we launch it before we run any .jar.  Both of them use the default ip to connect to database (`mongodb://127.0.0.1:27017`) 
#### GenerateLandscape
This .jar generate solutions of a problem instance.
* Launch `java -jar .\Generate Landscape.jar [file path] [nVehicles] [neighborhood] [nSolutions] [idSolution]`
    * file path: Route of the file who contain problem instance
    * nVehicles: Number of vehicles
    * neighborhood: index of neighborhood that we wanna explore 
    * nSolutions:  Number of solution to generate 
    * idSolution: If we have solutions for that instance on the database we can specify the initial solution, otherwise it take the first solution on this neighborhood.  If we don't have any solutions, then it build a random solution. This parameter is optional. 

For each instance , the program create a new db with the same name of the instance, for example R101_100 contain the solution for the instance R101_100.txt.

Example
> java -jar .\GenerateLandscape.jar "D:\Documents\instances\small\C101_25.txt" 5 1 25000

#### LandscapeAnalyzer
This .jar analyze a specified landscape with some measures to know the distribution and correlation between the solutions
* Launch `java -jar .\LandscapeAnalyzer.jar [file path] [nVehicles] [neighborhood] `
    * file path: Route of the file who contain problem instance
    * nVehicles: Number of Vehicles
    * neighborhood: index of neighborhood that we wanna analyze

On this .jar, we connect to the db which name is the same at the file, and analyze the neighborhood that we specified in command line.

Example
>  java -jar .\LandscapeAnalyzer.jar "D:\Documents\instances\small\C101_25.txt" 5 1