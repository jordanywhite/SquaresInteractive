package squInt;

import java.io.*;
import java.net.*;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Represents a host-to-host connection.
 * Runs a thread that listens for messages.
 * Can send messages using send()
 *
 * Received messages are stored in a queue. getIncMessageQueue() returns
 * a direct link to the queue object. Use the object's poll() method to
 * pop the next message off the front of the queue.
 */
public class DataPort implements Runnable {
	// message types
	public static final int INIT_MSG = 0;
	public static final int ACTION_MSG = 1;
	public static final int ROOM_MSG = 2;

	// member vars
	private int uniqueId = -1;
	private Socket socket;
	private DataOutputStream out;
	private BufferedReader in;
	private LinkedList<String> incMessages = new LinkedList<String>(); // received-action queue

	public DataPort(String hostname, int port) throws IOException {
		this(new Socket(hostname, port));
	}

	public DataPort(Socket s) throws IOException {
		socket = s;
		socket.setSoTimeout(0);
		socket.setKeepAlive(true);

		out = new DataOutputStream(socket.getOutputStream());
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}
	
	public int getUniqueId() {
		return uniqueId;
	}
	
	public void setUniqueId(int id) {
		uniqueId = id;
	}

	@Override
	public void run() {
		// Loop that monitors the socket for incoming messages
		while(true) {
			try {
				String newMessage = in.readLine();
				
				if(newMessage != null) {
					// add message to received-message queue
					incMessages.add(newMessage);
				}
			} catch (IOException e) {
				try {
					in.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				break;
			}
		}
	}

	/**
	 * Send a message to the target host
	 * @param msg
	 */
	public boolean send(String msg) {
		try {
			out.writeBytes(msg + '\n');
		} catch (IOException e) {
			System.out.println("SEND FAILED: " + msg);
			return false;
		}
		
		return true;
	}
	
	public LinkedList<String> getIncMessageQueue() {
		return incMessages;
	}
}
