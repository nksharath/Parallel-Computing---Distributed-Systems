/*
 * hw4q3seq.java
 * 
 * Version:
 *          $Id$
 * 
 * Revisions:
 *          $Log$
 * 
 */

/*
*@Problem       : SEQ code to implement water jug problem
*@Author        : Sharath Navalpakkam Krishnan : Batch : 4005 735-01
*@Version       : 1.0.1
*@LastModified  : 02/20/2013 8.45 PM
*
*/

//package hw4q3seq;

import java.util.*;

public class hw4q3seq
{
    //answer used to calculate GCD among jugs capacity
    static int answer=-99,max=-99;
    //A TreeMap to store key vs vector of nodes
    static TreeMap<Integer, Vector<Integer>> node_def=new TreeMap<Integer,Vector<Integer>>();
    //A TreeMap to store key vs parent id
    static TreeMap<Integer, Integer> parent_def=new TreeMap<Integer,Integer>();
    //A queue used for BFS
    static Queue<Object> queue_node=new LinkedList<Object>();
    static int node_counter=1;
    //To store K jugs 
    static Vector<Integer> capacity=new Vector<Integer>();
    static int ans=-1;
    
    /*
     * breadth_first_search     performs BFS
     * @param   k               required number of litres 
     * @param   node            Initial node    
     */
    static void breath_first_search(int k,int node)
    {
        Vector<Integer> current_pos=new Vector<Integer>(node_def.get(node));
        Vector<Integer> temp;
        for(int i=0;i<current_pos.size();i++)
        {
            // returns if a solution is found
            if(current_pos.get(i)==k)
            {
                ans=node;
                return;
            }
        }
        
        for(int i = 0; i < current_pos.size(); i++)
        {
            if((Integer)current_pos.get(i) < (Integer)capacity.get(i))
            {
                temp=new Vector(current_pos);
                
                temp.set(i, capacity.get(i));
                node_def.put(++node_counter,new Vector(temp));
                parent_def.put(node_counter,node);
                
                queue_node.add(node_counter);
            }
            
            // To remove water from the jug
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
        for(int i = 0; i < current_pos.size(); i++) {
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
    
    static void print_answer()
    {
        if(ans == -1) {
            System.out.println("No answer found");
            return;
        }
        int curr_node = ans;
        
        // Print each vector, and move to its parent
        while(curr_node != 0) {
            Vector<Integer> current_pos = node_def.get(curr_node);
            for(int i = 0; i < current_pos.size(); i++)
                System.out.print(current_pos.get(i) +"\t");
            System.out.println("\n");
            curr_node = (Integer)parent_def.get(curr_node);
        }
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
    public static void main(String[] args)
    {
        if(args.length<2)
        {
            System.out.println("Example: java hw4q3seq target_amount, bucket1, bucket2, ..., bucketk");
            System.exit(1);
        }
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
        {
            hcfcheck[i-1]=Integer.parseInt(args[i]);
            
        }
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
        //Get the max size of jug
        max=hcfcheckcopy[0];
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
        
        System.out.println("Capacity"+capacity);
        long t1=System.currentTimeMillis();
        breath_first_search(k, node_counter);
        long t2=System.currentTimeMillis();
        //Display the result
        print_answer();
        System.out.println("SEQ TIME="+(t2-t1)+"msec");
    
    }
}
