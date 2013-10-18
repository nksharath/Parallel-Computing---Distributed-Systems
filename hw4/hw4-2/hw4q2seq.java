/*
 * hw4q2seq.java
 * 
 * Version:
 *          $Id$
 * 
 * Revisions:
 *          $Log$
 * 
 */

/*
*@Problem       : SEQ code to implement mailman problem
*@Author        : Sharath Navalpakkam Krishnan : Batch : 4005 735-01
*@Version       : 1.0.1
*@LastModified  : 02/20/2013 2.45 PM
*
*/
//package hw4q2seq;
import edu.rit.util.Random;

public class hw4q2seq 
{
    public static void main(String[]args)
    {
         if(args.length!=2)
        {
            System.out.println("Example : java hw4q2seq 1 100 ");
            System.exit(1);
        }
        //Get the seed from user
        int seed=Integer.parseInt(args[0]);
        //Get value of N from user
        int N=Integer.parseInt(args[1]);
        long t1=System.currentTimeMillis();
        Random rand=Random.getInstance(seed);
        int count=0;
        //x , y co-ordiantes
        int x=0,y=0;
        //Generate probability and compute points
        while(count<N)
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
                ++count;
                break;
                case 14:x--;
                ++count;
                break;
                case 81:y++;
                ++count;
                break;
                case 82:y--;
                ++count;
                break;
                    
            }
        }
        //Display the co-ordinates
        System.out.println(x+" "+y);
        //Display the distance from origin
        System.out.println(Math.sqrt((x*x)+(y*y)));
        long t2=System.currentTimeMillis();
        System.out.println("SEQ TIME="+ (t2-t1)+" msec");
    }

}