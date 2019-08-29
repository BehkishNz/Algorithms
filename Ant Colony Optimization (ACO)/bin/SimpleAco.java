package aco;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;
/*
• Algorithm Ant colony optimization metaheuristic
    • 1. Set parameters, initialize pheromone trails
    • while termination conditions not met do
        • 2. ConstructAntSolutionss
        • 3. ApplyLocalSearch (optional)
        • 4. UpdatePheromones
    • end while


*/
public class SimpleAco {
	public static Point2D[] EuclideanDis;
	public static double alpha;// global updating factor
	public static double beta; // heuristic factor
    public static double phi; // local updating factor
    public static boolean OnlinePhermoneUpdate; // turns online phermone Update on and off
    public static int NumbOfAntsInCol; // number of ants in each colony
    public static double initialPhermone; // percentage amount of initial phermone
    public static int numberOfCities;
    public static int onlineUpdateConstant;
    public static int maxIteration;
    public static double q0;
    
	public static double[][] phermoneMatrix; // phermone between 2 cities 
    static double[] X_coord;
    static double[] Y_coord;
    static Random randomeNum = new Random();
    

	public static void initialValues(){ // change these values accordingly 
        alpha = 1;
        beta = 1;
        phi = 0.1;
        q0 = 0.5;
        OnlinePhermoneUpdate = true;
        NumbOfAntsInCol = 50;
        initialPhermone = 1000;
        numberOfCities = 29;
        onlineUpdateConstant = 12000;
		maxIteration = 2000;
        X_coord = new double[]{1150,630,40,750,750,1030,1650,1490,790,710,840,1170,
				970,510,750,1280,230,460,1040,590,830,490,1840,1260,1280,
				490,1460,1260,360};
		Y_coord = new double[]{1760,1660,2090,1100,2030,2070,650,1630,2260,1310,550,
				2300,1340,700,900,1200,590,860,950,1390,1770,500,1240,1500,
				790,2130,1420,1910,1980}; 

        
    }
    public static void initialzie(){
        EuclideanDis = new Point2D[numberOfCities];
        phermoneMatrix = new double [numberOfCities][numberOfCities];
		for(int i = 0; i< numberOfCities; i++){
				EuclideanDis[i] = new Point2D.Double(X_coord[i], Y_coord[i]);
                for(int j = 0; j<numberOfCities; j++){
                    phermoneMatrix[i][j] = initialPhermone;
                    if(i == j){
                        phermoneMatrix[i][j] = 0;
                    }
                }
		}
        
    }
    
	public static void main(String[] args) {
        //1. Set parameters, initialize pheromone trails
        initialValues();
        initialzie();
        double localBestCost = Double.POSITIVE_INFINITY;
        double PreviouslocalBestCost = 0;
        double globalBestCost = Double.POSITIVE_INFINITY;
        ArrayList <Integer>globalBestSolution = new ArrayList<Integer>();
        ArrayList <Integer>iterationBestSolution = new ArrayList<Integer>();
        int performanceCounter = 0; 
        
		for(int it = 0; it < maxIteration; it++){
			PreviouslocalBestCost = localBestCost;
			for(int i = 0; i< NumbOfAntsInCol; i++){
                //2. ConstructAntSolutions
                //nextInt(int bound) --> Returns a pseudorandom, uniformly distributed int value between 0 (inclusive)
                // and the specified value (exclusive), drawn from this random number generator's sequence.
				int nextRandNum = (randomeNum.nextInt(10000) % numberOfCities);
				Ant ant = new Ant(EuclideanDis[nextRandNum]);
				ConstructAntSolutions(ant, nextRandNum, it);
                
                //3. ApplyLocalSearch (optional)
                if(ant.cost< localBestCost){
					localBestCost = ant.cost;
					iterationBestSolution = ant.solution;
				}
                //4. UpdatePheromones 
				UpdatePheromones(ant); // Ant quantity model
				
		        if(OnlinePhermoneUpdate){
		            OnlineUpdate(ant);
		        }

			}
            if(localBestCost<globalBestCost){
                globalBestCost = localBestCost;
                globalBestSolution = iterationBestSolution;
            }
            if(localBestCost == PreviouslocalBestCost){
            	
            	performanceCounter++;
            }
            else{
            	performanceCounter = 0;
            	
            }
            
    		System.out.println("The best local cost found = " + localBestCost);
    		for(int i = 0; i< iterationBestSolution.size(); i++){
    			
    			System.out.print(iterationBestSolution.get(i) + ", ");
    		}
    		System.out.println();
    		System.out.println(it + "--------------------------------");
    		if(performanceCounter == 200){
    			System.out.println("local best have not been changed for more than 200 iterations");
    			break;
    			
    		}
		}
		System.out.println("The best cost found = " + globalBestCost);
		System.out.print("The path= ");
		for(int i = 0; i< globalBestSolution.size(); i++){
			
			System.out.print(globalBestSolution.get(i) + ", ");
		}
	}
    
	private static void UpdatePheromones(Ant ant) {
		for(int i = 0; i< ant.solution.size(); i++){
            int nextIndex = 0;
            if(i != (ant.solution.size()-1)){
                nextIndex = i+1; 
            }
            phermoneMatrix[i][nextIndex] += (onlineUpdateConstant/EuclideanDis[ant.solution.get(i)-1].distance(EuclideanDis[ant.solution.get(nextIndex)-1]));  
		}
		for (int i = 0; i<numberOfCities; i++){
			for(int j = 0; j<numberOfCities;j++){
				phermoneMatrix[i][j] = (phermoneMatrix[i][j])*(1-phi);
				if(phermoneMatrix[i][j] <1){
					phermoneMatrix[i][j] = 1;
					
				}
			}
		}
	}

	private static void ConstructAntSolutions(Ant ant, int index, int it) {
		double bestPobability = 0.0;
		double totalDistance = 0.1;
		int bestCityFound = -1; 
		boolean citiesLeft = false;
        boolean flag = true;
		ant.Tabu.add(index);
		ant.solution.add(index+1);
		int searchNum = 0;
		while(ant.solution.size() != numberOfCities && searchNum < 1000){
			//searchNum++;
			ArrayList <Integer> someCitiesFound = new ArrayList <Integer>();
			ArrayList <Double> someCitiesFoundProb = new ArrayList <Double>();
			for(int i = 0; i< numberOfCities; i++){
				if(!is_taboo(i, ant)){
					totalDistance += Math.pow(phermoneMatrix[index][i], alpha)/Math.pow(ant.currentCity.distance(EuclideanDis[i]), beta);
					}
			}
			
			for(int i = 0; i< numberOfCities; i++){
				if(!is_taboo(i, ant)){
					double temp = ((Math.pow(phermoneMatrix[index][i], alpha)/Math.pow(ant.currentCity.distance(EuclideanDis[i]), beta))/totalDistance);
					citiesLeft = true;
					if(temp>bestPobability){
						bestCityFound = i;
						bestPobability = temp;
					}
					else{
						someCitiesFound.add(i);
						someCitiesFoundProb.add(temp);
					}
				}
			}

			if(citiesLeft){
				if(bestCityFound == -1){
					int ranIndex = randomeNum.nextInt(1000) % someCitiesFound.size();
					bestCityFound = someCitiesFound.get(ranIndex);
					bestPobability = someCitiesFoundProb.get(ranIndex);
				}
				double q = ((randomeNum.nextDouble()));
				//System.out.println("q is " + q);
				if(q < q0){
					ant.solution.add(bestCityFound+1);
					ant.currentCity = EuclideanDis[bestCityFound];
					ant.Tabu.add(bestCityFound);
				}else{
					int visitedCity = numberOfCities;
					while(flag && visitedCity >0){
						int nextRandom = (randomeNum.nextInt(10000) % numberOfCities);
						if(!is_taboo(nextRandom, ant )){
							ant.solution.add(nextRandom+1);
							ant.currentCity = EuclideanDis[nextRandom];
							ant.Tabu.add(nextRandom);
							flag = false;
						}
						visitedCity--;
					}
                    flag = true;
				}
				bestPobability =0;
				bestCityFound = 0;
				citiesLeft = false;
			}
		}
		for(int i = 0; i< ant.solution.size(); i++){
            int nextIndex = 0;
            if(i != (ant.solution.size()-1)){
                nextIndex = i+1; 
            }
            ant.cost += EuclideanDis[ant.solution.get(i)-1].distance(EuclideanDis[ant.solution.get(nextIndex)-1]);
       }
        

	}

	private static void OnlineUpdate(Ant ant) {
		for(int i = 0; i< ant.solution.size(); i++){
            int nextIndex = 0;
            if(i != (ant.solution.size()-1)){
                nextIndex = i+1; 
            }
            //ant.cost += EuclideanDis[ant.solution.get(i)-1].distance(EuclideanDis[ant.solution.get(nextIndex)-1]);
			phermoneMatrix[i][nextIndex] += (onlineUpdateConstant/ant.cost);  
		}
	}

	private static boolean is_taboo(int city, Ant ant) {
		for (int i = 0; i < ant.Tabu.size(); i++){
			if(city == ant.Tabu.get(i)){
				return true;
			}
		}
		return false; 
	}
	
}

class Ant {

	public double cost = 0; 
	public Point2D currentCity;
	public ArrayList <Integer> Tabu = new ArrayList <Integer>();
    public ArrayList <Integer>solution = new ArrayList<Integer>();

	public Ant(Point2D city) {
		currentCity = city; 
	}

}