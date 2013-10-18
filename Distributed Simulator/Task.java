/**
*Task.java 
*
*@author Omkar Kolangade
*@author Aniket Kulkarni
*@author Sharath Navalpakkam Krishnan
*
*/
/**
 * Class Task provides a Task object in the simulation.
 */
public class Task {

    public int jobId;
    public int id;
    public boolean complete;

    /**
     * Create a Task object.
     *
     * @param  jobId  The job id of the job this task is a part of.
     *
     * @param  id  The unique task id of this task within it's job.
     */
    public Task(int jobId, int id) {

        this.jobId = jobId;
        this.id = id;
        complete = false;

    }

    public String toString() {
        return "task " + id + " of job " + jobId;
    }

}