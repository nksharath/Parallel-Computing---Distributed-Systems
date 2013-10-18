/*
 * hw3q1clu.java
 * 
 * Version:
 *          $Id$
 * 
 * Revisions:
 *          $Log$
 * 
 */

/*
*@Problem       : CLU code to compute cos(x) using taylor series
*@Author        : Sharath Navalpakkam Krishnan : Batch : 4005 735-01
*@Version       : 1.0.1
*@LastModified  : 02/05/2013 4.45 PM
*
*/
import java.text.*;
import java.math.*;
import edu.rit.pj.Comm;
import edu.rit.util.Range;
import java.io.BufferedWriter;
import java.io.FileWriter;


public class hw3q1clu
{
    //value of x
    static double x;
    //value of resultant term in every iteration
    static double term=0;
    //value of the sum after every outer loop computation
    static double sum=0;
    static int n=0;
    //Declaring Cluster parameters such as world size and rank
    static Comm world;
    static int size;
    static int rank;
    static FileWriter file;
    static BufferedWriter out;
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

    static void taylor()throws Exception
    {
        //Dividing the range among number of processors available
        Range chunk =new Range (0, 15000) .subrange (size, rank);
        int lb=chunk.lb();
        int ub = chunk.ub();

        for(double i=lb;i<=ub;i=i+0.1)
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
            out.write(""+sum+"\n");
             //System.out.println(sum);

        }

    }

    //Invoking main function

    public static void main(String[] args) throws Exception
    {
        Comm.init(args);
        
        //Initialising world,size and rank parameters
        world=Comm.world();
        size=world.size();
        rank=world.rank();
        
        if(args.length<1){
            System.out.println("Example : java -Dpj.np=4 hw3q1clu filename");
            System.exit(1);
        }
        //File input=new File("output"+"_"+rank+".txt");
        file=new FileWriter("output"+"_"+rank+".txt");
        out=new BufferedWriter(file);

        //Start time
        long t1=System.currentTimeMillis();
        taylor();
        long t2=System.currentTimeMillis();
        //Stop time
        System.out.println("CLU done in "+(t2-t1));

    }
}
