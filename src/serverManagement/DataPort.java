package serverManagement;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Represents a host-to-host connection.
 * Runs a thread that listens for messages.
 * Can send messages using send()
 *
 * Received messages are stored in a queue. getIncMessageQueue() returns
 * a direct link to the queue object. Use the object's poll() method to
 * pop the next message off the front of the queue.
 * 
 * @author Kai Jorgensen
 * @author Bryce Matsuda
 * @author Caleb Piekstra
 * @author Jordan White
 */
public class DataPort implements Runnable {
	
	// Queue size
	public static final int QUEUE_SIZE = 100;

	// member vars
	private int uniqueId = -1;
	private Socket socket;
	private DataOutputStream out;
	private BufferedReader in;	
	private BlockingQueue<String> incMessages = new ArrayBlockingQueue<String>(QUEUE_SIZE); // received-action queue

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
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
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
	
	public BlockingQueue<String> getIncMessageQueue() {
		return incMessages;
	}
}
