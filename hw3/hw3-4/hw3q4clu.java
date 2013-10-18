/*
 * hw3q4clu.java
 * 
 * Version:
 *          $Id$
 * 
 * Revisions:
 *          $Log$
 * 
 */

/*
*@Problem       : CLU code to perform matrix multiplication using 2D partitiioning
*@Author        : Sharath Navalpakkam Krishnan : Batch : 4005 735-01
*@Version       : 1.0.1
*@LastModified  : 02/07/2013 6.25 PM
*
*/
import edu.rit.mp.buf.IntegerItemBuf;
import edu.rit.pj.BarrierAction;
import edu.rit.pj.Comm;
import edu.rit.util.Random;
import edu.rit.util.Range;
import edu.rit.mp.IntegerBuf;
import java.io.*;
import java.math.*;

public class hw3q4clu 
{
    //Declaring Cluster paramters
    static Comm world;
    //Parameters to record row vs column participants
    static Comm newComm1,newComm2;
    //Number of processors
    static int size;
    static int rank;
    //Input size of the matrix to be multiplied
    static int N;
    //sub matrix A of each processor
    static int matrixa[][];
    //submatrix B of each processor
    static int matrixb[][];
    //Rresultant matrix C
    static int matrixc[][];
    //Local matrices used for computation
    static int temp1[][],temp2[][];
    static int temp3[][];
    static IntegerBuf[] result;
    //To record the range against columns Vs rows
    static Range rrange[],crange[];
    static FileWriter output;
    static PrintWriter out;

    public hw3q4clu() 
    {
        newComm1=null;
        newComm2=null;
        world=null;
    }
    
    
    public static void main(String[] args)throws Exception
    {   
        Comm.init(args);
        if(args.length<3){
            System.out.println("Example: java -Dpj.np=9 hw3q4clu N Seed filename");
            System.exit(1);
        }
            
        //Comm world = Comm.world();
        world=Comm.world();
        rank = world.rank();
        size = world.size();
        //Accepting Size of matrix from user 
        N=Integer.parseInt(args[0]);
        //Accepting the seed from the user
        long seed=Long.parseLong(args[1]);
        Random rand=Random.getInstance(seed);
        long t1,t2;
        t1=0;t2=0;
        //Start time
        if(rank==0)
        t1=System.currentTimeMillis();
        //submatrix a 
        matrixa=new int[(int)N/(int)Math.sqrt(size)][(int)N/(int)Math.sqrt(size)];
        //submatrix b
        matrixb=new int[(int)N/(int)Math.sqrt(size)][(int)N/(int)Math.sqrt(size)];
        //resultant matrix c
        matrixc=new int[N][N];
        rrange=new Range(0,N-1).subranges((int)Math.sqrt(size));
        crange=new Range(0,N-1).subranges((int)Math.sqrt(size));
        
        //Allocating memory for local matrix computation
        temp1=new int[N][(int)N/(int)Math.sqrt(size)];
        temp2=new int[(int)N/(int)Math.sqrt(size)][N];
        temp3=new int[(int)N/(int)Math.sqrt(size)][(int)N/(int)Math.sqrt(size)];
        IntegerBuf [] btemp1=IntegerBuf.rowSliceBuffers(temp1, rrange);
        IntegerBuf [] btemp2=IntegerBuf.colSliceBuffers(temp2, crange);
        
        //Generating random numbers for each submatrix by individual processors
        if(rank%(int)Math.sqrt(size)==0)
            rand.skip(rank*(N*N/size));
        else
        {
            int rankid=rank % (int)Math.sqrt(size);
            rand.skip(((rank-rankid)*(N*N/size))+ (rankid*N/(int)Math.sqrt(size)));
        }
        
        for(int i=0;i<((int)N/(int)Math.sqrt(size));i++)
        {
            if(i!=0)
                rand.skip((int)Math.sqrt(size-1)*((int)N/(int)Math.sqrt(size)));
            for(int j=0;j<((int)N/(int)Math.sqrt(size));j++)
            {
                //Each processor generates its own submatrix
                matrixa[i][j]=rand.nextInt(10);
                matrixb[i][j]=matrixa[i][j]+10;
            }
                        
        }
        
        //Record all participating columns for multiplication
        for(int i=0;i<(int)Math.sqrt(size);i++)
            ParticipatingCol(i);
        
        newComm2.allGather(IntegerBuf.buffer(matrixb), btemp1);
        
        //Record all participating rows for multiplication
        for(int i=0;i<size;i=i+(int)Math.sqrt(size))
            ParticipatingRow(i);
        
        newComm1.allGather(IntegerBuf.buffer(matrixa), btemp2);
        
        //Perform multiplication 
        performMultiplication();
        
        //Accumulate all values
        if(rank==0)
            gatherToroot();
        else
            world.gather(0, IntegerBuf.buffer(temp3), null);
        
        t2=System.currentTimeMillis();
        
        //If rank is 0 , write to file
        if(rank==0)
        writeMatrix(args[2]);
        
        //Stop time
         if(rank==0){
         t2=System.currentTimeMillis();      
         System.out.println("CLU TIME = "+(t2-t1)+"msec");
        }
    
            
        
    
       
        /*
        FileWriter output=new FileWriter("rank"+rank);
        for(int i=0;i<((int)N/(int)Math.sqrt(size));i++)
        {
            for(int j=0;j<((int)N/(int)Math.sqrt(size));j++)
            {
                output.write(matrixa[i][j]+"  ");
                output.write("\n");
            }
                
            output.flush();
        }*/
        
    }
    
   /*
     * ParticipatingCol  records all participating columns for matrix multiplication
     * @param   a  index value of participating column
     * 
     */
   static void ParticipatingCol(int a)throws Exception
    {   //System.out.println("here"+a);  
        if(rank!=a && rank%(int)Math.sqrt(size)!=a)
            world.createComm(false,a);
        else
            newComm2=world.createComm(true,a);               
    }
   /*
     * ParticipatingRow  records all participating rows for matrix multiplication
     * @param   a  index value of participating row
     * 
     */
   static void ParticipatingRow(int a)throws Exception
   {
       if(rank!=a)
       {
           if(rank>a && rank<(a+(int)Math.sqrt(size)))
               newComm1=world.createComm(true,a);
           else
               world.createComm(false,a);
       }
       else
           newComm1=world.createComm(true,a);
   }
   
   /*
     * perfomrMultiplication  performs matrix multiplication 
     * 
     * 
     */
   static void performMultiplication()
   {
       for(int i=0;i<(int)N/(int)Math.sqrt(size);i++)
           for(int j=0;j<(int)N/(int)Math.sqrt(size);j++)
               for(int k=0;k<N;k++)
                   temp3[i][j]=temp3[i][j]+temp2[i][k]*temp1[k][j];
   }
   /*
     * gatherToroot accumulates all resultant values to root
     * 
     */
   static void gatherToroot()throws Exception
   {
       result=IntegerBuf.patchBuffers(matrixc, rrange, crange);
       world.gather(0,IntegerBuf.buffer(temp3), result);
   }
  
   /*
     * writeMatrix          writes the resultant matrix to a file
     * @param   filename    filename to write data
     * 
     */
  static void  writeMatrix(String filename)throws Exception
   {
       output=new FileWriter(filename);
       out=new PrintWriter(output);
       
       for(int i=0;i<N;i++)
       {
           for(int j=0;j<N;j++)
               out.print(matrixc[i][j]+"\t");
           out.println();
           out.flush();
       }
       
       out.close();
   }
}

          
       
       
       
       
       
   
           
   