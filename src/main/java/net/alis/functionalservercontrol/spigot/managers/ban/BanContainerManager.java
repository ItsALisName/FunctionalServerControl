package net.alis.functionalservercontrol.spigot.managers.ban;

import net.alis.functionalservercontrol.api.interfaces.FunctionalPlayer;
import net.alis.functionalservercontrol.libraries.net.md_5.bungee.api.chat.HoverEvent;
import net.alis.functionalservercontrol.api.naf.v1_10_0.util.FID;
import net.alis.functionalservercontrol.spigot.additional.textcomponents.Component;
import net.alis.functionalservercontrol.spigot.managers.BaseManager;
import net.alis.functionalservercontrol.spigot.managers.TaskManager;
import net.alis.functionalservercontrol.api.enums.BanType;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import static net.alis.functionalservercontrol.spigot.additional.containers.StaticContainers.getBannedPlayersContainer;
import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;
import static net.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;
import static net.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

public class BanContainerManager {

    public void loadBansIntoRAM() {
        getBannedPlayersContainer().addToBansContainer(
                BaseManager.getBaseManager().getBannedIds(),
                BaseManager.getBaseManager().getBannedIps(),
                BaseManager.getBaseManager().getBannedPlayersNames(),
                BaseManager.getBaseManager().getBanInitiators(),
                BaseManager.getBaseManager().getBanReasons(),
                BaseManager.getBaseManager().getBanTypes(),
                BaseManager.getBaseManager().getBansDates(),
                BaseManager.getBaseManager().getBansTimes(),
                BaseManager.getBaseManager().getBannedUUIDs(),
                BaseManager.getBaseManager().getUnbanTimes(),
                BaseManager.getBaseManager().getBannedFids()
        );
        Bukkit.getConsoleSender().sendMessage(setColors("&a[FunctionalServerControl] Bans loaded into RAM(Total: %count%)".replace("%count%", String.valueOf(BaseManager.getBaseManager().getBannedIds().size()))));
    }

    public void addToBanContainer(String id, String ip, String playerName, String initiatorName, String reason, BanType banType, String realBanDate, String realBanTime, String uuid, Long time, FID fid) {
        getBannedPlayersContainer().addToBansContainer(
                id,
                ip,
                playerName,
                initiatorName,
                reason,
                banType,
                realBanDate,
                realBanTime,
                uuid,
                time,
                fid
        );
    }

    public void removeFromBanContainer(String expression, String param) {
        if(expression.equalsIgnoreCase("-n")) {
            if(getBannedPlayersContainer().getNameContainer().contains(param)) {
                getBannedPlayersContainer().getBanEntries().removeIf(banEntry -> banEntry.getName().equalsIgnoreCase(param));
                int indexOf = getBannedPlayersContainer().getNameContainer().indexOf(param);
                getBannedPlayersContainer().getIdsContainer().remove(indexOf);
                getBannedPlayersContainer().getIpContainer().remove(indexOf);
                getBannedPlayersContainer().getNameContainer().remove(indexOf);
                getBannedPlayersContainer().getInitiatorNameContainer().remove(indexOf);
                getBannedPlayersContainer().getReasonContainer().remove(indexOf);
                getBannedPlayersContainer().getBanTypesContainer().remove(indexOf);
                getBannedPlayersContainer().getRealBanDateContainer().remove(indexOf);
                getBannedPlayersContainer().getRealBanTimeContainer().remove(indexOf);
                getBannedPlayersContainer().getUUIDContainer().remove(indexOf);
                getBannedPlayersContainer().getBanTimeContainer().remove(indexOf);
                getBannedPlayersContainer().getFidsContainer().remove(indexOf);
                return;
            }
            return;
        }
        if(expression.equalsIgnoreCase("-ip")) {
            if(getBannedPlayersContainer().getIpContainer().contains(param)) {
                getBannedPlayersContainer().getBanEntries().removeIf(banEntry -> banEntry.getAddress().equalsIgnoreCase(param));
                int indexOf = getBannedPlayersContainer().getIpContainer().indexOf(param);
                getBannedPlayersContainer().getIdsContainer().remove(indexOf);
                getBannedPlayersContainer().getIpContainer().remove(indexOf);
                getBannedPlayersContainer().getNameContainer().remove(indexOf);
                getBannedPlayersContainer().getInitiatorNameContainer().remove(indexOf);
                getBannedPlayersContainer().getReasonContainer().remove(indexOf);
                getBannedPlayersContainer().getBanTypesContainer().remove(indexOf);
                getBannedPlayersContainer().getRealBanDateContainer().remove(indexOf);
                getBannedPlayersContainer().getRealBanTimeContainer().remove(indexOf);
                getBannedPlayersContainer().getUUIDContainer().remove(indexOf);
                getBannedPlayersContainer().getBanTimeContainer().remove(indexOf);
                getBannedPlayersContainer().getFidsContainer().remove(indexOf);
                return;
            }
            return;
        }
        if(expression.equalsIgnoreCase("-id")) {
            if(getBannedPlayersContainer().getIdsContainer().contains(param)) {
                getBannedPlayersContainer().getBanEntries().removeIf(banEntry -> banEntry.getId().equalsIgnoreCase(param));
                int indexOf = getBannedPlayersContainer().getIdsContainer().indexOf(param);
                getBannedPlayersContainer().getIdsContainer().remove(indexOf);
                getBannedPlayersContainer().getIpContainer().remove(indexOf);
                getBannedPlayersContainer().getNameContainer().remove(indexOf);
                getBannedPlayersContainer().getInitiatorNameContainer().remove(indexOf);
                getBannedPlayersContainer().getReasonContainer().remove(indexOf);
                getBannedPlayersContainer().getBanTypesContainer().remove(indexOf);
                getBannedPlayersContainer().getRealBanDateContainer().remove(indexOf);
                getBannedPlayersContainer().getRealBanTimeContainer().remove(indexOf);
                getBannedPlayersContainer().getUUIDContainer().remove(indexOf);
                getBannedPlayersContainer().getBanTimeContainer().remove(indexOf);
                getBannedPlayersContainer().getFidsContainer().remove(indexOf);
                return;
            }
            return;
        }
        if(expression.equalsIgnoreCase("-u")) {
            if(getBannedPlayersContainer().getUUIDContainer().contains(param)) {
                getBannedPlayersContainer().getBanEntries().removeIf(banEntry -> String.valueOf(banEntry.getUniqueId()).equalsIgnoreCase(param));
                int indexOf = getBannedPlayersContainer().getUUIDContainer().indexOf(param);
                getBannedPlayersContainer().getIdsContainer().remove(indexOf);
                getBannedPlayersContainer().getIpContainer().remove(indexOf);
                getBannedPlayersContainer().getNameContainer().remove(indexOf);
                getBannedPlayersContainer().getInitiatorNameContainer().remove(indexOf);
                getBannedPlayersContainer().getReasonContainer().remove(indexOf);
                getBannedPlayersContainer().getBanTypesContainer().remove(indexOf);
                getBannedPlayersContainer().getRealBanDateContainer().remove(indexOf);
                getBannedPlayersContainer().getRealBanTimeContainer().remove(indexOf);
                getBannedPlayersContainer().getUUIDContainer().remove(indexOf);
                getBannedPlayersContainer().getBanTimeContainer().remove(indexOf);
                getBannedPlayersContainer().getFidsContainer().remove(indexOf);
                return;
            }
            return;
        }
        if(expression.equalsIgnoreCase("-fid")) {
            FID fid = new FID(param);
            if(getBannedPlayersContainer().getFidsContainer().contains(fid)) {
                getBannedPlayersContainer().getBanEntries().removeIf(banEntry -> String.valueOf(banEntry.getFunctionalId()).equalsIgnoreCase(param));
                int indexOf = getBannedPlayersContainer().getFidsContainer().indexOf(fid);
                getBannedPlayersContainer().getIdsContainer().remove(indexOf);
                getBannedPlayersContainer().getIpContainer().remove(indexOf);
                getBannedPlayersContainer().getNameContainer().remove(indexOf);
                getBannedPlayersContainer().getInitiatorNameContainer().remove(indexOf);
                getBannedPlayersContainer().getReasonContainer().remove(indexOf);
                getBannedPlayersContainer().getBanTypesContainer().remove(indexOf);
                getBannedPlayersContainer().getRealBanDateContainer().remove(indexOf);
                getBannedPlayersContainer().getRealBanTimeContainer().remove(indexOf);
                getBannedPlayersContainer().getUUIDContainer().remove(indexOf);
                getBannedPlayersContainer().getBanTimeContainer().remove(indexOf);
                getBannedPlayersContainer().getFidsContainer().remove(indexOf);
                return;
            }
        }
    }

    public void sendBanList(CommandSender sender, int page) {
        TaskManager.preformAsync(() -> {
            if(getConfigSettings().isAllowedUseRamAsContainer()) {
                if(getBannedPlayersContainer().getIdsContainer().size() == 0) {
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.banlist.no-banned-players")));
                    return;
                }
                if(page < 0) {
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.banlist.page-not-zero")));
                    return;
                }
                int maxPages = getBannedPlayersContainer().getIdsContainer().size() / 10;
                if(maxPages == 0) maxPages = 1;
                if(page > maxPages) {
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.banlist.page-not-found").replace("%1$f", String.valueOf(page)).replace("%2$f", String.valueOf(maxPages))));
                    return;
                }
                int start = page * 10; if(page == 1) start = 0;
                int stop = start + 10; if(stop > getBannedPlayersContainer().getIdsContainer().size()) stop = getBannedPlayersContainer().getIdsContainer().size();
                String format = getFileAccessor().getLang().getString("commands.banlist.format");
                String hoverText = getFileAccessor().getLang().getString("commands.banlist.hover-text");
                sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.banlist.success").replace("%1$f", String.valueOf(page))));
                if(sender instanceof FunctionalPlayer) {
                    do {
                        ((FunctionalPlayer) sender).expansion().message(new Component.SimplifiedComponent(setColors("&e" + (start + 1) + ". " + format.replace("%1$f", getBannedPlayersContainer().getNameContainer().get(start)).replace("%2$f", getBannedPlayersContainer().getIdsContainer().get(start))))
                                        .setHoverEvent(
                                                HoverEvent.Action.SHOW_TEXT,
                                                setColors(hoverText
                                                        .replace("%1$f", getBannedPlayersContainer().getInitiatorNameContainer().get(start))
                                                        .replace("%2$f", getBannedPlayersContainer().getNameContainer().get(start)))
                                                        .replace("%3$f", getBannedPlayersContainer().getRealBanDateContainer().get(start))
                                                        .replace("%4$f", getBannedPlayersContainer().getRealBanTimeContainer().get(start))
                                                        .replace("%5$f", getBannedPlayersContainer().getReasonContainer().get(start))
                                                        .replace("%6$f", getBannedPlayersContainer().getIdsContainer().get(start)))
                                        .append(Component.addPardonButtons((FunctionalPlayer) sender, BaseManager.getBaseManager().getBannedPlayersNames().get(start))).translateDefaultColorCodes()
                        );
                        start = start + 1;
                    } while (start < stop);
                    return;
                }
            } else {
                if(BaseManager.getBaseManager().getBannedIds().size() == 0) {
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.banlist.no-banned-players")));
                    return;
                }
                if(page < 0) {
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.banlist.page-not-zero")));
                    return;
                }
                int maxPages = BaseManager.getBaseManager().getBannedIds().size() / 10;
                if(maxPages == 0) maxPages = 1;
                if(page > maxPages) {
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.banlist.page-not-found").replace("%1$f", String.valueOf(page)).replace("%2$f", String.valueOf(maxPages))));
                    return;
                }
                int start = page * 10; if(page == 1) start = 0;
                int stop = start + 10; if(stop > BaseManager.getBaseManager().getBannedIds().size()) stop = BaseManager.getBaseManager().getBannedIds().size();
                String format = getFileAccessor().getLang().getString("commands.banlist.format");
                String hoverText = getFileAccessor().getLang().getString("commands.banlist.hover-text");
                sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.banlist.success").replace("%1$f", String.valueOf(page))));
                if(sender instanceof FunctionalPlayer){
                    do {
                        ((FunctionalPlayer) sender).expansion().message(new Component.SimplifiedComponent(setColors("&e" + (start + 1) + ". " + format.replace("%1$f", BaseManager.getBaseManager().getBannedPlayersNames().get(start)).replace("%2$f", BaseManager.getBaseManager().getBannedIds().get(start))))
                                        .setHoverEvent(HoverEvent.Action.SHOW_TEXT, setColors(hoverText
                                            .replace("%1$f", BaseManager.getBaseManager().getBanInitiators().get(start))
                                            .replace("%2$f", BaseManager.getBaseManager().getBannedPlayersNames().get(start)))
                                            .replace("%3$f", BaseManager.getBaseManager().getBansDates().get(start))
                                            .replace("%4$f", BaseManager.getBaseManager().getBansTimes().get(start))
                                            .replace("%5$f", BaseManager.getBaseManager().getBanReasons().get(start))
                                            .replace("%6$f", BaseManager.getBaseManager().getBannedIds().get(start)))
                                        .append(Component.addPardonButtons((FunctionalPlayer)sender, BaseManager.getBaseManager().getBannedPlayersNames().get(start)))
                                        .translateDefaultColorCodes()
                        );
                        start = start + 1;
                    } while (start < stop);
                    return;
                }
            }
        });
    }

}
