/*
 * hw2q5seq.java
 *
 * Version:
 *          $Id$
 *
 * Revisions:
 *          $Log$
 *
 */

/*
*@Problem       : Sequential code to count number of primes using Sieve of Eratosthenes
*@Author        : Sharath Navalpakkam Krishnan : Batch : 4005 735-01
*@Version       : 1.0.1
*@LastModified  : 01/20/2013 4.45 PM
*
*/
//package hw2q5seq1;

public class hw2q5seq
{
    //Upper limit of prime numbers
    long n;
    //A boolean array to record marked/unmarked elements
    boolean flag[];
    //To count the number of primes
    int count=0;
    //Invoking Main function
    public static void main(String[] args)
    {
        hw2q5seq obj=new hw2q5seq();
        try{
        //Input-the value of n
        obj.n=Long.parseLong(args[0]);
        }
        catch(Exception e)
        {
            System.out.println("Example: java hw2q5seq NumberOfPrimes");
            System.exit(1);
        }
        //Initialising the flag array
        obj.flag=new boolean[(int)(obj.n)+1];
        //Timing starts here
        long t1=System.currentTimeMillis();
        //Making all elements true of flag array
        for(int i=2;i<obj.n;i++)
            obj.flag[i]=true;

        int k=2;

        while(k*k<obj.n)
        {
            if(obj.flag[k]==true)
            for(int j=k*k;j<=obj.n;j=j+k)
            {       if(j%k==0)
                    obj.flag[j]=false;
            else
                continue;
            }

            for(int a=k+1;a<obj.n;a++)
            {
                if(obj.flag[a]==true)
                {
                    k=a;
                    break;
                }
            }
         }

    for(int i=0;i<obj.n;i++)
    {
        //Checking if true , then count records number of primes
        if(obj.flag[i]==true)
            obj.count++;
    }
    //Timing stops here
     long t2=System.currentTimeMillis();

    System.out.println(obj.count);
    System.out.println("SEQ TIME="+(t2-t1)+"msec");
    }
}


