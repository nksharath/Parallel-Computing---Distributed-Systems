/*
 * hw1q2seq.java
 * 
 * Version:
 *          $Id$
 * 
 * Revisions:
 *          $Log$
 * 
 */

/*
*@Problem       : Sequential code to count the number of palindromes and print them
*@Author        : Sharath Navalpakkam Krishnan : Batch : 4005 735-01
*@Version       : 1.0.1
*@LastModified  : 12/14/2012 3.45 PM
*
*/
//package hw1q2seq;


import java.util.Scanner;
import java.io.File;
import java.util.Vector;
import java.lang.StringBuffer;
import java.util.ArrayList;
import java.util.Collections;
import edu.rit.pj.Comm;


public class hw1q2seq 
{
    //ArrayList to store words from input file
    static ArrayList<String> list=new ArrayList<String>();
    //vector to store all palindrome values
    static Vector<String> palindromes=new Vector<String>();
    //Number of lines in input file
    static int NumberLines;
    //A scanner object to perform read from file
    static Scanner read;
   
    /*
     * Compute  Checks for palindrome, and stores it in a vector
     * 
     */
    static void Compute()throws Exception
    {
        for(int counter=0;counter<=list.size()-1;++counter)
        {
            //Removing all spaces
            //Removing all punctuation '
            //Removing all punctuation "
            //Removing all hyphen - 
            list.set(counter,list.get(counter).replaceAll("\\s",""));
            list.set(counter,list.get(counter).replaceAll("\'",""));
            list.set(counter,list.get(counter).replaceAll("\"",""));
            list.set(counter,list.get(counter).replaceAll("-",""));
            // Checking if palindrome
            if(!list.get(counter).equals(""))
                if(new StringBuffer(list.get(counter).toLowerCase()).reverse().toString().
                        equals(list.get(counter).toLowerCase()))                       
                    palindromes.add(list.get(counter));
        }
    }
    
      /*
     * display  displays the list of palindromes
     * 
     */              
    static void display()
    {
        for(int i=0;i<palindromes.size();i++)
            System.out.println(palindromes.get(i));
    }
     
    //Invoking main function
    public static void main(String[] args)throws Exception 
    {
        Comm.init(args);
        File input=null;
        try
        {
        input=new File(args[0]);
        }
        catch(Exception e)
        {
            System.out.println("Example: java hw1q2seq filename.txt");
            System.exit(1);
        }
        read=new Scanner(input);
        NumberLines=read.nextInt();
        int count=0;
        //Reading all words from file
        while(read.hasNext())
        {
            list.add(read.nextLine());
            count++;            
        }
        //Start time
        long t1=System.currentTimeMillis();       
        Compute(); 
        long t2=System.currentTimeMillis();
        //Stop time
        System.out.println("No. of palindromes "+palindromes.size());
        System.out.println("SEQ Time = "+(t2-t1));
        
        display();
               
    }
}
        
        
        
        
