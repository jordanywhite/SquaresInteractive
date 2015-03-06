package squInt;
import java.net.InetAddress;


public abstract class User {
	Room room;	
	int userId;
	InetAddress myIp;
	InetAddress serverIp;
	
	public User(Room room) {
		this.room = room;
	}
	
	public int getId() {
		 return userId;
	}
	
	public InetAddress getIpAddress() {
		return myIp;
	}
	
}
