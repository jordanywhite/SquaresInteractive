package squInt;

import java.io.*;
import java.net.*;

/**
 * Represents a host-to-host connection.
 * Runs a thread that listens for messages and
 * provides methods for sending messages.
 *
 */
public abstract class DataPort implements Runnable {
	// message types
	public static final int ROOM = 0;
	public static final int CLIENT_ACTION = 1;

	// ints for actions
	public static final int MOVE_UP = 0, MOVE_DOWN = 1, MOVE_LEFT = 2, MOVE_RIGHT = 3, INTERACT = 4;

	private Socket socket;
	private DataOutputStream out;
	private BufferedReader in;

	public DataPort(String hostname, int port) throws IOException {
		this(new Socket(hostname, port));
	}

	public DataPort(Socket s) throws IOException {
		socket = s;
		socket.setSoTimeout(0);
		socket.setKeepAlive(true);

		out = new DataOutputStream(socket.getOutputStream());
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}

	@Override
	public void run() {
		listenForMessages();
	}
	
	/**
	 * Loop that monitors the socket for incoming messages
	 */
	private void listenForMessages() {
		while(true) {
			try {
				String newMessage = in.readLine();
				processMessage(newMessage);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Parses and processes the message received. Should
	 * be implemented depending on what messages are expected
	 * on this connection.
	 * @param str
	 */
	public abstract void processMessage(String str);

	/**
	 * Send a message to the target host
	 * @param msg
	 */
	public void send(String msg) {
		try {
			out.writeBytes(msg + '\n');
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Checks a string for valid message format.
	 * @param msg
	 * @return
	 */
	public static boolean isValidMessage(String msg) {
		// message format: SI#[MessageType]#[payload...]
		if(!msg.startsWith("SI#")) {
			System.out.println("IGNORED: " + msg);
			return false;
		}

		String[] splitMsg = msg.split("#");
		if(splitMsg.length != 3) {
			System.out.println("INVALID MSG FORMAT: " + msg);
			return false;
		}

		return true;
	}
}
