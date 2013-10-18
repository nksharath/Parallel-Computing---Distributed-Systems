/**
*StateEvent.java 
*
*@author Omkar Kolangade
*@author Aniket Kulkarni
*@author Sharath Navalpakkam Krishnan
*
*/
import edu.rit.sim.Event;

/**
 * Class StateEvent is used to store the state of a node while scheduling a 
 * task completion event in the simulation. The state of a node is required to
 * ensure that the node hasn't gone down and recovered at the time the task 
 * completion event is scheduled to run in the simulation.
 *
 * It extends the Event class in the Computer Science Course Library.
 */
public abstract class StateEvent extends Event {

    public int nodeState;

    /**
     * Create a new StateEvent object.
     *
     * @param  state  The current state of the node.
     */
    public StateEvent(int state) {
        super();
        this.nodeState = state;
    }

}
