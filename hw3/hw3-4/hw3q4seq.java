/*
 * hw3q4seq.java
 * 
 * Version:
 *          $Id$
 * 
 * Revisions:
 *          $Log$
 * 
 */

/*
*@Problem       : SEQ code to perform matrix multiplication 
*@Author        : Sharath Navalpakkam Krishnan : Batch : 4005 735-01
*@Version       : 1.0.1
*@LastModified  : 02/05/2013 3.25 PM
*
*/
//package hw3q4seq;
import edu.rit.pj.Comm;
import edu.rit.util.Random;
import java.io.*;
public class hw3q4seq {

    //Invoking main function 
    public static void main(String[] args) throws Exception
    {
        Comm.init(args);
         if(args.length<3){
            System.out.println("Example: java hw3q4seq N Seed filename");
            System.exit(1);
        }
        //Getting the dimension of the matrix from the user 
        int n=Integer.parseInt(args[0]);
        //Getting the seed from the user    
        long seed=Long.parseLong(args[1]);
        //Random Instance to generate random numbers 
        Random rand1=Random.getInstance(seed);
        //Start Time
        long t1=System.currentTimeMillis();
        //matrix a
        int a[][]=new int[n][n];
        //matrix b 
        int b[][]=new int[n][n];
        //matrix c to hold the result
        int c[][]=new int[n][n];
        for(int i=0;i<n;i++)
            for(int j=0;j<n;j++)
            {
                //Generating matrix a
                a[i][j]=rand1.nextInt(10);
                //Generating matrix b - with an increament of 10 
                b[i][j]=a[i][j]+10;
                //Initialising the result matrix c
                c[i][j]=0;
            }
        
        FileWriter output=new FileWriter(args[2]);
        PrintWriter out = new PrintWriter(output);
        
        //writing A matrix 
        out.println("A");       
        for(int i=0;i<n;i++)
        {
            for(int j=0;j<n;j++)
                out.print(a[i][j]+" ");
            out.println();
        }
        //Writing B matrix 
        out.println("B");
        for(int i=0;i<n;i++)
        {
            for(int j=0;j<n;j++)
                out.print(b[i][j]+" ");
            out.println();
        }
        //Performing matrix multiplication
        for(int i=0;i<n;i++)
        {
            for(int j=0;j<n;j++)
            {
                for(int k=0;k<n;k++)
                {
                    c[i][j]+=a[i][k]*b[k][j];
                }
            }
        }
        
        //Stop time
        long t2=System.currentTimeMillis();
        //Writing resultant matrix to file
        out.println("RESULTANT MATRIX ");
        for(int i=0;i<n;i++)
        {
            for(int j=0;j<n;j++)
                out.print(c[i][j]+" ");
            out.println();
        }
          out.flush();
          out.close();
          
          System.out.println("SEQ TIME = "+(t2-t1)+" msec");
    }
}
