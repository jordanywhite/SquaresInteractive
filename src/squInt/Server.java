package squInt;
import java.net.InetAddress;
import java.util.Hashtable;
import java.util.Queue;


/**
 * The User object for the server.
 *
 */
public class Server extends User {
	
	Hashtable<Integer, InetAddress> myIpLookup;
	int[] userIds;
	Queue<Action> actions;
	
	
	public Server() {
		super();
		
		setUserID(0);
		userIds = new int[30];
		userIds[0] = getUserID();
		
		myIpLookup = new Hashtable<Integer, InetAddress>();
//		myIpLookup.put(0, myIp);
	}
	
	public Room getMasterRoom() {
		return null;
	}
	
	public InetAddress getClientIpAddress(int id) {
		return null;
	}
}
