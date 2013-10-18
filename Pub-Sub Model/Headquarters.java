import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import edu.rit.ds.RemoteEventListener;
import edu.rit.ds.registry.NotBoundException;
import edu.rit.ds.registry.RegistryEvent;
import edu.rit.ds.registry.RegistryEventFilter;
import edu.rit.ds.registry.RegistryEventListener;
import edu.rit.ds.registry.RegistryProxy;

//@author : Sharath Navalpakkam Krishnan : sxn9447@rit.edu

/**
 * Class Headquarters tracks all events from 
 * all offices in the system
 */
public class Headquarters 
{
	//Registry Proxy
	private static RegistryProxy registry;
	//Remote Event Listener listening to all offices
	private static RemoteEventListener<GPSOfficeEvent> officeListener;
	//Registry Event Listener to detect GPSOffice existence 
	private static RegistryEventListener registryListener;
	
	private static RegistryEventFilter filter;
	/**
	 * main function	
	 */
	public static void main(String args[])throws Exception
	{
		if(args.length!=2)
			usage();
		String host = null;
		int port = 0;
		try
		{
		host=args[0];
		port=parseInt(args[1]);
		}
		catch(Exception exc)
		{
			usage();
		}
		registry=new RegistryProxy(host,port);
		
		//offices=new ArrayList(registry.list());
		
		
		registryListener=new RegistryEventListener() {
			
			@Override
			public void report(long arg0, RegistryEvent arg1) throws RemoteException {
				// TODO Auto-generated method stub
				listen(arg1.objectName());
				
			}
		};
		
		UnicastRemoteObject.exportObject (registryListener, 0);
		
		//Logging all events reported from event generator
		officeListener=new RemoteEventListener<GPSOfficeEvent>() {


			@Override
			public void report(long arg0, GPSOfficeEvent arg1)
					throws RemoteException {
				System.out.printf("%s",arg1.event);
				System.out.printf("%n");

			}
		};
		
	
		//A separate thread listening to events
		UnicastRemoteObject.exportObject(officeListener,0);
		
		filter=new RegistryEventFilter().reportType("GPSOffice").reportBound();
		registry.addEventListener(registryListener,filter);
		
		for(String objectname : registry.list("GPSOffice"))
		{
			listen(objectname);
		}
		
		
	}
	
	/**
    * Notify events to me
    *
    * @param  name  the name of the object
    *
    * @exception  RemoteException
    *    Thrown if any error occurs
    */
	private static void listen(String name)
	{
		try
		{
			GPSOfficeRef office=(GPSOfficeRef)registry.lookup(name);
			office.addListener(officeListener);
		}

		catch(NotBoundException exc)
		{
		}
		catch(RemoteException exc)
		{
		}
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
			System.err.printf ("Headquarters : Invalid <%s>:",arg);

			return 0;
		}
	}
	/**
	 * Print a usage message and exit.
	 */
	private static void usage()
	{
		System.err.println ("Usage: java Log <host> <port>");
		System.err.println ("<host> = Registry Server's host");
		System.err.println ("<port> = Registry Server's port");
		System.exit (1);
	}

}
