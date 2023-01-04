package by.alis.functionalservercontrol.spigot.Additional.CoreAdapters;

import by.alis.functionalservercontrol.spigot.Additional.CoreAdapters.Adapters.PaperAdapter;
import by.alis.functionalservercontrol.spigot.Additional.CoreAdapters.Adapters.SpigotAdapter;
import by.alis.functionalservercontrol.spigot.Additional.SomeUtils.OtherUtils;
import org.bukkit.Bukkit;

import static by.alis.functionalservercontrol.spigot.Additional.SomeUtils.TextUtils.setColors;

public class CoreAdapter {

    private static Adapter adapter;

    public static Adapter getAdapter() {
        return adapter;
    }

    public static boolean setAdapter() {
        String coreName = OtherUtils.getServerCoreName(Bukkit.getServer()).toLowerCase();
        if(coreName.contains("paper") || coreName.contains("purpur") || coreName.contains("pufferfish") || coreName.contains("airplane") || coreName.contains("petal")) {
            CoreAdapter.adapter = new PaperAdapter();
            Bukkit.getConsoleSender().sendMessage(setColors("&e[by.alis.functionalservercontrol.spigot.Additional.CoreAdapters.Adapters.PaperAdapter] Using PaperAdapter as core adapter"));
            return true;
        }
        if(coreName.contains("spigot")) {
            CoreAdapter.adapter = new SpigotAdapter();
            Bukkit.getConsoleSender().sendMessage(setColors("&e[by.alis.functionalservercontrol.spigot.Additional.CoreAdapters.Adapters.SpigotAdapter] Using SpigotAdapter as core adapter"));
            return true;
        }
        Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl] Failed to set core adapter, no further work possible! Disabling..."));
        CoreAdapter.adapter = null;
        return false;
    }
}
