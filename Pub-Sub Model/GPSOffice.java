
import edu.rit.ds.registry.AlreadyBoundException;
import edu.rit.ds.registry.RegistryEventHandler;
import edu.rit.ds.registry.RegistryProxy;
import java.io.IOException;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import edu.rit.ds.Lease;
import edu.rit.ds.RemoteEventGenerator;
import edu.rit.ds.RemoteEventListener;
import java.util.Set;

//@author : Sharath Navalpakkam Krishnan : sxn9447@rit.edu

/**
 * Class GPSOffice is a distributed object. Represents an operating office
 * in the package delivery system. 
 * Usage: java Start GPSOffice <I><host></I><I><port></I><I><mycity></I><I> 
 * <myX></I><I> <myY></I>
 * <BR><I>host</I> = Registry Server's host
 * <BR><I>port</I> = Registry Server's port
 * <BR><I>mycity</I> = Represents My City
 * <BR><I>myX</I> = My X coordinate
 * <BR><I>myY</I> = My Y coordinate
 */

public class GPSOffice extends RegistryEventHandler implements GPSOfficeRef{
	 /**
	  * Host Name
	  */
	private String host;
	 /**
	  * Port Number
	  */
	private int port;
	 /**
	  * My city name
	  */
	private String mycity;
	 /**
	  * My X coordinate
	  */
	private double myX;
	 /**
	  * My Y coordinate
	  */
	private double myY;
	 /**
	  * RegistryProxy
	  */
	private static RegistryProxy registry;
	 /**
	  * Threadpool to handle multiple customers
	  */
	private ExecutorService threadpool=null;
	 /**
	  * Event Generator to update headquaters 
	  * and customer
	  */
	private RemoteEventGenerator<GPSOfficeEvent> eventGenerator;
	/**
	  * Constructor Initializing Member Variables 
	  * and exporting remote object
	  *
	  * @param  args  Consists of input 
	  */
	public GPSOffice(String args[])throws IOException
	{
		if(args.length!=5)
		{
			
			System.out.println("Usage: java Start GPSOffice <host> <port> <mycity> <myX> <myY>");
			System.exit(1);
			
		}
		try
		{
		//host name
		host=args[0];
		//port number
		port=parseInt(args[1]);
		//Name of my city
		mycity=args[2];
		//My x coordinate
		myX=Double.parseDouble(args[3]);
		//My y coordinate
		myY=Double.parseDouble(args[4]);
		}
		catch(Exception exc)
		{
			try
			{
				System.out.println("Usage: java Start GPSOffice <host> <port> <mycity> <myX> <myY>");
				registry.lookup(host);
				System.gc();			
				System.exit(1);
			}
			
			catch(Exception exc1)
			{
				System.gc();			
				System.exit(1);
			}
			
		}
		//Registry Proxy
		registry=new RegistryProxy(host,port);
		UnicastRemoteObject.exportObject(this,0);
		//Event Generator to report events
		eventGenerator = new RemoteEventGenerator<GPSOfficeEvent>();
		//Thread Pool to handle multiple customers
		threadpool=Executors.newCachedThreadPool();
		try
		{
			//Binding remote object
			registry.bind(mycity,this);						
		}
		catch(AlreadyBoundException exc)
		{
			try
			{
				UnicastRemoteObject.unexportObject(this, true);
			}
			catch(NoSuchObjectException exc2)
			{

			}
			throw new IllegalArgumentException("GPSOffice(): <mycity>= \""+mycity+"already exists");
		}
		catch(RemoteException exc)
		{
			try
			{
				UnicastRemoteObject.unexportObject(this,true);
			}
			catch(NoSuchObjectException exc2)
			{
			}
			throw exc;
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
			return Integer.parseInt(arg);
		}
		catch(NumberFormatException exec)
		{
			throw new IllegalArgumentException("GPSOffice(): Invalid"+arg);		
		}
	}

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
	public void parcel(final PackageFactory mypackage)throws RemoteException
	{
		synchronized (this) {


			threadpool.submit(new Runnable() {

				public void run()
				{
					try
					{
						mypackage.settracking(System.currentTimeMillis());
						forward(mypackage);
					}
					catch(Exception exc)
					{
					}

				}
			});
		}
	}


	 /**
	    * Forwards the package to neighbour or beams the package 
	    * if the destination is closer than neighbour
	    * @param  parcel  package recieved from customer
	    *
	    * @exception  RemoteException
	    *     (unchecked exception) Thrown if any error while forwarding
	    *     the package
	    */
	public boolean forward(PackageFactory parcel)throws RemoteException
	{
		
		try{
			boolean status;
			
				
			parcel.logger.report(parcel.tracknum,new GPSOfficeEvent("Package number "+parcel.tracknum+
					" arrived at "+mycity+" office",parcel.tracknum));

			if(myX!=parcel.destX && myY!=parcel.destY)
			eventGenerator.reportEvent (new GPSOfficeEvent("Package number "+parcel.tracknum+"" +
					" arrived at "+mycity+" office",parcel.tracknum));
			
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
		
					
			//Building a TreeMap to maintain Routing Table
			TreeMap<Double,String> map=new TreeMap<Double, String>(getNeighbours(parcel));
			
			
			//comment-out this part of code to detect GPSOffice failure and re-route the packet without loss .
			
			//Performing HeartBeat to every neighbor to check if alive
			/*for(Map.Entry<Double,String> entry:map.entrySet())
			{
				try
				{
					GPSOfficeRef alive=(GPSOfficeRef)registry.lookup(entry.getValue());
					alive.getmyX();
				}
				catch(Exception exc)
				{
					map.remove(entry.getKey());

				}
			}*/
			
			//If I am the destination
			if(myX==parcel.destX && myY==parcel.destY)
			{
				parcel.status=true;
				eventGenerator.reportEvent (new GPSOfficeEvent("Package number "+parcel.tracknum+" " +
					"delivered from "+mycity+" office to ("+parcel.destX+","+parcel.destY+")",parcel.tracknum));
				
			parcel.logger.report(parcel.tracknum,new GPSOfficeEvent("Package number "+parcel.tracknum+" " +
					"delivered from "+mycity+" office to ("+parcel.destX+","+parcel.destY+")",parcel.tracknum));
				return parcel.status;
			}
			
			//Calculating distance
			double beamdistance=Math.sqrt(Math.pow(myX-parcel.destX, 2)+Math.pow(myY-parcel.destY,2));
			double neighbourdistance;
			
			//Determine whom to send the package next 
			String temp=sendnext(map,parcel.destX,parcel.destY,parcel);
			GPSOfficeRef beam=null;

			try
			{
				beam=(GPSOfficeRef)registry.lookup(temp);

			}
			catch(Exception exc)
			{	
			}

			//Calculating neighbor distance
			neighbourdistance=Math.sqrt(Math.pow(beam.getmyX()-parcel.destX, 2)+Math.pow(beam.getmyY()-parcel.destY, 2));
			
			//If destination is closer than neighbor
			if(beamdistance<=neighbourdistance)
			{
				parcel.status=true;
				eventGenerator.reportEvent (new GPSOfficeEvent("Package number "+parcel.tracknum+" " +
						"delivered from "+mycity+" office to ("+parcel.destX+","+parcel.destY+")",parcel.tracknum));

				parcel.logger.report(parcel.tracknum,new GPSOfficeEvent("Package number "+parcel.tracknum+" " +
						"delivered from "+mycity+" office to ("+parcel.destX+","+parcel.destY+")",parcel.tracknum));

				return parcel.status;
			}

			else
			{	

				GPSOfficeRef sendnext=null;
				try
				{
					sendnext=(GPSOfficeRef)registry.lookup(temp);
				}
				catch(Exception exc)
				{
					
				}
				//Where do I forward next 
				String transfercity=sendnext.getmycity();
				status=forwardParcelTo(transfercity,parcel);	
				return status;
			}
		}
		catch(Exception exc)
		{
			return false;
		}

	}
	
	 /**
	   * Forwards the package to the specified city
	   * 
	   * @param  name    Name of the city forwarded to
	   * @param  parcel  package received from customer
	   *
	   * @exception  RemoteException
	   *     (unchecked exception) Thrown if any error while forwarding
	   *     the package
	   */

	private boolean forwardParcelTo(String name,PackageFactory parcel) throws RemoteException
	{
		eventGenerator.reportEvent (new GPSOfficeEvent("Package number "+parcel.tracknum+" departed from "
				+mycity+" office",parcel.tracknum));

		parcel.logger.report(parcel.tracknum,new GPSOfficeEvent("Package number "+parcel.tracknum
				+" departed from "+mycity+" office",parcel.tracknum) );

		try
		{

			GPSOfficeRef office=(GPSOfficeRef)registry.lookup(name);
			return office.forward(parcel);
		}
		//Reporting Package Loss to HeadQuarters and Customer
		catch(Exception exc)
		{

			eventGenerator.reportEvent (new GPSOfficeEvent("Package number "+parcel.tracknum+
					" lost by "+mycity+" office",parcel.tracknum));
			parcel.logger.report(parcel.tracknum, new GPSOfficeEvent("Package number "+parcel.tracknum+
					" lost by "+mycity+" office",parcel.tracknum));

			return false;		


		}

	}
	 /**
	   * Generates the neighbors of my office 
	   * 
	   * @return TreeMap 	a map of neighbors and their 
	   * distances from my office
	   *
	   * @exception  RemoteException
	   *     (unchecked exception) Thrown if any error while forwarding
	   *     the package
	   */

	private TreeMap<Double, String> getNeighbours(PackageFactory parcel)throws RemoteException
	{
		//Building the list of offices 
		final ArrayList<String> offices=new ArrayList<String>(registry.list());
		//When a new offices comes up : 
		//Office going down is handled by heartbeat system above
		new RegistryEventHandler()
		{
			protected void bound(long arg0,long timestamp,String theName,Set<String> theTypes)
			{
				offices.add(theName);

			}

		};

		RegistryProxy registry=new RegistryProxy(host, port);

		GPSOfficeRef temp;
		TreeMap<Double, String> map=new TreeMap<Double, String>();
		for(int i=0;i<offices.size();i++)
		{
			if(mycity.equals(offices.get(i)))
			{
				continue;
			}
			try
			{
				temp=(GPSOfficeRef)registry.lookup(offices.get(i));
				//Calculating the distance
				double distance=Math.sqrt(Math.pow(myX-temp.getmyX(),2) + Math.pow(myY-temp.getmyY(),2));
				map.put(distance, temp.getmycity());	
			}
			catch(Exception exc)
			{
				eventGenerator.reportEvent (new GPSOfficeEvent("Package number "+parcel.tracknum+
						" lost by "+mycity+" office",parcel.tracknum));
				parcel.logger.report(parcel.tracknum, new GPSOfficeEvent("Package number "+parcel.tracknum+
						" lost by "+mycity+" office",parcel.tracknum));

			}

		}
		//The map is sorted by distance in ascending order
		return map;		

	}
	 /**
	    * Determines where the package must be forwarded next
	    * from given neighbors
	    * @param  map  a TreeMap of neighbors
	    * @param  destX	the X coordinate of destination
	    * @param  destY the Y coordinate  of destination
	    * 
	    * @return String The closest office to the destination
	    *
	    * @exception  RemoteException
	    *     (unchecked exception) Thrown if any error while forwarding
	    *     the package
	    */

	public String sendnext(TreeMap<Double,String> map,double destX,double destY,PackageFactory parcel) throws RemoteException
	{
		TreeMap<Double,String> mapreturn=new TreeMap<Double, String>();
		ArrayList<String> list=new ArrayList<String>();

		for(Map.Entry<Double,String> tuple: map.entrySet())
		{
			list.add(tuple.getValue());						

		}

		double distance;
		for(int i=0;i<3;i++)
		{
			try
			{
				
				GPSOfficeRef temp=(GPSOfficeRef)registry.lookup(list.get(i));
				distance=Math.sqrt(Math.pow(temp.getmyX()-destX,2) + Math.pow(temp.getmyY()-destY,2));
				mapreturn.put(distance,list.get(i));
			}
			catch(Exception exc)
			{

			}
		}

		Map.Entry<Double,String> returnval=(mapreturn.firstEntry());
		//The first entry is the closest office to the destination
		return returnval.getValue();		

	}
	
	 /**
	   * get method for x-coordinate
	   *
	   *@return double x-coordinate
	   */

	public double getmyX()
	{
		return myX;
	}
	
	 /**
	   * get method for y-coordinate
	   *
	   *@return double y-coordinate
	   */

	public double getmyY()
	{
		return myY;
	}

	 /**
	   * get method for my city
	   *
	   *@return String my-city
	   */

	public String getmycity()
	{
		return mycity;
	}
	
	 /**
	   * Adding Listeners to event generator
	   *@param  listener  the remote event listener to be added
	   *@return Lease 	returns the Lease 
	   */

	public Lease addListener(RemoteEventListener<GPSOfficeEvent> listener)throws RemoteException
	{
		return eventGenerator.addListener(listener);
	}


}
