/*
 * hw2q4smp.java
 *
 * Version:
 *          $Id$
 *
 * Revisions:
 *          $Log$
 *
 */

/*
*@Problem       : SMP code to calculate prefix sum
*@Author        : Sharath Navalpakkam Krishnan : Batch : 4005 735-01
*@Version       : 1.0.1
*@LastModified  : 01/20/2013 4.45 PM
*
*/
//package hw2q4smp;
import edu.rit.pj.Comm;
import edu.rit.pj.BarrierAction;
import edu.rit.pj.IntegerForLoop;
import edu.rit.pj.ParallelRegion;
import edu.rit.pj.ParallelTeam;
import edu.rit.util.Random;
import sun.security.jca.GetInstance;
import edu.rit.pj.reduction.SharedInteger;
import java.io.*;

public class hw2q4smp
{
    //Declaring n value
    static int n;
    //An array to hold original values of x
    static int x[];
    //A copy array of x
    static int y[];
    //Loop Variable
    static int j;
    //A file writer to write output to a file
    static FileWriter fstream;
    //A buffered writer to write output to a file
    static BufferedWriter out;
    //Invoking Main Function
    public static void main(String[] args) throws Exception
    {
        Comm.init(args);
        int seed=0;
        try{
        //Input - n value
        n=Integer.parseInt(args[0]);
        // Input- Seed value
        seed=Integer.parseInt(args[1]);
        fstream=new FileWriter(args[2]);
        }
        catch(Exception e)
        {
            System.out.println("Example: java hw2q4smp n seed filename");
            System.exit(1);
        }
        x=new int[n];
        y=new int[n];
        ParallelTeam TeamObj=new ParallelTeam();
        //Creating random number object
        Random rand=Random.getInstance(seed);


        out=new BufferedWriter(fstream);

        //Generating random numbers for x and y
        for(int i=0;i<n;i++)
        {
            x[i]=rand.nextInt(10);
            y[i]=x[i];

        }
        out.write("ORIGINAL DATA");
        out.newLine();
        for(int a=0;a<n;a++)
            out.write(""+x[a]+" ");
        out.newLine();
        out.newLine();
        //Timing starts here
        long t1=System.currentTimeMillis();

        for(j=0;j<=(int)(Math.log10(n) / Math.log10(2));j++)
        {
        //Creating New parallel Team and Parallel Region
        TeamObj.execute(new ParallelRegion()
        {
            public void run()throws Exception
            {
                execute((int)Math.pow(2,j),n-1,new IntegerForLoop()
                {
                    public void run(int first,int last)throws Exception
                    {
                        for(int counter=first;counter<=last;counter++)
                        {
                            y[counter]=x[counter-(int)Math.pow(2,j)] + x[counter];
                        }
                        }},
                        //Creating new barrier action for sequential dependencies
                        new BarrierAction()
                        {
                            @Override
                            public void run() throws Exception
                            {
                                for(int k=0;k<n;k++)
                                    out.write(y[k]+" ");
                                // System.out.print(y[k]+" , ");
                            }
                        });
            }
        });

        //System.out.println();
        out.newLine();

        for(int a=0;a<n;a++)
            x[a]=y[a];
        }
        //Timing stops here
        long t2=System.currentTimeMillis();
        out.newLine();
        out.write("FINAL DATA");
        out.newLine();
        for(int a=0;a<n;a++)
            out.write(""+x[a]+" ");
        out.newLine();
        out.write("SEQ TIME="+(t2-t1)+"msec");
        //Flushing the output file
         out.flush();
         //Closing the file
         out.close();

        System.out.println("SMP TIME="+(t2-t1)+" msec");


    }
}
