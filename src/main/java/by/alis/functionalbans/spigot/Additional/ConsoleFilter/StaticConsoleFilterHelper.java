package by.alis.functionalbans.spigot.Additional.ConsoleFilter;

public class StaticConsoleFilterHelper {

    private static final ConsoleFilterHelper consoleFilterHelper = new ConsoleFilterHelper();

    public static ConsoleFilterHelper getConsoleFilterHelper() {
        return consoleFilterHelper;
    }
}
