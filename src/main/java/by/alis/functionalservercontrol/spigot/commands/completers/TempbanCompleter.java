package by.alis.functionalservercontrol.spigot.commands.completers;

import by.alis.functionalservercontrol.spigot.additional.misc.TemporaryCache;
import by.alis.functionalservercontrol.spigot.additional.misc.TextUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static by.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;

public class TempbanCompleter implements TabCompleter {

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
            if (args.length == 1) {
                List<String> a = new ArrayList<>();
                if (sender.hasPermission("functionalservercontrol.temp-ban")) {
                    if (sender.hasPermission("functionalservercontrol.use.silently")) {
                        a.add("-s");
                    }
                    a.addAll(TemporaryCache.getOnlinePlayerNames());
                    if(!getConfigSettings().isHideIpsFromCompletions()) a.addAll(TemporaryCache.getOnlineIps().values());
                }
                return TextUtils.sortList(a, args);
            }

            if (args.length == 2 && args[0].equalsIgnoreCase("-s")) {
                List<String> b = new ArrayList<>();
                if (sender.hasPermission("functionalservercontrol.temp-ban") && sender.hasPermission("functionalservercontrol.use.silently")) {
                    b.addAll(TemporaryCache.getOnlinePlayerNames());
                    if(!getConfigSettings().isHideIpsFromCompletions()) b.addAll(TemporaryCache.getOnlineIps().values());
                }
                return TextUtils.sortList(b, args);
            }

            if (args.length == 2 && !args[0].equalsIgnoreCase("-s")) {
                List<String> c = new ArrayList<>();
                if (sender.hasPermission("functionalservercontrol.temp-ban")) {
                    c.add("1s");
                    c.add("1m");
                    c.add("10m");
                    c.add("1h");
                    c.add("10h");
                    c.add("1d");
                }
                return TextUtils.sortList(c, args);
            }
        return Collections.singletonList("");
    }
}
