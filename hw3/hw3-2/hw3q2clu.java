/*
 * hw3q2clu.java
 * 
 * Version:
 *          $Id$
 * 
 * Revisions:
 *          $Log$
 * 
 */

/*
*@Problem       : CLU code to perform geometrical transformations on image
*@Author        : Sharath Navalpakkam Krishnan : Batch : 4005 735-01
*@Version       : 1.0.1
*@LastModified  : 02/05/2013 2.45 PM
*
*/
import edu.rit.color.HSB;

import edu.rit.image.PJGColorImage;
import edu.rit.image.PJGImage;

import edu.rit.mp.IntegerBuf;

import edu.rit.pj.Comm;

import edu.rit.pj.reduction.IntegerOp;
import edu.rit.util.Arrays;
import edu.rit.util.Range;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import java.io.FileInputStream;
import java.io.BufferedInputStream;

public class hw3q2clu 
{
    //Initialising Cluster parameters such as world size and rank
    static Comm world;
    static int size;
    static int rank;
    
    //Declaring height width and corresponding geometric movement dx and dy
    static int height,width,dx,dy;
    static int[][] matrix;
    static int[][] matrixnew;
    //To load the given image
    static PJGColorImage image;
    //To generate the new transformed image
    static PJGColorImage imagenew;
    static Range[] ranges;
    static Range myrange;
    static int mylb;
    static int myub;
    
    static File filenamesrc;
    static File filenamedest;
    
    static IntegerBuf[] slices;
    static IntegerBuf myslice;

    //Invoking main method
    public static void main(String[] args) throws Exception
    {
        // Start Time
        long t1 = System.currentTimeMillis();

        // Initialize cluster parameters such as world size and rank
        Comm.init (args);
        world = Comm.world();
        size = world.size();
        rank = world.rank();
        if(args.length<4){
            System.out.println("Example:java hw3q2clu julia.pjg dx dy julia_shift_seq.pjg");
            System.exit(1);
        }
        //Recieving the file source name : Image
        filenamesrc=new File(args[0]);
        //Recieving the transformation value along x
        dx=Integer.parseInt(args[1]);
        //Receiving the transformation value along y
        dy=Integer.parseInt(args[2]);
        //The name of the new image
        filenamedest=new File(args[3]);
        
        image=new PJGColorImage();
        //Loading the given image
        PJGImage.Reader reader = image.prepareToRead(new BufferedInputStream(new FileInputStream (filenamesrc)));
        reader.read();
        //Getting the height and width parameters 
        height=image.getHeight();
        width=image.getWidth();
        
         matrix = new int [height] [];
         //Calculating range for each processor 
         ranges = new Range (0, height-1) .subranges (size);
         myrange = ranges[rank];
        mylb = myrange.lb();
        myub = myrange.ub();
       
        //Matrix to store new image pixel values
        matrixnew=new int[height][width];
        matrix=image.getMatrix();
       
        Arrays.allocate (matrixnew, width);
      
        //Performing row slice partitioninig for each processor
        slices = IntegerBuf.rowSliceBuffers (matrix, ranges);
        //Allocating corresponding chunk to each processor
        myslice = slices[rank];
        
        for(int i=mylb;i<=myub;i++)
        {
            for(int j=0;j<width;j++)
            {
                //Performing the geometric transformation
                int xnew=(j+dx)%width;
                int ynew=(i+dy)%height;
                
               // imagenew.setPixel(ynew, xnew, color);
                matrixnew[ynew][xnew]=matrix[i][j];
              
            }
        }
        
       // world.gather(0, myslice, slices);
        //Creating a result buffer to hold the reduce operation from all processors
        IntegerBuf result=IntegerBuf.buffer(matrixnew);
        //Performing reduce on all processors
        world.reduce(0, result, IntegerOp.SUM);
        //rank 0 writes the file for the new image 
        if (rank == 0)
            {
            imagenew = new PJGColorImage (height, width, matrixnew);
            PJGImage.Writer writer =
                imagenew.prepareToWrite
                    (new BufferedOutputStream
                        (new FileOutputStream (filenamedest)));
            writer.write();
            writer.close();
            }
        //Stop time
        long t2=System.currentTimeMillis();
        System.out.println("TIME IN CLU = "+(t2-t1)+"msec");
   
    }
}
