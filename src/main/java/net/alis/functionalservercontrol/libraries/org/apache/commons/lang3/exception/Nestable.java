package net.alis.functionalservercontrol.libraries.org.apache.commons.lang3.exception;

import java.io.PrintStream;
import java.io.PrintWriter;

public interface Nestable {
    Throwable getCause();

    String getMessage();

    String getMessage(int paramInt);

    String[] getMessages();

    Throwable getThrowable(int paramInt);

    int getThrowableCount();

    Throwable[] getThrowables();

    int indexOfThrowable(Class paramClass);

    int indexOfThrowable(Class paramClass, int paramInt);

    void printStackTrace(PrintWriter paramPrintWriter);

    void printStackTrace(PrintStream paramPrintStream);

    void printPartialStackTrace(PrintWriter paramPrintWriter);
}
