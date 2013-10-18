/*
 * hw2q2seq.java
 *
 * Version:
 *          $Id$
 *
 * Revisions:
 *          $Log$
 *
 */

/*
*@Problem       : Sequential code to calculate  Collatz Conjecture
*@Author        : Sharath Navalpakkam Krishnan : Batch : 4005 735-01
*@Version       : 1.0.1
*@LastModified  : 01/20/2013 3.45 PM
*
*/
//package hw2q2seq;
import java.util.*;

public class hw2q2seq
{
    //Invoking Main Function
    public static void main(String[] args)
    {
        //Max Iterations
        long maxiter=0;
        //Local count variable
        long count=0;
        //Records count of number of failures
        long failcount = 0;
        //Value of N , provided by user
        long N=0;
        //x Value to perform the conjecture on
        double x;

        try
        {
            // Input N from user
            N=Long.parseLong(args[0]);
            //Max iterations input from user
            maxiter=Long.parseLong(args[1]);
        }
        catch(Exception e)
        {
            System.out.println("Example : java hw2q2seq N maxiter");
            System.exit(1);
        }

        //Timing starts here
        long t1=System.currentTimeMillis();

        //For N iterations
        for(long i=1;i<=N;i++)
        {
            //Initializing x value
            x=i;
            //Initializing local count to 0 every iteration
            count = 0;

            while(x>1 && count < maxiter)
            {
                if(x%2==0)
                {
                    x=x/2;
                    count++;
                }
                else
                {
                    x=3*x+1;
                    count++;
                }
            }

             if(x > 1 && count >= maxiter)
             {
                 //Counting the number of failure iterations
                 failcount++;
             }
        }
        //End of timing
        long t2=System.currentTimeMillis();
        System.out.println("SEQ TIME="+(t2-t1)+"msec");
        System.out.println(failcount);
    }
}

