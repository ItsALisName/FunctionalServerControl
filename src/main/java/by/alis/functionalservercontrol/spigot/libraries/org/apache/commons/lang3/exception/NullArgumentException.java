package by.alis.functionalservercontrol.spigot.libraries.org.apache.commons.lang3.exception;

public class NullArgumentException extends IllegalArgumentException {

    private static final long serialVersionUID = 1174360235354917591L;

    public NullArgumentException(String argName) {
        super(((argName == null) ? "Argument" : argName) + " must not be null.");
    }
}
