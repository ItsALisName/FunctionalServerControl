package by.alis.functionalservercontrol.spigot.Managers;

import by.alis.functionalservercontrol.api.Enums.BanType;
import by.alis.functionalservercontrol.spigot.Additional.CoreAdapters.CoreAdapter;
import by.alis.functionalservercontrol.spigot.Additional.Misc.OtherUtils;
import by.alis.functionalservercontrol.spigot.FunctionalServerControl;
import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;

import static by.alis.functionalservercontrol.databases.DataBases.getSQLiteManager;
import static by.alis.functionalservercontrol.spigot.Additional.Containers.StaticContainers.getBanContainerManager;
import static by.alis.functionalservercontrol.spigot.Additional.Containers.StaticContainers.getBannedPlayersContainer;
import static by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getGlobalVariables;
import static by.alis.functionalservercontrol.spigot.Additional.Misc.OtherUtils.generateRandomNumber;
import static by.alis.functionalservercontrol.spigot.Additional.Misc.TextUtils.setColors;
import static by.alis.functionalservercontrol.spigot.Additional.Misc.WorldTimeAndDateClass.getDate;
import static by.alis.functionalservercontrol.spigot.Additional.Misc.WorldTimeAndDateClass.getTime;
import static by.alis.functionalservercontrol.spigot.Managers.Files.SFAccessor.getFileAccessor;

public class ImportManager {

    /**
     * Static class
     */
    public ImportManager() {}

    public static void importDataFromVanilla(CommandSender sender) {
        Bukkit.getScheduler().runTaskAsynchronously(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class), () -> {
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
                    OfflinePlayer target = CoreAdapter.getAdapter().getOfflinePlayer(aNem);
                    String reason = entry.getReason();
                    String initiatorName = entry.getSource();
                    if (initiatorName.equalsIgnoreCase("Server")) {
                        initiatorName = getGlobalVariables().getConsoleVariableName();
                    }
                    String realBanDate = getDate(entry.getCreated());
                    String realBanTime = getTime(entry.getCreated());
                    String name = target.getName();
                    UUID uuid;
                    if(getSQLiteManager().getUuidByName(name).equalsIgnoreCase("null")) {
                        uuid = target.getUniqueId();
                        getSQLiteManager().insertIntoAllPlayers(target.getName(), uuid, generateRandomNumber() + "." + generateRandomNumber() + "." + generateRandomNumber() + "." + generateRandomNumber());
                    } else {
                        uuid = UUID.fromString(getSQLiteManager().getUuidByName(name));
                    }
                    String ip = getSQLiteManager().getIpByUUID(uuid);
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
                        getBannedPlayersContainer().addToBansContainer(id, ip, name, initiatorName, reason, type, realBanDate, realBanTime, String.valueOf(uuid), time);
                    }
                    switch (getConfigSettings().getStorageType()) {
                        case SQLITE:
                            getSQLiteManager().insertIntoBannedPlayers(id, ip, name, initiatorName, reason, type, realBanDate, realBanTime, uuid, time);
                        case H2: {
                        }
                    }
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
                    if (initiatorName.equalsIgnoreCase("Server")) {
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
                    UUID uuid;
                    if (OtherUtils.isArgumentIP(nameOrIp)) {
                        if (OtherUtils.getOnlinePlayerByIP(nameOrIp) != null) {
                            OfflinePlayer player = OtherUtils.getOnlinePlayerByIP(nameOrIp);
                            if(getSQLiteManager().getUuidByName(player.getName()).equalsIgnoreCase("null")) {
                                uuid = player.getUniqueId();
                                getSQLiteManager().insertIntoAllPlayers(player.getName(), uuid, generateRandomNumber() + "." + generateRandomNumber() + "." + generateRandomNumber() + "." + generateRandomNumber());
                            } else {
                                uuid = UUID.fromString(getSQLiteManager().getUuidByName(player.getName()));
                            }
                            if (getConfigSettings().isAllowedUseRamAsContainer()) {
                                getBannedPlayersContainer().addToBansContainer(id, nameOrIp, player.getName(), initiatorName, reason, type, realBanDate, realBanTime, String.valueOf(uuid), time);
                            }
                            switch (getConfigSettings().getStorageType()) {
                                case SQLITE: getSQLiteManager().insertIntoBannedPlayers(id, nameOrIp, player.getName(), initiatorName, reason, type, realBanDate, realBanTime, uuid, time);
                                case H2: {}
                            }
                            Bukkit.getConsoleSender().sendMessage(setColors("&6[FunctionalServerControl] &eBan for player '%player%' imported, ID generated: '%id%'".replace("%player%", player.getName()).replace("%id%", id)));
                        } else {
                            if(getConfigSettings().isAllowedUseRamAsContainer()) {
                                getBanContainerManager().addToBanContainer(id, nameOrIp, "NULL_PLAYER", initiatorName, reason, type, realBanDate, realBanTime, "NULL_PLAYER", time);
                            }
                            getSQLiteManager().insertIntoNullBannedPlayersIP(id, nameOrIp, initiatorName, reason, type, realBanDate, realBanTime, time);
                            Bukkit.getConsoleSender().sendMessage(setColors("&6[FunctionalServerControl] &eBan for IP '%player%' imported, ID generated: '%id%'".replace("%player%", nameOrIp).replace("%id%", id)));
                        }
                    } else {
                        if(OtherUtils.isNotNullPlayer(nameOrIp)) {
                            OfflinePlayer player = CoreAdapter.getAdapter().getOfflinePlayer(nameOrIp);
                            switch (getConfigSettings().getStorageType()) {
                                case SQLITE: getSQLiteManager().insertIntoBannedPlayers(id, getSQLiteManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realBanDate, realBanTime, player.getUniqueId(), -1);
                                case H2: {}
                            }
                            if(getConfigSettings().isAllowedUseRamAsContainer()) {
                                getBannedPlayersContainer().addToBansContainer(id, getSQLiteManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realBanDate, realBanTime, String.valueOf(player.getUniqueId()), -1L);
                            }
                            Bukkit.getConsoleSender().sendMessage(setColors("&6[FunctionalServerControl] &eBan for player '%player%' imported, ID generated: '%id%'".replace("%player%", player.getName()).replace("%id%", id)));
                        } else {
                            switch (getConfigSettings().getStorageType()) {
                                case SQLITE: getSQLiteManager().insertIntoNullBannedPlayers(id, nameOrIp, initiatorName, reason, type, realBanDate, realBanTime, time);
                                case H2: {}
                            }
                            if(getConfigSettings().isAllowedUseRamAsContainer()) {
                                getBanContainerManager().addToBanContainer(id, "NULL_PLAYER", nameOrIp, initiatorName, reason, type, realBanDate, realBanTime, "NULL_PLAYER", time);
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
