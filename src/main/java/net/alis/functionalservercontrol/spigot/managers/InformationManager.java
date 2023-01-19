package net.alis.functionalservercontrol.spigot.managers;

import net.alis.functionalservercontrol.api.enums.StatsType;
import net.alis.functionalservercontrol.spigot.additional.coreadapters.CoreAdapter;
import net.alis.functionalservercontrol.spigot.additional.textcomponents.MD5TextUtils;
import net.alis.functionalservercontrol.spigot.additional.textcomponents.AdventureApiUtils;
import net.alis.functionalservercontrol.spigot.additional.misc.OtherUtils;
import net.alis.functionalservercontrol.spigot.managers.time.TimeSettingsAccessor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import static net.alis.functionalservercontrol.spigot.additional.containers.StaticContainers.getBannedPlayersContainer;
import static net.alis.functionalservercontrol.spigot.additional.containers.StaticContainers.getMutedPlayersContainer;
import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;
import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getGlobalVariables;
import static net.alis.functionalservercontrol.spigot.additional.misc.TextUtils.isTextNotNull;
import static net.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;
import static net.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

public class InformationManager {

    /**
     * Static class
     */
    public InformationManager() {}

    public static void getCachedInformation(CommandSender sender, String flag, String param) {
        TaskManager.preformAsync(() -> {
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
                    if(BaseManager.getBaseManager().getBannedIds().contains(param)) {
                        int indexOf = BaseManager.getBaseManager().getBannedIds().indexOf(param);
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getinfo.success").replace("%1$f", getGlobalVariables().getVariableId()).replace("%2$f", param)));
                        String name = BaseManager.getBaseManager().getBannedPlayersNames().get(indexOf);
                        long time = BaseManager.getBaseManager().getUnbanTimes().get(indexOf);
                        String translatedTime;
                        if(time > 0) {
                            translatedTime = timeSettingsAccessor.getTimeManager().convertFromMillis(timeSettingsAccessor.getTimeManager().getPunishTime(time));
                        } else {
                            translatedTime = getGlobalVariables().getVariableNever();
                        }
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getinfo.format")
                                .replace("%1$f", getGlobalVariables().getVariableStatusBanned())
                                .replace("%2$f", name)
                                .replace("%3$f", BaseManager.getBaseManager().getBanInitiators().get(indexOf))
                                .replace("%4$f", BaseManager.getBaseManager().getBanReasons().get(indexOf))
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
                    if(BaseManager.getBaseManager().getMutedIds().contains(param)) {
                        int indexOf = BaseManager.getBaseManager().getMutedIds().indexOf(param);
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getinfo.success").replace("%1$f", getGlobalVariables().getVariableId()).replace("%2$f", param)));
                        String name = BaseManager.getBaseManager().getMutedPlayersNames().get(indexOf);
                        long time = BaseManager.getBaseManager().getUnmuteTimes().get(indexOf);
                        String translatedTime;
                        if(time > 0) {
                            translatedTime = timeSettingsAccessor.getTimeManager().convertFromMillis(timeSettingsAccessor.getTimeManager().getPunishTime(time));
                        } else {
                            translatedTime = getGlobalVariables().getVariableNever();
                        }
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getinfo.format")
                                .replace("%1$f", getGlobalVariables().getVariableStatusMuted())
                                .replace("%2$f", name)
                                .replace("%3$f", BaseManager.getBaseManager().getMuteInitiators().get(indexOf))
                                .replace("%4$f", BaseManager.getBaseManager().getMuteReasons().get(indexOf))
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
                    if(BaseManager.getBaseManager().getBannedIps().contains(param)) {
                        int indexOf = BaseManager.getBaseManager().getBannedIps().indexOf(param);
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getinfo.success").replace("%1$f", getGlobalVariables().getVariableIp()).replace("%2$f", param)));
                        String name = BaseManager.getBaseManager().getBannedPlayersNames().get(indexOf);
                        long time = BaseManager.getBaseManager().getUnbanTimes().get(indexOf);
                        String translatedTime;
                        if(time > 0) {
                            translatedTime = timeSettingsAccessor.getTimeManager().convertFromMillis(timeSettingsAccessor.getTimeManager().getPunishTime(time));
                        } else {
                            translatedTime = getGlobalVariables().getVariableNever();
                        }
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getinfo.format")
                                .replace("%1$f", getGlobalVariables().getVariableStatusBanned())
                                .replace("%2$f", name)
                                .replace("%3$f", BaseManager.getBaseManager().getBanInitiators().get(indexOf))
                                .replace("%4$f", BaseManager.getBaseManager().getBanReasons().get(indexOf))
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
                    if(BaseManager.getBaseManager().getMutedIps().contains(param)) {
                        int indexOf = BaseManager.getBaseManager().getMutedIps().indexOf(param);
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getinfo.success").replace("%1$f", getGlobalVariables().getVariableIp()).replace("%2$f", param)));
                        String name = BaseManager.getBaseManager().getMutedPlayersNames().get(indexOf);
                        long time = BaseManager.getBaseManager().getUnmuteTimes().get(indexOf);
                        String translatedTime;
                        if(time > 0) {
                            translatedTime = timeSettingsAccessor.getTimeManager().convertFromMillis(timeSettingsAccessor.getTimeManager().getPunishTime(time));
                        } else {
                            translatedTime = getGlobalVariables().getVariableNever();
                        }
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getinfo.format")
                                .replace("%1$f", getGlobalVariables().getVariableStatusMuted())
                                .replace("%2$f", name)
                                .replace("%3$f", BaseManager.getBaseManager().getMuteInitiators().get(indexOf))
                                .replace("%4$f", BaseManager.getBaseManager().getMuteReasons().get(indexOf))
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
                    if(BaseManager.getBaseManager().getBannedPlayersNames().contains(param)) {
                        int indexOf = BaseManager.getBaseManager().getBannedPlayersNames().indexOf(param);
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getinfo.success").replace("%1$f", getGlobalVariables().getVariableName()).replace("%2$f", param)));
                        String name = BaseManager.getBaseManager().getBannedPlayersNames().get(indexOf);
                        long time = BaseManager.getBaseManager().getUnbanTimes().get(indexOf);
                        String translatedTime;
                        if(time > 0) {
                            translatedTime = timeSettingsAccessor.getTimeManager().convertFromMillis(timeSettingsAccessor.getTimeManager().getPunishTime(time));
                        } else {
                            translatedTime = getGlobalVariables().getVariableNever();
                        }
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getinfo.format")
                                .replace("%1$f", getGlobalVariables().getVariableStatusBanned())
                                .replace("%2$f", name)
                                .replace("%3$f", BaseManager.getBaseManager().getBanInitiators().get(indexOf))
                                .replace("%4$f", BaseManager.getBaseManager().getBanReasons().get(indexOf))
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
                    if(BaseManager.getBaseManager().getMutedPlayersNames().contains(param)) {
                        int indexOf = BaseManager.getBaseManager().getMutedPlayersNames().indexOf(param);
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getinfo.success").replace("%1$f", getGlobalVariables().getVariableName()).replace("%2$f", param)));
                        String name = BaseManager.getBaseManager().getMutedPlayersNames().get(indexOf);
                        long time = BaseManager.getBaseManager().getUnmuteTimes().get(indexOf);
                        String translatedTime;
                        if(time > 0) {
                            translatedTime = timeSettingsAccessor.getTimeManager().convertFromMillis(timeSettingsAccessor.getTimeManager().getPunishTime(time));
                        } else {
                            translatedTime = getGlobalVariables().getVariableNever();
                        }
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getinfo.format")
                                .replace("%1$f", getGlobalVariables().getVariableStatusMuted())
                                .replace("%2$f", name)
                                .replace("%3$f", BaseManager.getBaseManager().getMuteInitiators().get(indexOf))
                                .replace("%4$f", BaseManager.getBaseManager().getMuteReasons().get(indexOf))
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
                    if(BaseManager.getBaseManager().getBannedUUIDs().contains(param)) {
                        int indexOf = BaseManager.getBaseManager().getBannedUUIDs().indexOf(param);
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getinfo.success").replace("%1$f", getGlobalVariables().getVariableUUID()).replace("%2$f", param)));
                        String name = BaseManager.getBaseManager().getBannedPlayersNames().get(indexOf);
                        long time = BaseManager.getBaseManager().getUnbanTimes().get(indexOf);
                        String translatedTime;
                        if(time > 0) {
                            translatedTime = timeSettingsAccessor.getTimeManager().convertFromMillis(timeSettingsAccessor.getTimeManager().getPunishTime(time));
                        } else {
                            translatedTime = getGlobalVariables().getVariableNever();
                        }
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getinfo.format")
                                .replace("%1$f", getGlobalVariables().getVariableStatusBanned())
                                .replace("%2$f", name)
                                .replace("%3$f", BaseManager.getBaseManager().getBanInitiators().get(indexOf))
                                .replace("%4$f", BaseManager.getBaseManager().getBanReasons().get(indexOf))
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
                    if(BaseManager.getBaseManager().getMutedUUIDs().contains(param)) {
                        int indexOf = BaseManager.getBaseManager().getMutedUUIDs().indexOf(param);
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getinfo.success").replace("%1$f", getGlobalVariables().getVariableUUID()).replace("%2$f", param)));
                        String name = BaseManager.getBaseManager().getMutedPlayersNames().get(indexOf);
                        long time = BaseManager.getBaseManager().getUnmuteTimes().get(indexOf);
                        String translatedTime;
                        if(time > 0) {
                            translatedTime = timeSettingsAccessor.getTimeManager().convertFromMillis(timeSettingsAccessor.getTimeManager().getPunishTime(time));
                        } else {
                            translatedTime = getGlobalVariables().getVariableNever();
                        }
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getinfo.format")
                                .replace("%1$f", getGlobalVariables().getVariableStatusMuted())
                                .replace("%2$f", name)
                                .replace("%3$f", BaseManager.getBaseManager().getMuteInitiators().get(indexOf))
                                .replace("%4$f", BaseManager.getBaseManager().getMuteReasons().get(indexOf))
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
            }
        });
    }

    public static void sendHistory(CommandSender recipient, int lines, @Nullable String attribute) { //Добавить команду /fsc history
        TaskManager.preformAsync(() -> {
            if(lines <= 0){
                recipient.sendMessage(setColors(getFileAccessor().getLang().getString("commands.history.not-zero")));
                return;
            }

            if(attribute == null) {
                recipient.sendMessage(setColors(getFileAccessor().getLang().getString("commands.history.message-num")
                        .replace("%1$f", String.valueOf(lines))
                        .replace("%2$f", String.join("\n", BaseManager.getBaseManager().getRecordsFromHistory(recipient, lines, null)))
                ));
            } else {
                recipient.sendMessage(setColors(getFileAccessor().getLang().getString("commands.history.message-attribute")
                        .replace("%1$f", String.valueOf(lines))
                        .replace("%2$f", attribute)
                        .replace("%3$f", String.join("\n", BaseManager.getBaseManager().getRecordsFromHistory(recipient, lines, attribute)))
                ));
            }
        });
    }

    public static void sendStatistic(CommandSender sender, String like, String playerName) {
        TaskManager.preformAsync(() -> {
            OfflinePlayer player = CoreAdapter.getAdapter().getOfflinePlayer(playerName);
            if(player != null && OtherUtils.isNotNullPlayer(player.getUniqueId())) {
                if (like.equalsIgnoreCase("admin")) {
                    String adminBans = BaseManager.getBaseManager().getAdminStatsInfo(player, StatsType.Administrator.STATS_BANS);
                    String adminKicks = BaseManager.getBaseManager().getAdminStatsInfo(player, StatsType.Administrator.STATS_KICKS);
                    String adminMutes = BaseManager.getBaseManager().getAdminStatsInfo(player, StatsType.Administrator.STATS_MUTES);
                    String adminUnbans = BaseManager.getBaseManager().getAdminStatsInfo(player, StatsType.Administrator.STATS_UNBANS);
                    String adminUnmutes = BaseManager.getBaseManager().getAdminStatsInfo(player, StatsType.Administrator.STATS_UNMUTES);
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getstatistic.admin-info-format")
                            .replace("%1$f", playerName)
                            .replace("%2$f", isTextNotNull(adminBans) ? adminBans : "0")
                            .replace("%3$f", isTextNotNull(adminMutes) ? adminMutes : "0")
                            .replace("%4$f", isTextNotNull(adminKicks) ? adminKicks : "0")
                            .replace("%5$f", isTextNotNull(adminUnbans) ? adminUnbans : "0")
                            .replace("%6$f", isTextNotNull(adminUnmutes) ? adminUnmutes : "0")
                    ));
                    return;
                }
                if (like.equalsIgnoreCase("player")) {
                    String playerBans = BaseManager.getBaseManager().getPlayerStatsInfo(player, StatsType.Player.STATS_BANS);
                    String playerKicks = BaseManager.getBaseManager().getPlayerStatsInfo(player, StatsType.Player.STATS_KICKS);
                    String playerMutes = BaseManager.getBaseManager().getPlayerStatsInfo(player, StatsType.Player.STATS_MUTES);
                    String playerBlockedCommand = BaseManager.getBaseManager().getPlayerStatsInfo(player, StatsType.Player.BLOCKED_COMMANDS_USED);
                    String playerBlockedWords = BaseManager.getBaseManager().getPlayerStatsInfo(player, StatsType.Player.BLOCKED_WORDS_USED);
                    String playerAdvertiseAttempts = BaseManager.getBaseManager().getPlayerStatsInfo(player, StatsType.Player.ADVERTISE_ATTEMPTS);
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getstatistic.player-info-format")
                            .replace("%1$f", playerName)
                            .replace("%2$f", isTextNotNull(playerKicks) ? playerKicks : "0")
                            .replace("%3$f", isTextNotNull(playerBans) ? playerBans : "0")
                            .replace("%4$f", isTextNotNull(playerMutes) ? playerMutes : "0")
                            .replace("%5$f", isTextNotNull(playerBlockedCommand) ? playerBlockedCommand : "0")
                            .replace("%6$f", isTextNotNull(playerBlockedWords) ? playerBlockedWords : "0")
                            .replace("%7$f", isTextNotNull(playerAdvertiseAttempts) ? playerAdvertiseAttempts : "0")
                    ));
                    return;
                }
            } else {
                sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getstatistic.no-info").replace("%1$f", playerName)));
                return;
            }
        });
    }

}
