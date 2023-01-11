package by.alis.functionalservercontrol.spigot.managers.mute;

import by.alis.functionalservercontrol.api.enums.MuteType;
import by.alis.functionalservercontrol.spigot.additional.misc.AdventureApiUtils;
import by.alis.functionalservercontrol.spigot.additional.misc.MD5TextUtils;
import by.alis.functionalservercontrol.spigot.FunctionalServerControl;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static by.alis.functionalservercontrol.databases.DataBases.getSQLiteManager;
import static by.alis.functionalservercontrol.spigot.additional.containers.StaticContainers.getMutedPlayersContainer;
import static by.alis.functionalservercontrol.spigot.additional.globalsettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalservercontrol.spigot.additional.globalsettings.StaticSettingsAccessor.getGlobalVariables;
import static by.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;
import static by.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

public class MuteContainerManager {

    public void loadMutesIntoRAM() {
        getMutedPlayersContainer().addToMuteContainer(
                getSQLiteManager().getMutedIds(),
                getSQLiteManager().getMutedIps(),
                getSQLiteManager().getMutedPlayersNames(),
                getSQLiteManager().getMuteInitiators(),
                getSQLiteManager().getMuteReasons(),
                getSQLiteManager().getMuteTypes(),
                getSQLiteManager().getMuteDates(),
                getSQLiteManager().getMuteTimes(),
                getSQLiteManager().getMutedUUIDs(),
                getSQLiteManager().getUnmuteTimes()
        );
        Bukkit.getConsoleSender().sendMessage(setColors("&a[FunctionalServerControl] Mutes loaded into RAM(Total: %count%)".replace("%count%", String.valueOf(getMutedPlayersContainer().getIdsContainer().size()))));
    }

    public void addToMuteContainer(String id, String ip, String playerName, String initiatorName, String reason, MuteType muteType, String realBanDate, String realBanTime, String uuid, Long time) {
        getMutedPlayersContainer().addToMuteContainer(
                id,
                ip,
                playerName,
                initiatorName,
                reason,
                muteType,
                realBanDate,
                realBanTime,
                uuid,
                time
        );
    }

    public void removeFromMuteContainer(String expression, String param) {
        if(expression.equalsIgnoreCase("-n")) {
            if(getMutedPlayersContainer().getNameContainer().contains(param)) {
                getMutedPlayersContainer().getMuteEntries().removeIf(entry -> entry.getName().equalsIgnoreCase(param));
                int indexOf = getMutedPlayersContainer().getNameContainer().indexOf(param);
                getMutedPlayersContainer().getIdsContainer().remove(indexOf);
                getMutedPlayersContainer().getIpContainer().remove(indexOf);
                getMutedPlayersContainer().getNameContainer().remove(indexOf);
                getMutedPlayersContainer().getInitiatorNameContainer().remove(indexOf);
                getMutedPlayersContainer().getReasonContainer().remove(indexOf);
                getMutedPlayersContainer().getMuteTypesContainer().remove(indexOf);
                getMutedPlayersContainer().getRealMuteDateContainer().remove(indexOf);
                getMutedPlayersContainer().getRealMuteTimeContainer().remove(indexOf);
                getMutedPlayersContainer().getUUIDContainer().remove(indexOf);
                getMutedPlayersContainer().getMuteTimeContainer().remove(indexOf);
                return;
            }
            return;
        }
        if(expression.equalsIgnoreCase("-ip")) {
            if(getMutedPlayersContainer().getIpContainer().contains(param)) {
                getMutedPlayersContainer().getMuteEntries().removeIf(entry -> entry.getAddress().equalsIgnoreCase(param));
                int indexOf = getMutedPlayersContainer().getIpContainer().indexOf(param);
                getMutedPlayersContainer().getIdsContainer().remove(indexOf);
                getMutedPlayersContainer().getIpContainer().remove(indexOf);
                getMutedPlayersContainer().getNameContainer().remove(indexOf);
                getMutedPlayersContainer().getInitiatorNameContainer().remove(indexOf);
                getMutedPlayersContainer().getReasonContainer().remove(indexOf);
                getMutedPlayersContainer().getMuteTypesContainer().remove(indexOf);
                getMutedPlayersContainer().getRealMuteDateContainer().remove(indexOf);
                getMutedPlayersContainer().getRealMuteTimeContainer().remove(indexOf);
                getMutedPlayersContainer().getUUIDContainer().remove(indexOf);
                getMutedPlayersContainer().getMuteTimeContainer().remove(indexOf);
                return;
            }
            return;
        }
        if(expression.equalsIgnoreCase("-id")) {
            if(getMutedPlayersContainer().getIdsContainer().contains(param)) {
                getMutedPlayersContainer().getMuteEntries().removeIf(entry -> entry.getId().equalsIgnoreCase(param));
                int indexOf = getMutedPlayersContainer().getIdsContainer().indexOf(param);
                getMutedPlayersContainer().getIdsContainer().remove(indexOf);
                getMutedPlayersContainer().getIpContainer().remove(indexOf);
                getMutedPlayersContainer().getNameContainer().remove(indexOf);
                getMutedPlayersContainer().getInitiatorNameContainer().remove(indexOf);
                getMutedPlayersContainer().getReasonContainer().remove(indexOf);
                getMutedPlayersContainer().getMuteTypesContainer().remove(indexOf);
                getMutedPlayersContainer().getRealMuteDateContainer().remove(indexOf);
                getMutedPlayersContainer().getRealMuteTimeContainer().remove(indexOf);
                getMutedPlayersContainer().getUUIDContainer().remove(indexOf);
                getMutedPlayersContainer().getMuteTimeContainer().remove(indexOf);
                return;
            }
            return;
        }
        if(expression.equalsIgnoreCase("-u")) {
            if(getMutedPlayersContainer().getUUIDContainer().contains(param)) {
                getMutedPlayersContainer().getMuteEntries().removeIf(entry -> String.valueOf(entry.getUniqueId()).equalsIgnoreCase(param));
                int indexOf = getMutedPlayersContainer().getUUIDContainer().indexOf(param);
                getMutedPlayersContainer().getIdsContainer().remove(indexOf);
                getMutedPlayersContainer().getIpContainer().remove(indexOf);
                getMutedPlayersContainer().getNameContainer().remove(indexOf);
                getMutedPlayersContainer().getInitiatorNameContainer().remove(indexOf);
                getMutedPlayersContainer().getReasonContainer().remove(indexOf);
                getMutedPlayersContainer().getMuteTypesContainer().remove(indexOf);
                getMutedPlayersContainer().getRealMuteDateContainer().remove(indexOf);
                getMutedPlayersContainer().getRealMuteTimeContainer().remove(indexOf);
                getMutedPlayersContainer().getUUIDContainer().remove(indexOf);
                getMutedPlayersContainer().getMuteTimeContainer().remove(indexOf);
                return;
            }
            return;
        }
    }

    public void sendMuteList(CommandSender sender, int page) {
        Bukkit.getScheduler().runTaskAsynchronously(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class), () -> {
            if(getConfigSettings().isAllowedUseRamAsContainer()) {
                if(getMutedPlayersContainer().getIdsContainer().size() == 0) {
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.mutelist.no-muted-players")));
                    return;
                }
                if(page < 0) {
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.mutelist.page-not-zero")));
                    return;
                }
                int maxPages = getMutedPlayersContainer().getIdsContainer().size() / 10;
                if(maxPages == 0) maxPages = 1;
                if(page > maxPages) {
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.mutelist.page-not-found").replace("%1$f", String.valueOf(page)).replace("%2$f", String.valueOf(maxPages))));
                    return;
                }
                int start = page * 10; if(page == 1) start = 0;
                int stop = start + 10; if(stop > getMutedPlayersContainer().getIdsContainer().size()) stop = getMutedPlayersContainer().getIdsContainer().size();
                String format = getFileAccessor().getLang().getString("commands.mutelist.format");
                String hoverText = getFileAccessor().getLang().getString("commands.mutelist.hover-text");
                sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.mutelist.success").replace("%1$f", String.valueOf(page))));
                if(getConfigSettings().isServerSupportsHoverEvents() && sender instanceof Player) {
                    if(getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("MD5")) {
                            do {
                                sender.spigot().sendMessage(
                                        MD5TextUtils.appendTwo(
                                                MD5TextUtils.createHoverText(setColors(
                                                                "&e" + (start + 1) + ". " + format.replace("%1$f", getMutedPlayersContainer().getNameContainer().get(start)).replace("%2$f", getMutedPlayersContainer().getIdsContainer().get(start))),
                                                        setColors(hoverText
                                                                .replace("%1$f", getMutedPlayersContainer().getInitiatorNameContainer().get(start))
                                                                .replace("%2$f", getMutedPlayersContainer().getNameContainer().get(start)))
                                                                .replace("%3$f", getMutedPlayersContainer().getRealMuteDateContainer().get(start))
                                                                .replace("%4$f", getMutedPlayersContainer().getRealMuteTimeContainer().get(start))
                                                                .replace("%5$f", getMutedPlayersContainer().getReasonContainer().get(start))
                                                                .replace("%6$f", getMutedPlayersContainer().getIdsContainer().get(start))
                                                ),
                                                MD5TextUtils.addPardonButtons((Player) sender, getSQLiteManager().getMutedPlayersNames().get(start))
                                        )
                                );
                                start = start + 1;
                            } while (start < stop);
                            return;
                    }
                    if(getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("ADVENTURE")) {
                            do {
                                sender.sendMessage(
                                        AdventureApiUtils.createHoverText(
                                                setColors("&e" + (start + 1) + ". " + format.replace("%1$f", getMutedPlayersContainer().getNameContainer().get(start)).replace("%2$f", getMutedPlayersContainer().getIdsContainer().get(start))),
                                                setColors(hoverText
                                                        .replace("%1$f", getMutedPlayersContainer().getInitiatorNameContainer().get(start))
                                                        .replace("%2$f", getMutedPlayersContainer().getNameContainer().get(start)))
                                                        .replace("%3$f", getMutedPlayersContainer().getRealMuteDateContainer().get(start))
                                                        .replace("%4$f", getMutedPlayersContainer().getRealMuteTimeContainer().get(start))
                                                        .replace("%5$f", getMutedPlayersContainer().getReasonContainer().get(start))
                                                        .replace("%6$f", getMutedPlayersContainer().getIdsContainer().get(start))
                                        ).append(AdventureApiUtils.addPardonButtons((Player)sender, getSQLiteManager().getMutedPlayersNames().get(start)))
                                );
                                start = start + 1;
                            } while (start < stop);
                            return;
                    }
                } else {
                    do {
                        sender.sendMessage(setColors("&e" + (start + 1) + ". " +format.replace("%1$f", getMutedPlayersContainer().getNameContainer().get(start)).replace("%2$f", getMutedPlayersContainer().getIdsContainer().get(start))));
                        start = start + 1;
                    } while (start < stop);
                    return;
                }
            } else {
                switch (getConfigSettings().getStorageType()) {
                    case SQLITE: {
                        if(getSQLiteManager().getMutedIds().size() == 0) {
                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.mutelist.no-muted-players")));
                            return;
                        }
                        if(page < 0) {
                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.mutelist.page-not-zero")));
                            return;
                        }
                        int maxPages = getSQLiteManager().getMutedIds().size() / 10;
                        if(maxPages == 0) maxPages = 1;
                        if(page > maxPages) {
                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.mutelist.page-not-found").replace("%1$f", String.valueOf(page)).replace("%2$f", String.valueOf(maxPages))));
                            return;
                        }
                        int start = page * 10; if(page == 1) start = 0;
                        int stop = start + 10; if(stop > getSQLiteManager().getMutedIds().size()) stop = getSQLiteManager().getMutedIds().size();
                        String format = getFileAccessor().getLang().getString("commands.mutelist.format");
                        String hoverText = getFileAccessor().getLang().getString("commands.mutelist.hover-text");
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.mutelist.success").replace("%1$f", String.valueOf(page))));
                        if(getConfigSettings().isServerSupportsHoverEvents() && sender instanceof Player) {
                            if(getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("MD5")) {
                                    do {
                                        sender.spigot().sendMessage(
                                                MD5TextUtils.appendTwo(
                                                        MD5TextUtils.createHoverText(setColors(
                                                                        "&e" + (start + 1) + ". " + format.replace("%1$f", getSQLiteManager().getMutedPlayersNames().get(start)).replace("%2$f", getSQLiteManager().getMutedIds().get(start))),
                                                                setColors(hoverText
                                                                        .replace("%1$f", getSQLiteManager().getMuteInitiators().get(start))
                                                                        .replace("%2$f", getSQLiteManager().getMutedPlayersNames().get(start)))
                                                                        .replace("%3$f", getSQLiteManager().getMuteDates().get(start))
                                                                        .replace("%4$f", getSQLiteManager().getMuteTimes().get(start))
                                                                        .replace("%5$f", getSQLiteManager().getMuteReasons().get(start))
                                                                        .replace("%6$f", getSQLiteManager().getMutedIds().get(start))
                                                        ),
                                                        MD5TextUtils.addPardonButtons((Player) sender, getSQLiteManager().getMutedPlayersNames().get(start))
                                                )
                                        );
                                        start = start + 1;
                                    } while (start < stop);
                                    return;
                            }
                            if(getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("ADVENTURE")) {
                                    do {
                                        sender.sendMessage(
                                                AdventureApiUtils.createHoverText(
                                                        setColors("&e" + (start + 1) + ". " + format.replace("%1$f", getSQLiteManager().getMutedPlayersNames().get(start)).replace("%2$f", getSQLiteManager().getMutedIds().get(start))),
                                                        setColors(hoverText
                                                                .replace("%1$f", getSQLiteManager().getMuteInitiators().get(start))
                                                                .replace("%2$f", getSQLiteManager().getMutedPlayersNames().get(start)))
                                                                .replace("%3$f", getSQLiteManager().getMuteDates().get(start))
                                                                .replace("%4$f", getSQLiteManager().getMuteTimes().get(start))
                                                                .replace("%5$f", getSQLiteManager().getMuteReasons().get(start))
                                                                .replace("%6$f", getSQLiteManager().getMutedIds().get(start))
                                                ).append(AdventureApiUtils.addPardonButtons((Player)sender, getSQLiteManager().getMutedPlayersNames().get(start)))
                                        );
                                        start = start + 1;
                                    } while (start < stop);
                                    return;
                            }
                        } else {
                            do {
                                sender.sendMessage(setColors("&e" + (start + 1) + ". " +format.replace("%1$f", getSQLiteManager().getMutedPlayersNames().get(start)).replace("%2$f", getSQLiteManager().getMutedIds().get(start))));
                                start = start + 1;
                            } while (start < stop);
                            return;
                        }
                    }
                    case H2: {

                    }
                }
            }

        });
    }
    
}
