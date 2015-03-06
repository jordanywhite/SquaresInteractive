package squInt;
import java.net.InetAddress;
import java.util.Hashtable;
import java.util.Queue;


public class Server extends User {
	
	Hashtable<Integer, InetAddress> myIpLookup;
	int[] userIds;
	Queue<Action> actions;
	
	
	public Server(Room room) {
		super(room);
		
		userId = 0;
		userIds = new int[30];
		userIds[0] = userId;
		
		myIpLookup = new Hashtable<Integer, InetAddress>();
		myIpLookup.put(0, myIp);
	}
	
	public Room getMasterRoom() {
		return null;
	}
	
	public InetAddress getClientIpAddress(int id) {
		return null;
	}
}
