package serverManagement;

/**
 * a simple tuple/pair for incoming server messages containing the source
 * DataPort object and the String of the message
 * 
 * @author Kai Jorgensen
 * @author Bryce Matsuda
 * @author Caleb Piekstra
 * @author Jordan White
 * 
 */
public class ServerQueuedMessage {
	public final DataPort source;
	public final String message;
	
	public ServerQueuedMessage(DataPort source, String message) {
		this.source = source;
		this.message = message;
	}
}
