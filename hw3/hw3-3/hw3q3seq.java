/*
 * hw3q3seq.java
 * 
 * Version:
 *          $Id$
 * 
 * Revisions:
 *          $Log$
 * 
 */

/*
*@Problem       : SEQ code to perform bubble sort
*@Author        : Sharath Navalpakkam Krishnan : Batch : 4005 735-01
*@Version       : 1.0.1
*@LastModified  : 02/05/2013 6.45 PM
*
*/
import edu.rit.util.Random;
import edu.rit.pj.Comm;
public class hw3q3seq 
{   
    //The number of numbers to sort 
    static int N;
    //The array to hold N numbers
    static int n[];
    //The seed value to generate random N numbers
    static long seed;
    //Declaring Random object
    static Random rand;

    public static void main(String[] args) throws Exception
    {
        Comm.init(args);
         if(args.length<2){
            System.out.println("Example:java -Dpj.np=4 hw3q3seq N seed");
            System.exit(1);
        }
        //Getting the Number of values from the user
        N=Integer.parseInt(args[0]);
        //Getting the seed value from the user 
        seed=Long.parseLong(args[1]);
        //Allocating array to hold the N values
        n=new int[N];
        //Creating a random instance with the given seed
        rand=Random.getInstance(seed);
        //Start Time
        long t1=System.currentTimeMillis();
        for(int i=0;i<N;i++)
        {
            //Generating random N numbers 
            n[i]=rand.nextInt(331);
            //System.out.println(n[i]);
        }
        
        //Performing bubble sort and swapping large vs small values
        for(int i=0;i<N;i++)
        {
            for(int j=0;j<i;j++)
            {
                if(n[i]<n[j])
                {
                    int temp=n[i];
                    n[i]=n[j];
                    n[j]=temp;
                }
            }
        }
        
        //stop time
        long t2=System.currentTimeMillis();
        
         
        // To display the sorted elements 
        
        /* for(int i=0;i<N;i++)
        {
           
            System.out.println(n[i]);
        }   */
        
        System.out.println("SEQ TIME ="+(t2-t1)+"msec");
       
     
        
    }
}
