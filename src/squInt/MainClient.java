package squInt;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;

import serverManagement.DataPort;
import actions.PlayerAction;
import actions.PlayerInit;
import actions.PlayerUpdate;
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
	private String ipAddr = "localhost";
//		private String ipAddr = "10.12.18.80";

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
		
		// Prompt the user for the ip address of the server, "localhost" as default
		// If the client has a hard-coded IPv4 then don't prompt for the address
		if (client.ipAddr.equals("localhost")) {
			client.askForIP(client);			
		}
		
		// Wait for the client to connect to the server
		client.waitForConnection(client);
		// ok, connection is established. 

		// Initialize the receiver so the client can get messages from the server
		client.setUpReceiverThread(client);
	}
	
	/**
	 * Using input dialogs, ask the user for the IP address
	 * of the server. 
	 * 
	 * If the IP is invalid or the user does not provide an IP,
	 * the default "localhost" is used.
	 * 
	 * @param client
	 */
	private void askForIP(MainClient client) {
		String s = (String)JOptionPane.showInputDialog(
				client.gui,
				"Enter the server's IP Address:\n"
						+ "or \"localhost\" if the server is local",
				"Server's IP Address",
				JOptionPane.PLAIN_MESSAGE,
				null,
				null,
				"localhost");

		//If a string was returned, say so.
		if ((s != null) && (s.length() > 0)) {
			if (s.equals("localhost") || validIP(s)) {
				JOptionPane.showMessageDialog(client.gui,
						"Server IP: " + s,
						"Server IP Addres",
						JOptionPane.PLAIN_MESSAGE);
				client.ipAddr = s;	
			} else {
				JOptionPane.showMessageDialog(client.gui,
						"Invalid IP, using \"localhost\"",
						"Invalid IP Address",
						JOptionPane.WARNING_MESSAGE);
				client.ipAddr = "localhost";
			}
			return;
		}
		// User did not input anything, set to default
		JOptionPane.showMessageDialog(client.gui,
				"No IP provided, using \"localhost\".");
		client.ipAddr = "localhost";
	}

	/**
	 * Checks if an IPv4 address is valid
	 * 
	 * @source http://stackoverflow.com/questions/4581877/validating-ipv4-string-in-java
	 * 
	 * @param ip	The IPv4 address
	 * @return		Whether the ip was valid
	 */
	public static boolean validIP (String ip) {
	    try {
	        if (ip == null || ip.isEmpty()) {
	            return false;
	        }

	        String[] parts = ip.split( "\\." );
	        if ( parts.length != 4 ) {
	            return false;
	        }

	        for ( String s : parts ) {
	            int i = Integer.parseInt( s );
	            if ( (i < 0) || (i > 255) ) {
	                return false;
	            }
	        }
	        if(ip.endsWith(".")) {
	                return false;
	        }

	        return true;
	    } catch (NumberFormatException nfe) {
	        return false;
	    }
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
					// Tell the GUI to initialize a player
					PlayerInit playerInit = PlayerInit.parseFromMsg(msg);

					// If the GUI does not know about this player, create a new player
					if (!gui.players.containsKey(playerInit.playerId)) {
						gui.createPlayer(playerInit.playerId, playerInit.avatarName, playerInit.x, playerInit.y, playerInit.direction);
					} else {
						// If the GUI already knows about this player, update the player's data 
						// to make sure the client is synchronized with the server.
						gui.updatePlayer(playerInit.playerId, playerInit.x, playerInit.y, playerInit.direction);
					}
					break;
				case ServerMessage.ROOM_MSG:
					break;
				case ServerMessage.UPDATE_MSG:	
					// Tell the GUI to update a player
					PlayerUpdate playerUpdate = PlayerUpdate.parseFromMsg(msg);

					// If the GUI knows about this player, update it
					if (gui.players.containsKey(playerUpdate.playerId)) {
						gui.updatePlayer(playerUpdate.playerId, playerUpdate.x, playerUpdate.y, playerUpdate.direction);
					}
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
