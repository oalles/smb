package es.omarall.smb.exceptions;

public class MBException extends RuntimeException {

    public MBException() {
    }

    public MBException(String message) {
        super(message);
    }

    public MBException(Throwable cause) {
        super(cause);
    }

    public MBException(String message, Throwable cause) {
        super(message, cause);
    }

    public MBException(String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
