/*
 * hw3q2seq.java
 * 
 * Version:
 *          $Id$
 * 
 * Revisions:
 *          $Log$
 * 
 */

/*
*@Problem       : SEQ code to perform geometrical transformations on image
*@Author        : Sharath Navalpakkam Krishnan : Batch : 4005 735-01
*@Version       : 1.0.1
*@LastModified  : 02/05/2013 2.45 PM
*
*/
import edu.rit.color.IntRGB;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import edu.rit.image.PJGColorImage;
import edu.rit.image.PJGImage;
import edu.rit.pj.Comm;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import edu.rit.pj.Comm;

public class hw3q2seq 
{

    //Invoking main method
    public static void main(String[] args)throws Exception
    {
        Comm.init(args);
        // Start Time
        long t1=System.currentTimeMillis();
         if(args.length<4){
            System.out.println("Example:java -Dpj.np=4 hw3q2seq julia.pjg dx dy julia_shift_seq.pjg");
            System.exit(1);
        }
        
        //Recieving the file source name : Image
        File filenamesrc=new File(args[0]);
        //Declaring height width and corresponding geometric movement dx and dy
        int dx,dy;
        int height,width;
        //Recieving the transformation value along x
        dx=Integer.parseInt(args[1]);
        //Receiving the transformation value along y
        dy=Integer.parseInt(args[2]);
        
        File filenamedest=new File(args[3]);
        //To load the given image
        PJGColorImage image=new PJGColorImage();
        PJGImage.Reader reader = image.prepareToRead(new BufferedInputStream(new FileInputStream (filenamesrc)));
        reader.read();
        //Getting the height and width parameters 
        height=image.getHeight();
        width=image.getWidth();
        //System.out.println(height+" "+width);
        int matrix[][]=new int[height][width];
        int matrixnew[][]=new int[height][width];
        
        edu.rit.color.IntRGB color=new IntRGB();
        matrix=image.getMatrix();
        
        for(int i=0;i<height;i++)
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
        //To generate the new transformed image
        PJGColorImage imagenew=new PJGColorImage(height,width,matrixnew);
        PJGImage.Writer writer = imagenew.prepareToWrite(new BufferedOutputStream(new FileOutputStream (filenamedest)));
        //Writing the new image into a PJG file
        writer.write();
        writer.close();
        reader.close();
        //Stop time
        long t2=System.currentTimeMillis();
        System.out.println("TIME IN SEQ = "+(t2-t1)+"msec");
        
    }
}


        
        
