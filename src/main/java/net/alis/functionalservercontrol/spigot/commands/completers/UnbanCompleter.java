package net.alis.functionalservercontrol.spigot.commands.completers;

import net.alis.functionalservercontrol.spigot.additional.misc.TextUtils;
import net.alis.functionalservercontrol.spigot.managers.BaseManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

import static net.alis.functionalservercontrol.spigot.additional.containers.StaticContainers.getBannedPlayersContainer;
import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;

public class UnbanCompleter implements TabCompleter {

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (sender.hasPermission("functionalservercontrol.unban")) {
            if (args.length == 1) {
                List<String> a;
                if (getConfigSettings().isAllowedUseRamAsContainer()) {
                    a = getBannedPlayersContainer().getNameContainer();
                    a.removeIf((cmd) -> cmd.equalsIgnoreCase("NULL_PLAYER"));
                    return TextUtils.sortList(a, args);
                } else {
                    a = BaseManager.getBaseManager().getBannedPlayersNames();
                    a.removeIf((cmd) -> cmd.equalsIgnoreCase("NULL_PLAYER"));
                    return TextUtils.sortList(a, args);
                }
            }
            if(args[0].equalsIgnoreCase("-s") && args.length == 2) {
                if(sender.hasPermission("functionalservercontrol.use.silently")) {
                    List<String> a;
                    if (getConfigSettings().isAllowedUseRamAsContainer()) {
                        a = getBannedPlayersContainer().getNameContainer();
                        a.removeIf((cmd) -> cmd.equalsIgnoreCase("NULL_PLAYER"));
                        return TextUtils.sortList(a, args);
                    } else {
                        a = BaseManager.getBaseManager().getBannedPlayersNames();
                        a.removeIf((cmd) -> cmd.equalsIgnoreCase("NULL_PLAYER"));
                        return TextUtils.sortList(a, args);
                    }
                }
            }
            if(args[0].equalsIgnoreCase("-id") && args.length == 2) {
                if(getConfigSettings().isAllowedUseRamAsContainer()){
                    return TextUtils.sortList(getBannedPlayersContainer().getIdsContainer(), args);
                } else {
                    return TextUtils.sortList(BaseManager.getBaseManager().getBannedIds(), args);
                }
            }
            if((args[0].equalsIgnoreCase("-id") && args[1].equalsIgnoreCase("-s") || args[0].equalsIgnoreCase("-s") && args[1].equalsIgnoreCase("-id")) && args.length == 3) {
                if(sender.hasPermission("functionalservercontrol.use.silently")){
                    if (getConfigSettings().isAllowedUseRamAsContainer()) {
                        return TextUtils.sortList(getBannedPlayersContainer().getIdsContainer(), args);
                    } else {
                        return TextUtils.sortList(BaseManager.getBaseManager().getBannedIds(), args);
                    }
                }
            }
            return Collections.singletonList("");
        }
        return Collections.singletonList("");
    }
}
