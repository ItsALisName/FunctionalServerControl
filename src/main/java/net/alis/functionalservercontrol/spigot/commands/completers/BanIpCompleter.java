package net.alis.functionalservercontrol.spigot.commands.completers;

import net.alis.functionalservercontrol.spigot.additional.misc.TemporaryCache;
import net.alis.functionalservercontrol.spigot.additional.misc.TextUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;

public class BanIpCompleter implements TabCompleter {

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
            if (args.length == 1) {
                List<String> a = new ArrayList<>();
                if (sender.hasPermission("functionalservercontrol.ban-ip")) {
                    if (sender.hasPermission("functionalservercontrol.use.unsafe-flags")) {
                        a.add("-a");
                    }
                    if (sender.hasPermission("functionalservercontrol.use.silently")) {
                        a.add("-s");
                    }
                    a.addAll(TemporaryCache.getOnlinePlayerNames());
                    if(!getConfigSettings().isHideIpsFromCompletions()) a.addAll(TemporaryCache.getOnlineIps().values());
                }
                return TextUtils.sortList(a, args);
            }

            if (args[0].equalsIgnoreCase("-s") && args.length == 2) {
                List<String> a = new ArrayList<>();
                if (sender.hasPermission("functionalservercontrol.ban-ip") && sender.hasPermission("functionalservercontrol.use.silently")) {
                    a.addAll(TemporaryCache.getOnlinePlayerNames());
                    if(!getConfigSettings().isHideIpsFromCompletions()) a.addAll(TemporaryCache.getOnlineIps().values());
                }
                return TextUtils.sortList(a, args);
            }

            if (args[0].equalsIgnoreCase("-s") && args.length == 3) {
                List<String> a = new ArrayList<>();
                if (sender.hasPermission("functionalservercontrol.ban-ip") && sender.hasPermission("functionalservercontrol.use.silently")) {
                    a.add("1min");
                    a.add("1day");
                    a.add("1mon");
                    return TextUtils.sortList(a, args);
                }
                return Collections.singletonList("");
            }

        return Collections.singletonList("");
    }
}
