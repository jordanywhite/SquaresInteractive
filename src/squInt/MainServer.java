package squInt;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * The main method for the server
 * 
 * @author Kai Jorgensen
 * @author Bryce Matsuda
 * @author Caleb Piekstra
 * @author Jordan White
 *
 */
public class MainServer {
	public static int numConnections = 5;
	
	public static void main(String[] args) {
		/** TO RUN: run MainServer and then run MainClient numConnections times **/
		
		// create the server connection and get the incoming-message queue object
		ServerConnectionTable serverTable = new ServerConnectionTable(9999); // the server's collection of DataPorts
		LinkedList<ServerQueuedMessage> serverQueue = serverTable.getIncMessageQueue(); // the server's master incoming message queue (from all sources).
		// (ServerQueuedMessage is just a tuple of the source DataPort
		//  and message String so we can know the source)

		// start the connection server
		Thread serverTableThread = new Thread(serverTable);
		serverTableThread.start();
		
		// server is now ready and listening for connections!!!

		// wait until this many connections are established
		while(serverTable.getNumConnections() < numConnections) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		// send message to all
		System.out.println("SERVER SENDING TO ALL: \"Snotty trails?\"");
		serverTable.sendToAll("Snotty trails?");
		
		// now have server just wait for inc messages and print them (until program is terminated)
		while(true) {
			if(serverQueue.peek() != null) {
				// ServerQueuedMessage object just contains the source DataPort and the received String
				ServerQueuedMessage sqm = serverQueue.poll();
				System.out.println("SERV RCVD from " + sqm.source.getUniqueId() + ": " + sqm.message);
			}
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
	}
}
