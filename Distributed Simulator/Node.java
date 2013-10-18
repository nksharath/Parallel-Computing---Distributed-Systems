/**
*Node.java
* 
*@author Omkar Kolangade
*@author Aniket Kulkarni
*@author Sharath Navalpakkam Krishnan
*
*/
import java.util.concurrent.LinkedBlockingQueue;

import edu.rit.numeric.ExponentialPrng;
import edu.rit.numeric.ListSeries;
import edu.rit.sim.Event;
import edu.rit.sim.Simulation;
import edu.rit.util.Random;

/**
 * Class Node provides a node object in the simulation.
 */
public class Node {

    private Simulation sim;
    private int name;
    private LinkedBlockingQueue<Task> queue;
    private ExponentialPrng tprocPrng;
    private CentralServer server;
    private boolean working;
    private boolean kickStart = false;
    private ListSeries makespanSeries;
    private int jobs;

    public int state = 0;

    /**
     * Variable to determine if system execution should be displayed by printing
     * events.
     */
    private boolean transcript;

    /**
     * Create a new Node object.
     *
     * @param  sim  The simulation object.
     *
     * @param  name  The name of the node.
     *
     * @param  prng  The pseudo-random numner generator
     *
     * @param  tproc  Mean time for processing a task.
     *
     * @param  server  The central server.
     *
     * @param  transcript  Flag to print system execution details.
     *
     * @param  makespanSeries  ListSeries to keep a track of the total number of
     *                         completed jobs in the system.
     *
     * @param  jobs  Total number of jobs in the system.
     */
    public Node( Simulation sim, 
                 int name, 
                 Random prng, 
                 double tproc, 
                 CentralServer server,
                 boolean transcript,
                 ListSeries makespanSeries, 
                 int jobs) {

        this.sim = sim;
        this.name = name;
        queue = new LinkedBlockingQueue<Task>();
        tprocPrng = new ExponentialPrng(prng, 1.0 / tproc);
        this.server = server;
        working = true;
        this.transcript = transcript;
        this.makespanSeries = makespanSeries;
        this.jobs = jobs;

    }

    /**
     * Method used by the central server to add tasks to the node.
     *
     * @param  t  The task to be added to the node's task queue.
     */
    public void addTask(Task t) {

        try {
            // Add the task to the local task queue.
            queue.add(t);
        } catch (Exception exc) {
            System.out.println(this + ": Server could not add task to my queue!");
        }

        if (transcript) {
            System.out.printf("%.3f ", sim.time());
            System.out.println(this + " received " + t);
        }

        // Ensure that the initial startProcessing() method is called only once.
        if (kickStart == false) {
            startProcessing();
            kickStart = true;
        }

    }

    public int getNumberOfPendingTasks() {
        return queue.size();
    }

    /**
     * Method to start processing the tasks in the Node's queue.
     */
    private void startProcessing() {

        if (working && (makespanSeries.length() < jobs)) {

            if (queue.size() > 0) {

                Task task = null;
                try {
                    // Add the task to the local task queue.
                    task = queue.take();
                } catch (Exception exc) {
                    System.out.println(this + ": Problem getting a task from the queue!");
                }
                final Task t = task;

                sim.doAfter(tprocPrng.next(), new StateEvent(state) {
                    public void perform() {

                        // Check if the node did not fail and then come up
                        // before executing the scheduled event.
                        if (working && (state == nodeState)) {

                            if (transcript) {
                                System.out.printf("%.3f ", sim.time());
                                System.out.println("Node " + name + " finished processing " + t);
                            }

                            // Report task completion to the central server.
                            server.reportCompletion(name, t.jobId, t.id);

                            // Continue processing the next task if more tasks
                            // are in the queue.
                            startProcessing();

                        }
                    }
                });

            } else {
                sim.doAfter(0.1, new Event() {
                    public void perform() {
                        startProcessing();
                    }
                });
            }
        }

    }

    /**
     * Method to simulate the failure of a node.
     */
    public void failNode() {

        working = false;
        queue.clear();

        if (state == Integer.MAX_VALUE) {
            state = 0;
        } else {
            state++;
        }

        if (transcript) {
            System.out.printf("%.3f ", sim.time());
            System.out.println(this + " went down!");
        }

    }

    /**
     * Method to simulate the recovery of a node after a failure.
     */
    public void resurrectNode() {
        working = true;
        startProcessing();

        if (transcript) {
            System.out.printf("%.3f ", sim.time());
            System.out.println(this + " is up again ...");
        }

    }

    /**
     * Method to check if a node is working.
     *
     * @return  True if the node is working. False if it has failed.
     */
    public boolean isWorking() {
        return working;
    }

    public String toString() {
        return "Node " + name;
    }

}
