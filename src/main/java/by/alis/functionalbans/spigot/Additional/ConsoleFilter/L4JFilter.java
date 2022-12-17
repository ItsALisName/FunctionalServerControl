package by.alis.functionalbans.spigot.Additional.ConsoleFilter;


import by.alis.functionalbans.spigot.Additional.Logger.ConsoleMessageListener;
import by.alis.functionalbans.spigot.Additional.Logger.LogWriter;
import org.apache.logging.log4j.LogManager;

public class L4JFilter implements ConsoleFilterCore {

    @Override
    public void eventLog() {
        ((org.apache.logging.log4j.core.Logger) LogManager.getRootLogger()).addFilter(new EventAsyncConsoleLog());
        ((org.apache.logging.log4j.core.Logger) LogManager.getRootLogger()).addFilter(new EventConsoleLog());
        ((org.apache.logging.log4j.core.Logger) LogManager.getRootLogger()).addFilter(new ConsoleMessageListener());
    }

    @Override
    public void replaceMessage() {
        ((org.apache.logging.log4j.core.Logger) LogManager.getRootLogger()).addFilter(new ReplaceFilter());
    }

    @Override
    public void hideMessage() { ((org.apache.logging.log4j.core.Logger) LogManager.getRootLogger()).addFilter(new HideFilter()); }

}
