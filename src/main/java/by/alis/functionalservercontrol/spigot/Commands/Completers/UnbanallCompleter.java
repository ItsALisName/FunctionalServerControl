package by.alis.functionalservercontrol.spigot.Commands.Completers;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class UnbanallCompleter implements TabCompleter {

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            if (sender.hasPermission("functionalservercontrol.unban-all") && sender.hasPermission("functionalservercontrol.use.silently")) {
                return Collections.singletonList("-s");
            }
            return Collections.singletonList("");
        }
        return Collections.singletonList("");
    }
}
