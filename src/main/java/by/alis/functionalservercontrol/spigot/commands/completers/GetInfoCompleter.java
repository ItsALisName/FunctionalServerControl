package by.alis.functionalservercontrol.spigot.commands.completers;

import by.alis.functionalservercontrol.spigot.additional.misc.TextUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static by.alis.functionalservercontrol.spigot.additional.containers.StaticContainers.getBannedPlayersContainer;
import static by.alis.functionalservercontrol.spigot.additional.containers.StaticContainers.getMutedPlayersContainer;
import static by.alis.functionalservercontrol.spigot.additional.globalsettings.StaticSettingsAccessor.getConfigSettings;

public class GetInfoCompleter implements TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(sender.hasPermission("functionalservercontrol.getinfo")) {
            if (args.length == 1) {
                return TextUtils.sortList(Arrays.asList("-ip", "-id", "-name", "-uuid"), args);
            }
            if(args.length == 2) {
                if(args[0].equalsIgnoreCase("-id")) {
                    if(getConfigSettings().isAllowedUseRamAsContainer()) {
                        List<String> a = new ArrayList<>(getBannedPlayersContainer().getIdsContainer());
                        a.addAll(getMutedPlayersContainer().getIdsContainer());
                        return TextUtils.sortList(a, args);
                    }
                }
                if(args[0].equalsIgnoreCase("-ip")) {
                    if(getConfigSettings().isAllowedUseRamAsContainer()) {
                        List<String> a = new ArrayList<>(getBannedPlayersContainer().getIpContainer());
                        a.addAll(getMutedPlayersContainer().getIpContainer());
                        a.removeIf((ip) -> ip.equalsIgnoreCase("NULL_PLAYER"));
                        return TextUtils.sortList(a, args);
                    }
                }
                if(args[0].equalsIgnoreCase("-name")) {
                    if(getConfigSettings().isAllowedUseRamAsContainer()) {
                        List<String> a = new ArrayList<>(getBannedPlayersContainer().getNameContainer());
                        a.addAll(getMutedPlayersContainer().getNameContainer());
                        a.removeIf((name) -> name.equalsIgnoreCase("NULL_PLAYER"));
                        return TextUtils.sortList(a, args);
                    }
                }
                if(args[0].equalsIgnoreCase("-uuid")) {
                    if(getConfigSettings().isAllowedUseRamAsContainer()) {
                        List<String> a = new ArrayList<>(getBannedPlayersContainer().getUUIDContainer());
                        a.addAll(getMutedPlayersContainer().getUUIDContainer());
                        a.removeIf((uuid) -> uuid.equalsIgnoreCase("NULL_PLAYER"));
                        return TextUtils.sortList(a, args);
                    }
                }
            }
        }
        return Collections.singletonList("");
    }
}
