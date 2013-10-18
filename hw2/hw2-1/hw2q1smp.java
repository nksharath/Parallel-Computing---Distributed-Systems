/*
 * hw2q1smp.java
 *
 * Version:
 *          $Id$
 *
 * Revisions:
 *          $Log$
 *
 */

/*
*@Problem       : SMP code to Calculate Julia Set
*@Author        : Prof Alan Kaminsky, Sharath Navalpakkam Krishnan : Batch : 4005 735-01
*@Version       : 1.0.1
*@LastModified  : 01/20/2013 1.45 PM
*
*/
//package hw2q1smp;


import edu.rit.color.HSB;

import edu.rit.image.PJGColorImage;
import edu.rit.image.PJGImage;

import edu.rit.pj.Comm;
import edu.rit.pj.IntegerForLoop;
import edu.rit.pj.IntegerSchedule;
import edu.rit.pj.ParallelRegion;
import edu.rit.pj.ParallelTeam;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
public class hw2q1smp
{

// Prevent construction.

    private hw2q1smp()
        {
        }

// Program shared variables.

    // Command line arguments.
    static int width;
    static int height;
    static double xcenter;
    static double ycenter;
    static double resolution;
    static int maxiter;
    static double gamma;
    static File filename;

    static double dx;
    static double dy;

    // Initial pixel offsets from center.
    static int xoffset;
    static int yoffset;

    // Image matrix.
    static int[][] matrix;
    static PJGColorImage image;

    // Table of hues.
    static int[] huetable;

// Main program.

    /**
     * Mandelbrot Set main program.
     */
    public static void main
        (String[] args)
        throws Exception
        {
        Comm.init (args);

        // Start timing.
        long t1 = System.currentTimeMillis();

        // Validate command line arguments.
        if (args.length != 10) usage();
        width = Integer.parseInt (args[0]);
        height = Integer.parseInt (args[1]);
        xcenter = Double.parseDouble (args[2]);
        ycenter = Double.parseDouble (args[3]);

        dx=Double.parseDouble(args[4]);
        dy=Double.parseDouble(args[5]);

        resolution = Double.parseDouble (args[6]);
        maxiter = Integer.parseInt (args[7]);
        gamma = Double.parseDouble (args[8]);
        filename = new File (args[9]);

        // Initial pixel offsets from center.
        xoffset = -(width - 1) / 2;
        yoffset = (height - 1) / 2;

        // Create image matrix to store results.
        matrix = new int [height] [width];
        image = new PJGColorImage (height, width, matrix);

        // Create table of hues for different iteration counts.
        huetable = new int [maxiter+1];
        for (int i = 0; i < maxiter; ++ i)
            {
            huetable[i] = HSB.pack
                (/*hue*/ (float) Math.pow (((double)i)/((double)maxiter),gamma),
                 /*sat*/ 1.0f,
                 /*bri*/ 1.0f);
            }
        huetable[maxiter] = HSB.pack (1.0f, 1.0f, 0.0f);

        long t2 = System.currentTimeMillis();

        // Compute all rows and columns.
        new ParallelTeam().execute (new ParallelRegion()
            {
            public void run() throws Exception
                {
                execute (0, height-1, new IntegerForLoop()
                    {
                    public void run (int first, int last)
                        {
                        for (int r = first; r <= last; ++ r)
                            {
                            int[] matrix_r = matrix[r];
                            double y = ycenter + (yoffset - r) / resolution;

                            for (int c = 0; c < width; ++ c)
                                {
                                double x = xcenter + (xoffset + c) / resolution;

                                // Iterate until convergence.
                                int i = 0;
                                double aold = x;
                                double bold = y;
                                double a = 0.0;
                                double b = 0.0;
                                double zmagsqr = 0.0;
                                while (i < maxiter && zmagsqr <= 4.0)
                                    {
                                    ++ i;
                                    a = (aold*aold*aold-3.0*aold*bold*bold)+dx;
                                    b = (3.0*aold*aold*bold-bold*bold*bold)+dy;
                                    zmagsqr = a*a + b*b;
                                    aold = a;
                                    bold = b;
                                    }

                                // Record number of iterations for pixel.
                                matrix_r[c] = huetable[i];
                                }
                            }
                        }
                    });
                }
            });

        long t3 = System.currentTimeMillis();

        // Write image to PJG file.
        PJGImage.Writer writer =
            image.prepareToWrite
                (new BufferedOutputStream
                    (new FileOutputStream (filename)));
        writer.write();
        writer.close();

        // Stop timing.
        long t4 = System.currentTimeMillis();
        System.out.println ((t2-t1) + " msec pre");
        System.out.println ((t3-t2) + " msec calc");
        System.out.println ((t4-t3) + " msec post");
        System.out.println ((t4-t1) + " msec total");
        }

// Hidden operations.

    /**
     * Print a usage message and exit.
     */
    private static void usage()
        {
        System.err.println ("Usage: java -Dpj.nt=<K> edu.rit.smp.fractal.MandelbrotSetSmp <width> <height> <xcenter> <ycenter> <resolution> <maxiter> <gamma> <filename>");
        System.err.println ("<K> = Number of processors");
        System.err.println ("<width> = Image width (pixels)");
        System.err.println ("<height> = Image height (pixels)");
        System.err.println ("<xcenter> = X coordinate of center point");
        System.err.println ("<ycenter> = Y coordinate of center point");
        System.err.println ("<resolution> = Pixels per unit");
        System.err.println ("<maxiter> = Maximum number of iterations");
        System.err.println ("<gamma> = Used to calculate pixel hues");
        System.err.println ("<filename> = PJG image file name");
        System.exit (1);
        }
}
