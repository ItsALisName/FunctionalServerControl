package by.alis.functionalservercontrol.spigot.Commands.Completers;

import by.alis.functionalservercontrol.spigot.Additional.SomeUtils.TemporaryCache;
import by.alis.functionalservercontrol.spigot.Additional.SomeUtils.TextUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class GetClientCompleter implements TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender.hasPermission("functionalservercontrol.getclient")) {
            if(args.length == 1) {
                return TextUtils.sortList(TemporaryCache.getOnlinePlayerNames(), args);
            }
        }
        return Collections.singletonList("");
    }

}
