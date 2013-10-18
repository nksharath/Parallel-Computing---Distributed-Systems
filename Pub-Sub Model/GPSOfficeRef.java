import java.rmi.Remote;
import edu.rit.ds.Lease;
import edu.rit.ds.RemoteEventListener;
import java.rmi.RemoteException;
//@author : Sharath Navalpakkam Krishnan : sxn9447@rit.edu

/**
 * Interface GPSOfficeRef is a remote interface for the distributed objects 
 * of GPSOffice
 */

public interface GPSOfficeRef extends Remote {


	 /**
	    * Recieves the parcel from Customer and creates a new thread
	    * and forwards towards destination
	    *
	    * @param  mypackage  package recieved from customer
	    *
	    * @exception  RemoteException
	    *     (unchecked exception) Thrown if any error while forwarding
	    *     the package
	    */
	public void parcel(PackageFactory mypackage)throws RemoteException;
	 /**
	    * Forwards the package to neighbour or beams the package 
	    * if the destination is closer than neighbour
	    * @param  parcel  package recieved from customer
	    *
	    * @exception  RemoteException
	    *     (unchecked exception) Thrown if any error while forwarding
	    *     the package
	    */
	public boolean forward(final PackageFactory parcel)throws RemoteException;
	/**
	   * get method for x-coordinate
	   *
	   *@return double x-coordinate
	   */
	public double getmyX()throws RemoteException;
	 /**
	   * get method for y-coordinate
	   *
	   *@return double y-coordinate
	   *@exception  RemoteException
	   *     (unchecked exception) Thrown if any error arises in 
	   *     this block
	   */

	public double getmyY()throws RemoteException;
	 /**
	   * get method for my city
	   *
	   *@return String my-city
	   *
	   *@exception  RemoteException
	   *     (unchecked exception) Thrown if any error arises in 
	   *     this block
	   */
	public String getmycity()throws RemoteException;	
	/**
	   * Adding Listeners to event generator
	   *@param  listener  the remote event listener to be added
	   *@return Lease 	returns the Lease 
	   *@exception  RemoteException
	   *     (unchecked exception) Thrown if any error arises in 
	   *     this block
	   */
	public Lease addListener(RemoteEventListener<GPSOfficeEvent> listener)throws RemoteException;

}
