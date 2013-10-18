/*
 * hw4q1clu.java
 * 
 * Version:
 *          $Id$
 * 
 * Revisions:
 *          $Log$
 * 
 */

/*
*@Problem       : CLU code to implement bellman algorithm
*@Author        : Sharath Navalpakkam Krishnan : Batch : 4005 735-01
*@Version       : 1.0.1
*@LastModified  : 02/20/2013 4.45 PM
*
*/

//package hw4q1clu;
import edu.rit.util.Range;
import edu.rit.pj.reduction.*;
import edu.rit.mp.IntegerBuf;
import edu.rit.pj.Comm;
import edu.rit.util.Random;
import edu.rit.util.Range;

public class hw4q1clu
{
    //Number of nodes
    static int N;
    //Seed value
    static int seed;
    //To store max distance
    static int max_distance;
    //To record source node
    static int source;
    //To generate random numbers
    static Random rand;
    //matrix to initialise values
    static int matrix[][];
    //matrix to store distance
    static int distance[];
    //Comm object
    static Comm world;
    //To get size
    static int size;
    //To get rank
    static int rank;
    //buffer to collect values from all processors
    static IntegerBuf buff;
    
    /*
     * Init     Initialises distance matrix to infinity
     */
    static void Init()
    {
        for(int i=0;i<N;i++)
            distance[i]=9999;
        distance[source]=0;
        
    }
    
    /*
     * bellman      performs bellam algorithm
     */
    static void bellman()throws Exception
    {
        //Running for 1 level to get neighbouring distance
        for(int i=0;i<N;i++)
        {
            if(distance[i] > distance[source]+matrix[source][i])
                distance[i]=distance[source]+matrix[source][i];
        }
        //Running for remaining levels 
        for(int u=0;u<N;u++)
            for(int v=0;v<N;v++)
            {
                if(distance[v] > distance[u]+matrix[u][v])
                    distance[v]=distance[u]+matrix[u][v];
                world.reduce(0, buff, IntegerOp.MINIMUM);
                world.broadcast(0, buff);
            }
        
    }
    /*
     * display      displays the shortest path
     */
    static void display()
    {
        for(int i=0;i<N;i++)
            System.out.println(distance[i]+"  ");
    }
    
    //Invoking main function
    public static void main(String[] args)throws Exception
    {
        if(args.length!=4)
        {
            System.out.println("Example : java -Dpj.np=4 hw4q1clu node seed max_distance source");
            System.exit(1);
        }
        Comm.init(args);
        world=Comm.world();
        size=world.size();
        rank=world.rank();
        N=Integer.parseInt(args[0]);
        seed=Integer.parseInt(args[1]);
        max_distance=Integer.parseInt(args[2]);
        source=Integer.parseInt(args[3]);
        matrix=new int[N][N];
        distance=new int[N];
        rand=Random.getInstance(seed);
        Range ranges[] =new Range(0,N-1).subranges(size);
        buff=IntegerBuf.buffer(distance);
        Range myrange=ranges[rank];
        long t1=0;
        if(rank==0)
            t1=System.currentTimeMillis();
        //Generating Input
        for(int i=0;i<N;i++)
        {
            for(int j=0;j<N;j++)
            {
                if(i==j)
                    matrix[i][j]=0;
                else{
                    int temp=rand.nextInt(max_distance);
                    if(temp==0)
                        temp=1;
                    matrix[i][j]=temp;
                }
            }
        }
        //Initialise distance matrix
        Init();
        //Bellman algorithm
        bellman();
        if(rank==0)
        {
            long t2=System.currentTimeMillis();
            display();
            System.out.println("CLU TIME = "+(t2-t1)+" msec");
        }
        
    }
}
