package squInt;

/**
 * A class representing an action taken by a client.
 * This is sent to the server in string form.
 *
 */
public class ClientAction {
	private int userID;
	private Action action;
	
	ClientAction(int userID, Action action) {
		this.userID = userID;
		this.action = action;
	}
	
	public int getUserID() { return userID; }
	public Action getAction() { return action; }
}
