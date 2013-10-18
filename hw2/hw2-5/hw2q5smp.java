/*
 * hw2q5smp.java
 *
 * Version:
 *          $Id$
 *
 * Revisions:
 *          $Log$
 *
 */

/*
*@Problem       : SMP code to count number of primes using Sieve of Eratosthenes
*@Author        : Sharath Navalpakkam Krishnan : Batch : 4005 735-01
*@Version       : 1.0.1
*@LastModified  : 01/20/2013 4.45 PM
*
*/
//package hw2q5smp1;

import edu.rit.pj.Comm;
import edu.rit.pj.IntegerForLoop;
import edu.rit.pj.ParallelRegion;
import edu.rit.pj.ParallelTeam;
import edu.rit.util.Random;
import sun.security.jca.GetInstance;
import edu.rit.pj.reduction.SharedInteger;

public class hw2q5smp
{
    //Upper limit of prime numbers
    static int n;
    //To count the number of primes
    static SharedInteger count=new SharedInteger(0);
    //A boolean array to record marked/unmarked elements
    static boolean flag[];

    public static void main(String[] args) throws Exception
    {
        Comm.init(args);
        try{
        //Input-the value of n
        n=Integer.parseInt(args[0]);
        }
        catch(Exception e)
        {
            System.out.println("Example: java hw2q5smp NumberOfPrimes");
            System.exit(1);
        }
        //Initialising the flag array
        flag=new boolean[n+1];
        //Timing starts here
        long t1=System.currentTimeMillis();
        //A new Parallel team to initialise the flag array
        new ParallelTeam().execute(new ParallelRegion()
        {
            public void run()throws Exception
            {
                execute(2,(n-1),new IntegerForLoop()
                {
                    public void run(int first,int last)throws Exception
                    {

                        for(int counter=first;counter<=last;counter++)
                        {
                         flag[counter]=true;
                        }
                    }
                });
            }
        });

      //A new parallel team to run the Sieve algorithm
      new ParallelTeam().execute(new ParallelRegion()
        {
            public void run()throws Exception
            {
                execute(2,(int)Math.sqrt(n),new IntegerForLoop()
                {
                    public void run(int first,int last)throws Exception
                    {

                        for(int counter=first;counter<=last;counter++)
                        {
                           if(flag[counter])
                            for(int j=counter*counter;j<=n;j=j+counter)
                             flag[j]=false;
                        }
                    }
                });
            }
        });

        //A new parallel team to count the number of primes
        new ParallelTeam().execute(new ParallelRegion()
        {
            public void run()throws Exception
            {

                execute(0,n,new IntegerForLoop()
                {
                    int countlocal=0;
                    public void run(int first,int last)throws Exception
                    {
                        for(int counter=first;counter<=last;counter++)
                        {
                            if(flag[counter])
                                ++countlocal;

                        }

                    }

                    @Override
                    public void finish() throws Exception
                    {
                        count.addAndGet(countlocal);
                    }

                });
            }
        });
        //Timing ends here
        long t2=System.currentTimeMillis();
        System.out.println(count.get());
        System.out.println("SMP Time="+(t2-t1)+"msec");
    }
}


