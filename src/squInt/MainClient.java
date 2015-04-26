package squInt;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import serverManagement.DataPort;
import actions.PlayerAction;
import actions.PlayerInit;
import actions.ServerMessage;

/**
 * The main back-end client
 * 
 * @author Kai Jorgensen
 * @author Bryce Matsuda
 * @author Caleb Piekstra
 * @author Jordan White
 *
 */
public class MainClient {

	/**
	 * server IP we're connecting to
	 */
	private static final String ipAddr = "localhost";
//		private static final String ipAddr = "10.12.18.80";

	/**
	 * The front end GUI attached to this client
	 */
	private SquintGUI gui;

	/**
	 * a queue containing any new messages received by the connection
	 */
	private static BlockingQueue<String> incMsgQueue = null; 

	/**
	 * how long messages can stay in the queue
	 */
	private static final int QUEUE_TIMEOUT = 100; // In minutes

	/**
	 * Our connection to the server
	 */
	public DataPort connection = null;

	public static void main(String[] args) {

		// Create the main client
		MainClient client = new MainClient();

		// Set up the GUI for the client
		client.gui = new SquintGUI(client);
		client.gui.initGUI(client.gui);
		
		client.waitForConnection(client);
		// ok, connection is established. 

		client.setUpReceiverThread(client);
	}
	
	private void waitForConnection(MainClient client) {
		// keep trying to connect if the connection fails 
		while (client.connection == null) {
			try {
				// init a connection to the server
				client.connection = new DataPort(ipAddr, 9999);
				// If a connection could not be established, wait a little and try again
				if (client.connection == null) {	
					Thread.sleep(1000);
					continue;
				}
				// set the queue to point to the DataPort's queue
				incMsgQueue = client.connection.getIncMessageQueue();
				System.out.println("Connection established!");
				// start DataPort listener thread
				Thread serverPlayerThread = new Thread(client.connection);
				serverPlayerThread.start();
			} catch (IOException e) {
				System.out.println("Client failed to connect to host: " +
						"Are you sure the server is running?");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	private void setUpReceiverThread(MainClient client) {
		// Set up the thread to listen for messages from the server
		Thread receiverThread = new Thread(client.new Receiver());
		receiverThread.start();

		//connection.send("SI#" + DataPort.INIT_MSG);
	}

	/**
	 * 
	 * Receiver - parses messages sent by the server
	 *
	 */
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
					System.out.println("TIMEOUT: Did not receive any data in " 
							+ POLL_TIMEOUT + " minutes. Closing connection.");
					// not really closing the connection, but kill the receiver
					return;
				}
				System.out.println("Message received: " + msg);
				
				// Get the type of the message
				int msgType = ServerMessage.getMessageType(msg);
				
				// Figure out what to do based on the type of message
				switch (msgType) {
				case ServerMessage.ACTION_MSG:
					// Tell the GUI to move the player
					PlayerAction playerAction = PlayerAction.parseFromMsg(msg);
					if (playerAction != null) {
						gui.movePlayer(PlayerAction.getActionNum(playerAction.action), 
								playerAction.playerId);
					}
					break;
				case ServerMessage.INIT_MSG:	
					PlayerInit playerInit = PlayerInit.parseFromMsg(msg);
					if (!gui.players.containsKey(playerInit.playerId)) {
						gui.createPlayer(playerInit.playerId, playerInit.avatarName, playerInit.x, playerInit.y, playerInit.direction);
					}
					break;
				case ServerMessage.ROOM_MSG:
					break;
				case ServerMessage.UPDATE_MSG:
					break;
				case ServerMessage.INVALID_MSG:
					System.out.println("ERROR: Message was invalid.");
					break;
				default: break;
				}
			}
		}		
	}
}
