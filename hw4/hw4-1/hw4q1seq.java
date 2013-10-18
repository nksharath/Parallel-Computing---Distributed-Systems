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
 *@Problem       : SEQ code to implement bellman algorithm
 *@Author        : Sharath Navalpakkam Krishnan : Batch : 4005 735-01
 *@Version       : 1.0.1
 *@LastModified  : 02/20/2013 4.45 PM
 *
 */

//package hw4q1seq;
import edu.rit.util.Random;


public class hw4q1seq
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
    static void bellman()
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
            }
        
    }
     /*
     * display      displays the shortest path
     */
    static void display()
    {
        for(int i=0;i<N;i++)
            System.out.print(distance[i]+"  ");
    }
    
    
    
    //Invoking main function
    public static void main(String[] args)
    {
        if(args.length!=4)
        {
            System.out.println("Example : java hw4q1seq node seed max_distance source");
            System.exit(1);
        }
        N=Integer.parseInt(args[0]);
        seed=Integer.parseInt(args[1]);
        max_distance=Integer.parseInt(args[2]);
        source=Integer.parseInt(args[3]);
        matrix=new int[N][N];
        distance=new int[N];
        rand=Random.getInstance(seed);
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
        display();
    }
}
