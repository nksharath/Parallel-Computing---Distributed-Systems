/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
//package latinsquareclu;

import java.util.ArrayList;
import edu.rit.pj.reduction.SharedIntegerArray;
import edu.rit.util.Random;
import edu.rit.mp.Buf;
import edu.rit.mp.IntegerBuf;
import edu.rit.pj.Comm;
import edu.rit.pj.CommRequest;
import edu.rit.util.Range;
import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * Filename: LatinSquareSeq.java
 * @author Sharath Navalpakkam Krishnan - sxn9447
 * @author Rahul Nuggehalli Gopinathan - rnn4511
 */


public class LatinSquareClu {


	public SharedIntegerArray[] sharedGrid;
	public int[][] latinGrid;
	public boolean validity = true;
	public boolean isFailure = false;
	public boolean loopResult = false;
	Random randomNumber;
        static Comm world;
        static CommRequest req;
	int size;
        static int rank;
	static long t1;
        static long t2;
        
        static FileWriter file;
        static PrintWriter out;
	

	static ArrayList<LatinSquareClu> allSolutions = new ArrayList<LatinSquareClu>();
	static ArrayList<LatinSquareClu> allResults = new ArrayList<LatinSquareClu>();
	

	public LatinSquareClu() {

	}
	
	public void setGrid(int[][] incoming){
		this.latinGrid = incoming.clone();
	}
	
	public int[][] getGrid(){
		return this.latinGrid;
	}

	public LatinSquareClu(int size) {

		this.size = size;
		latinGrid = new int[size][size];
		sharedGrid = new SharedIntegerArray[size];
		randomNumber = Random.getInstance(System.currentTimeMillis());
		// positionFillers = new ArrayList<LatinSquareSeq.Position>();

		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				latinGrid[i][j] = -99;
			}

		}

		// latinGrid[0][1] = 1;
		// latinGrid[0][3] = 3;
		//
		// latinGrid[1][1] = 2;
		//
		// latinGrid[2][2] = 3;
		//
		// latinGrid[3][2] = 4;

		// latinGrid[0][0] = 8;
		// latinGrid[0][1] = 9;
		// latinGrid[0][3] = 5;
		// latinGrid[0][6] = 2;
		//
		// latinGrid[1][0] = 4;
		// latinGrid[1][5] = 2;
		//
		//
		// latinGrid[2][4] = 8;
		// latinGrid[2][5] = 9;
		// latinGrid[2][6] = 4;
		//
		// latinGrid[3][1] = 5;
		// latinGrid[3][6] = 9;
		//
		// latinGrid[4][0] = 7;
		// latinGrid[4][1] = 4;
		// latinGrid[4][7] = 8;
		// latinGrid[4][8] = 5;
		//
		// latinGrid[5][2] = 8;
		// latinGrid[5][7] = 3;
		//
		// latinGrid[6][2] = 5;
		// latinGrid[6][3] = 3;
		// latinGrid[6][4] = 7;
		//
		// latinGrid[7][3] = 8;
		// latinGrid[7][8] = 6;
		//
		// latinGrid[8][2] = 1;
		// latinGrid[8][5] = 4;
		// latinGrid[8][7] = 2;
		// latinGrid[8][8] = 7;

	}

	public LatinSquareClu(int[][] config) {

		validity = true;
		isFailure = false;
		this.size = config.length;
		this.latinGrid = config;
		sharedGrid = new SharedIntegerArray[this.size];

		for (int i = 0; i < size; i++) {
			sharedGrid[i] = new SharedIntegerArray(latinGrid[i]);
		}

	}

	public void setAsFailure() {
		this.validity = false;
		this.isFailure = true;
	}

	public boolean hasSatisfiedConstratints()throws Exception {
            
           

		for (int row = 0; row < this.size; row++) {
			for (int col = 0; col < this.size; col++) {

				int element = latinGrid[row][col];

				for (int innerCol = 0; innerCol < size; innerCol++) {

					if (element == latinGrid[row][innerCol] && innerCol != col
							&& element != -99) {
						this.validity = false;
						this.isFailure = true;
						return false;
					}

				}

				for (int innerRow = 0; innerRow < size; innerRow++) {

					if (element == latinGrid[innerRow][col] && innerRow != row
							&& element != -99) {
						this.validity = false;
						this.isFailure = true;
						return false;
					}

				}

			}

		}

		this.validity = true;
		return true;

	}

	public boolean hasAllVariablesAssigned() {

		for (int row = 0; row < this.size; row++) {
			for (int col = 0; col < this.size; col++) {
				if (latinGrid[row][col] == -99) {
					return false;
				}
			}
		}

		return true;

	}

	public boolean isValid()throws Exception {

		if (!this.isFailure) {

			if (hasAllVariablesAssigned() && hasSatisfiedConstratints()) {
				return true;
			}

		}

		return false;

	}

	public void print() {

		for (int row = 0; row < this.size; row++) {
			for (int col = 0; col < this.size; col++) {

				System.out.print(latinGrid[row][col] + " ");

			}
			System.out.println();
		}

	}

	public ArrayList<LatinSquareClu> getNeighbours() {

		ArrayList<LatinSquareClu> neighbours = new ArrayList<LatinSquareClu>();

		for (int row = 0; row < size; row++) {
			int[] rowValues = this.latinGrid[row];

			for (int i = 0; i < rowValues.length; i++) {

				if (rowValues[i] == -99) {

					for (int whatToPut = 0; whatToPut < size; whatToPut++) {

						int[][] temp = new int[size][size];
						int[] rowTemp = new int[size];

						temp = this.latinGrid.clone();
						rowTemp = temp[row].clone();
						rowTemp[i] = whatToPut + 1;
						temp[row] = rowTemp;
						neighbours.add(new LatinSquareClu(temp));

					}

					return neighbours;

				}

			}
		}

		return null;

	}
	
	static public void getAllSolutions(LatinSquareClu initialOne)throws Exception{
		
            
		
		ArrayList<LatinSquareClu> neighbours = initialOne.getNeighbours();

		for(int i = 0; i < neighbours.size(); i++){
			LatinSquareClu newConfig = neighbours.get(i);
			
                        
                        
			if(newConfig.hasSatisfiedConstratints()){
				
				if(newConfig.hasAllVariablesAssigned()){
//					if(!allNeighbours.contains(newConfig))
                                                t2=System.currentTimeMillis();
						allSolutions.add(newConfig);	
                                                file=new FileWriter("rank_"+rank+".txt");
                                                out=new PrintWriter(file);
						newConfig.printfile();
						out.println();
                                                out.println("CLU TIME = "+(t2-t1)+"msec");
                                                out.flush();
						world.floodSend(IntegerBuf.emptyBuffer());
                                                break;
                                               
                                               
                                              
					}else{
                                                if(req.isFinished())break;
						getAllSolutions(newConfig);
					}
				
			} 
			
		}
		
	}
	
        void printfile()
        {
		for (int row = 0; row < this.size; row++) {
			for (int col = 0; col < this.size; col++) {

				out.print(latinGrid[row][col] + " ");

			}
			out.println();
                        out.flush();
		}
                out.close();

	}
            
        

	
	public static void main(String[] args) throws Exception 
        {
            t1=System.currentTimeMillis();
            Comm.init(args);
            world=Comm.world();
            int sizep=world.size();
            rank=world.rank();  
            

		LatinSquareClu square = new LatinSquareClu(Integer.parseInt(args[0]));
                ArrayList<LatinSquareClu> firstNeighbour = square.getNeighbours();
                Range chunk=new Range(0,firstNeighbour.size()-1).subrange(sizep, rank);
                int lb=chunk.lb();
                int ub=chunk.ub();
                req=new CommRequest();
                world.floodReceive (IntegerBuf.emptyBuffer(), req);
                
                for(int counter=lb;counter<=ub;counter++)
                            {
                                if(req.isFinished())
						break;
                                 getAllSolutions(firstNeighbour.get(counter));
                                   
                            }
              

		

		
		//System.out.println(allSolutions.size());


	}

}
