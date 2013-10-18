/*
 * hw4q3clu.java
 * 
 * Version:
 *          $Id$
 * 
 * Revisions:
 *          $Log$
 * 
 */

/*
*@Problem       : CLU code to implement water jug problem
*@Author        : Sharath Navalpakkam Krishnan : Batch : 4005 735-01
*@Version       : 1.0.1
*@LastModified  : 02/20/2013 10.45 PM
*
*/

//package hw4q3clu;

import edu.rit.mp.IntegerBuf;
import java.lang.reflect.Array;
import java.util.*;
import java.util.Collection;
import edu.rit.pj.Comm;
import edu.rit.util.Range;
import edu.rit.pj.CommRequest;
import java.io.BufferedWriter;
import java.io.FileWriter;

public class hw4q3clu
{
    static CommRequest req=new CommRequest();
    //answer used to calculate GCD among jugs capacity
    static int answer=-99,max=-99;
    //A TreeMap to store key vs vector of nodes
    static TreeMap<Integer, Vector<Integer>> node_def=new TreeMap<Integer,Vector<Integer>>();
    //A TreeMap to store key vs parent id
    static TreeMap<Integer, Integer> parent_def=new TreeMap<Integer,Integer>();
    //A queue used for BFS
    static Queue<Integer> queue_node=new LinkedList<Integer>();
    static int node_counter=1;
    //To store K jugs 
    static Vector<Integer> capacity=new Vector<Integer>();
    static int ans=-1;
    //To store world
    static Comm world;
    //To store rank and size
    static int rank,size;
    static long t1,t2;
    
    /*
     * breadth_first_search     performs BFS
     * @param   k               required number of litres 
     * @param   node            Initial node    
     */
    static void breath_first_search(int k,int node)throws Exception
    {
        Vector<Integer> current_pos=new Vector<Integer>(node_def.get(node));
        Vector<Integer> temp;
        
        Range[] range1=new Range(0,current_pos.size()-1).subranges(size);
        Range myrange1=range1[rank];
        for(int i=myrange1.lb();i<=myrange1.ub();i++)
        {
            if(req.isFinished())System.exit(1);
            
            if(current_pos.get(i)==k)
            {
                // returns if a solution is found
                ans=node;
                //Telling all other thugs that a result is found
                world.floodSend(IntegerBuf.emptyBuffer());
                return;
            }
        }
        
        Range[] range2=new Range(0,current_pos.size()-1).subranges(size);
        Range myrange2=range2[rank];
        for(int i=myrange2.lb();i<=myrange2.ub();i++)
        {
            if(req.isFinished())System.exit(1);
            
            if((Integer)current_pos.get(i) < (Integer)capacity.get(i))
            {
                temp=new Vector(current_pos);
                
                temp.set(i, capacity.get(i));
                node_def.put(++node_counter,new Vector(temp));
                parent_def.put(node_counter,node);
                
                queue_node.add(node_counter);
            }
            //To remove water from jug
            if((Integer)current_pos.get(i) > 0) {
                
                temp = new Vector(current_pos);
                //temp=current_pos;
                temp.set(i, capacity.get(i));
                node_def.put(++node_counter,new Vector(temp));
                parent_def.put(node_counter,node);
                queue_node.add(node_counter);
            }
        }
        
        // Transfer from every jug to every other jug
        Range[] range3=new Range(0,current_pos.size()-1).subranges(size);
        Range myrange3=range3[rank];
        for(int i=myrange3.lb();i<=myrange3.ub();i++)
        {
            if(req.isFinished())System.exit(1);
            
            for(int j = 0; j < current_pos.size(); j ++) {
                if(i == j)
                    continue;
                // Transfer if ith jug is not empty
                if(current_pos.get(i) != 0)  {
                    // Transfer if second jug is not full
                    if(current_pos.get(j) != capacity.get(j)) {
                        temp = new Vector(current_pos);
                        //temp=current_pos;
                        if((Integer)current_pos.get(i) > ((Integer)(capacity.get(j)) - (Integer)(current_pos.get(j))))
                        {
                            temp.set(j, capacity.get(j));
                            temp.set(i,((Integer)temp.get(i)-((Integer)temp.get(j)-(Integer)current_pos.get(j))));
                            
                        }
                        else {
                            temp.set(i,0);
                            temp.set(j,((Integer)temp.get(j)+(Integer)capacity.get(i)));
                            
                        }
                        node_def.put(++node_counter,new Vector(temp));
                        parent_def.put(node_counter,node);
                        queue_node.add(node_counter);
                    }
                    else
                        continue;
                }
                else
                    break;
            }
            
        }
        
        if(queue_node.size() > 1) {
            int next = (Integer)queue_node.peek();
            queue_node.remove();
            breath_first_search(k, next);
        }
    }
     /*
     * print_answer     prints the result
     * 
     */
    
    static void print_answer()throws Exception
    {
        FileWriter stream=new FileWriter("rank_"+rank+".txt");
        BufferedWriter out=new BufferedWriter(stream);
        
        if(ans == -1) {
            out.write("No answer found"+" MY RANK ="+rank);
            out.flush();
            out.close();
            return;
        }
        
        
        int curr_node = ans;
        t2=System.currentTimeMillis();
        
        // Print each vector, and move to its parent
        out.write("MY RANK = "+rank);
        out.write("\n");
        while(curr_node != 0) {
            Vector<Integer> current_pos = node_def.get(curr_node);
            for(int i = 0; i < current_pos.size(); i++)
                out.write(current_pos.get(i) +"\t");
            out.write("\n");
            curr_node = (Integer)parent_def.get(curr_node);
        }
        out.write("CLU TIME = "+(t2-t1)+"msec");
        out.flush();
        out.close();
    }
    
      /*
     * calculate    calculates HCF 
     * @param   a   value to hold first number
     * @param   b   value to hold second number
     */
    static void calculate(int a,int b)
    {
        int remainder=a%b;
        if(remainder==0){
            answer=b;
            return;
        }
        calculate(b, remainder);
        
        
    }
    
    
    //Invoking main
    public static void main(String[] args)throws Exception
    {
        if(args.length<2)
        {
            System.out.println("Example: java -Dpj.np=2 hw4q3clu target_amount, bucket1, bucket2, ..., bucketk");
            System.exit(1);
        }
        Comm.init(args);
        world=Comm.world();
        size=world.size();
        rank=world.rank();
        world.floodReceive(IntegerBuf.emptyBuffer(),req);
         //Initial state
        Vector<Integer> initial_state=new Vector();
        //Required amount of litres in jug
        int k=Integer.parseInt(args[0]);
        //Adding jugs to vector
        for(int i=1;i<args.length;i++)
        {
            
            capacity.add(Integer.parseInt(args[i]));
            initial_state.add(0);
        }
        //Store an array of jug sizes
        int []hcfcheck=new int[capacity.size()];
        int hcf;
        for(int i=1;i<args.length;i++)
            hcfcheck[i-1]=Integer.parseInt(args[i]);
        //Make a copy of jug sizes - to calculate HCF
        int [] hcfcheckcopy=new int[hcfcheck.length];
        hcfcheckcopy=hcfcheck.clone();
        
        hcf=hcfcheck[0];
        int count=0;
        //Calculating HCF among all jug sizes
        while(count<=hcfcheck.length-2)
        {
            if(hcfcheck[count+1]==0 && hcf==0)
            {
                count++;
                continue;
            }
            
            if(hcf==0)
            {
                count++;
                hcf=hcfcheck[count+1];
                continue;
            }
            
            if(hcfcheck[count+1]==0)
            {
                count++;
                continue;
            }
            
            if(hcf<hcfcheck[count+1])
            {
                int local=hcf;
                hcf=hcfcheck[count+1];
                hcfcheck[count+1]=local;
            }
            calculate(hcf,hcfcheck[count+1]);
            count+=1;
        }
        max=hcfcheckcopy[0];
        //Get the max size of jug
        for(int i=0;i<hcfcheckcopy.length;i++)
        {
            if(max<hcfcheckcopy[i])
            {
                max=hcfcheckcopy[i];
                
            }
            
        }
        //If required litres is more than jug size or
        //If there is no solution possible
        if(answer==-99 || k%answer!=0 || k>max)
        {
            System.out.println("NO SOLUTION POSSIBLE");
            System.exit(1);
        }
        
        node_def.put(node_counter, new Vector(initial_state));
        
        parent_def.put(node_counter, 0);
        
        
        t1=System.currentTimeMillis();
        breath_first_search(k, node_counter);
        //Display the result
        print_answer();
    
    }
}
