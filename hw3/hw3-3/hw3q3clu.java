/*
 * hw3q3clu.java
 * 
 * Version:
 *          $Id$
 * 
 * Revisions:
 *          $Log$
 * 
 */

/*
*@Problem       : CLU code to perform bubble sort
*@Author        : Sharath Navalpakkam Krishnan : Batch : 4005 735-01
*@Version       : 1.0.1
*@LastModified  : 02/05/2013 6.45 PM
*
*/
import edu.rit.mp.buf.IntegerItemBuf;
import edu.rit.pj.BarrierAction;
import edu.rit.pj.Comm;
import edu.rit.util.Random;
import edu.rit.util.Range;
import edu.rit.mp.IntegerBuf;

public class hw3q3clu
{

    //Declaring cluster paramters such as world size and rank
    static Comm world;
    static int size;
    static int rank;
    
    //Invoking main function
    public static void main(String[] args) throws Exception
    {
        
        Comm.init(args);
        //Initialising cluster parameters such as world rank and size
        Comm world = Comm.world();
        rank = world.rank();
        size = world.size();
        if(args.length<2){
            System.out.println("Example:java -Dpj.np=4 hw3q3clu N seed");
            System.exit(1);
        }
            
        //Receiving the number of elements from user 
        int N = Integer.parseInt(args[0]);
        //Receiving the seed value from user 
        int seed = Integer.parseInt(args[1]);
        //The full matrix that will hold the elements
        int[] fullMatrix = new int[N];
        //Local matrix for per process execution
        int[] myMatrix = new int[N / size];
        
        //To generate random numbers 
        Random rand = Random.getInstance(seed);
        
        //Start Time
        long t1=System.currentTimeMillis();
        //Skipping random numbers : so as to generate unique randoms 
        rand.skip((N / size) * rank);
        for (int i = 0; i < N / size; i++) {
            myMatrix[i] = rand.nextInt(331);
        }

        //Performing initial round of sorting : Round 1 
        for (int i = 0; i < myMatrix.length; i++) {
            for (int j = 0; j < i; j++) {
                if (myMatrix[i] < myMatrix[j]) {
                    int temp = myMatrix[i];
                    myMatrix[i] = myMatrix[j];
                    myMatrix[j] = temp;
                }
            }

        }
        //A buffer to hold the result array after sorting in first round
        IntegerBuf[] completeArray = IntegerBuf.sliceBuffers(fullMatrix, new Range(0, N - 1).subranges(size));
        
        //Gathering all sorted elements from all processors 
        world.allGather(IntegerBuf.buffer(myMatrix), completeArray);

        //Performing remaining rounds of bubble sort in parallel
        for (int i = 0; i < N / size; i++) {
            IntegerBuf myArray = completeArray[rank];
            IntegerBuf next = null;
            IntegerBuf prev = null;
            
            // Checking at each stage if the upper bound of local elements is greater than the lower bound of the next successive element
            if (rank == 0) {
                next = completeArray[rank + 1];
                if (myArray.get(myArray.length() - 1) > next.get(0)) {
                    myArray.put(myArray.length() - 1, next.get(0));
                }
            }
            
            else if (rank == size - 1) {
                prev = completeArray[rank - 1];
                if (myArray.get(0) < prev.get(prev.length() - 1)) {
                    myArray.put(0, prev.get(prev.length() - 1));
                }
            } else {
                next = completeArray[rank + 1];
                prev = completeArray[rank - 1];
                if (myArray.get(myArray.length() - 1) > next.get(0)) {
                    myArray.put(myArray.length() - 1, next.get(0));
                }
                if (myArray.get(0) < prev.get(prev.length() - 1)) {
                    myArray.put(0, prev.get(prev.length() - 1));
                }
            }

            //Accumulating matrix from myArray
            for (int g = 0; g < myMatrix.length; g++) {
                myMatrix[g] = myArray.get(g);
            }

            // Again performing bubble sort locally 
            for (int ii = 0; ii < myMatrix.length; ii++) {
                for (int jj = 0; jj < ii; jj++) {
                    if (myMatrix[ii] < myMatrix[jj]) {
                        int temp = myMatrix[ii];
                        myMatrix[ii] = myMatrix[jj];
                        myMatrix[jj] = temp;
                    }
                }

            }
            // Gathering all sorted elements from all processors 
            world.allGather(IntegerBuf.buffer(myMatrix), completeArray);
        }
        
        if(rank == 0){
            long t2=System.currentTimeMillis();
            System.out.println("CLU TIME = "+(t2-t1)+"msec");
            
            // To display the sorted elements 
            
           /* for(int i = 0; i < completeArray.length; i++){
                IntegerBuf hereOne = completeArray[i];
                for(int j = 0; j<hereOne.length(); j++){
                    System.out.println(hereOne.get(j));
                }
            }*/
        }
      
    }
}
