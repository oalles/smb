package es.omarall.smb.exceptions;

public class RootMessageEntityRequired extends MBException {

    public RootMessageEntityRequired() {
    }

    public RootMessageEntityRequired(String message) {
        super(message);
    }

    public RootMessageEntityRequired(Throwable cause) {
        super(cause);
    }

    public RootMessageEntityRequired(String message, Throwable cause) {
        super(message, cause);
    }

    public RootMessageEntityRequired(String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
