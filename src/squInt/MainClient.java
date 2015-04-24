package squInt;

import gui_client.SquintGUI;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * The main method for a client
 * 
 * @author Kai Jorgensen
 * @author Bryce Matsuda
 * @author Caleb Piekstra
 * @author Jordan White
 *
 */
public class MainClient {
	
	private static final String ipAddr = "localhost";
	
	private static final int QUEUE_TIMEOUT = 100; // In minutes
	
	private SquintGUI gui;
	
	private static BlockingQueue<String> incMsgQueue = null; // a queue containing any new messages received by the connection
	
	private static DataPort connection = null;
	
//	private static final String ipAddr = "10.12.18.33";
	
	public static void main(String[] args) {

		// Create the main client
		MainClient client = new MainClient();
		
		// Set up the GUI for the client
		client.gui = new SquintGUI();
		client.gui.initGUI(client.gui);
		
		// TODO I am not sure if this already happens, but we 
		// should keep trying to connect if the connection fails 
		while (connection == null) {
			try {
				// init a connection to the server
				connection = new DataPort(ipAddr, 9999);
				// If a connection could not be established, wait a little and try again
				if (connection == null) {	
					Thread.sleep(1000);
					continue;
				}
				// set the queue to point to the DataPort's queue
				incMsgQueue = connection.getIncMessageQueue();
				System.out.println("Connection established!");
				// start DataPort listener thread
				Thread serverPlayerThread = new Thread(connection);
				serverPlayerThread.start();
			} catch (IOException e) {
				System.out.println("Client failed to connect to host: Are you sure the server is running?");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
//				e.printStackTrace();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
		
		// ok, connection is established. 
		
		// Set up the thread to listen for messages from the server
		Thread receiverThread = new Thread(client.new Receiver());
		receiverThread.start();
//		
//		// wait until we receive a message
//		String msg = null;
//		while (msg == null) {
//			try {
//				// Wait for the message
//				msg = incMsgQueue.poll(QUEUE_TIMEOUT, TimeUnit.MINUTES);
//			} catch (InterruptedException e) {				
//				e.printStackTrace();
//			}			
//		}
//		
//		// print the message!
//		// poll() also removes it from the queue
//		System.out.println("RCVD: " + msg);
//		
//		// send a response
//		connection.send("Trails indeed!");
//		System.out.println("SENT: Trails indeed!");
		
	}
	
	public class Receiver implements Runnable {
		
		public final int POLL_TIMEOUT = QUEUE_TIMEOUT;
		
		@Override
		public void run() {
			while (true) {				
				System.out.println("WAIT: Waiting for data from the host...");
				String msg = null;
				try {
					// Sit here and wait for data for at most POLL_TIMEOUT time
					msg = incMsgQueue.poll(POLL_TIMEOUT, TimeUnit.MINUTES);
				} catch (InterruptedException e) {
					System.out.println("Error in receive buffer");
					e.printStackTrace();
				}
				// If there is no data, try again
				if (msg == null) {
					System.out.println("TIMEOUT: Did not receive any data in " + POLL_TIMEOUT + " minutes. Closing connection.");
					// not really closing the connection, but kill the receiver
					return;
				}
				System.out.println("SUCCESS: Data received!");
				
				
				
				// If we have an INIT_MSG, create a player
				// TODO
				
				// Let's pretend we get data from the host and it is an INIT_MSG
				if (gui.player == null) {
					gui.createPlayer(0, "glasses");
				}
				
				// If we have an UPDATE_MSG, update map so that the specified player is at the specified location
				// TODO
				
				// If we have movement data, it's time to move
				// TODO
//				if () {
//					gui.movePlayer(direction, player);			
//				}
			}
		}		
	}
}
