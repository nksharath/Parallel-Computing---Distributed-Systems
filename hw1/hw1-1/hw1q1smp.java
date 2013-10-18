
/*
 * hw1q1smp.java
 * 
 * Version:
 *          $Id$
 * 
 * Revisions:
 *          $Log$
 * 
 */

/*
*@Problem       : SMP code to calculate INS and FNS values for m students & n subjects
*@Author        : Sharath Navalpakkam Krishnan : Batch : 4005 735-01
*@Version       : 1.0.1
*@LastModified  : 12/14/2012 3.45 PM
*
*/
//package hw1q1smp.java;

import edu.rit.pj.ParallelRegion;
import edu.rit.pj.ParallelTeam;
import edu.rit.pj.IntegerForLoop;
import java.math.*;
import edu.rit.util.Random;
import edu.rit.pj.Comm;

public class hw1q1smp 
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
    static void CalculateINS()throws Exception
    {
        new ParallelTeam().execute(new ParallelRegion()
        {        
            public void run()throws Exception
            {
                execute (0,m-1,new IntegerForLoop()
                {
                    public void run(int first,int last)
                    {
                    for(int counter=first;counter<=last;++counter)
                    {
                        for(int j=0;j<n;j++)
                        {
                            // Computing INS score if between 0 and 100 
                            if(scores[counter][j]>0 && scores[counter][j]<100)
                                INS_result[counter]+=Math.pow(scores[counter][j],2);  
                        }                         
                        INS_result[counter]=INS_result[counter]/n;
                    }
                    }
                });
                    
             }
            
        }); 
        
              
    }
    
    /*
     * CalculateFNS   Calculates the FNS values for m students
     * 
     */
    static void CalculateFNS()throws Exception
    {  
        new ParallelTeam().execute(new ParallelRegion()
        {        
            public void run()throws Exception
            {
                execute (0,m-1,new IntegerForLoop()
                {
                    public void run(int first,int last)
                    {
                    for(int counter=first;counter<=last;++counter)
                    {    
                        if(counter>m*0.6)
                            FNS_result[counter]=1.0;
                        else
                            if(counter>m*0.3 && counter<=m*0.6)
                                FNS_result[counter]=2.0;
                            else
                                if(counter>m*0.1 && counter<=m*0.3)
                                    FNS_result[counter]=3.0;
                                else
                                    if(counter<=m*0.1)
                                        FNS_result[counter]=4.0;
                    }
                    }
                });
              }
        });    
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
        try
        {
        //m is the number of students
        m=Integer.parseInt(args[0]);
        //n is the number of students
        n=Integer.parseInt(args[1]); 
        //seed for random generator
        seed=Long.parseLong(args[2]);
        }
        catch(Exception e)
        {
            System.out.println("Example : java hw1q1smp No.of.Students No.of.Subjects Seed");
            System.exit(1);
        }
        
        scores=new double[m][n];
        FNS_result=new double[m];
        INS_result=new double[m];
     
        Random generate=Random.getInstance(seed);
        
        for(int i=0;i<m;i++)
            for(int j=0;j<n;j++)
                scores[i][j]=generate.nextDouble()*100;
        //System.out.println("Random "+scores[0][0]);
        long t1=System.currentTimeMillis();
        CalculateINS();  
        for(int i=0;i<m;i++)
            for(int j=i+1;j<m;j++)
            {
                if(INS_result[i]<INS_result[j])
                {
                    double temp=INS_result[i];
                    INS_result[i]=INS_result[j];
                    INS_result[j]=temp;
                }                 
                
            }
        CalculateFNS();  
        long t2=System.currentTimeMillis();
        System.out.println("SMP Time = "+(t2-t1));
        display();
       
        
            
    }
}
