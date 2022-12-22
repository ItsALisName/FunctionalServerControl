package by.alis.functionalbans.spigot.Commands.Completers;

import by.alis.functionalbans.spigot.Additional.Other.TemporaryCache;
import by.alis.functionalbans.spigot.Additional.Other.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KickCompleter implements TabCompleter {

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {

        if (args.length == 1) {
            List<String> a = new ArrayList<>();
            if(sender.hasPermission("functionalbans.kick")) {
                if(sender.hasPermission("functionalbans.use.silently")) {
                    a.add("-s");
                }
                a.addAll(TemporaryCache.getOnlinePlayerNames());
            }
            return TextUtils.sortList(a, args);
        }

        if(args[0].equalsIgnoreCase("-s") && args.length == 2) {
            if(!sender.hasPermission("functionalbans.use.silently")) {
                return Collections.singletonList("");
            }
        }

        return Collections.singletonList("");
    }
}
