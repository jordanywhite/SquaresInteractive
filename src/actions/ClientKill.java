package actions;

public class ClientKill {
	/*
	 * Just an idea:
	 * 	Have the server keep track of how long it has been since it has received messages
	 * 	from clients. If that time reaches a timeout, send a KillMessage to the client.
	 * 	If the client responds back saying "Don't Kill Me!" then reset the timer.
	 * 	If the client does not respond in a certain period of time, kill the connection.
	 */
}
