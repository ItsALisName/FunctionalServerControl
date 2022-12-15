package by.alis.functionalbans.spigot.Commands.Completers;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class UnbanallCompleter implements TabCompleter {

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {

        if(args.length == 1) {
            if(sender.hasPermission("functionalbans.unban-all") && sender.hasPermission("functionalbans.use.silently")) {
                return Collections.singletonList("-s");
            }
            return null;
        }

        return null;
    }
}
