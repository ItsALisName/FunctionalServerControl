package by.alis.functionalservercontrol.spigot.commands.completers;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * The class responsible for autocompleting the "/kickall" command
 */
public class KickAllCompleter implements TabCompleter {

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
            if (args.length == 1) {
                if (sender.hasPermission("functionalservercontrol.kick-all") && sender.hasPermission("functionalservercontrol.use.silently")) {
                    return Collections.singletonList("-s");
                }
                return Collections.singletonList("");
            }

            return Collections.singletonList("");

    }
}
