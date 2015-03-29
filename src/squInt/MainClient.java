package squInt;

import java.io.*;
import java.net.*;

/**
 * The main method for a client
 *
 */
public class MainClient {
	
	private static final String ipAddr = "localhost";
//	private static final String ipAddr = "10.12.18.33";
	
	public static void main(String[] args) {
		DataPortClient dpc = null; // the client's connection to the server
		
		try {
			dpc = new DataPortClient(ipAddr, 9999);
			Thread serverPlayerThread = new Thread(dpc);
			serverPlayerThread.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
//		Client client = new Client();
	}
}
