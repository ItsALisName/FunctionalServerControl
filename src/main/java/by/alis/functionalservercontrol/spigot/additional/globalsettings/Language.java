package by.alis.functionalservercontrol.spigot.additional.globalsettings;

import by.alis.functionalservercontrol.spigot.FunctionalServerControl;
import org.bukkit.Bukkit;

import static by.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

public class Language {

    private String[] titleWhenMuted;
    private String[] titleWhenUnmuted;



    public String[] getTitleWhenMuted() {
        return titleWhenMuted;
    }
    public String[] getTitleWhenUnmuted() {
        return titleWhenUnmuted;
    }


    public void loadLanguage() {
        Bukkit.getScheduler().runTaskAsynchronously(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class), () -> {
            titleWhenUnmuted = getFileAccessor().getLang().getString("other.title.when-unmuted.text").split("\\|");
            titleWhenMuted = getFileAccessor().getLang().getString("other.title.when-muted.text").split("\\|");
        });
    }

    public void reloadLanguage() {
        titleWhenUnmuted = getFileAccessor().getLang().getString("other.title.when-unmuted.text").split("\\|");
        titleWhenMuted = getFileAccessor().getLang().getString("other.title.when-muted.text").split("\\|");
    }


    //Work in progress

}
