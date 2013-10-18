import edu.rit.ds.RemoteEvent;
//@author : Sharath Navalpakkam Krishnan : sxn9447@rit.edu

/**
 * Class GPSOfficeEvent extends RemoteEvent to track
 * all events generated by offices and to report it to
 * HeadQuarters and Customer
 */
public class GPSOfficeEvent extends RemoteEvent {
	
	private static final long serialVersionUID = 1L;
	public final String event;
	public final long trackid;

	 /**
	   * Constructor to Initialise member variables
	   *
	   * @param  event	 The event that has occured  
	   * @param  trackid The tracking id of the package
	   */
	public GPSOfficeEvent(String event,long trackid)
	{
		this.event=event;
		this.trackid=trackid;
	}


}
