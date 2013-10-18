//package latinsquareseq;
import edu.rit.pj.IntegerForLoop;
import edu.rit.pj.ParallelRegion;
import edu.rit.pj.ParallelTeam;
import java.util.ArrayList;

import edu.rit.util.Random;

/**
 * Filename: LatinSquareSeq.java
 * @author Rahul Nuggehalli Gopinathan - rnn4511
 */

/**
 * @author Rahul Nuggehalli Gopinathan - rnn4511
 *
 */

public class LatinSquareSeq {

//	static class Position {
//		int position;
//		int value;
//
//		public Position(int position, int value) {
//			this.position = position;
//			this.value = value;
//		}
//	}

	public int[][] latinGrid;
	public boolean validity = true;
	public boolean isFailure = false;
	Random randomNumber;
	int size;
        static LatinSquareSeq square;
        static LatinSquareSeq solution;

//	ArrayList<Position> positionFillers;

	public LatinSquareSeq() {

	}

	public LatinSquareSeq(int size) {

		this.size = size;
		latinGrid = new int[size][size];
		randomNumber = Random.getInstance(System.currentTimeMillis());
//		positionFillers = new ArrayList<LatinSquareSeq.Position>();

		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				latinGrid[i][j] = -99;
			}
		}

//		latinGrid[0][1] = 1;
//		latinGrid[0][3] = 3;
//
//		latinGrid[1][1] = 2;
//
//		latinGrid[2][2] = 3;
//
//		latinGrid[3][2] = 4;

		latinGrid[0][0] = 8;
		latinGrid[0][1] = 9;
		latinGrid[0][3] = 5;
		latinGrid[0][6] = 2;

		latinGrid[1][0] = 4;
		latinGrid[1][5] = 2;


		latinGrid[2][4] = 8;
		latinGrid[2][5] = 9;
		latinGrid[2][6] = 4;

		latinGrid[3][1] = 5;
		latinGrid[3][6] = 9;

		latinGrid[4][0] = 7;
		latinGrid[4][1] = 4;
		latinGrid[4][7] = 8;
		latinGrid[4][8] = 5;

		latinGrid[5][2] = 8;
		latinGrid[5][7] = 3;

		latinGrid[6][2] = 5;
		latinGrid[6][3] = 3;
		latinGrid[6][4] = 7;

		latinGrid[7][3] = 8;
		latinGrid[7][8] = 6;

		latinGrid[8][2] = 1;
		latinGrid[8][5] = 4;
		latinGrid[8][7] = 2;
		latinGrid[8][8] = 7;

	}

	public LatinSquareSeq(int[][] config) {

		validity = true;
		isFailure = false;
		this.size = config.length;
		this.latinGrid = config;

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

	public boolean hasAllVariablesAssigned(){

		for(int row = 0; row < this.size; row++){
			for(int col = 0; col < this.size; col++){
				if(latinGrid[row][col] == -99){
					return false;
				}
			}
		}

		return true;

	}

	public boolean isValid(){

		if(!this.isFailure){

			if(hasAllVariablesAssigned() && hasSatisfiedConstratints()){
				return true;
			}

		}

		return false;

	}


	public void print(){

		for(int row = 0; row < this.size; row++){
			for(int col = 0; col < this.size; col++){

					System.out.print(latinGrid[row][col] + " ");

			}
			System.out.println();
		}

	}

	public ArrayList<LatinSquareSeq> getNeighbours(){

		ArrayList<LatinSquareSeq> neighbours = new ArrayList<LatinSquareSeq>();


		for(int row = 0; row < size; row++){
			int[] rowValues = this.latinGrid[row];

			for(int i = 0; i < rowValues.length; i++){

				if(rowValues[i] == -99){

					for(int whatToPut = 0; whatToPut < size; whatToPut++){

						int[][] temp = new int[size][size];
						int[] rowTemp = new int[size];

						temp = this.latinGrid.clone();
						rowTemp = temp[row].clone();
						rowTemp[i] = whatToPut + 1;
						temp[row] = rowTemp;
						neighbours.add(new LatinSquareSeq(temp));

					}

					return neighbours;

				}

			}
		}

		return null;

	}

	LatinSquareSeq solve(LatinSquareSeq initialOne){
		LatinSquareSeq failure = new LatinSquareSeq();
		failure.setAsFailure();

		if(initialOne.hasAllVariablesAssigned()){
			return initialOne;
		}

		ArrayList<LatinSquareSeq> neighbours = initialOne.getNeighbours();

		for (int i = 0; i < neighbours.size(); i++) {

			LatinSquareSeq newConfig = neighbours.get(i);
			LatinSquareSeq result;

			if(newConfig.hasSatisfiedConstratints()){

				result = solve(newConfig);

				if(!result.isFailure){
					result.validity = true;
					result.isFailure = false;
					return result;
				}

			}

		}

		return failure;


	}

	public static void main(String[] args)throws Exception {

		square = new LatinSquareSeq(13);
		//LatinSquareSeq solution;
                final long start = System.currentTimeMillis();
                 new ParallelTeam().execute(new ParallelRegion()
        {
            public void run()throws Exception
            {
                final ArrayList<LatinSquareSeq> firstNeighbour = square.getNeighbours();;

                execute(1,firstNeighbour.size()-1,new IntegerForLoop()
                {
                    public void run(int first,int last)throws Exception
                    {
//                        for(long counter=first;counter<=last;counter++)
//                        {
                            for(int i=first;i<=last;i++)
                            {
                                solution = square.solve(firstNeighbour.get(i));


		//long end = System.currentTimeMillis();

		if(solution.isValid()){
                        long stop = System.currentTimeMillis();
			solution.print();
                        System.out.println("SMP = "+(stop-start));
//                        System.out.println();
//                        System.out.println();
                        System.exit(1);
		} else {
			//System.out.println("NO RESULT");
                       // System.exit(1);
		}
                }

                    }
                });
                        }
        });

		//System.out.println("Time : " + (end - start));



	}

}
