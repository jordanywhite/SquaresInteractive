package squInt;

import java.io.*;
import java.net.*;

/**
 * The main method for a client
 *
 */
public class MainClient {
	
	public static void main(String[] args) {
		Client client = new Client();
		DataPortClient dpc = null; // the client's connection to the server
		
		try {
			dpc = new DataPortClient("localhost", 9999);
			Thread serverPlayerThread = new Thread(dpc);
			serverPlayerThread.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
