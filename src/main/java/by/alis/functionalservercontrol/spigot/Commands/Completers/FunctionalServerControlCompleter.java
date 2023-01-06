package by.alis.functionalservercontrol.spigot.Commands.Completers;

import by.alis.functionalservercontrol.spigot.Additional.Misc.TextUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FunctionalServerControlCompleter implements TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> a = new ArrayList<>();
            if (sender.hasPermission("functionalservercontrol.purge")) a.add("purge");
            if (sender.hasPermission("functionalservercontrol.reload")) a.add("reload");
            if(sender.hasPermission("functionalservercontrol.import")) a.add("import");
            if(sender.hasPermission("functionalservercontrol.history")) a.add("history");
            return TextUtils.sortList(a, args);
        }

        if (args[0].equalsIgnoreCase("purge") && args.length == 2) {
            if (sender.hasPermission("functionalservercontrol.purge")) return TextUtils.sortList(Arrays.asList("cache", "history"), args);
        }

        if (args[0].equalsIgnoreCase("reload") && args.length == 2) {
            if (sender.hasPermission("functionalservercontrol.reload")) {
                return TextUtils.sortList(Arrays.asList("all", "globalvariables", "settings", "commandlimiter", "cooldowns"), args);
            }
            return Collections.singletonList("");
        }

        if(args[0].equalsIgnoreCase("import") && args.length == 2) {
            if(sender.hasPermission("functionalservercontrol.import")) return Collections.singletonList("vanilla");
        }

        if(args[0].equalsIgnoreCase("history")) {
            if(args.length == 2) {
                if(sender.hasPermission("functionalservercontrol.history")) return TextUtils.sortList(Arrays.asList("1", "2", "5", "10"), args);
            }
            if(args.length == 3) {
                if(sender.hasPermission("functionalservercontrol.history")) return Collections.singletonList("attribute");
            }
        }

        return Collections.singletonList("");
    }
}
