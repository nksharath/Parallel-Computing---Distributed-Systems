import java.io.Serializable;

import edu.rit.ds.RemoteEventListener;

//@author : Sharath Navalpakkam Krishnan : sxn9447@rit.edu
/**
 * Class PackageFactory creates the package to be forwarded
 * 
 */
public class PackageFactory implements Serializable{

	private static final long serialVersionUID = 1L;
	//Package tracking number
	public long tracknum;
	//destination x coordinate
	public double destX;
	//destination y coordinate
	public double destY;
	//originating office name
	public String originname;
	//Remote event listener to notify customer
	public RemoteEventListener<GPSOfficeEvent> logger;
	//Delivery status
	public boolean status=false;

	  /**
	    * Constructor initialising the member variables.
	    *
	    * @param  name  Originating office name
	    * @param  x		x coordinate
	    * @param  y		y coordinate
	    * @param  logger  Remote Event Listener to notify customer
	    */
	public PackageFactory(String name,double X,double Y,RemoteEventListener<GPSOfficeEvent> logger)
	{
		originname=name;
		destX=X;
		destY=Y;	
		this.logger=logger;
	}
	 /**
	    * setter method for setting tracking id 
	    * from the originating office
	    *
	    * @param  trackid	tracking number
	
	    */
	public void settracking(long trackid)
	{
		this.tracknum=trackid;		
	}	


}
