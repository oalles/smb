package es.neivi.smb.test.events;

public class MessageCreatedEvent extends PersistentApplicationEvent {

	private final String message;

	public MessageCreatedEvent(String source, String message) {
		super(source);
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public String toString() {
		return "MessageCreatedEvent {message: " + message + ", date: "
				+ getDate() + "}";
	}

}
