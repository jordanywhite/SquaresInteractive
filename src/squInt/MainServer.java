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
		Server server = new Server();
		DataPortClient dpc = null; // the local client DataPort
		ServerConnectionTable serverTable; // the server's collection of DataPorts

		serverTable = new ServerConnectionTable(9999);
		Thread serverTableThread = new Thread(serverTable);
		serverTableThread.start();
		
		try {
			dpc = new DataPortClient("localhost", 9999);
			Thread localClientThread = new Thread(dpc);
			localClientThread.start();
			
			while(!serverTable.isInitialized()) ;
			
			System.out.println("SERVER SENDING TO ALL: \"Snotty trails?\"");
			serverTable.sendToAll("Snotty trails?");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
