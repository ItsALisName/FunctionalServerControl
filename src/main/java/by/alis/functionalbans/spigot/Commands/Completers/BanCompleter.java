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

public class BanCompleter implements TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {

        if(args.length == 1) {
            List<String> a = new ArrayList<>();
            if(sender.hasPermission("functionalbans.ban")) {
                if (sender.hasPermission("functionalbans.use.unsafe-flags")) {
                    a.add("-a");
                }
                if (sender.hasPermission("functionalbans.use.silently")) {
                    a.add("-s");
                }
                for(Player p : Bukkit.getOnlinePlayers()) {
                    a.add(p.getName());
                }
            }
            return a;
        }

        if(args[0].equalsIgnoreCase("-a") && args.length == 2) {
            List<String> a = new ArrayList<>();
            if(sender.hasPermission("functionalbans.ban") && sender.hasPermission("functionalbans.use.unsafe-flags")) {
                a.add("-s");
            }
            return a;
        }

        if(args[0].equalsIgnoreCase("-s") && args.length == 2) {
            List<String> a = new ArrayList<>();
            if(sender.hasPermission("functionalbans.ban") && sender.hasPermission("functionalbans.use.silently")) {
                for(Player p : Bukkit.getOnlinePlayers()) {
                    a.add(p.getName());
                }
            }
            return a;
        }

        if(args[0].equalsIgnoreCase("-s") && args.length == 3) {
            List<String> a = new ArrayList<>();
            if(sender.hasPermission("functionalbans.ban") && sender.hasPermission("functionalbans.use.silently")) {
                a.add("1min"); a.add("1day"); a.add("1mon");
                return a;
            }
            return null;
        }

        return null;
    }
}
