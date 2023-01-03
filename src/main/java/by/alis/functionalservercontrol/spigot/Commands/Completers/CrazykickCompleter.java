package by.alis.functionalservercontrol.spigot.Commands.Completers;

import by.alis.functionalservercontrol.spigot.Additional.SomeUtils.TemporaryCache;
import by.alis.functionalservercontrol.spigot.Additional.SomeUtils.TextUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CrazykickCompleter implements TabCompleter {

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
            if (args.length == 1) {
                List<String> a = new ArrayList<>();
                if (sender.hasPermission("functionalservercontrol.crazy-kick")) {
                    if (sender.hasPermission("functionalservercontrol.use.silently")) {
                        a.add("-s");
                    }
                    a.addAll(TemporaryCache.getOnlinePlayerNames());
                    return TextUtils.sortList(a, args);
                }
                return Collections.singletonList("");
            }

            if (args[0].equalsIgnoreCase("-s") && args.length == 2) {
                if (sender.hasPermission("functionalservercontrol.crazy-kick") && sender.hasPermission("functionalservercontrol.use.silently")) {
                    return TextUtils.sortList(TemporaryCache.getOnlinePlayerNames(), args);
                }
                return Collections.singletonList("");
            }

            if (args[0].equalsIgnoreCase("-s") && args.length == 3) {
                if (sender.hasPermission("functionalservercontrol.crazy-kick") && sender.hasPermission("functionalservercontrol.use.silently")) {
                    return Arrays.asList("red", "dark_red", "green", "blue");
                }
                return Collections.singletonList("");
            }

            if (args.length == 2) {
                if (sender.hasPermission("functionalservercontrol.crazy-kick")) {
                    return TextUtils.sortList(Arrays.asList("red", "dark_red", "green", "blue"), args);
                }
                return Collections.singletonList("");
            }
        return Collections.singletonList("");
    }
}
