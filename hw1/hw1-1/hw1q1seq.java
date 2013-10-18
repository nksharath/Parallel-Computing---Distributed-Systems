/*
 * hw1q1seq.java
 * 
 * Version:
 *          $Id$
 * 
 * Revisions:
 *          $Log$
 * 
 */

/*
*@Problem       : Sequential code to calculate INS and FNS values for m students & n subjects
*@Author        : Sharath Navalpakkam Krishnan : Batch : 4005 735-01
*@Version       : 1.0.1
*@LastModified  : 12/14/2012 3.45 PM
*
*/
//package hw1q1seq.java;

import java.math.*;
import edu.rit.util.Random;
import edu.rit.pj.Comm;

public class hw1q1seq 
{
    // m represents number of students, n is number of subjects
    static int m,n;
    //maintains scores of m students in n subjects
    static double[][] scores;
    //Stores the INS values of m students
    static double[] INS_result;
    //Stores the FNS values for m students
    static double[] FNS_result;
    
   /*
     * CalculateINS   Calculates the INS values across n subjects for m students
     * 
     */
    
    static void CalculateINS()
    {
        for(int i=0;i<m;i++)
        {
            for(int j=0;j<n;j++)
            {
                // Computing INS score if between 0 and 100
                if(scores[i][j]>0 && scores[i][j]<100)
                    INS_result[i]+=Math.pow(scores[i][j],2);                    
            }
            INS_result[i]=INS_result[i]/n;
        }
        
        //Sorting the values of INS 
        for(int i=0;i<m;i++)
            for(int j=i+1;j<m-1;j++)
            {
                if(INS_result[i]<INS_result[j])
                {
                    double temp=INS_result[i];
                    INS_result[i]=INS_result[j];
                    INS_result[j]=temp;
                }                 
                
            }
              
    }
     /*
     * CalculateFNS   Calculates the FNS values for m students
     * 
     */
    
    static void CalculateFNS()
    {
        for(int i=0;i<m;i++)
        {
            if(i>m*0.6)
                FNS_result[i]=1.0;
            else
            if(i>m*0.3 && i<=m*0.6)
                FNS_result[i]=2.0;
            else
            if(i>m*0.1 && i<=m*0.3)
                FNS_result[i]=3.0;
            else
            if(i<=m*0.1)
                FNS_result[i]=4.0;
        }
      
    }  
    
     /*
     * display  displays the INS and FNS values for each student
     * 
     */
    
    static void display()
    {
        for(int i=0;i<m;i++)
        {
            System.out.println("INS of "+(i+1)+"="+INS_result[i]+"\t");
            System.out.print("FNS of "+(i+1)+"="+FNS_result[i]+"\n");
        }
        
    }
    // Invoking main function
            
    public static void main(String[] args) throws Exception
    {      
        Comm.init(args);
        long seed=1;
        //m is the number of students
        try
        {
        m=Integer.parseInt(args[0]);
        //n is the number of students
        n=Integer.parseInt(args[1]);
        //seed for random generator
        seed=Long.parseLong(args[2]);
        }
        catch(Exception e)
        {
            System.out.println("Example : java hw1q1seq No.of.Students No.of.Subjects Seed");
            System.exit(1);
        }
        scores=new double[m][n];
        FNS_result=new double[m];
        INS_result=new double[m];
        
        Random generate=Random.getInstance(seed);
        
        //Populating values for subjects 
        for(int i=0;i<m;i++)
            for(int j=0;j<n;j++)
                scores[i][j]=generate.nextDouble()*100;
        
        //Starting Time
        long t1=System.currentTimeMillis();
        CalculateINS();
        CalculateFNS();  
        long t2=System.currentTimeMillis();
        //Stop time
        System.out.println("Sequential Time = "+(t2-t1));
        
        display();
            
    }
}
