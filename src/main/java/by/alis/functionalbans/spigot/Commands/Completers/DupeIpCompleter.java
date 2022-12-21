package by.alis.functionalbans.spigot.Commands.Completers;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DupeIpCompleter implements TabCompleter {

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {

        if(args.length == 1) {
            List<String> a = new ArrayList<>();
            if(sender.hasPermission("functionalbans.dupeip")) {
                a.add("kick");
                a.add("info");
                if(sender.hasPermission("functionalbans.dupeip.create-report")) {
                    a.add("createreport");
                }
                if(sender.hasPermission("functionalbans.dupeip.delete-report")) {
                    a.add("deletereport");
                }
                return a;
            }
            return null;
        }

        if(args[0].equalsIgnoreCase("kick") && args.length == 2) {
            if(sender.hasPermission("functionalbans.dupeip") && sender.hasPermission("functionalbans.use.silently")) {
                return Arrays.asList("-s");
            }
            return null;
        }

        return null;
    }

}
