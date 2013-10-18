/*
 * hw4q2clu.java
 * 
 * Version:
 *          $Id$
 * 
 * Revisions:
 *          $Log$
 * 
 */

/*
*@Problem       : CLU code to implement mailman problem
*@Author        : Sharath Navalpakkam Krishnan : Batch : 4005 735-01
*@Version       : 1.0.1
*@LastModified  : 02/20/2013 2.45 PM
*
*/
//package hw4q2clu;
import edu.rit.util.Random;
import edu.rit.mp.buf.*;
import edu.rit.pj.reduction.*;
import edu.rit.pj.Comm;
import edu.rit.util.Range;


public class hw4q2clu
{
    public static void main(String[]args)throws Exception
    {
        if(args.length!=2)
        {
            System.out.println("Example : java -Dpj.np=7 hw4q2clu 1 100 ");
            System.exit(1);
        }
        //Comm object
        Comm.init(args);
        //Initialise world
        Comm world=Comm.world();
        //get size
        int size=world.size();
        //get rank
        int rank=world.rank();
        //Get the seed from user
        int seed=Integer.parseInt(args[0]);
        //Get value of N from user
        int N=Integer.parseInt(args[1]);
        
        long t1=System.currentTimeMillis();
        Random rand=Random.getInstance(seed);
        int count=0;
        //x , y co-ordiantes
        int x=0,y=0;
        //Dividing problem size with available number of processors
        Range[] range=new Range(0,N-1).subranges(size);
        Range myrange=range[rank];
        int lb=myrange.lb();
        int ub=myrange.ub();
        IntegerItemBuf buffx,buffy;
        rand.skip(lb);
        //Generate probability and compute points
        while(lb<=ub)
        {
            
            int temp=rand.nextInt(1000);
            
            if(temp>0 && temp <=500)
                temp=12;
            else
                if(temp>500 && temp<=750)
                    temp=14;
                else
                    if(temp>750 && temp<=875)
                        temp=81;
                    else
                        if(temp>875 && temp<=1000)
                            temp=82;
           
            switch(temp)
            {
                case 12:x++;
                ++lb;
                break;
                case 14:x--;
                ++lb;
                break;
                case 81:y++;
                ++lb;
                break;
                case 82:y--;
                ++lb;
                break;
                    
            }
        }
        buffx=IntegerItemBuf.buffer(x);
        buffy=IntegerItemBuf.buffer(y);
        //Perform allreduce to get x and y values
        world.allReduce(buffx,IntegerOp.SUM);
        world.allReduce(buffy,IntegerOp.SUM);
        if(rank==0)
        {
            System.out.println(buffx.item+" "+buffy.item);
            System.out.println(Math.sqrt((buffx.item*buffx.item)+(buffy.item*buffy.item)));
            long t2=System.currentTimeMillis();
            System.out.println("SEQ TIME="+ (t2-t1)+" msec");
        }
    }

}
