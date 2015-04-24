package serverManagement;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Initializes server socket and creates thread to listen for connections.
 * The server uses this class to receive messages from its connected hosts
 * and to send messages to all its connected hosts via sendAll().
 * 
 * Received messages are stored in a queue. getIncMessageQueue() returns
 * a direct link to the queue object. Use the object's poll() method to
 * pop the next message off the front of the queue.
 * 
 * @author Kai Jorgensen
 * @author Bryce Matsuda
 * @author Caleb Piekstra
 * @author Jordan White
 * 
 */
public class ServerConnectionTable implements Runnable {
	private static final int SERVER_QUEUE_SIZE = 1000;
	private int nextUniqueId = 0;
	private ServerSocket serverSocket;
	private boolean initialized = false;
	
	private BlockingQueue<ServerQueuedMessage> incMessages = new ArrayBlockingQueue<ServerQueuedMessage>(SERVER_QUEUE_SIZE); // combined received-action queue for all dataports
	
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
		    System.out.println("MainServer started on port " + port + ".");
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
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
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
		CopyOnWriteArrayList<DataPort> connectionsCopy = new CopyOnWriteArrayList<DataPort>(connections);
		for(DataPort dp : connectionsCopy) {
			if(!dp.send(msg)) {
				System.out.println("SEND FAILED: " + msg);
			}
		}
	}
	
	/**
	 * @return the queue object used to store all incoming messages
	 */
	public BlockingQueue<ServerQueuedMessage> getIncMessageQueue() {
		return incMessages;
	}
	
	/**
	 * @param id the unique id of the client connection
	 * @return a DataPort representing the connection
	 */
	public DataPort getConnectionByUniqueId(int id) {
		CopyOnWriteArrayList<DataPort> connectionsCopy = new CopyOnWriteArrayList<DataPort>(connections);
		for(DataPort dp : connectionsCopy) {
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
