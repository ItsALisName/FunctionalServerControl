package by.alis.functionalservercontrol.spigot.commands.completers;

import by.alis.functionalservercontrol.spigot.additional.misc.TextUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static by.alis.functionalservercontrol.spigot.additional.containers.StaticContainers.getBannedPlayersContainer;
import static by.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;

public class BanListCompleter implements TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(getConfigSettings().isAllowedUseRamAsContainer() && sender.hasPermission("functionalservercontrol.mutelist")) {
            int i = 1;
            List<String> a = new ArrayList<>();
            do {
                a.add(String.valueOf(i));
                i = i + 1;
            } while (i < (getBannedPlayersContainer().getIdsContainer().size() / 10));
            return TextUtils.sortList(a, args);
        }
        return Collections.singletonList("");
    }
}
