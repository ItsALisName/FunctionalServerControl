package by.alis.functionalservercontrol.spigot.commands.completers;

import by.alis.functionalservercontrol.spigot.additional.misc.TemporaryCache;
import by.alis.functionalservercontrol.spigot.additional.misc.TextUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static by.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;

public class CheatCheckCompleter implements TabCompleter {

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
            if (getConfigSettings().isCheatCheckFunctionEnabled()) {

                if (args.length == 1) {
                    if (sender.hasPermission("functionalservercontrol.cheatcheck")) {
                        return TextUtils.sortList(Arrays.asList("start", "stop", "confirm", "refute"), args);
                    }
                    return Collections.singletonList("");
                }

                if (args[0].equalsIgnoreCase("start") && args.length == 2) {
                    if (sender.hasPermission("functionalservercontrol.cheatcheck")) {
                        return TextUtils.sortList(TemporaryCache.getOnlinePlayerNames(), args);
                    }
                    return Collections.singletonList("");
                }

                if (args[0].equalsIgnoreCase("start") && args.length == 3) {
                    if (sender.hasPermission("functionalservercontrol.cheatcheck")) {
                        return Arrays.asList("10s", "1m", "5min", "10min");
                    }
                    return Collections.singletonList("");
                }

                if (args[0].equalsIgnoreCase("stop") && args.length == 2) {
                    if (sender.hasPermission("functionalservercontrol.cheatcheck")) {
                        return TextUtils.sortList(TemporaryCache.getCheckingPlayersNames(), args);
                    }
                    return Collections.singletonList("");
                }

                if (args[0].equalsIgnoreCase("confirm") && args.length == 2) {
                    if (sender.hasPermission("functionalservercontrol.cheatcheck")) {
                        return TextUtils.sortList(TemporaryCache.getCheckingPlayersNames(), args);
                    }
                    return Collections.singletonList(null);
                }

                if (args[0].equalsIgnoreCase("refute") && args.length == 2) {
                    if (sender.hasPermission("functionalservercontrol.cheatcheck")) {
                        return TextUtils.sortList(TemporaryCache.getCheckingPlayersNames(), args);
                    }
                    return Collections.singletonList("");
                }

            }

        return Collections.singletonList("");
    }
}
