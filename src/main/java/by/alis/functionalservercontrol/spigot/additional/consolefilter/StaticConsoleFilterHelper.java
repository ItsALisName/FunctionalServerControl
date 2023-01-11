package by.alis.functionalservercontrol.spigot.additional.consolefilter;

public class StaticConsoleFilterHelper {

    private static final ConsoleFilterHelper consoleFilterHelper = new ConsoleFilterHelper();

    public static ConsoleFilterHelper getConsoleFilterHelper() {
        return consoleFilterHelper;
    }
}
