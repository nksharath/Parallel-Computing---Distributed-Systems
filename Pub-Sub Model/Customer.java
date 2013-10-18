import edu.rit.ds.RemoteEventListener;
import edu.rit.ds.registry.RegistryProxy;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

//@author : Sharath Navalpakkam Krishnan : sxn9447@rit.edu

/**
 * Class Customer is a client program for sending a package to the specified destination
 * <P>
 * Usage: java Customer <I><host></I> <I><port></I> <I><name></I> <I><DestX></I><I> <DestY></I>
 * <BR><I>host</I> = Registry Server's host
 * <BR><I>port</I> = Registry Server's port
 * <BR><I>name</I> = Name of the Origin Office from which package is shipped
 * <BR><I>DestX</I> = Destination Coordinate X
 * <BR><I>DestY</I> = Destination Coordinate Y
 */

public class Customer {
	/**
	 * Name of the Origin Office
     */
	public final String originname;
	/**
	 * Destination coordinate X
     */
	public final double destX;
	/**
	 * Destination coordinate Y
     */
	public final double destY;
	/**
	 * Remote Event Listener : Tracking Updates from Offices
     */
	static RemoteEventListener<GPSOfficeEvent> logger=null;
	
	/**
	 * Constructor initialising the origin name
	 *
	 * @param  originNode    Originating node ID.
	 * @param  serialNumber  Serial number.
	 * @param  title         Article title.
	 */

	Customer(String originname,double destX,double destY)
	{
		this.originname=originname;
		this.destX=destX;
		this.destY=destY;	

	}
	/**
	 * Customer main program.
     */

	public static void main(String args[])throws Exception
	{
		if(args.length!=5)usage();
		
		String host = null;
		int port = 0;
		String city = null;
		double x = 0,y = 0;
		try
		{
		//Recieving Host Name
		host=args[0];
		//Recieving Port
		port=parseInt(args[1]);
		//Recieving Origin City
		city=args[2];
		//Recieving DestX
		x=Double.parseDouble(args[3]);
		//Recieving DestY
		y=Double.parseDouble(args[4]);	
		}
		catch(Exception exc)
		{
			usage();
		}
		Customer customerobj=new Customer(city,x,y);
		RegistryProxy registry=new RegistryProxy(host,port);
		//Initial lookup of origin city
		GPSOfficeRef office=(GPSOfficeRef)registry.lookup(city);
		
		//Event listener : Tracking events 
		logger=new RemoteEventListener<GPSOfficeEvent>() {


			public void report(long arg0,GPSOfficeEvent arg1) throws RemoteException {

				System.out.printf("%s",arg1.event);
				System.out.printf("%n");
				if(arg1.event.contains("delivered") || arg1.event.contains("lost"))
				{
					logger=null;
					System.exit(1);
				}
			}			

		};


		UnicastRemoteObject.exportObject(logger,0);
		//Creating package
		final PackageFactory mypackage=new PackageFactory(customerobj.originname,customerobj.destX,customerobj.destY,logger);
		//Sending package to originating office
		office.parcel(mypackage);	

	}
	
	/**
	 * Print a usage message and exit.
	 */
	private static void usage()
	{
		System.err.println ("Usage: java Customer <host> <port> <name> <DestX> <DestY>");
		System.err.println ("<host> = Registry Server's host");
		System.err.println ("<port> = Registry Server's port");
		System.err.println ("<name> = City of originating node");
		System.err.println ("<DestX> = Destination X-coordinate");
		System.err.println ("<DestY> = Destination Y-coordinate");
		System.exit (1);
	}
	
	 /**
	    * Parse an integer command line argument.
	    *
	    * @param  arg  Command line argument.
	    *
	    * @return  Integer value of <TT>arg</TT>.
	    *
	    * @exception  NumberFormatException
	    *     (unchecked exception) Thrown if <TT>arg</TT> cannot be parsed as an
	    *     integer.
	    */
	private static int parseInt(String arg)
	{
		try
		{
			return Integer.parseInt (arg);
		}
		catch (NumberFormatException exc)
		{
			System.err.printf ("Customer: Invalid <%s>:",arg);
			usage();
			return 0;
		}
	}
}
