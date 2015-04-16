package squInt;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * The main method for the server
 *
 */
public class MainServer {
	
	public static void main(String[] args) {
		// vars for the local client connection/queue
		DataPort localClientDataPort = null; // the local client DataPort
		LinkedList<String> localClientQueue = null; // the local client's incoming message queue
		
		// create the server connection and get the queue object
		ServerConnectionTable serverTable = new ServerConnectionTable(9999); // the server's collection of DataPorts
		LinkedList<ServerQueuedMessage> serverQueue = serverTable.getIncMessageQueue(); // the server's master incoming message queue (from all sources).
		// (ServerQueuedMessage is just a tuple of the source DataPort
		//  and message String so we can know the source)

		// start the connection server
		Thread serverTableThread = new Thread(serverTable);
		serverTableThread.start();
		
		// server is now ready and listening for connections!!!
		
		try {
			// connect the local client to the server and start the connection
			localClientDataPort = new DataPort("localhost", 9999);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// assign the local client message queue to the connection's (DataPort's) queue
		localClientQueue = localClientDataPort.getIncMessageQueue();
		
		// start the local client's connection listener
		Thread localClientThread = new Thread(localClientDataPort);
		localClientThread.start();
		
		// wait until this many connections are established
		while(serverTable.getNumConnections() < 2) ;
		
		// send message to all
		System.out.println("SERVER SENDING TO ALL: \"Snotty trails?\"");
		serverTable.sendToAll("Snotty trails?");
		
		// keep checking the queue until we have a received message in it
		while(localClientQueue.peek() == null) ;
		
		// pop the message off the front of the queue
		String clientReceivedMsg = localClientQueue.poll();
		System.out.println("CL RCVD: " + clientReceivedMsg);
		
		// send a message from the local client to the server
		localClientDataPort.send("hello");
		System.out.println("CL SENT: hello");
		
		// now have server just wait for inc messages and print them (until program is terminated)
		while(true) {
			if(serverQueue.peek() != null) {
				// ServerQueuedMessage just contains the source DataPort and the received String
				ServerQueuedMessage sqm = serverQueue.poll();
				System.out.println("SERV RCVD from " + sqm.source.getUniqueId() + ": " + sqm.message);
			}
		}
	}
}
