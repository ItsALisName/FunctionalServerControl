package by.alis.functionalbans.spigot.Commands.Completers;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TempbanCompleter implements TabCompleter {

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {

        if(args.length == 1) {
            List<String> a = new ArrayList<>();
            if(sender.hasPermission("functionalbans.temp-ban")) {
                if(sender.hasPermission("functionalbans.use.silently")) {
                    a.add("-s");
                }
                for (Player player : Bukkit.getOnlinePlayers()) {
                    a.add(player.getName());
                }
            }
            return a;
        }

        if(args.length == 2 && args[0].equalsIgnoreCase("-s")) {
            List<String> b = new ArrayList<>();
            if(sender.hasPermission("functionalbans.temp-ban") && sender.hasPermission("functionalbans.use.silently")) {
                for(Player player : Bukkit.getOnlinePlayers()) {
                    b.add(player.getName());
                }
            }
            return b;
        }

        if(args.length == 2 && !args[0].equalsIgnoreCase("-s")) {
            List<String> c = new ArrayList<>();
            if(sender.hasPermission("functionalbans.temp-ban")) {
                c.add("1s"); c.add("1m"); c.add("10m"); c.add("1h"); c.add("10h"); c.add("1d");
            }
            return c;
        }

        return null;
    }
}
