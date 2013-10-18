/*
 * hw2q3smp.java
 *
 * Version:
 *          $Id$
 *
 * Revisions:
 *          $Log$
 *
 */

/*
*@Problem       : SMP code to calculate particle final position
*@Author        : Sharath Navalpakkam Krishnan : Batch : 4005 735-01
*@Version       : 1.0.1
*@LastModified  : 01/20/2013 2.45 PM
*
*/
//package hw2q3smp;
import edu.rit.pj.Comm;
import edu.rit.pj.LongForLoop;
import edu.rit.pj.ParallelRegion;
import edu.rit.pj.ParallelTeam;
import edu.rit.util.Random;
import sun.security.jca.GetInstance;
import edu.rit.pj.reduction.SharedLong;

public class hw2q3smp
{
    //Number of steps,and seed
    static long N,seed;
    //points x,y,z
    static SharedLong x,y,z;

    //Invoking Main function
    public static void main(String[] args) throws Exception
    {
        Comm.init(args);
        x=new SharedLong(0);
        y=new SharedLong(0);
        z=new SharedLong(0);

        try{
        //Seed provided by the user
        seed=Long.parseLong(args[0]);
        //Input - value of N , Number of steps
        N=Long.parseLong(args[1]);
        }
        catch(Exception e)
        {
            System.out.println("Example: java hw2q3smp seed N");
            System.exit(1);
        }

        //Timing starts here
        long t1=System.currentTimeMillis();
        //New parallel team and region
        new ParallelTeam().execute(new ParallelRegion()
        {
            public void run()throws Exception
            {
                execute(0,N,new LongForLoop()
                {
                    //Generating random number per thread
                    Random prng_thread=Random.getInstance(seed);
                    long count_thread=1;
                    long x1,y1,z1;
                    public void run(long first,long last)throws Exception
                    {
                        prng_thread.setSeed (seed);
                        //skipping one random number , for synchronization of random numbers
                        prng_thread.skip (first);

                        x1=0;
                        y1=0;
                        z1=0;

                        for(long counter=first;counter<=last;++counter)
                        {
                            //Calculating probability
                            int prob=prng_thread.nextInt(Integer.MAX_VALUE)%6;
                            //Switch case to choose 1/6 probabilty
                            switch(prob)
                            {
                                case 0:x1=x1-1;
                                break;
                                case 1:x1=x1+1;
                                break;
                                case 2:y1=y1-1;
                                break;
                                case 3:y1=y1+1;
                                break;
                                case 4:z1=z1-1;
                                break;
                                case 5:z1=z1+1;
                                break;
                            }
                        }
                    }
                    //Performing add up to x,y,z points
                    public void finish()
                    {
                        x.addAndGet(x1);
                        y.addAndGet(y1);
                        z.addAndGet(z1);
                    }
                });
            }
        });

        //Calculating the distance from origin
        double distance=Math.sqrt((x.longValue()*x.longValue())+(y.longValue()*y.longValue())+(z.longValue()*z.longValue()));
        //Timing stops here
        long t2=System.currentTimeMillis();
        System.out.println(x+" "+y+" "+z);
        System.out.println(distance);
        System.out.println("SMP TIME="+(t2-t1)+"msec");
    }
}




