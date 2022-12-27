package by.alis.functionalservercontrol.spigot.Commands.Completers;

import by.alis.functionalservercontrol.spigot.Additional.Other.TemporaryCache;
import by.alis.functionalservercontrol.spigot.Additional.Other.TextUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
                    a.addAll(TemporaryCache.getOnlineIps().values());
                }
                return TextUtils.sortList(a, args);
            }

            if (args.length == 2 && args[0].equalsIgnoreCase("-s")) {
                List<String> b = new ArrayList<>();
                if (sender.hasPermission("functionalservercontrol.temp-ban") && sender.hasPermission("functionalservercontrol.use.silently")) {
                    b.addAll(TemporaryCache.getOnlinePlayerNames());
                    b.addAll(TemporaryCache.getOnlineIps().values());
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
