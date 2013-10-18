/**
*CentralServer.java 
*@author Omkar Kolangade
*@author Aniket Kulkarni
*@author Sharath Navalpakkam Krishnan
*
*/
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingDeque;
import edu.rit.numeric.ListSeries;
import edu.rit.numeric.Series;
import edu.rit.sim.Event;
import edu.rit.sim.Simulation;

/**
 * Class CentralServer provides a server object in the simulation.
 */
public class CentralServer {

    private Simulation sim;
    private LinkedBlockingDeque<Task> inputQueue;
    private ArrayList<Task> outputQueue;
    private ArrayList<ArrayList<Task>> taskList;
    private ArrayList<Job> jobs;
    private ArrayList<Node> nodes;
    private int dist;
    private int[] nodeState;
    private int policy;
    private ListSeries makespanSeries;
    private boolean stop = false;
    private int tjobs;

    /**
     * Variable to determine if system execution should be displayed by printing
     * events.
     */
    private boolean transcript;

    /**
     * Create a new CentralServer object.
     *
     * @param  sim  The simulation object.
     *
     * @param  nodes  The number of nodes in the system
     *
     * @param  policy  Specifies simulation policy - A or B.
     *
     * @param  transcript  Flag to print system execution details.
     *
     * @param  makespanSeries  ListSeries to record mean makespan times.
     *
     * @param  tjobs  Total number of jobs in the system.
     */
    public CentralServer( Simulation sim, 
                          int nodes, 
                          int policy, 
                          boolean transcript, 
                          ListSeries makespanSeries, 
                          int tjobs) {

        this.sim = sim;
        inputQueue = new LinkedBlockingDeque<Task>();
        outputQueue = new ArrayList<Task>();
        taskList = new ArrayList<ArrayList<Task>>(nodes);
        this.jobs = new ArrayList<Job>();
        this.nodes = new ArrayList<Node>();
        dist = 0;
        nodeState = new int[nodes];
        this.policy = policy;
        this.transcript = transcript;
        this.makespanSeries = makespanSeries;
        this.tjobs = tjobs;

        // Initialize the taskList.
        for (int i = 0; i < nodes; i++) {
            taskList.add(i, new ArrayList<Task>());
        }

        // Initialize the node state array.
        for (int i = 0; i < nodeState.length; i++) {
            nodeState[i] = 0;
        }

        startNodeMonitoring();

    }

    /**
     * Method to start and continue distributing tasks to nodes in a round-robin
     * order.
     */
    public void startDistributing() {

        if (inputQueue.size() > 0 && (makespanSeries.length() < tjobs)) {

            // Determine which node to send the task to.
            int node = getNextNode();

            // In case all nodes are down, wait and try again.
            if (node == -1) {
                // Continue the distribution.
                sim.doAfter(0.25, new Event() {
                    public void perform() {
                        startDistributing();
                    }
                });
            } else {
                if (nodes.get(node).isWorking()) {
                    Task t = inputQueue.removeFirst();

                    // Add the task to the local task list copy of the node.
                    taskList.get(node).add(t);

                    // Add the task to the node's task queue.
                    nodes.get(node).addTask(t);

                    Job j = jobs.get(t.jobId);
                    // Mark the makespan start time when the first task of a job
                    // is assigned to a node.
                    if (!j.firstTaskAssigned) {
                        j.startTime = sim.time();
                        j.firstTaskAssigned = true;
                    }
                }

                // Continue the distribution.
                sim.doAfter(0.1, new Event() {
                    public void perform() {
                        startDistributing();
                    }
                });
            }

        } else if (makespanSeries.length() < tjobs) {
            // Continue the distribution.
            sim.doAfter(0.25, new Event() {
                public void perform() {
                    startDistributing();
                }
            });
        }

    }

    /**
     * Method to determine which node to distribute the next task to. The
     * distribution is done in a round-robin order. The method also ensures that
     * the selected node is not down.
     * 
     * If all the nodes are down, the method return a value of -1.
     *
     * @return  The name of the node to distribute the next task to.
     */
    private int getNextNode() {

        int node = -1;
        int loopCount = 0;

        do {
            node = dist % nodes.size();

            if (dist == nodes.size() - 1) {
                // 'Reset' the distribution variable.
                dist = 0;
            } else {
                dist++;
            }

            loopCount++;

            // In case all the nodes are down, return.
            if (loopCount > nodes.size()) {
                return -1;
            }

        } while (nodes.get(node).isWorking() == false);

        return node;
    }

    /**
     * Method to start and continue monitoring of nodes for inconsistencies.
     */
    public void startNodeMonitoring() {

        if (makespanSeries.length() < tjobs) {

            sim.doAfter(0.01, new Event() {

                public void perform() {
                    for (int i = 0; i < nodes.size(); i++) {
                        if (nodes.get(i).state != nodeState[i]) {

                            // Start the process of resolving task inconsistency
                            // for the node.
                            nodeStateMismatch(i);

                            // Get the node's latest state.
                            nodeState[i] = nodes.get(i).state;
                        }
                    }
                    // Keep monitoring the nodes periodically.
                    sim.doAfter(0.01, new Event() {
                        public void perform() {
                            startNodeMonitoring();
                        }
                    });
                }
            });

        }

    }

    /**
     * Method to add a node reference to the server.
     *
     * @param  n  The node to be added.
     */
    public void addNode(Node n) {
        nodes.add(n);
    }

    /**
     * Method to add a job to the system.
     *
     * @param  j  The job to be added.
     */
    public void addJob(Job j) {

        // Add the job to the job monitor queue.
        jobs.add(j);

        for (int i = 0; i < j.tasks.size(); i++) {
            inputQueue.add(j.tasks.get(i));
        }
    }

    /**
     * Method for nodes to report task completion to the central server.
     *
     * @param  node  The node that finishes the task.
     *
     * @param  jobId  The job id of the finished task.
     *
     * @param  taskId  The id of the finished task.
     */
    public void reportCompletion(int node, int jobId, int taskId) {

        if (taskList.get(node).size() == 0) {
            return;
        }

        Task t = null;
        Job job = null;

        // Mark the task completed in the parent Job object.
        for (int i = 0; i < jobs.size(); i++) {
            job = jobs.get(i);
            if (job.id == jobId) {

                for (int j = 0; j < job.tasks.size(); j++) {
                    t = job.tasks.get(j);
                    if (t.id == taskId) {
                        t.complete = true;
                        break;
                    }
                }
                break;
            }
        }

        t = null;
        int remIndex = -1;
        for (int i = 0; i < taskList.get(node).size(); i++) {
            t = taskList.get(node).get(i);
            if (t.id == taskId) {
                remIndex = i;
                break;
            }
        }
        taskList.get(node).remove(remIndex);

        // Add it to the output queue.
        outputQueue.add(t);

        // Check if the task's completion means completion of it's parent job.
        if (jobs.get(jobId).isComplete()) {

            // Calculate and add the makespan of the job to the series.
            double startTime = jobs.get(jobId).startTime;
            double makespan = sim.time() - startTime;
            jobs.get(jobId).makespan = makespan;
            makespanSeries.add(makespan);

            if (transcript) {
                // System.out.printf("%.3f ", sim.time());
                // System.out.println("Job " + jobId +
                // " finished! <------------");
            }

            // If all the jobs are complete, print the results and exit.
            if (allJobsComplete()) {
                printResults();
            }
        }

    }

    /**
     * Method to check if all the jobs in the simulation have finished.
     *
     * @return  True if all jobs in the system are complete. False otherwise.
     */
    private boolean allJobsComplete() {
        for (int i = 0; i < jobs.size(); i++) {
            if (!jobs.get(i).isComplete()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Method to print the simulation results.
     */
    private void printResults() {

        for (int i = 0; i < jobs.size(); i++) {
            // System.out.println("Job " + i + " makespan: "
            // + jobs.get(i).makespan);
        }

        // System.out.println(outputQueue);

        // Series.Stats stats = makespanSeries.stats();
        // System.out.println("\nMean makespan time: " + stats.mean + "\n");

    }

    /**
     * Method to resolve node state mismatch. The tasks of a node having a 
     * state mismatch are put into the input queue again to be redistributed.
     *
     * @param  node  The node having a state mismatch.
     */
    private void nodeStateMismatch(int node) {

        // Add the failed node's tasks to the input queue.
        for (int i = 0; i < taskList.get(node).size(); i++) {
            Task t = taskList.get(node).get(i);

            // if (!jobs.get(t.jobId).tasks.get(t.id).complete) {
            try {

                if (policy == 0) {
                    // No task priority policy.
                    inputQueue.putLast(t);
                } else {
                    // Task priority policy.
                    inputQueue.putFirst(t);
                }

            } catch (Exception exc) {
                exc.printStackTrace();
            }
            // }
        }

        // Clear the local copy of the node's task queue.
        taskList.get(node).clear();
    }

}

