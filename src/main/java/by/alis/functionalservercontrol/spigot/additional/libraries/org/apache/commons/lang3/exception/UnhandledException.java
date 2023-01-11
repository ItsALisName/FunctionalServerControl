package by.alis.functionalservercontrol.spigot.additional.libraries.org.apache.commons.lang3.exception;

public class UnhandledException extends NestableRuntimeException {

    public UnhandledException(Throwable cause) {
        super(cause);
    }

    public UnhandledException(String message, Throwable cause) {
        super(message, cause);
    }
}
