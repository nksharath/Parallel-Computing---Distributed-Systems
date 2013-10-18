/*
 * hw2q3seq.java
 *
 * Version:
 *          $Id$
 *
 * Revisions:
 *          $Log$
 *
 */

/*
*@Problem       : Sequential code to calculate particle final position
*@Author        : Sharath Navalpakkam Krishnan : Batch : 4005 735-01
*@Version       : 1.0.1
*@LastModified  : 01/20/2013 2.45 PM
*
*/
//package hw2q3seq;

import edu.rit.util.Random;
import sun.security.jca.GetInstance;

public class hw2q3seq
{
    //Invoking Main function
    public static void main(String[] args)
    {
        //Initializing values of points x,y,z
        double x,y,z;
        x=0;
        y=0;
        z=0;
        //Number of steps,and seed
        long N,seed;
        N=0;
        seed=0;
        try
        {
            //Seed provided by the user
            seed=Long.parseLong(args[0]);
             //Input - value of N , Number of steps
            N=Long.parseLong(args[1]);
        }
        catch(Exception e)
        {
            System.out.println("Example: java hw2q3seq seed N");
            System.exit(1);
        }
        //Random function to generate numbers based on seed
        Random rand=Random.getInstance(seed);


        //Defines the probability
        int prob;
        //Timing starts here
        long t1=System.currentTimeMillis();
        for(long i=0;i<N;i++)
        {
            //Calculating probability
            prob=rand.nextInt(Integer.MAX_VALUE)%6;
            //Switch case to choose 1/6 probabilty
            switch(prob)
            {
                case 0:x=x-1;
                break;

                case 1:x=x+1;
                break;

                case 2:y=y-1;
                break;

                case 3:y=y+1;
                break;

                case 4:z=z-1;
                break;

                case 5:z=z+1;
                break;


            }
        }

        //Calculating distance from origin
        double distance=Math.sqrt((x*x)+(y*y)+(z*z));
        //Timing stops here
        long t2=System.currentTimeMillis();

        System.out.println(x+" "+y+" "+z);
        System.out.println(distance);
        System.out.println("SEQ TIME="+(t2-t1)+"msec");

    }
}
