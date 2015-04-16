package squInt;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Initializes server socket and creates thread to listen for connections.
 * The server uses this class to receive messages from its connected hosts
 * and to send messages to all its connected hosts via sendAll().
 * 
 * Received messages are stored in a queue. getIncMessageQueue() returns
 * a direct link to the queue object. Use the object's poll() method to
 * pop the next message off the front of the queue.
 */
public class ServerConnectionTable implements Runnable {
	private int nextUniqueId = 0;
	private ServerSocket serverSocket;
	private boolean initialized = false;
	
	private LinkedList<ServerQueuedMessage> incMessages = new LinkedList<ServerQueuedMessage>(); // combined received-action queue for all dataports
	
	/**
	 * Vector of DataPorts, one for each client connected to the server.
	 */
	private Vector<DataPort> connections;
	
	/**
	 * constructor opens socket on port for incoming connections
	 * @param port
	 */
	public ServerConnectionTable(int port) {
		connections = new Vector<DataPort>();
		
		try {
		    serverSocket = new ServerSocket(port);
		    System.out.println("Server started on port " + port + ".");
		} catch (IOException e) {
		    System.out.println("Could not start server!");
		    return;
		}
	}

	/**
	 * This thread starts a separate thread to listen for incoming connections
	 * and then loops endlessly through connections to look for new messages
	 * in the receive queues. If it finds one, it adds it to the table's
	 * own receive queue (which is for all the server's connections).
	 */
	@Override
	public void run() {
		// start thread to listen for new connections
		Thread ncl = new Thread(new NewConnectionListener());
		ncl.start();
		
		initialized = true;
		
		String receivedMessage = null;
		CopyOnWriteArrayList<DataPort> connectionsCopy = null;
		// loop to check for new received messages on connections
		while(true) {
			connectionsCopy = new CopyOnWriteArrayList<DataPort>(connections);
			for(DataPort dp : connectionsCopy) {
				receivedMessage = dp.getIncMessageQueue().poll();
				if(receivedMessage != null) {
					incMessages.add(new ServerQueuedMessage(dp, receivedMessage));
				}
			}
		}
	}
	
	/**
	 * Returns true if ready to send messages.
	 * @return
	 */
	public boolean isInitialized() {
		return initialized;
	}
	
	/**
	 * Sends a string to all the clients connected to the server
	 * @param msg
	 */
	public void sendToAll(String msg) {
		for(DataPort dp : connections) {
			if(!dp.send(msg)) {
				System.out.println("SEND FAILED: " + msg);
			}
		}
	}
	
	/**
	 * @return the queue object used to store all incoming messages
	 */
	public LinkedList<ServerQueuedMessage> getIncMessageQueue() {
		return incMessages;
	}
	
	/**
	 * @param id the unique id of the client connection
	 * @return a DataPort representing the connection
	 */
	public DataPort getConnectionByUniqueId(int id) {
		for(DataPort dp : connections) {
			if (dp.getUniqueId() == id) {
				return dp;
			}
		}
		
		return null;
	}
	
	/**
	 * @return the number of established client connections
	 */
	public int getNumConnections() {
		return connections.size();
	}

	public void checkForExistingServer() {
		// TODO: use multicast to look for an existing server and connect to it

		//		try {
		//			// use multicast to see if another server is looking for clients
		//			DatagramSocket socket = new DatagramSocket(PORT);
		//
		//			String message = "SI#Init";
		//			byte[] buf = message.getBytes();
		//
		//			// send it
		//			InetAddress group = InetAddress.getByName("230.0.0.1");
		//			DatagramPacket packet = new DatagramPacket(buf, buf.length, group, PORT);
		//			socket.send(packet);
		//
		//		} catch (IOException e) {
		//			e.printStackTrace();
		//		}
	}
	
	private class NewConnectionListener implements Runnable {

		@Override
		public void run() {
		    Socket clientSocket = null;
		    
			while(true) {
				// check for new connection
			    try {
			        clientSocket = serverSocket.accept();
			        
			        DataPort clientDataPort = new DataPort(clientSocket);
			        clientDataPort.setUniqueId(nextUniqueId);
			        nextUniqueId++;
			        connections.add(clientDataPort);
			        
			        Thread serverConnectionThread = new Thread(clientDataPort);
			        serverConnectionThread.start();
			        
					System.out.println("Player " + clientDataPort.getUniqueId() + " found; player thread started.");
			    } catch (IOException e) {
			        System.out.println("Player found but socket failed!");
			    }
			}
		}//run
		
	}//NewConnectionListener
	
}//ServerConnectionTable
