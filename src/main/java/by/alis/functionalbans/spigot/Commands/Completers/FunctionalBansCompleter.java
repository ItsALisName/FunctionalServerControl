package by.alis.functionalbans.spigot.Commands.Completers;

import by.alis.functionalbans.spigot.Additional.Other.TemporaryCache;
import by.alis.functionalbans.spigot.Additional.Other.TextUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class FunctionalBansCompleter implements TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {

        if(args.length == 1) {
            List<String> a = new ArrayList<>();
            if(sender.hasPermission("functionalbans.purge")) {a.add("purge");}
            if(sender.hasPermission("functionalbans.reload")) {a.add("reload");}
            return TextUtils.sortList(a, args);
        }

        if(args[0].equalsIgnoreCase("purge") && args.length == 2) {
            List<String> b = new ArrayList<>();
            if(sender.hasPermission("functionalbans.purge")) {
                b.add("cache");
            }
            return TextUtils.sortList(b, args);
        }

        if(args[0].equalsIgnoreCase("reload") && args.length == 2) {
            List<String> c = new ArrayList<>();
            if(sender.hasPermission("functionalbans.reload")) {
                Collections.addAll(c, "all", "globalvariables", "settings");
                return TextUtils.sortList(c, args);
            }
            return Collections.singletonList("");
        }
        return Collections.singletonList("");
    }
}
