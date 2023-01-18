package net.alis.functionalservercontrol.spigot.additional.coreadapters;

import net.alis.functionalservercontrol.spigot.additional.coreadapters.adapters.PaperAdapter;
import net.alis.functionalservercontrol.spigot.additional.coreadapters.adapters.SpigotAdapter;
import net.alis.functionalservercontrol.spigot.additional.misc.OtherUtils;
import org.bukkit.Bukkit;

import static net.alis.functionalservercontrol.spigot.additional.misc.OtherUtils.isClassExists;
import static net.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;

public class CoreAdapter {

    private static Adapter adapter;

    public static Adapter getAdapter() {
        return adapter;
    }

    public static boolean setAdapter() {
        String coreName = OtherUtils.getServerCoreName(Bukkit.getServer()).toLowerCase();
        if(coreName.contains("paper") || coreName.contains("purpur") || coreName.contains("pufferfish") || coreName.contains("airplane") || coreName.contains("petal")) {
            Adapter.adventureApiExists = isClassExists("net.kyori.adventure.text.Component");
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
