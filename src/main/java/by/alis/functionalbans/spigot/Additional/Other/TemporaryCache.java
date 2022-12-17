package by.alis.functionalbans.spigot.Additional.Other;

import by.alis.functionalbans.spigot.FunctionalBansSpigot;
import by.alis.functionalbans.spigot.Managers.Bans.UnbanManager;
import by.alis.functionalbans.spigot.Managers.Files.FileAccessor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

import static by.alis.functionalbans.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getGlobalVariables;
import static by.alis.functionalbans.spigot.Additional.Other.TextUtils.setColors;
import static by.alis.functionalbans.spigot.Managers.Files.SFAccessor.getFileAccessor;

public class TemporaryCache {

    private static HashMap<OfflinePlayer, CommandSender> unsafeBannedPlayers = new HashMap<>();
    private static HashMap<OfflinePlayer, CommandSender> unsafeMutedPlayers = new HashMap<>();
    private static List<String> onlinePlayerNames = new ArrayList<>();
    private static Map<Player, String> onlineIps = new HashMap<>();
    private static int bansDeletedCount, muteDeletedCount;
    private static final UnbanManager unbanManager = new UnbanManager();


    public static HashMap<OfflinePlayer, CommandSender> getUnsafeMutedPlayers() {
        return unsafeMutedPlayers;
    }
    public static void setUnsafeMutedPlayers(OfflinePlayer whoBanned, CommandSender initiator) {
        unsafeMutedPlayers.put(whoBanned, initiator);
    }
    public static HashMap<OfflinePlayer, CommandSender> getUnsafeBannedPlayers() {
        return unsafeBannedPlayers;
    }
    public static void setUnsafeBannedPlayers(OfflinePlayer whoMuted, CommandSender initiator) {
        unsafeBannedPlayers.put(whoMuted, initiator);
    }

    public static void preformCommandUndo(CommandSender initiator) {
        Bukkit.getScheduler().runTaskAsynchronously(FunctionalBansSpigot.getProvidingPlugin(FunctionalBansSpigot.class), () -> {
            for(Map.Entry<OfflinePlayer, CommandSender> e : unsafeBannedPlayers.entrySet()) {
                if(e.getValue().equals(initiator)) {
                    bansDeletedCount = bansDeletedCount + 1;
                    unsafeBannedPlayers.remove(e.getKey());
                    unbanManager.preformUnban(e.getKey(), getGlobalVariables().getDefaultReason());
                }
            }
            if(bansDeletedCount != 0) {
                initiator.sendMessage(setColors(getFileAccessor().getLang().getString("commands.fb-undo.success").replace("%1$f", String.valueOf(bansDeletedCount)).replace("%2$f", getGlobalVariables().getVarUnbanned())));
            }

            for(Map.Entry<OfflinePlayer, CommandSender> e : unsafeMutedPlayers.entrySet()) {
                if(e.getValue().equals(initiator)) {
                    muteDeletedCount = muteDeletedCount + 1;
                    unsafeMutedPlayers.remove(e.getKey());
                    // TODO: 14.12.2022 Unmute
                }
            }
            if(muteDeletedCount != 0) {
                initiator.sendMessage(setColors(getFileAccessor().getLang().getString("commands.fb-undo.success").replace("%1$f", String.valueOf(bansDeletedCount)).replace("%2$f", getGlobalVariables().getVarUnbanned())));
            }

            if(bansDeletedCount == 0 && muteDeletedCount == 0) {
                initiator.sendMessage(setColors(getFileAccessor().getLang().getString("commands.fb-undo.nothing-to-undo")));
                return;
            }
            bansDeletedCount = 0;
            muteDeletedCount = 0;

        });
    }

    /**
     * Used to get a list of names of online players
     * @return List of online player names
     */
    public static List<String> getOnlinePlayerNames() {
        return onlinePlayerNames;
    }

    /**
     * Expands the list of names of online players
     * @param player The player whose name will be added
     */
    public static void setOnlinePlayerNames(Player player) {
        TemporaryCache.onlinePlayerNames.add(player.getName());
    }

    /**
     * Removes a specific name from the list
     * @param player Player whose name will be removed
     */
    public static void unsetOnlinePlayerName(Player player) {
        TemporaryCache.onlinePlayerNames.remove(player.getName());
    }

    public static Map<Player, String> getOnlineIps() {
        return onlineIps;
    }

    public static void setOnlineIps(Player player) {
        TemporaryCache.onlineIps.put(player, player.getAddress().getAddress().getHostAddress());
    }

    public static void unsetOnlineIps(Player player) {
        TemporaryCache.onlineIps.remove(player);
    }
}
