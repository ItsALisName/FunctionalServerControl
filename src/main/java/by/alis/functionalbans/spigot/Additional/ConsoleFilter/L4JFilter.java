package by.alis.functionalbans.spigot.Additional.ConsoleFilter;


import org.apache.logging.log4j.LogManager;
import org.bukkit.Bukkit;

public class L4JFilter implements ConsoleFilterCore {

    @Override
    public synchronized void eventLog() {
        ((org.apache.logging.log4j.core.Logger) LogManager.getRootLogger()).addFilter(new EventAsyncConsoleLog());
        ((org.apache.logging.log4j.core.Logger) LogManager.getRootLogger()).addFilter(new EventConsoleLog());
    }

    @Override
    public void replaceMessage() {
        ((org.apache.logging.log4j.core.Logger) LogManager.getRootLogger()).addFilter(new ReplaceFilter());
    }

    @Override
    public void hideMessage() { ((org.apache.logging.log4j.core.Logger) LogManager.getRootLogger()).addFilter(new HideFilter()); }

}
