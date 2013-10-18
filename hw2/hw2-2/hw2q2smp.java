/*
 * hw2q2smp.java
 *
 * Version:
 *          $Id$
 *
 * Revisions:
 *          $Log$
 *
 */

/*
*@Problem       : SMP code to calculate  Collatz Conjecture
*@Author        : Sharath Navalpakkam Krishnan : Batch : 4005 735-01
*@Version       : 1.0.1
*@LastModified  : 01/20/2013 3.45 PM
*
*/
//package hw2q2smp;

import edu.rit.pj.Comm;
import edu.rit.pj.LongForLoop;
import edu.rit.pj.ParallelRegion;
import edu.rit.pj.ParallelTeam;
import edu.rit.pj.reduction.SharedLong;
import java.util.*;

public class hw2q2smp
{
    //Max Iterations
    static long maxiter;
    //Value of N , provided by user
    static long N;
    //Records count of number of failures
    static SharedLong failcount;

  //Invoking Main Function
    public static void main(String[] args) throws Exception
    {
        Comm.init(args);
        try
        {
            // Input N from user
            N=Long.parseLong(args[0]);
            //Max iterations input from user
            maxiter=Long.parseLong(args[1]);
        }
        catch(Exception e)
        {
            System.out.println("Example: java hw2q2smp N maxiter");
            System.exit(1);
        }
        failcount=new SharedLong(0);

        //Timing starts here
        long t1=System.currentTimeMillis();

        //Creating a new Parallel Team and Parallel Region
        new ParallelTeam().execute(new ParallelRegion()
        {
            public void run()throws Exception
            {
                execute(1,N,new LongForLoop()
                {
                    public void run(long first,long last)throws Exception
                    {
                        for(long counter=first;counter<=last;counter++)
                        {
                            //Initializing x value
                            double x=counter;
                            //Initializing local count to 0 every iteration
                            long count=0;
                            while(x>1 && count<maxiter)
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
                                 failcount.incrementAndGet();
                             }
                        }
                    }
                });
            }
        });

        //End of timing
        long t2=System.currentTimeMillis();
        System.out.println("SMP TIME="+(t2-t1)+"msec");

        System.out.println(failcount.get());
    }
}

