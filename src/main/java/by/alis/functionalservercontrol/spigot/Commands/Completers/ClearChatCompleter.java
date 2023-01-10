package by.alis.functionalservercontrol.spigot.Commands.Completers;

import by.alis.functionalservercontrol.spigot.Additional.Misc.TemporaryCache;
import by.alis.functionalservercontrol.spigot.Additional.Misc.TextUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClearChatCompleter implements TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender.hasPermission("functionalservercontrol.clearchat")) {
            if(args.length == 1) {
                List<String> a = new ArrayList<>(TemporaryCache.getOnlinePlayerNames());
                if (sender.hasPermission("functionalservercontrol.clearchat.all")) {
                    a.add("all");
                }
                return TextUtils.sortList(a, args);
            }
        }
        return Collections.singletonList("");
    }
}
