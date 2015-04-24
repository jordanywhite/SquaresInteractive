package squint;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import serverManagement.DataPort;
import actions.PlayerAction;

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
	//	private static final String ipAddr = "10.12.18.33";

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
				System.out.println("SUCCESS: Data received! Data: " + msg);

				String[] splitMsg = msg.split("#");
				int msgType = Integer.parseInt(splitMsg[1]);

				// If we have an INIT_MSG, create a player
				// Let's pretend we get data from the host and it is an INIT_MSG
				if (msgType == DataPort.INIT_MSG) {
					// TODO: Make a separate class for parsing the data
					try {
						String[] payload = splitMsg[2].split("@");

						if(msgType == DataPort.INIT_MSG) {
							int playerId = Integer.parseInt(payload[0]);
							String avatarName = payload[1];
							int x = Integer.parseInt(payload[2]);
							int y = Integer.parseInt(payload[3]);
							// If we are receiving a spawn player message for a player we already
							// know about, do not create it							
							if (!gui.players.containsKey(playerId)) {
								gui.createPlayer(playerId, avatarName, x, y);
							}
						}

					} catch (NumberFormatException e) {
						System.out.println("ERROR PARSING MESSAGE: " + msg);
					}
				}

				// If we have an ACTION_MSG, update map so that the specified player 
				// is at the specified location
				PlayerAction playerAction = PlayerAction.parseFromMsg(msg);
				if (playerAction != null) {
					gui.movePlayer(PlayerAction.getActionNum(playerAction.action), 
							playerAction.playerId);
				}
			}
		}		
	}
}
