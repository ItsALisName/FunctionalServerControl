package by.alis.functionalservercontrol.spigot.Commands.Completers;

import by.alis.functionalservercontrol.spigot.Additional.Misc.TextUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

import static by.alis.functionalservercontrol.databases.DataBases.getSQLiteManager;
import static by.alis.functionalservercontrol.spigot.Additional.Containers.StaticContainers.getBannedPlayersContainer;
import static by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;

public class UnbanCompleter implements TabCompleter {

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
            if (args.length == 1) {
                if (sender.hasPermission("functionalservercontrol.unban")) {
                    List<String> a;
                    if (getConfigSettings().isAllowedUseRamAsContainer()) {
                        a = getBannedPlayersContainer().getNameContainer();
                        a.removeIf((cmd) -> cmd.equalsIgnoreCase("NULL_PLAYER"));
                        return TextUtils.sortList(a, args);
                    } else {
                        switch (getConfigSettings().getStorageType()) {
                            case SQLITE: {
                                a = getSQLiteManager().getBannedPlayersNames();
                                a.removeIf((cmd) -> cmd.equalsIgnoreCase("NULL_PLAYER"));
                                return TextUtils.sortList(a, args);
                            }
                            case MYSQL: {
                                return Collections.singletonList("");
                            }
                            case H2: {
                                return Collections.singletonList("");
                            }
                        }
                    }
                }
                return Collections.singletonList("");
            }
        return Collections.singletonList("");
    }
}
