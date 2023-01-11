package by.alis.functionalservercontrol.spigot.additional.libraries.org.apache.commons.lang3.exception;

public class CloneFailedException extends NestableRuntimeException {
    private static final long serialVersionUID = 20091223L;

    public CloneFailedException(String message) {
        super(message);
    }

    public CloneFailedException(Throwable cause) {
        super(cause);
    }

    public CloneFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
