package net.alis.functionalservercontrol.spigot.commands.completers;

import net.alis.functionalservercontrol.spigot.additional.misc.TemporaryCache;
import net.alis.functionalservercontrol.spigot.additional.misc.TextUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class GetVersionCompleter implements TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender.hasPermission("functionalservercontrol.getversion")) {
            if(args.length == 1) {
                return TextUtils.sortList(TemporaryCache.getOnlinePlayerNames(), args);
            }
        }
        return Collections.singletonList("");
    }
}
