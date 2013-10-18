/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
//package latinsquareseq;

import edu.rit.pj.IntegerForLoop;
import edu.rit.pj.ParallelRegion;
import edu.rit.pj.ParallelTeam;
import java.util.ArrayList;
import edu.rit.pj.reduction.SharedIntegerArray;
import edu.rit.util.Random;

/**
 * Filename: LatinSquareSMP1.java
 * @author Rahul Nuggehalli Gopinathan - rnn4511
 */

/**
 * @author Rahul Nuggehalli Gopinathan - rnn4511
 *
 */
public class LatinSquareSMP1 {


	public SharedIntegerArray[] sharedGrid;
	public int[][] latinGrid;
	public boolean validity = true;
	public boolean isFailure = false;
	public boolean loopResult = false;
	Random randomNumber;
	int size;
	long start = System.currentTimeMillis();


	static ArrayList<LatinSquareSMP1> allSolutions = new ArrayList<LatinSquareSMP1>();
	static ArrayList<LatinSquareSMP1> allResults = new ArrayList<LatinSquareSMP1>();


	public LatinSquareSMP1() {

	}

	public void setGrid(int[][] incoming){
		this.latinGrid = incoming.clone();
	}

	public int[][] getGrid(){
		return this.latinGrid;
	}

	public LatinSquareSMP1(int size) {

		this.size = size;
		latinGrid = new int[size][size];
		sharedGrid = new SharedIntegerArray[size];
		randomNumber = Random.getInstance(System.currentTimeMillis());
		// positionFillers = new ArrayList<LatinSquareSMP1.Position>();

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

		for (int i = 0; i < size; i++) {
			sharedGrid[i] = new SharedIntegerArray(latinGrid[i]);
		}

	}

	public LatinSquareSMP1(int[][] config) {

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

	public boolean hasSatisfiedConstratints() {

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

	public boolean isValid() {

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

	public ArrayList<LatinSquareSMP1> getNeighbours() {

		ArrayList<LatinSquareSMP1> neighbours = new ArrayList<LatinSquareSMP1>();

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
						neighbours.add(new LatinSquareSMP1(temp));

					}

					return neighbours;

				}

			}
		}

		return null;

	}

	public void getAllSolutions(LatinSquareSMP1 initialOne){


		ArrayList<LatinSquareSMP1> neighbours = initialOne.getNeighbours();

		for(int i = 0; i < neighbours.size(); i++){
			LatinSquareSMP1 newConfig = neighbours.get(i);

			if(newConfig.hasSatisfiedConstratints()){

				if(newConfig.hasAllVariablesAssigned()){
//					if(!allNeighbours.contains(newConfig))
						allSolutions.add(newConfig);
						newConfig.print();
						System.out.println();
						long stop = System.currentTimeMillis();
						System.out.println("NEW SMP Time : " + (stop - start));
						System.exit(-1);
					}else{
						getAllSolutions(newConfig);
					}

			}

		}

	}




	public static void main(String[] args) throws Exception {

		final LatinSquareSMP1 square = new LatinSquareSMP1(16);

                final long start = System.currentTimeMillis();

                 new ParallelTeam().execute(new ParallelRegion()
        {
            public void run()throws Exception
            {
                final ArrayList<LatinSquareSMP1> firstNeighbour = square.getNeighbours();;

                execute(1,firstNeighbour.size()-1,new IntegerForLoop()
                {
                    public void run(int first,int last)throws Exception
                    {
//                        for(long counter=first;counter<=last;counter++)
//                        {
                            for(int i=first;i<=last;i++)
                            {

                                square.getAllSolutions(firstNeighbour.get(i));
                            }
                    }
                });
            }});


		System.out.println(allSolutions.size());


	}

}
