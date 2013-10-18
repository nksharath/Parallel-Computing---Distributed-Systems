/*
 * hw1q4seq.java
 * 
 * Version:
 *          $Id$
 * 
 * Revisions:
 *          $Log$
 * 
 */

/*
*@Problem       : Sequential code to computer cos(x) using Taylor Series
*@Author        : Sharath Navalpakkam Krishnan : Batch : 4005 735-01
*@Version       : 1.0.1
*@LastModified  : 12/14/2012 3.45 PM
*
*/
//package hw1q4seq;
import java.text.*;
import java.math.*;
import edu.rit.pj.Comm;

public class hw1q4seq 
{
    static double x;
    static double term=0;
    static double sum=0;
    static int n=0;
    
    /*
     * factorial        calculates the factorial of given value
     * @param   n       the value whos factorial to be calculated
     * 
     */
    static double factorial(int n)
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
    
    static void taylor()
    {
        for(double i=0;i<=15000.1;i=i+0.1)
        {
            x=i;
            x=(double)Math.round(x * 10) / 10;
            
            //To display the value of x 
            //System.out.println(x);
            
            x=x%(2*Math.PI);
            n=0;
            sum=0;
            
            do
            {
 
                term=(Math.pow(-1,n)*Math.pow(x,2*n))/factorial(2*n);
                sum=sum+term;
                n++;
          
            }while(Math.abs(term/sum)>=0.001);
            
            //To display the result
            //  System.out.println(sum);
             
        }
        
    }
  
    //Invoking main function

    public static void main(String[] args) throws Exception
    {
        Comm.init(args);
        //Start time
        long t1=System.currentTimeMillis();
        taylor();
        long t2=System.currentTimeMillis();
        //Stop time
        System.out.println("SEQ done in "+(t2-t1));
    
    }
}
