/**
*FaultSim.java 
*@author Omkar Kolangade
*@author Aniket Kulkarni
*@author Sharath Navalpakkam Krishnan
*
*/
import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;
import edu.rit.numeric.AggregateXYSeries;
import edu.rit.numeric.ListSeries;
import edu.rit.numeric.Statistics;
import edu.rit.numeric.plot.Plot;
import edu.rit.sim.Simulation;
import edu.rit.util.Random;

/**
 * Class FaultSim is the main simulation program. It creates and maintatins the
 * required simulation entities, gets command line arguments from the user,
 * runs the simulations and presents data after analysis.
 * <P>
 * Usage: java FaultSim <I>nodes></I> <I>jobs</I> <I>seed</I> <I>tproc</I> 
 *                      <I>mttfLower</I> <I>mttfUpper</I> <I>mttfDelta</I>
 * <BR><I>nodes></I> = Number of nodes in the system
 * <BR><I>jobs</I> = Number of jobs (each having four tasks) in the system
 * <BR><I>seed</I> = Seed for pseudo-random number generator (PRNG)
 * <BR><I>tproc</I> = Mean time required for a node to process a task
 * <BR><I>mttfLower</I> = Lower bound of the range of mean times to failure 
 *                        (MTTF) for a node (same for all nodes)
 * <BR><I>mttfUpper</I> = Upper bound of the range of mean times to failure 
 *                        (MTTF) for a node (same for all nodes)
 * <BR><I>mttfDelta</I> = MTTF decrement factor for the mttf range
 */
public class FaultSim {

    private static Simulation sim;
    private static int nodes;
    private static ArrayList<Node> nodeList;
    private static int jobs;
    private static long seed;
    private static double tproc;
    private static Random prng;
    private static CentralServer server;
    private static FaultGenerator fGenerator;
    private static double mttflower;
    private static double mttfupper;
    private static double mttfdelta;

    private static ListSeries mttfseries;
    private static ListSeries makespanSeries0;
    private static ListSeries makespanSeries1;
    private static ListSeries result0series;
    private static ListSeries result1series;

    /**
     * Variable to determine if system execution should be displayed by printing
     * events.
     */
    private static boolean transcript = false;

    /**
     * Mean Time To Failure (MTTF) of nodes.
     */
    private static double mttf;

    /**
     * Mean Time To Repair (MTTR) of nodes.
     */
    private static double mttr;

    /**
     * Variable to determine fault tolerance policy. Values: 0 - No priority 1 -
     * Priority
     */
    private static int policy;

    // The main method.
    public static void main(String[] args) {

        if (args.length != 7) {
            System.out.println("Usage: java FaultSim "
                    + "<nodes> <jobs> <seed> <tproc> " 
                    + "<mttflower> <mttfupper> <mttfdelta>");
            System.exit(1);
        }

        sim = new Simulation();
        mttfseries = new ListSeries();
        result0series = new ListSeries();
        result1series = new ListSeries();

        // Parse the command line arguments.
        try {
            nodes = Integer.parseInt(args[0]);
            jobs = Integer.parseInt(args[1]);
            seed = Long.parseLong(args[2]);
            prng = Random.getInstance(seed);
            tproc = Double.parseDouble(args[3]);
            mttflower = Double.parseDouble(args[4]);
            mttfupper = Double.parseDouble(args[5]);
            mttfdelta = Double.parseDouble(args[6]);
        } catch (Exception exc) {
            System.err.println("FaultSim: Error parsing numerical arguments.");
            System.exit(1);
        }

        // Check for valid policy value.
        if (policy != 0 && policy != 1) {
            System.out.println("Invalid policy value '" + policy + "'. " 
                + "Correct values are 0 and 1.");
            System.exit(1);
        }
        // Run the simulation for for a range of interarrival times.
        System.out
                .println("Mean Time to Failure (MTTF) \t Makespan(Policy A) \t\t Makespan(Policy B) \t\t\t t \t\t\t p");
        for (double j = mttfupper; j >= mttflower; j -= mttfdelta) {

            // Ensure that mttr and mttf are the same.
            mttf = j;
            mttr = 0.5;
            mttfseries.add(mttf);
            System.out.print("\t" + mttf + "\t\t\t");

            // Policy A simulation.
            makespanSeries0 = new ListSeries();
            policy = 0;
            sim = new Simulation();
            // Initialize the server.
            server = new CentralServer(sim, nodes, policy, 
                                            transcript, makespanSeries0, jobs);

            // Add jobs to the server's input queue.
            for (int i = 0; i < jobs; i++) {
                server.addJob(new Job(i, 4));
            }

            nodeList = new ArrayList<Node>();

            // Initialize the processing nodes.
            for (int i = 0; i < nodes; i++) {
                Node n = new Node(sim, i, prng, tproc, server, 
                                            transcript, makespanSeries0, jobs);

                // Add the node to the list of nodes and the server.
                nodeList.add(n);
                server.addNode(n);
            }

            // Initialize the fault generator.
            fGenerator = new FaultGenerator(sim, nodeList, seed, mttf, mttr, 
                                                        makespanSeries0, jobs);

            server.startDistributing();

            sim.run();
            result0series.add(makespanSeries0.stats().mean);
            System.out.print(makespanSeries0.stats().mean + "\t\t");


            // Policy B simulation.
            policy = 1;
            makespanSeries1 = new ListSeries();

            sim = new Simulation();

            // Initialize the server.
            server = new CentralServer(sim, nodes, policy, 
                                            transcript, makespanSeries1, jobs);

            // Add jobs to the server's input queue.
            for (int i = 0; i < jobs; i++) {
                server.addJob(new Job(i, 4));
            }

            nodeList = new ArrayList<Node>();

            // Initialize the processing nodes.
            for (int i = 0; i < nodes; i++) {
                Node n = new Node(sim, i, prng, tproc, server, 
                                            transcript, makespanSeries1, jobs);

                // Add the node to the list of nodes and the server.
                nodeList.add(n);
                server.addNode(n);
            }

            // Initialize the fault generator.
            fGenerator = new FaultGenerator(sim, nodeList, seed, mttf, mttr, 
                                                        makespanSeries1, jobs);

            server.startDistributing();

            sim.run();

            result1series.add(makespanSeries1.stats().mean);
            System.out.print(makespanSeries1.stats().mean + "\t");

            double[] tandp = 
                Statistics.tTestUnequalVariance(result0series, result1series);
            System.out.print("\t" + tandp[0] + "\t\t" + tandp[1]);
            System.out.println();

        }

        // Plot a graph of mean interarrival times v/s mean response times.
        new Plot()
            .frameTitle("Makespan comparison for Policy A and Policy B")
            .rightMargin(50)
            .leftMargin(120)
            .xAxisTitle("Mean time to failure (MTTF)")
            .xAxisTickFormat(new DecimalFormat("0.0"))
            .yAxisTitle("Makespan")
            .yAxisTitleOffset(60)
            .xAxisStart(mttflower)
            .xAxisEnd(mttfupper)
            .xAxisMajorDivisions((int) (mttfupper - mttflower))
            .xAxisMinorDivisions(10)
            .yAxisTickFormat(new DecimalFormat("0.0"))
            .seriesDots(null)
            .seriesColor(Color.BLACK)
            .xySeries(new AggregateXYSeries(mttfseries, result0series))
            .seriesColor(Color.RED)
            .xySeries(new AggregateXYSeries(mttfseries, result1series))
            .labelPosition(Plot.RIGHT).labelOffset(10)
            .labelColor(Color.BLACK)
            .label("Policy A", mttfseries.x(mttfseries.length() - 1), 
                result0series.x(result0series.length() - 1))
            .labelColor(Color.RED)
            .label("Policy B", mttfseries.x(mttfseries.length() - 1), 
                result1series.x(result1series.length() - 1))
            .getFrame().
            setVisible(true);

    }
}
