/*
 * hw1q4smp.java
 * 
 * Version:
 *          $Id$
 * 
 * Revisions:
 *          $Log$
 * 
 */

/*
*@Problem       : SMP code to computer cos(x) using Taylor Series
*@Author        : Sharath Navalpakkam Krishnan : Batch : 4005 735-01
*@Version       : 1.0.1
*@LastModified  : 12/14/2012 3.45 PM
*
*/
//package hw1q4smp;
import java.text.*;
import java.math.*;
import edu.rit.pj.Comm;
import edu.rit.pj.IntegerForLoop;
import edu.rit.pj.ParallelRegion;
import edu.rit.pj.ParallelTeam;

public class hw1q4smp
{
    double x;
    double term;
    double sum;
    int n;

    
    public hw1q4smp(double x)
    {
        term=0;
        sum=0;
        n=0;
        this.x=((double)Math.round(x * 10) / 10);
       
    }
    
     /*
     * factorial        calculates the factorial of given value
     * @param   n       the value whos factorial to be calculated
     * 
     */
    double factorial(int n)
    {
        double fact=1;
        if(n==0 || n==1)
            return fact;
        for(double i=1;i<=n;i++)
        {
            fact=fact*i;
        }
        return fact;
    }
    /*
     * taylor  Calculates the value of cos(x) using taylor series 
     * 
     */
    
    void taylor()throws Exception
    {
        //To display value of x
        //System.out.println(x); 
        
        x=x%(2*Math.PI);     
        n=0;
        sum=0;
        term=0;
        do
        {
            //taylor series formula
            term=(Math.pow(-1,n)*Math.pow(x,2*n))/factorial(2*n);
            sum=sum+term;
            n++;         
        }
        while(Math.abs(term/sum)>=0.001); 
        
        //To display the result 
        //System.out.println(sum);
    }
    
    /*
     * evaluate  Calls function taylor() for each thread 
     * 
     */
    
    static void evaluate()throws Exception
    {
        new ParallelTeam().execute(new ParallelRegion()
        {
            public void run() throws Exception
            {
                execute(0,15000,new IntegerForLoop()
                {
                  
                    
                    public void run(int first,int last)throws Exception
                    {
                        for(double counter=first;counter<=last+0.1;counter+=0.1)
                        {
                            //Calling taylor()
                            new hw1q4smp(counter).taylor();
                          
                         }
                    }
                });
            }
        });
        
    }
                    
    //Invoking main function      

    public static void main(String[] args) throws Exception
    {
        Comm.init(args);
        //Start Time
        long t1=System.currentTimeMillis();
        
        evaluate();
        
        long t2=System.currentTimeMillis();
        //Stop time
        System.out.println("SMP done in "+(t2-t1));
     
    }
}
