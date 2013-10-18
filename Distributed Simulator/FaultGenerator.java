/**
*FaultGenerator.java 
*
*@author Omkar Kolangade
*@author Aniket Kulkarni
*@author Sharath Navalpakkam Krishnan
*
*/


import java.util.ArrayList;
import edu.rit.numeric.ExponentialPrng;
import edu.rit.numeric.ListSeries;
import edu.rit.sim.Event;
import edu.rit.sim.Simulation;
import edu.rit.util.Random;

/**
 * Class FaultGenerator provides a fault generator object in the simulation.
 */
public class FaultGenerator {

    private Simulation sim;
    private ArrayList<Node> nodeList;
    private boolean stop;
    private ListSeries makespanSeries;
    private int jobs;

    /**
     * PRNG for determining which node to fail.
     */
    private Random prng;

    /**
     * Exponential PRNG for determining node failure timing.
     */
    private ExponentialPrng mttfPrng;

    /**
     * Exponential PRNG for determining node recovery timing.
     */
    private ExponentialPrng mttrPrng;

    /**
     * Create a new FaultGenerator object.
     *
     * @param  sim  The simulation object.
     *
     * @param  nodeList  List of nodes in the system.
     *
     * @param  seed  Seed for the PRNG.
     *
     * @param  mttf  Mean time to failure.
     *
     * @param  mttr  Mean time to recovery.
     *
     * @param  makespanSeries  ListSeries to keep a track of the total number of
     *                         completed jobs in the system.
     *
     * @param  jobs  Total number of jobs in the system.
     */
    public FaultGenerator( Simulation sim, 
                           ArrayList<Node> nodeList, 
                           long seed, 
                           double mttf, 
                           double mttr,
                           ListSeries makespanSeries, 
                           int jobs) {

        this.sim = sim;
        this.nodeList = nodeList;
        this.prng = Random.getInstance(seed);
        this.mttfPrng = new ExponentialPrng(prng, 1.0 / mttf);
        this.mttrPrng = new ExponentialPrng(prng, 1.0 / mttr);
        this.makespanSeries = makespanSeries;
        this.jobs = jobs;

        generateFault();

    }

    /**
     * Method to fail a random node for a random amount of time.
     */
    private void generateFault() {

        if (makespanSeries.length() < jobs) {

            int node;
            int loopCount = 0;

            do {
                // Select a random node.
                int r = Math.abs(prng.nextInteger());
                node = r % nodeList.size();

                loopCount++;

                if (loopCount == nodeList.size()) {
                    node = -1;
                    break;
                }
            } while (nodeList.get(node).isWorking() == false);

            if (node == -1) {
                // Continue to generate faults.
                sim.doAfter(mttfPrng.next(), new Event() {
                    public void perform() {
                        generateFault();
                    }
                });
                return;
            } else {

                final int n = node;

                // Fail the node.
                nodeList.get(n).failNode();

                // Set up a recovery event for the node.
                sim.doAfter(mttrPrng.next(), new Event() {
                    public void perform() {
                        nodeList.get(n).resurrectNode();
                    }
                });

                // Continue to generate faults.
                sim.doAfter(mttfPrng.next(), new Event() {
                    public void perform() {
                        generateFault();
                    }
                });
            }
        }
    }

}
