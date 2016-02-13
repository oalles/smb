package es.omarall.smb.exceptions;

public class InvalidPayloadException extends MBException {

	public InvalidPayloadException() {
	}

	public InvalidPayloadException(String message) {
		super(message);
	}

	public InvalidPayloadException(Throwable cause) {
		super(cause);
	}

	public InvalidPayloadException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidPayloadException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
