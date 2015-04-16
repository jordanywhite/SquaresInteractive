package squInt;

/**
 * a simple tuple/pair for incoming server messages containing the source
 * DataPort object and the String of the message
 */
public class ServerQueuedMessage {
	public final DataPort source;
	public final String message;
	
	public ServerQueuedMessage(DataPort source, String message) {
		this.source = source;
		this.message = message;
	}
}
