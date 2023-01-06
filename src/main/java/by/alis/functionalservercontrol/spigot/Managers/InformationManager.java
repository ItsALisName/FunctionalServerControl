package by.alis.functionalservercontrol.spigot.Managers;

import by.alis.functionalservercontrol.spigot.Additional.Misc.AdventureApiUtils;
import by.alis.functionalservercontrol.spigot.Additional.Misc.MD5TextUtils;
import by.alis.functionalservercontrol.spigot.FunctionalServerControl;
import by.alis.functionalservercontrol.spigot.Managers.TimeManagers.TimeSettingsAccessor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import static by.alis.functionalservercontrol.databases.DataBases.getSQLiteManager;
import static by.alis.functionalservercontrol.spigot.Additional.Containers.StaticContainers.getBannedPlayersContainer;
import static by.alis.functionalservercontrol.spigot.Additional.Containers.StaticContainers.getMutedPlayersContainer;
import static by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getGlobalVariables;
import static by.alis.functionalservercontrol.spigot.Additional.Misc.TextUtils.setColors;
import static by.alis.functionalservercontrol.spigot.Managers.Files.SFAccessor.getFileAccessor;

public class InformationManager {

    /**
     * Static class
     */
    public InformationManager() {}

    public static void getCachedInformation(CommandSender sender, String flag, String param) {
        Bukkit.getScheduler().runTaskAsynchronously(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class), () -> {
            if(flag.equalsIgnoreCase("-id")) {
                TimeSettingsAccessor timeSettingsAccessor = new TimeSettingsAccessor();
                if (getConfigSettings().isAllowedUseRamAsContainer()) {
                    if(getBannedPlayersContainer().getIdsContainer().contains(param)) {
                        int indexOf = getBannedPlayersContainer().getIdsContainer().indexOf(param);
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getinfo.success").replace("%1$f", getGlobalVariables().getVariableId()).replace("%2$f", param)));
                        String name = getBannedPlayersContainer().getNameContainer().get(indexOf);
                        long time = getBannedPlayersContainer().getBanTimeContainer().get(indexOf);
                        String translatedTime;
                        if(time > 0) {
                            translatedTime = timeSettingsAccessor.getTimeManager().convertFromMillis(timeSettingsAccessor.getTimeManager().getPunishTime(time));
                        } else {
                            translatedTime = getGlobalVariables().getVariableNever();
                        }
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getinfo.format")
                                .replace("%1$f", getGlobalVariables().getVariableStatusBanned())
                                .replace("%2$f", name)
                                .replace("%3$f", getBannedPlayersContainer().getInitiatorNameContainer().get(indexOf))
                                .replace("%4$f", getBannedPlayersContainer().getReasonContainer().get(indexOf))
                                .replace("%5$f", translatedTime)
                        ));
                        if(getConfigSettings().isServerSupportsHoverEvents()) {
                            if(sender instanceof Player) {
                                Player player = ((Player) sender).getPlayer();
                                if (sender.hasPermission("functionalservercontrol.unban")) {
                                    if (getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("MD5")) {
                                        player.spigot().sendMessage(MD5TextUtils.createClickableSuggestCommandText(getGlobalVariables().getButtonUnban(), "/unban " + name));
                                        return;
                                    }
                                    if (getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("ADVENTURE")) {
                                        player.sendMessage(AdventureApiUtils.createClickableSuggestCommandText(getGlobalVariables().getButtonUnban(), "/unban " + name));
                                        return;
                                    }
                                }
                            }
                        }
                        return;
                    }
                    if(getMutedPlayersContainer().getIdsContainer().contains(param)) {
                        int indexOf = getMutedPlayersContainer().getIdsContainer().indexOf(param);
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getinfo.success").replace("%1$f", getGlobalVariables().getVariableId()).replace("%2$f", param)));
                        String name = getMutedPlayersContainer().getNameContainer().get(indexOf);
                        long time = getMutedPlayersContainer().getMuteTimeContainer().get(indexOf);
                        String translatedTime;
                        if(time > 0) {
                            translatedTime = timeSettingsAccessor.getTimeManager().convertFromMillis(timeSettingsAccessor.getTimeManager().getPunishTime(time));
                        } else {
                            translatedTime = getGlobalVariables().getVariableNever();
                        }
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getinfo.format")
                                .replace("%1$f", getGlobalVariables().getVariableStatusMuted())
                                .replace("%2$f", name)
                                .replace("%3$f", getMutedPlayersContainer().getInitiatorNameContainer().get(indexOf))
                                .replace("%4$f", getMutedPlayersContainer().getReasonContainer().get(indexOf))
                                .replace("%5$f", translatedTime)
                        ));
                        if(getConfigSettings().isServerSupportsHoverEvents()) {
                            if(sender instanceof Player) {
                                Player player = ((Player) sender).getPlayer();
                                if (sender.hasPermission("functionalservercontrol.unmute")) {
                                    if (getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("MD5")) {
                                        player.spigot().sendMessage(MD5TextUtils.createClickableSuggestCommandText(getGlobalVariables().getButtonUnmute(), "/unmute " + name));
                                        return;
                                    }
                                    if (getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("ADVENTURE")) {
                                        player.sendMessage(AdventureApiUtils.createClickableSuggestCommandText(getGlobalVariables().getButtonUnmute(), "/unmute " + name));
                                        return;
                                    }
                                }
                            }
                        }
                        return;
                    }
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getinfo.not-found").replace("%1$f", getGlobalVariables().getVariableId()).replace("%2$f", param)));
                    return;
                } else {
                    switch (getConfigSettings().getStorageType()) {
                        case SQLITE: {
                            if(getSQLiteManager().getBannedIds().contains(param)) {
                                int indexOf = getSQLiteManager().getBannedIds().indexOf(param);
                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getinfo.success").replace("%1$f", getGlobalVariables().getVariableId()).replace("%2$f", param)));
                                String name = getSQLiteManager().getBannedPlayersNames().get(indexOf);
                                long time = getSQLiteManager().getUnbanTimes().get(indexOf);
                                String translatedTime;
                                if(time > 0) {
                                    translatedTime = timeSettingsAccessor.getTimeManager().convertFromMillis(timeSettingsAccessor.getTimeManager().getPunishTime(time));
                                } else {
                                    translatedTime = getGlobalVariables().getVariableNever();
                                }
                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getinfo.format")
                                        .replace("%1$f", getGlobalVariables().getVariableStatusBanned())
                                        .replace("%2$f", name)
                                        .replace("%3$f", getSQLiteManager().getBanInitiators().get(indexOf))
                                        .replace("%4$f", getSQLiteManager().getBanReasons().get(indexOf))
                                        .replace("%5$f", translatedTime)
                                ));
                                if(getConfigSettings().isServerSupportsHoverEvents()) {
                                    if(sender instanceof Player) {
                                        Player player = ((Player) sender).getPlayer();
                                        if (sender.hasPermission("functionalservercontrol.unban")) {
                                            if (getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("MD5")) {
                                                player.spigot().sendMessage(MD5TextUtils.createClickableSuggestCommandText(getGlobalVariables().getButtonUnban(), "/unban " + name));
                                                return;
                                            }
                                            if (getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("ADVENTURE")) {
                                                player.sendMessage(AdventureApiUtils.createClickableSuggestCommandText(getGlobalVariables().getButtonUnban(), "/unban " + name));
                                                return;
                                            }
                                        }
                                    }
                                }
                                return;
                            }
                            if(getSQLiteManager().getMutedIds().contains(param)) {
                                int indexOf = getSQLiteManager().getMutedIds().indexOf(param);
                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getinfo.success").replace("%1$f", getGlobalVariables().getVariableId()).replace("%2$f", param)));
                                String name = getSQLiteManager().getMutedPlayersNames().get(indexOf);
                                long time = getSQLiteManager().getUnmuteTimes().get(indexOf);
                                String translatedTime;
                                if(time > 0) {
                                    translatedTime = timeSettingsAccessor.getTimeManager().convertFromMillis(timeSettingsAccessor.getTimeManager().getPunishTime(time));
                                } else {
                                    translatedTime = getGlobalVariables().getVariableNever();
                                }
                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getinfo.format")
                                        .replace("%1$f", getGlobalVariables().getVariableStatusMuted())
                                        .replace("%2$f", name)
                                        .replace("%3$f", getSQLiteManager().getMuteInitiators().get(indexOf))
                                        .replace("%4$f", getSQLiteManager().getMuteReasons().get(indexOf))
                                        .replace("%5$f", translatedTime)
                                ));
                                if (getConfigSettings().isServerSupportsHoverEvents()) {
                                    if (sender instanceof Player) {
                                        Player player = ((Player) sender).getPlayer();
                                        if (sender.hasPermission("functionalservercontrol.unmute")) {
                                            if (getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("MD5")) {
                                                player.spigot().sendMessage(MD5TextUtils.createClickableSuggestCommandText(getGlobalVariables().getButtonUnmute(), "/unmute " + name));
                                                return;
                                            }
                                            if (getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("ADVENTURE")) {
                                                player.sendMessage(AdventureApiUtils.createClickableSuggestCommandText(getGlobalVariables().getButtonUnmute(), "/unmute " + name));
                                                return;
                                            }
                                        }
                                    }
                                }
                                return;
                            }
                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getinfo.not-found").replace("%1$f", getGlobalVariables().getVariableId()).replace("%2$f", param)));
                            return;
                        }
                        case H2: {}
                    }
                }
                return;
            }

            if(flag.equalsIgnoreCase("-ip")) {
                TimeSettingsAccessor timeSettingsAccessor = new TimeSettingsAccessor();
                if (getConfigSettings().isAllowedUseRamAsContainer()) {
                    if(getBannedPlayersContainer().getIpContainer().contains(param)) {
                        int indexOf = getBannedPlayersContainer().getIpContainer().indexOf(param);
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getinfo.success").replace("%1$f", getGlobalVariables().getVariableIp()).replace("%2$f", param)));
                        String name = getBannedPlayersContainer().getNameContainer().get(indexOf);
                        long time = getBannedPlayersContainer().getBanTimeContainer().get(indexOf);
                        String translatedTime;
                        if(time > 0) {
                            translatedTime = timeSettingsAccessor.getTimeManager().convertFromMillis(timeSettingsAccessor.getTimeManager().getPunishTime(time));
                        } else {
                            translatedTime = getGlobalVariables().getVariableNever();
                        }
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getinfo.format")
                                .replace("%1$f", getGlobalVariables().getVariableStatusBanned())
                                .replace("%2$f", name)
                                .replace("%3$f", getBannedPlayersContainer().getInitiatorNameContainer().get(indexOf))
                                .replace("%4$f", getBannedPlayersContainer().getReasonContainer().get(indexOf))
                                .replace("%5$f", translatedTime)
                        ));
                        if(getConfigSettings().isServerSupportsHoverEvents()) {
                            if(sender instanceof Player) {
                                Player player = ((Player) sender).getPlayer();
                                if (sender.hasPermission("functionalservercontrol.unban")) {
                                    if (getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("MD5")) {
                                        player.spigot().sendMessage(MD5TextUtils.createClickableSuggestCommandText(getGlobalVariables().getButtonUnban(), "/unban " + name));
                                        return;
                                    }
                                    if (getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("ADVENTURE")) {
                                        player.sendMessage(AdventureApiUtils.createClickableSuggestCommandText(getGlobalVariables().getButtonUnban(), "/unban " + name));
                                        return;
                                    }
                                }
                            }
                        }
                        return;
                    }
                    if(getMutedPlayersContainer().getIpContainer().contains(param)) {
                        int indexOf = getMutedPlayersContainer().getIpContainer().indexOf(param);
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getinfo.success").replace("%1$f", getGlobalVariables().getVariableIp()).replace("%2$f", param)));
                        String name = getMutedPlayersContainer().getNameContainer().get(indexOf);
                        long time = getMutedPlayersContainer().getMuteTimeContainer().get(indexOf);
                        String translatedTime;
                        if(time > 0) {
                            translatedTime = timeSettingsAccessor.getTimeManager().convertFromMillis(timeSettingsAccessor.getTimeManager().getPunishTime(time));
                        } else {
                            translatedTime = getGlobalVariables().getVariableNever();
                        }
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getinfo.format")
                                .replace("%1$f", getGlobalVariables().getVariableStatusMuted())
                                .replace("%2$f", name)
                                .replace("%3$f", getMutedPlayersContainer().getInitiatorNameContainer().get(indexOf))
                                .replace("%4$f", getMutedPlayersContainer().getReasonContainer().get(indexOf))
                                .replace("%5$f", translatedTime)
                        ));
                        if(getConfigSettings().isServerSupportsHoverEvents()) {
                            if(sender instanceof Player) {
                                Player player = ((Player) sender).getPlayer();
                                if (sender.hasPermission("functionalservercontrol.unmute")) {
                                    if (getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("MD5")) {
                                        player.spigot().sendMessage(MD5TextUtils.createClickableSuggestCommandText(getGlobalVariables().getButtonUnmute(), "/unmute " + name));
                                        return;
                                    }
                                    if (getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("ADVENTURE")) {
                                        player.sendMessage(AdventureApiUtils.createClickableSuggestCommandText(getGlobalVariables().getButtonUnmute(), "/unmute " + name));
                                        return;
                                    }
                                }
                            }
                        }
                        return;
                    }
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getinfo.not-found").replace("%1$f", getGlobalVariables().getVariableIp()).replace("%2$f", param)));
                    return;
                } else {
                    switch (getConfigSettings().getStorageType()) {
                        case SQLITE: {
                            if(getSQLiteManager().getBannedIps().contains(param)) {
                                int indexOf = getSQLiteManager().getBannedIps().indexOf(param);
                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getinfo.success").replace("%1$f", getGlobalVariables().getVariableIp()).replace("%2$f", param)));
                                String name = getSQLiteManager().getBannedPlayersNames().get(indexOf);
                                long time = getSQLiteManager().getUnbanTimes().get(indexOf);
                                String translatedTime;
                                if(time > 0) {
                                    translatedTime = timeSettingsAccessor.getTimeManager().convertFromMillis(timeSettingsAccessor.getTimeManager().getPunishTime(time));
                                } else {
                                    translatedTime = getGlobalVariables().getVariableNever();
                                }
                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getinfo.format")
                                        .replace("%1$f", getGlobalVariables().getVariableStatusBanned())
                                        .replace("%2$f", name)
                                        .replace("%3$f", getSQLiteManager().getBanInitiators().get(indexOf))
                                        .replace("%4$f", getSQLiteManager().getBanReasons().get(indexOf))
                                        .replace("%5$f", translatedTime)
                                ));
                                if(getConfigSettings().isServerSupportsHoverEvents()) {
                                    if(sender instanceof Player) {
                                        Player player = ((Player) sender).getPlayer();
                                        if (sender.hasPermission("functionalservercontrol.unban")) {
                                            if (getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("MD5")) {
                                                player.spigot().sendMessage(MD5TextUtils.createClickableSuggestCommandText(getGlobalVariables().getButtonUnban(), "/unban " + name));
                                                return;
                                            }
                                            if (getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("ADVENTURE")) {
                                                player.sendMessage(AdventureApiUtils.createClickableSuggestCommandText(getGlobalVariables().getButtonUnban(), "/unban " + name));
                                                return;
                                            }
                                        }
                                    }
                                }
                                return;
                            }
                            if(getSQLiteManager().getMutedIps().contains(param)) {
                                int indexOf = getSQLiteManager().getMutedIps().indexOf(param);
                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getinfo.success").replace("%1$f", getGlobalVariables().getVariableIp()).replace("%2$f", param)));
                                String name = getSQLiteManager().getMutedPlayersNames().get(indexOf);
                                long time = getSQLiteManager().getUnmuteTimes().get(indexOf);
                                String translatedTime;
                                if(time > 0) {
                                    translatedTime = timeSettingsAccessor.getTimeManager().convertFromMillis(timeSettingsAccessor.getTimeManager().getPunishTime(time));
                                } else {
                                    translatedTime = getGlobalVariables().getVariableNever();
                                }
                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getinfo.format")
                                        .replace("%1$f", getGlobalVariables().getVariableStatusMuted())
                                        .replace("%2$f", name)
                                        .replace("%3$f", getSQLiteManager().getMuteInitiators().get(indexOf))
                                        .replace("%4$f", getSQLiteManager().getMuteReasons().get(indexOf))
                                        .replace("%5$f", translatedTime)
                                ));
                                if (getConfigSettings().isServerSupportsHoverEvents()) {
                                    if (sender instanceof Player) {
                                        Player player = ((Player) sender).getPlayer();
                                        if (sender.hasPermission("functionalservercontrol.unmute")) {
                                            if (getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("MD5")) {
                                                player.spigot().sendMessage(MD5TextUtils.createClickableSuggestCommandText(getGlobalVariables().getButtonUnmute(), "/unmute " + name));
                                                return;
                                            }
                                            if (getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("ADVENTURE")) {
                                                player.sendMessage(AdventureApiUtils.createClickableSuggestCommandText(getGlobalVariables().getButtonUnmute(), "/unmute " + name));
                                                return;
                                            }
                                        }
                                    }
                                }
                                return;
                            }
                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getinfo.not-found").replace("%1$f", getGlobalVariables().getVariableIp()).replace("%2$f", param)));
                            return;
                        }
                        case H2: {}
                    }
                }
                return;
            }

            if(flag.equalsIgnoreCase("-name")) {
                TimeSettingsAccessor timeSettingsAccessor = new TimeSettingsAccessor();
                if (getConfigSettings().isAllowedUseRamAsContainer()) {
                    if(getBannedPlayersContainer().getNameContainer().contains(param)) {
                        int indexOf = getBannedPlayersContainer().getNameContainer().indexOf(param);
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getinfo.success").replace("%1$f", getGlobalVariables().getVariableName()).replace("%2$f", param)));
                        String name = getBannedPlayersContainer().getNameContainer().get(indexOf);
                        long time = getBannedPlayersContainer().getBanTimeContainer().get(indexOf);
                        String translatedTime;
                        if(time > 0) {
                            translatedTime = timeSettingsAccessor.getTimeManager().convertFromMillis(timeSettingsAccessor.getTimeManager().getPunishTime(time));
                        } else {
                            translatedTime = getGlobalVariables().getVariableNever();
                        }
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getinfo.format")
                                .replace("%1$f", getGlobalVariables().getVariableStatusBanned())
                                .replace("%2$f", name)
                                .replace("%3$f", getBannedPlayersContainer().getInitiatorNameContainer().get(indexOf))
                                .replace("%4$f", getBannedPlayersContainer().getReasonContainer().get(indexOf))
                                .replace("%5$f", translatedTime)
                        ));
                        if(getConfigSettings().isServerSupportsHoverEvents()) {
                            if(sender instanceof Player) {
                                Player player = ((Player) sender).getPlayer();
                                if (sender.hasPermission("functionalservercontrol.unban")) {
                                    if (getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("MD5")) {
                                        player.spigot().sendMessage(MD5TextUtils.createClickableSuggestCommandText(getGlobalVariables().getButtonUnban(), "/unban " + name));
                                        return;
                                    }
                                    if (getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("ADVENTURE")) {
                                        player.sendMessage(AdventureApiUtils.createClickableSuggestCommandText(getGlobalVariables().getButtonUnban(), "/unban " + name));
                                        return;
                                    }
                                }
                            }
                        }
                        return;
                    }
                    if(getMutedPlayersContainer().getNameContainer().contains(param)) {
                        int indexOf = getMutedPlayersContainer().getNameContainer().indexOf(param);
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getinfo.success").replace("%1$f", getGlobalVariables().getVariableName()).replace("%2$f", param)));
                        String name = getMutedPlayersContainer().getNameContainer().get(indexOf);
                        long time = getMutedPlayersContainer().getMuteTimeContainer().get(indexOf);
                        String translatedTime;
                        if(time > 0) {
                            translatedTime = timeSettingsAccessor.getTimeManager().convertFromMillis(timeSettingsAccessor.getTimeManager().getPunishTime(time));
                        } else {
                            translatedTime = getGlobalVariables().getVariableNever();
                        }
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getinfo.format")
                                .replace("%1$f", getGlobalVariables().getVariableStatusMuted())
                                .replace("%2$f", name)
                                .replace("%3$f", getMutedPlayersContainer().getInitiatorNameContainer().get(indexOf))
                                .replace("%4$f", getMutedPlayersContainer().getReasonContainer().get(indexOf))
                                .replace("%5$f", translatedTime)
                        ));
                        if(getConfigSettings().isServerSupportsHoverEvents()) {
                            if(sender instanceof Player) {
                                Player player = ((Player) sender).getPlayer();
                                if (sender.hasPermission("functionalservercontrol.unmute")) {
                                    if (getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("MD5")) {
                                        player.spigot().sendMessage(MD5TextUtils.createClickableSuggestCommandText(getGlobalVariables().getButtonUnmute(), "/unmute " + name));
                                        return;
                                    }
                                    if (getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("ADVENTURE")) {
                                        player.sendMessage(AdventureApiUtils.createClickableSuggestCommandText(getGlobalVariables().getButtonUnmute(), "/unmute " + name));
                                        return;
                                    }
                                }
                            }
                        }
                        return;
                    }
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getinfo.not-found").replace("%1$f", getGlobalVariables().getVariableName()).replace("%2$f", param)));
                    return;
                } else {
                    switch (getConfigSettings().getStorageType()) {
                        case SQLITE: {
                            if(getSQLiteManager().getBannedPlayersNames().contains(param)) {
                                int indexOf = getSQLiteManager().getBannedPlayersNames().indexOf(param);
                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getinfo.success").replace("%1$f", getGlobalVariables().getVariableName()).replace("%2$f", param)));
                                String name = getSQLiteManager().getBannedPlayersNames().get(indexOf);
                                long time = getSQLiteManager().getUnbanTimes().get(indexOf);
                                String translatedTime;
                                if(time > 0) {
                                    translatedTime = timeSettingsAccessor.getTimeManager().convertFromMillis(timeSettingsAccessor.getTimeManager().getPunishTime(time));
                                } else {
                                    translatedTime = getGlobalVariables().getVariableNever();
                                }
                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getinfo.format")
                                        .replace("%1$f", getGlobalVariables().getVariableStatusBanned())
                                        .replace("%2$f", name)
                                        .replace("%3$f", getSQLiteManager().getBanInitiators().get(indexOf))
                                        .replace("%4$f", getSQLiteManager().getBanReasons().get(indexOf))
                                        .replace("%5$f", translatedTime)
                                ));
                                if(getConfigSettings().isServerSupportsHoverEvents()) {
                                    if(sender instanceof Player) {
                                        Player player = ((Player) sender).getPlayer();
                                        if (sender.hasPermission("functionalservercontrol.unban")) {
                                            if (getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("MD5")) {
                                                player.spigot().sendMessage(MD5TextUtils.createClickableSuggestCommandText(getGlobalVariables().getButtonUnban(), "/unban " + name));
                                                return;
                                            }
                                            if (getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("ADVENTURE")) {
                                                player.sendMessage(AdventureApiUtils.createClickableSuggestCommandText(getGlobalVariables().getButtonUnban(), "/unban " + name));
                                                return;
                                            }
                                        }
                                    }
                                }
                                return;
                            }
                            if(getSQLiteManager().getMutedPlayersNames().contains(param)) {
                                int indexOf = getSQLiteManager().getMutedPlayersNames().indexOf(param);
                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getinfo.success").replace("%1$f", getGlobalVariables().getVariableName()).replace("%2$f", param)));
                                String name = getSQLiteManager().getMutedPlayersNames().get(indexOf);
                                long time = getSQLiteManager().getUnmuteTimes().get(indexOf);
                                String translatedTime;
                                if(time > 0) {
                                    translatedTime = timeSettingsAccessor.getTimeManager().convertFromMillis(timeSettingsAccessor.getTimeManager().getPunishTime(time));
                                } else {
                                    translatedTime = getGlobalVariables().getVariableNever();
                                }
                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getinfo.format")
                                        .replace("%1$f", getGlobalVariables().getVariableStatusMuted())
                                        .replace("%2$f", name)
                                        .replace("%3$f", getSQLiteManager().getMuteInitiators().get(indexOf))
                                        .replace("%4$f", getSQLiteManager().getMuteReasons().get(indexOf))
                                        .replace("%5$f", translatedTime)
                                ));
                                if (getConfigSettings().isServerSupportsHoverEvents()) {
                                    if (sender instanceof Player) {
                                        Player player = ((Player) sender).getPlayer();
                                        if (sender.hasPermission("functionalservercontrol.unmute")) {
                                            if (getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("MD5")) {
                                                player.spigot().sendMessage(MD5TextUtils.createClickableSuggestCommandText(getGlobalVariables().getButtonUnmute(), "/unmute " + name));
                                                return;
                                            }
                                            if (getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("ADVENTURE")) {
                                                player.sendMessage(AdventureApiUtils.createClickableSuggestCommandText(getGlobalVariables().getButtonUnmute(), "/unmute " + name));
                                                return;
                                            }
                                        }
                                    }
                                }
                                return;
                            }
                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getinfo.not-found").replace("%1$f", getGlobalVariables().getVariableName()).replace("%2$f", param)));
                            return;
                        }
                        case H2: {}
                    }
                }
                return;
            }

            if(flag.equalsIgnoreCase("-uuid")) {
                TimeSettingsAccessor timeSettingsAccessor = new TimeSettingsAccessor();
                if (getConfigSettings().isAllowedUseRamAsContainer()) {
                    if(getBannedPlayersContainer().getUUIDContainer().contains(param)) {
                        int indexOf = getBannedPlayersContainer().getUUIDContainer().indexOf(param);
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getinfo.success").replace("%1$f", getGlobalVariables().getVariableUUID()).replace("%2$f", param)));
                        String name = getBannedPlayersContainer().getNameContainer().get(indexOf);
                        long time = getBannedPlayersContainer().getBanTimeContainer().get(indexOf);
                        String translatedTime;
                        if(time > 0) {
                            translatedTime = timeSettingsAccessor.getTimeManager().convertFromMillis(timeSettingsAccessor.getTimeManager().getPunishTime(time));
                        } else {
                            translatedTime = getGlobalVariables().getVariableNever();
                        }
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getinfo.format")
                                .replace("%1$f", getGlobalVariables().getVariableStatusBanned())
                                .replace("%2$f", name)
                                .replace("%3$f", getBannedPlayersContainer().getInitiatorNameContainer().get(indexOf))
                                .replace("%4$f", getBannedPlayersContainer().getReasonContainer().get(indexOf))
                                .replace("%5$f", translatedTime)
                        ));
                        if(getConfigSettings().isServerSupportsHoverEvents()) {
                            if(sender instanceof Player) {
                                Player player = ((Player) sender).getPlayer();
                                if (sender.hasPermission("functionalservercontrol.unban")) {
                                    if (getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("MD5")) {
                                        player.spigot().sendMessage(MD5TextUtils.createClickableSuggestCommandText(getGlobalVariables().getButtonUnban(), "/unban " + name));
                                        return;
                                    }
                                    if (getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("ADVENTURE")) {
                                        player.sendMessage(AdventureApiUtils.createClickableSuggestCommandText(getGlobalVariables().getButtonUnban(), "/unban " + name));
                                        return;
                                    }
                                }
                            }
                        }
                        return;
                    }
                    if(getMutedPlayersContainer().getUUIDContainer().contains(param)) {
                        int indexOf = getMutedPlayersContainer().getUUIDContainer().indexOf(param);
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getinfo.success").replace("%1$f", getGlobalVariables().getVariableUUID()).replace("%2$f", param)));
                        String name = getMutedPlayersContainer().getNameContainer().get(indexOf);
                        long time = getMutedPlayersContainer().getMuteTimeContainer().get(indexOf);
                        String translatedTime;
                        if(time > 0) {
                            translatedTime = timeSettingsAccessor.getTimeManager().convertFromMillis(timeSettingsAccessor.getTimeManager().getPunishTime(time));
                        } else {
                            translatedTime = getGlobalVariables().getVariableNever();
                        }
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getinfo.format")
                                .replace("%1$f", getGlobalVariables().getVariableStatusMuted())
                                .replace("%2$f", name)
                                .replace("%3$f", getMutedPlayersContainer().getInitiatorNameContainer().get(indexOf))
                                .replace("%4$f", getMutedPlayersContainer().getReasonContainer().get(indexOf))
                                .replace("%5$f", translatedTime)
                        ));
                        if(getConfigSettings().isServerSupportsHoverEvents()) {
                            if(sender instanceof Player) {
                                Player player = ((Player) sender).getPlayer();
                                if (sender.hasPermission("functionalservercontrol.unmute")) {
                                    if (getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("MD5")) {
                                        player.spigot().sendMessage(MD5TextUtils.createClickableSuggestCommandText(getGlobalVariables().getButtonUnmute(), "/unmute " + name));
                                        return;
                                    }
                                    if (getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("ADVENTURE")) {
                                        player.sendMessage(AdventureApiUtils.createClickableSuggestCommandText(getGlobalVariables().getButtonUnmute(), "/unmute " + name));
                                        return;
                                    }
                                }
                            }
                        }
                        return;
                    }
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getinfo.not-found").replace("%1$f", getGlobalVariables().getVariableUUID()).replace("%2$f", param)));
                    return;
                } else {
                    switch (getConfigSettings().getStorageType()) {
                        case SQLITE: {
                            if(getSQLiteManager().getBannedUUIDs().contains(param)) {
                                int indexOf = getSQLiteManager().getBannedUUIDs().indexOf(param);
                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getinfo.success").replace("%1$f", getGlobalVariables().getVariableUUID()).replace("%2$f", param)));
                                String name = getSQLiteManager().getBannedPlayersNames().get(indexOf);
                                long time = getSQLiteManager().getUnbanTimes().get(indexOf);
                                String translatedTime;
                                if(time > 0) {
                                    translatedTime = timeSettingsAccessor.getTimeManager().convertFromMillis(timeSettingsAccessor.getTimeManager().getPunishTime(time));
                                } else {
                                    translatedTime = getGlobalVariables().getVariableNever();
                                }
                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getinfo.format")
                                        .replace("%1$f", getGlobalVariables().getVariableStatusBanned())
                                        .replace("%2$f", name)
                                        .replace("%3$f", getSQLiteManager().getBanInitiators().get(indexOf))
                                        .replace("%4$f", getSQLiteManager().getBanReasons().get(indexOf))
                                        .replace("%5$f", translatedTime)
                                ));
                                if(getConfigSettings().isServerSupportsHoverEvents()) {
                                    if(sender instanceof Player) {
                                        Player player = ((Player) sender).getPlayer();
                                        if (sender.hasPermission("functionalservercontrol.unban")) {
                                            if (getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("MD5")) {
                                                player.spigot().sendMessage(MD5TextUtils.createClickableSuggestCommandText(getGlobalVariables().getButtonUnban(), "/unban " + name));
                                                return;
                                            }
                                            if (getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("ADVENTURE")) {
                                                player.sendMessage(AdventureApiUtils.createClickableSuggestCommandText(getGlobalVariables().getButtonUnban(), "/unban " + name));
                                                return;
                                            }
                                        }
                                    }
                                }
                                return;
                            }
                            if(getSQLiteManager().getMutedUUIDs().contains(param)) {
                                int indexOf = getSQLiteManager().getMutedUUIDs().indexOf(param);
                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getinfo.success").replace("%1$f", getGlobalVariables().getVariableUUID()).replace("%2$f", param)));
                                String name = getSQLiteManager().getMutedPlayersNames().get(indexOf);
                                long time = getSQLiteManager().getUnmuteTimes().get(indexOf);
                                String translatedTime;
                                if(time > 0) {
                                    translatedTime = timeSettingsAccessor.getTimeManager().convertFromMillis(timeSettingsAccessor.getTimeManager().getPunishTime(time));
                                } else {
                                    translatedTime = getGlobalVariables().getVariableNever();
                                }
                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getinfo.format")
                                        .replace("%1$f", getGlobalVariables().getVariableStatusMuted())
                                        .replace("%2$f", name)
                                        .replace("%3$f", getSQLiteManager().getMuteInitiators().get(indexOf))
                                        .replace("%4$f", getSQLiteManager().getMuteReasons().get(indexOf))
                                        .replace("%5$f", translatedTime)
                                ));
                                if (getConfigSettings().isServerSupportsHoverEvents()) {
                                    if (sender instanceof Player) {
                                        Player player = ((Player) sender).getPlayer();
                                        if (sender.hasPermission("functionalservercontrol.unmute")) {
                                            if (getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("MD5")) {
                                                player.spigot().sendMessage(MD5TextUtils.createClickableSuggestCommandText(getGlobalVariables().getButtonUnmute(), "/unmute " + name));
                                                return;
                                            }
                                            if (getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("ADVENTURE")) {
                                                player.sendMessage(AdventureApiUtils.createClickableSuggestCommandText(getGlobalVariables().getButtonUnmute(), "/unmute " + name));
                                                return;
                                            }
                                        }
                                    }
                                }
                                return;
                            }
                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getinfo.not-found").replace("%1$f", getGlobalVariables().getVariableUUID()).replace("%2$f", param)));
                            return;
                        }
                        case H2: {}
                    }
                }
                return;
            }
        });
    }

    public static void sendHistory(CommandSender recipient, int lines, @Nullable String attribute) { //Добавить команду /fsc history
        Bukkit.getScheduler().runTaskAsynchronously(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class), () -> {
            if(lines <= 0){
                recipient.sendMessage(setColors(getFileAccessor().getLang().getString("commands.history.not-zero")));
                return;
            }

            if(attribute == null) {
                switch (getConfigSettings().getStorageType()) {
                    case SQLITE: recipient.sendMessage(setColors(getFileAccessor().getLang().getString("commands.history.message-num")
                            .replace("%1$f", String.valueOf(lines))
                            .replace("%2$f", String.join("\n", getSQLiteManager().getRecordsFromHistory(recipient, lines, null)))
                    ));
                    case H2: {}
                }
            } else {
                switch (getConfigSettings().getStorageType()) {
                    case SQLITE: recipient.sendMessage(setColors(getFileAccessor().getLang().getString("commands.history.message-attribute")
                            .replace("%1$f", String.valueOf(lines))
                            .replace("%2$f", attribute)
                            .replace("%3$f", String.join("\n", getSQLiteManager().getRecordsFromHistory(recipient, lines, attribute)))
                    ));
                    case H2: {}
                }
            }
        });
    }

}
