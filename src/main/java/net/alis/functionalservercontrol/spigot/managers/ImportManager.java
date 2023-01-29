package net.alis.functionalservercontrol.spigot.managers;

import net.alis.functionalservercontrol.api.interfaces.OfflineFunctionalPlayer;
import net.alis.functionalservercontrol.api.naf.v1_10_0.util.FID;
import net.alis.functionalservercontrol.api.enums.BanType;
import net.alis.functionalservercontrol.spigot.additional.misc.OtherUtils;
import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;

import static net.alis.functionalservercontrol.spigot.additional.containers.StaticContainers.getBanContainerManager;
import static net.alis.functionalservercontrol.spigot.additional.containers.StaticContainers.getBannedPlayersContainer;
import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;
import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getGlobalVariables;
import static net.alis.functionalservercontrol.spigot.additional.misc.OtherUtils.generateRandomNumber;
import static net.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;
import static net.alis.functionalservercontrol.spigot.additional.misc.WorldTimeAndDateClass.getDate;
import static net.alis.functionalservercontrol.spigot.additional.misc.WorldTimeAndDateClass.getTime;
import static net.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

public class ImportManager {

    /**
     * Static class
     */
    public ImportManager() {}

    public static void importDataFromVanilla(CommandSender sender) {
        TaskManager.preformAsync(() -> {
            @NotNull Set<BanEntry> namesBans = Bukkit.getBanList(BanList.Type.NAME).getBanEntries();
            @NotNull Set<BanEntry> ipBans = Bukkit.getBanList(BanList.Type.IP).getBanEntries();
            sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.import.importing-started").replace("%1$f", "Vanilla Bans") + "&8(Async)"));
            if(namesBans.size() == 0 && ipBans.size() == 0) {
                sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.import.empty").replace("%1$f", "Vanilla Bans")));
                return;
            }
            IdsManager idsManager = new IdsManager();
            if(namesBans.size() != 0) {
                for (BanEntry entry : namesBans) {
                    String aNem = entry.getTarget();
                    OfflineFunctionalPlayer target = OfflineFunctionalPlayer.get(aNem);
                    String reason = entry.getReason();
                    String initiatorName = entry.getSource();
                    if (initiatorName.equalsIgnoreCase("ServerInfo")) {
                        initiatorName = getGlobalVariables().getConsoleVariableName();
                    }
                    String realBanDate = getDate(entry.getCreated());
                    String realBanTime = getTime(entry.getCreated());
                    String name = target.nickname();
                    UUID uuid;
                    if(BaseManager.getBaseManager().getUuidByName(name).equalsIgnoreCase("null")) {
                        uuid = target.getUniqueId();
                        BaseManager.getBaseManager().insertIntoAllPlayers(target.nickname(), uuid, generateRandomNumber() + "." + generateRandomNumber() + "." + generateRandomNumber() + "." + generateRandomNumber(), target.getFunctionalId());
                    } else {
                        uuid = UUID.fromString(BaseManager.getBaseManager().getUuidByName(name));
                    }
                    String ip = BaseManager.getBaseManager().getIpByUUID(uuid);
                    BanType type;
                    long time;
                    String id = idsManager.getId();
                    if (entry.getExpiration() == null) {
                        time = -1;
                        type = BanType.PERMANENT_NOT_IP;
                    } else {
                        time = entry.getExpiration().getTime();
                        type = BanType.TIMED_NOT_IP;
                    }
                    if (getConfigSettings().isAllowedUseRamAsContainer()) {
                        getBannedPlayersContainer().addToBansContainer(id, ip, name, initiatorName, reason, type, realBanDate, realBanTime, String.valueOf(uuid), time, target.getFunctionalId());
                    }
                    BaseManager.getBaseManager().insertIntoBannedPlayers(id, ip, name, initiatorName, reason, type, realBanDate, realBanTime, uuid, time, target.getFunctionalId());
                    Bukkit.getConsoleSender().sendMessage(setColors("&6[FunctionalServerControl] &eBan for player '%player%' imported, ID generated: '%id%'".replace("%player%", name).replace("%id%", id)));
                }
                for(BanEntry entry : namesBans) {
                    Bukkit.getBanList(BanList.Type.NAME).pardon(entry.getTarget());
                }
            }
            if(ipBans.size() != 0) {
                for (BanEntry entry : ipBans) {
                    String nameOrIp = entry.getTarget();
                    String reason = entry.getReason();
                    String initiatorName = entry.getSource();
                    if (initiatorName.equalsIgnoreCase("ServerInfo")) {
                        initiatorName = getGlobalVariables().getConsoleVariableName();
                    }
                    String realBanDate = getDate(entry.getCreated());
                    String realBanTime = getTime(entry.getCreated());
                    String id = idsManager.getId();
                    BanType type;
                    long time;
                    if (entry.getExpiration() == null) {
                        type = BanType.PERMANENT_IP;
                        time = -1;
                    } else {
                        type = BanType.TIMED_IP;
                        time = entry.getExpiration().getTime();
                    }
                    if (OtherUtils.isArgumentIP(nameOrIp)) {
                        if (OtherUtils.getOnlinePlayerByIP(nameOrIp) != null) {
                            OfflineFunctionalPlayer player = OtherUtils.getOnlinePlayerByIP(nameOrIp);
                            if(BaseManager.getBaseManager().getUuidByName(player.nickname()).equalsIgnoreCase("null")) {
                                UUID uuid = player.getUniqueId();
                                BaseManager.getBaseManager().insertIntoAllPlayers(player.nickname(), uuid, generateRandomNumber() + "." + generateRandomNumber() + "." + generateRandomNumber() + "." + generateRandomNumber(), new FID(player.nickname()));
                            }
                            if (getConfigSettings().isAllowedUseRamAsContainer()) {
                                getBannedPlayersContainer().addToBansContainer(id, nameOrIp, player.nickname(), initiatorName, reason, type, realBanDate, realBanTime, String.valueOf(player.getUniqueId()), time, player.getFunctionalId());
                            }
                            BaseManager.getBaseManager().insertIntoBannedPlayers(id, nameOrIp, player.nickname(), initiatorName, reason, type, realBanDate, realBanTime, player.getUniqueId(), time, new FID(player.nickname()));
                            Bukkit.getConsoleSender().sendMessage(setColors("&6[FunctionalServerControl] &eBan for player '%player%' imported, ID generated: '%id%'".replace("%player%", player.nickname()).replace("%id%", id)));
                        } else {
                            FID fid = FID.random();
                            UUID uuid = UUID.randomUUID();
                            if(getConfigSettings().isAllowedUseRamAsContainer()) {
                                getBanContainerManager().addToBanContainer(id, nameOrIp, "NULL_PLAYER", initiatorName, reason, type, realBanDate, realBanTime, uuid.toString(), time, fid);
                            }
                            BaseManager.getBaseManager().insertIntoNullBannedPlayersIP(id, nameOrIp, initiatorName, reason, type, realBanDate, realBanTime, time, uuid, fid);
                            Bukkit.getConsoleSender().sendMessage(setColors("&6[FunctionalServerControl] &eBan for IP '%player%' imported, ID generated: '%id%'".replace("%player%", nameOrIp).replace("%id%", id)));
                        }
                    } else {
                        OfflineFunctionalPlayer player = OfflineFunctionalPlayer.get(nameOrIp);
                        if(player != null) {
                            BaseManager.getBaseManager().insertIntoBannedPlayers(id, BaseManager.getBaseManager().getIpByUUID(player.getUniqueId()), player.nickname(), initiatorName, reason, type, realBanDate, realBanTime, player.getUniqueId(), -1, player.getFunctionalId());
                            if(getConfigSettings().isAllowedUseRamAsContainer()) {
                                getBannedPlayersContainer().addToBansContainer(id, BaseManager.getBaseManager().getIpByUUID(player.getUniqueId()), player.nickname(), initiatorName, reason, type, realBanDate, realBanTime, String.valueOf(player.getUniqueId()), -1L, player.getFunctionalId());
                            }
                            Bukkit.getConsoleSender().sendMessage(setColors("&6[FunctionalServerControl] &eBan for player '%player%' imported, ID generated: '%id%'".replace("%player%", player.nickname()).replace("%id%", id)));
                        } else {
                            FID fid = FID.random();
                            UUID uuid = UUID.randomUUID();
                            BaseManager.getBaseManager().insertIntoNullBannedPlayers(id, nameOrIp, initiatorName, reason, type, realBanDate, realBanTime, player.getUniqueId(), time, new FID(player.nickname()));
                            if(getConfigSettings().isAllowedUseRamAsContainer()) {
                                getBanContainerManager().addToBanContainer(id, "NULL_PLAYER", nameOrIp, initiatorName, reason, type, realBanDate, realBanTime, uuid.toString(), time, fid);
                            }
                            Bukkit.getConsoleSender().sendMessage(setColors("&6[FunctionalServerControl] &eBan for player '%player%' imported, ID generated: '%id%'".replace("%player%", nameOrIp).replace("%id%", id)));
                        }
                    }
                    Bukkit.getBanList(BanList.Type.IP).pardon(nameOrIp);
                }
            }
            Bukkit.getConsoleSender().sendMessage(setColors("&a[FunctionalServerControl] Importing finished, clearing data from Vanilla Bans"));
            sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.import.importing-ended").replace("%1$f", "Vanilla Bans")));
        });
    }

}
