package squInt;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Initializes server socket and waits for 4 connections (including the server's client thread).
 * Also contains the connection data for each connected client.
 *
 */
public class ServerConnectionTable implements Runnable {
	private ServerSocket serverSocket;
	private boolean initialized = false;
	
	/**
	 * Array of DataPorts, one for each client connected to the server.
	 */
	private DataPortServer[] connections;
	
	public ServerConnectionTable(int port) {
		connections = new DataPortServer[4];
		
		try {
		    serverSocket = new ServerSocket(port);
		    System.out.println("Server started on port " + port + ".");
		} catch (IOException e) {
		    System.out.println("Could not start server!");
		    return;
		}
	}

	@Override
	public void run() {
		System.out.println("Waiting for 4 players...");
		for (int i=0; i<4; i++) {
		    Socket client = null;
		    try {
		        client = serverSocket.accept();
		        connections[i] = new DataPortServer(client);
		        Thread serverConnectionThread = new Thread(connections[i]);
		        serverConnectionThread.start();
				System.out.println("Player found; player thread started.");
		    } catch (IOException e) {
		        System.out.println("Player found but socket failed!");
		    }
		}
		initialized = true;
	}
	
	/**
	 * Returns true if ready to send messages.
	 * @return
	 */
	public boolean isInitialized() {
		return initialized;
	}
	
	/**
	 * Sends a string to all the clients in the table
	 * @param msg
	 */
	public void sendToAll(String msg) {
		for(int i=0; i<connections.length; i++) {
			if(connections[i] != null) {
				connections[i].send(msg);
			}
		}
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
}
