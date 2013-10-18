/*
 * hw2q4seq.java
 *
 * Version:
 *          $Id$
 *
 * Revisions:
 *          $Log$
 *
 */

/*
*@Problem       : Sequential code to calculate prefix sum
*@Author        : Sharath Navalpakkam Krishnan : Batch : 4005 735-01
*@Version       : 1.0.1
*@LastModified  : 01/20/2013 4.45 PM
*
*/
//package hw2q4seq;

import edu.rit.util.Random;
import java.io.*;

public class hw2q4seq
{
    //Invoking Main Function
    public static void main(String[] args) throws Exception
    {

        //Declaring n value
        int n=0;
        //Seed Value
        int seed=0;
        FileWriter fstream=null;
        try{
        //Input - n value
        n=Integer.parseInt(args[0]);
        // Input- Seed value
        seed=Integer.parseInt(args[1]);
        //A file writer to write output to a file
        fstream=new FileWriter(args[2]);
        }
        catch(Exception e)
        {
            System.out.println("Example: java hw2q4seq n seed filename");
            System.exit(1);
        }


        BufferedWriter out=new BufferedWriter(fstream);

        //An array to hold original values of x
        int x[]=new int[n];
        //Creating random number
        Random rand=Random.getInstance(seed);
        //A copy array of x
        int y[]=new int[n];



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
        for(int j=0;j<=(int)(Math.log10(n) / Math.log10(2));j++)
        {
            for(int i=(int)Math.pow(2,j);i<n;i++)
            {
                y[i]=x[i-(int)Math.pow(2,j)] + x[i];

            }

        for(int k=0;k<n;k++)
        {
            //System.out.print(y[k]+" , ");
            out.write(y[k]+"  ");
        }
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

        System.out.println("SEQ TIME="+(t2-t1));

    }
}
