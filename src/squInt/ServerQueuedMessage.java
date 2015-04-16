package squInt;

public class ServerQueuedMessage {
	public final DataPort source;
	public final String message;
	
	public ServerQueuedMessage(DataPort source, String message) {
		this.source = source;
		this.message = message;
	}
}
