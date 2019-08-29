import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

public class TabuSearch {
	static int size = 20;
	static int[][] flow = new int[size][size];
	static int[][] distance = new int[size][size];
	static int[][] Tabu = new int[size][size];
	static int[] solution =new int[size];
	static int MAXITERATION = 100000;
	static int TabuTenure = 14;
	static int overAllCost = 0;
	static int globalmin = (int) Double.POSITIVE_INFINITY;
	static int counter = 0;
	
	
	// initializes solution, distance and flow
	public static void initArryas(){
		for(int i = 0; i<solution.length ; i++){
			flow[i][i] = -1;
			distance[i][i] = -1;
			solution[i] = i;
			for(int j = 0; j< solution.length ; j++){
				if(i == j){
					Tabu[i][j] = -1;
				}
				else{
					Tabu[i][j] = 0;
				}
			}	
		}	
	}
	// fills in flow and distance based on given info 
	public static void FillinArrays(){
		
		String csvFlowFile = "Flow.csv";
		String csvDistanceFile = "Distance.csv";
		
		//String csvFlowFile = "Book2.csv";
		//String csvDistanceFile = "Book1.csv";
		
        String line = "";
        String cvsSplitBy = ",";
        int x = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(csvFlowFile))) {

        	while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] row = line.split(cvsSplitBy);
      
                for(int i = 0; i < row.length; i++)
                {
                	
                	flow[x][i] = Integer.parseInt(row[i]);
                }
                x++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        
        line = "";
        x = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(csvDistanceFile))) {

        	while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] row = line.split(cvsSplitBy);
      
                for(int i = 0; i < row.length; i++)
                {
                	
                	distance[x][i] = Integer.parseInt(row[i]);
                }
                x++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
		
		
		
		
		
		
		/*
		distance[0][1] = 22;
		distance[0][2] = 53;
		distance[0][3] = 53;
		distance[1][2] = 40;
		distance[1][3] = 62;
		distance[2][3] = 55;

		flow[0][1] = 3;
		flow[0][2] = 0;
		flow[0][3] = 2;
		flow[1][2] = 0;
		flow[1][3] = 1;
		flow[2][3] = 4;
		
		for(int i = 0; i < size; i++){
			for(int j = i+1; j< size; j++ ){
				distance[j][i] = distance[i][j];
				flow[j][i] = flow[i][j];
			}
		}
		*/

	}
	
	static void printArrays(){
		System.out.println("Distance = ");
		for(int i = 0; i < size; i++){
			
			for(int j = 0 ; j < solution.length ; j++){
				System.out.print(distance[i][j] + " ");
			}
			System.out.println("");
		}
		System.out.println("flow = ");
		for(int i = 0; i < size; i++){
			
			for(int j = 0 ; j < solution.length ; j++){
				System.out.print(flow[i][j] + " ");
			}
			System.out.println("");
		}
		System.out.println("Tabu = ");
		for(int i = 0; i < size; i++){
			
			for(int j = 0 ; j < solution.length ; j++){
				System.out.print(Tabu[i][j] + " ");
			}
			System.out.println("");
		}
		}
		
		
	
	// checks if a certain candidate is in the tabu list
	public static boolean InTabuList(int row, int colomn){
		if(row>colomn){
			int temp = row;
			row = colomn;
			colomn = temp;
			
		}
		return(Tabu[row][colomn]>0);
	}
	// checks if a candidate passes the aspiration conditions
	public static boolean InAspiration(int i, int j){
		int [] tempSolution = new int[size];
		tempSolution = solution.clone();
		tempSolution[i] = solution[j];
		tempSolution[j] = solution[i];
		int tempCost = CalculateTotalCost(tempSolution);
		return (tempCost <  globalmin);
	//	return false;
	}
	//adds selected solution to the tabu list
	// decrements all the other solutions in the tabu by one
	// Increments frequency 
	public static void AddToTabuList(int row, int colomn){
		for(int i = 0; i < solution.length; i++){
			for(int j = i; j< solution.length ; j++){
				if(Tabu[i][j] >0){
					Tabu[i][j]--;
				}			
			}	
		}
		if(row>colomn){
			int temp = row;
			row = colomn;
			colomn = temp;
			
		}
		Tabu[row][colomn] = TabuTenure;
		Tabu[colomn][row]++;
	}
	// for calculation frequency base analysis 
	// returns 1 if the first set of number is less frequent 
	// returns 2 if the second set of numbers are less frequent 
	public static int FreqAnalysis(int row1, int col1, int row2, int col2){
		if(row1 > col1){
			int temp = row1;
			row1 = col1;
			col1= temp;	
		}
		if(row2 > col2){
			int temp = row2;
			row2 = col2;
			col2= temp;
		}
		
		if(Tabu[col1][row1]>= Tabu[col2][row2] ){
			return 1;
		}
		return 2;
		
	}
	// selects the best candidates in the neighborhood 
	public static int[] Bestcandidate(){
		int MinCost = (int) Double.POSITIVE_INFINITY;
		int [] BestSolution = new int[size];
		BestSolution = solution.clone();
		int row = 0;
		int column = 0; 
		//int maxNeighbours = changeGenerateRandomNumber(15)+5;
		//System.out.println(maxNeighbours);
		int maxNeighbours = size;
		for(int i = 0; i<maxNeighbours; i++){
			for(int j = i+1; j<size; j++){
				if(!InTabuList(solution[i],solution[j]) || InAspiration(i, j)){
					int [] tempSolution = new int[size];
					tempSolution = solution.clone();
					tempSolution[i] = solution[j];
					tempSolution[j] = solution[i];
					int tempCost = CalculateTotalCost(tempSolution);
					if(tempCost < MinCost){
						MinCost = tempCost;
						BestSolution = tempSolution;
						row = i;
						column = j;
					}
					if(tempCost == MinCost &&FreqAnalysis(row, column, i, j) == 1){
						MinCost = tempCost;
						BestSolution = tempSolution;
						row = i;
						column = j;
					}
				}	
			}
			
		}
		
		AddToTabuList(solution[row], solution[column]);
		return BestSolution;
	}
	
	// calculates cost for any 2 given indecies 
	public static int CalculateCost(int[] tempSolution, int row, int colomn){
		return flow[tempSolution[row]][tempSolution[colomn]] * distance[row][colomn];	
	}
	// calculates the total cost
	public static int CalculateTotalCost(int[] tempSolution){
		int total = 0;
		for(int i = 0; i< tempSolution.length; i++){
			for(int j = i+1; j<tempSolution.length; j++){
				total += CalculateCost(tempSolution, i,j);	
			}
		}
		return total;
	}
	
	// changes the initial solution randomely 
	public static int[] changeInitSolution(){
		int [] newSolution = new int[size];
		newSolution = solution.clone();
		for(int i=0; i<size; i++){
			int temp = newSolution[i];
			Random randomno = new Random();
			int rnd = randomno.nextInt(size);
			newSolution[i]= newSolution[rnd];
			newSolution[rnd] = temp;
		}
		
		return newSolution;
		
	}
	// generates a random number from 0 to the given number 
	public static int changeGenerateRandomNumber(int maxNum){
		Random randomno = new Random();
		int rnd = randomno.nextInt(maxNum);
		return rnd;
		
		
		
	}
	
	public static void main(String []args){
		//initArryas();
		//FillinArrays();
		//int[] temp = {5, 0, 6 ,4, 16, 12, 7, 19, 14, 18, 15, 10, 11, 1, 3, 8, 2, 9 ,13, 17 };
		//int cost = CalculateTotalCost(temp);
		//System.out.println(cost);
		
	//}
		//for(int x = 0; x< 3; x++){
		initArryas();
		FillinArrays();
		//int randomIterator = changeGenerateRandomNumber(100);
		//System.out.println("TabuTenure = " + TabuTenure);
		//printArrays();
		
		//solution = changeInitSolution();
		//System.out.print("initial Solution is = ");
		//for(int i = 0; i< solution.length ; i++ ){
		//	System.out.print(solution[i] + " ");
		//}
		//System.out.println("");
		for(int k = 0 ; k<MAXITERATION ; k++){
			//if(k == randomIterator){
			//	randomIterator = changeGenerateRandomNumber(100);
			//	TabuTenure = changeGenerateRandomNumber(50);
			//}
			solution = Bestcandidate();
			//System.out.println("Tabu list is = ");
			//for(int i = 0; i< solution.length ; i++ ){
			//	for(int j = 0 ; j < solution.length ; j++){
					//System.out.print(Tabu[i][j] + " ");
			//	}
				//System.out.println("");
			//}
			//System.out.print("Solution is = ");
			//for(int i = 0; i< solution.length ; i++ ){
			//	System.out.print(solution[i] + " ");
			//}
			//System.out.println("");
			//System.out.print("cost is = ");
			overAllCost = CalculateTotalCost(solution);
			if(globalmin > overAllCost){
				globalmin = overAllCost;
				
			}
			//System.out.println(overAllCost);
			//System.out.println("-----------------------------");
			//System.out.println(globalmin);
			//System.out.println(k);
			if(overAllCost == 1285){
				break;
			}
			counter = k;
		}
		System.out.println("Tabu list is = ");
		for(int i = 0; i< solution.length ; i++ ){
			for(int j = 0 ; j < solution.length ; j++){
				System.out.print(Tabu[i][j] + " ");
			}
			System.out.println("");
		}
		System.out.print("Solution is = ");
		for(int i = 0; i< solution.length ; i++ ){
			System.out.print(solution[i] + " ");
		}
		System.out.println("");
		System.out.print("cost is = ");
		System.out.println(overAllCost);
		System.out.println("number of itirations =" + counter);
		System.out.println("-----------------------------");
		
		
		
	//	}
	}
	
}
