package net.alis.functionalservercontrol.spigot.additional.consolefilter;


import net.alis.functionalservercontrol.spigot.additional.logger.ConsoleMessageListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

public class L4JFilter implements Filter {

    @Override
    public void eventLog() {
        ((Logger) LogManager.getRootLogger()).addFilter(new EventAsyncConsoleLog());
        ((Logger) LogManager.getRootLogger()).addFilter(new EventConsoleLog());
        ((Logger) LogManager.getRootLogger()).addFilter(new ConsoleMessageListener());
    }

    @Override
    public void replaceMessage() {
        ((Logger) LogManager.getRootLogger()).addFilter(new ReplaceFilter());
    }

    @Override
    public void hideMessage() { ((Logger) LogManager.getRootLogger()).addFilter(new HideFilter()); }

}
