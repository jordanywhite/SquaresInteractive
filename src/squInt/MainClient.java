package squInt;

import gui_client.SquintGUI;

import java.io.IOException;
import java.util.LinkedList;

/**
 * The main method for a client
 *
 */
public class MainClient {
	
	private static final String ipAddr = "localhost";
	
	private SquintGUI gui;
	
//	private static final String ipAddr = "10.12.18.33";
	
	public static void main(String[] args) {

		// Create the main client
		MainClient client = new MainClient();
		
		// Set up the GUI for the client
		client.gui = new SquintGUI();
		client.gui.initGUI();
		
		DataPort connection = null; // the client's connection to the server
		LinkedList<String> incMsgQueue = null; // a queue containing any new messages received by the connection
		
		try {
			// init a connection to the server
			connection = new DataPort(ipAddr, 9999);
			// set the queue to point to the DataPort's queue
			incMsgQueue = connection.getIncMessageQueue();
			
			// start DataPort listener thread
			Thread serverPlayerThread = new Thread(connection);
			serverPlayerThread.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// ok, connection is established. 
		
		// wait until we receive a message
		while (incMsgQueue.peek() == null) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		// print the message!
		// poll() also removes it from the queue
		String msg = incMsgQueue.poll();
		System.out.println("RCVD: " + msg);
		
		// send a response
		connection.send("Trails indeed!");
		System.out.println("SENT: Trails indeed!");
		
	}
}
