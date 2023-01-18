package net.alis.functionalservercontrol.spigot.additional.globalsettings;

import net.alis.functionalservercontrol.spigot.managers.TaskManager;

import static net.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

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
        TaskManager.preformAsync(() -> {
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
