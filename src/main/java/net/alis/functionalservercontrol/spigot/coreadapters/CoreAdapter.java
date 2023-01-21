package net.alis.functionalservercontrol.spigot.coreadapters;

import net.alis.functionalservercontrol.spigot.coreadapters.adapters.PaperAdapter;
import net.alis.functionalservercontrol.spigot.coreadapters.adapters.SpigotAdapter;
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
            return true;
        }
        if(coreName.contains("spigot")) {
            CoreAdapter.adapter = new SpigotAdapter();
            return true;
        }
        Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControlSpigot] Failed to set core adapter, no further work possible! Disabling..."));
        CoreAdapter.adapter = null;
        return false;
    }
}
