package by.alis.functionalbans.spigot.Commands.Completers;

import by.alis.functionalbans.spigot.Additional.Other.TemporaryCache;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CrazykickCompleter implements TabCompleter {

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {

        if(args.length == 1) {
            List<String> a = new ArrayList<>();
            if(sender.hasPermission("functionalbans.crazy-kick")) {
                if(sender.hasPermission("functionalbans.use.silently")) {
                    a.add("-s");
                }
                a.addAll(TemporaryCache.getOnlinePlayerNames());
                return a;
            }
            return null;
        }

        if(args[0].equalsIgnoreCase("-s") && args.length == 2) {
            if(sender.hasPermission("functionalbans.crazy-kick") && sender.hasPermission("functionalbans.use.silently")) {
                return TemporaryCache.getOnlinePlayerNames();
            }
            return null;
        }

        if(args[0].equalsIgnoreCase("-s") && args.length == 3) {
            if(sender.hasPermission("functionalbans.crazy-kick") && sender.hasPermission("functionalbans.use.silently")) {
                return Arrays.asList("red", "dark_red", "green", "blue");
            }
            return null;
        }

        if(args.length == 2) {
            if(sender.hasPermission("functionalbans.crazy-kick")) {
                return Arrays.asList("red", "dark_red", "green", "blue");
            }
            return null;
        }

        return null;
    }
}
