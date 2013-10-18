/**
*Job.java 
*
*@author Omkar Kolangade
*@author Aniket Kulkarni
*@author Sharath Navalpakkam Krishnan
*
*/
import java.util.ArrayList;

/**
 * Class Job provides a Job object in the simulation.
 */
public class Job {

    public ArrayList<Task> tasks;
    public int id;
    public double startTime;
    public double makespan;
    public boolean firstTaskAssigned = false;

    /**
     * Create a new Job object.
     *
     * @param  id  The unique job ID.
     *
     * @param  tasks  The number of tasks in the job.
     */
    public Job(int id, int tasks) {

        this.id = id;
        this.tasks = new ArrayList<Task>();

        for (int i = 0; i < tasks; i++) {
            this.tasks.add(new Task(id, i));
        }

    }

    /**
     * Method to check if a job is complete i.e. all it's tasks are complete.
     */
    public boolean isComplete() {
        
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).complete == false) {
                return false;
            }
        }
        return true;
    }

    public String toString() {
        return "Job " + id;
    }

}