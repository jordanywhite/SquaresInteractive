import java.net.InetAddress;
import java.util.Hashtable;
import java.util.Queue;


public class Server extends User {
	Hashtable<Integer, InetAddress> myIpLookup;
	int[] userIds;
	Queue<Action> actions;
}
