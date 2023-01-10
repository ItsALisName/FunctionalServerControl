package by.alis.functionalservercontrol.spigot.Managers.Kick;

import by.alis.functionalservercontrol.api.Enums.KickType;
import by.alis.functionalservercontrol.api.Enums.StatsType;
import by.alis.functionalservercontrol.api.Events.KickPreprocessEvent;
import by.alis.functionalservercontrol.spigot.Additional.CoreAdapters.CoreAdapter;
import by.alis.functionalservercontrol.spigot.FunctionalServerControl;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static by.alis.functionalservercontrol.databases.DataBases.getSQLiteManager;
import static by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getGlobalVariables;
import static by.alis.functionalservercontrol.spigot.Additional.Misc.TextUtils.setColors;
import static by.alis.functionalservercontrol.spigot.Additional.Misc.WorldTimeAndDateClass.getDate;
import static by.alis.functionalservercontrol.spigot.Additional.Misc.WorldTimeAndDateClass.getTime;
import static by.alis.functionalservercontrol.spigot.Managers.CheatCheckerManager.getCheatCheckerManager;
import static by.alis.functionalservercontrol.spigot.Managers.Files.SFAccessor.getFileAccessor;

/**
 * The class responsible for kicking the player from the server with a new method.
 */
public class KickManager {

    /**
     * Kicks a player from the server
     * @param player Player who got kicked
     * @param initiator Admin who kicked a player
     * @param reason The reason the player was kicked
     * @param announceKick Will it be reported in the global chat?
     */
    public void preformKick(@NotNull Player player, CommandSender initiator, @Nullable String reason, boolean announceKick) {
        String finalReason = null;
        String initiatorName = null;
        if(initiator instanceof Player) {
            initiatorName = ((Player) initiator).getName();
        } else {
            initiatorName = getGlobalVariables().getConsoleVariableName();
        }

        if(reason == null || reason.equalsIgnoreCase("")) {
            finalReason = getGlobalVariables().getDefaultReason();
        } else {
            finalReason = reason;
        }

        KickPreprocessEvent kickPreprocessEvent = new KickPreprocessEvent(false, player, initiator, finalReason, KickType.SINGLE);
        if(getConfigSettings().isApiEnabled()) {
            Bukkit.getPluginManager().callEvent(kickPreprocessEvent);
        }

        if(kickPreprocessEvent.isCancelled()) return;

        if(finalReason.equalsIgnoreCase(getGlobalVariables().getDefaultReason())) {
            if(!getConfigSettings().isKickAllowedWithoutReason() && !initiator.hasPermission("functionalservercontrol.use.no-reason")) {
                initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.no-reason")));
                kickPreprocessEvent.setCancelled(true);
                return;
            }
        }

        if(!announceKick && initiator instanceof Player) {
            if(!initiator.hasPermission("functionalservercontrol.use.silently")) {
                initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.flag-no-perms").replace("%1$f", "-s")));
                kickPreprocessEvent.setCancelled(true);
                return;
            }
        }

        if(getConfigSettings().isProhibitYourselfInteraction()) {
            if(initiator.getName().equalsIgnoreCase(player.getName())) {
                initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.no-yourself-actions")));
                kickPreprocessEvent.setCancelled(true);
                return;
            }
        }

        finalReason = kickPreprocessEvent.getReason();

        if(getConfigSettings().isCheatCheckFunctionEnabled()) {
            if(getCheatCheckerManager().isPlayerChecking(player) && getConfigSettings().isPreventKickDuringCheatCheck()) {
                initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.kick-player-on-check")));
                kickPreprocessEvent.setCancelled(true);
                return;
            }
        }

        if(player.hasPermission("functionalservercontrol.kick.bypass") && !initiator.hasPermission("functionalservercontrol.bypass-break")) {
            initiator.sendMessage(setColors(getFileAccessor().getLang().getString("commands.kick.target-bypass")));
            kickPreprocessEvent.setCancelled(true);
            return;
        }

        CoreAdapter.getAdapter().kick(player, setColors(String.join("\n", getFileAccessor().getLang().getStringList("kick-format")).replace("%1$f", finalReason).replace("%2$f", initiatorName)));
        if(announceKick) {
            CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.kick.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", finalReason)));
        }
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                getSQLiteManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.kick").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", reason == null ? getGlobalVariables().getDefaultReason() : reason).replace("%4$f", getDate() + ", " + getTime()));
                if(initiator instanceof Player) getSQLiteManager().updateAdminStatsInfo((Player)initiator, StatsType.Administrator.STATS_KICKS);
                getSQLiteManager().updatePlayerStatsInfo(player, StatsType.Player.STATS_KICKS);
            }
            case H2: {}
        }
        return;

    }

    /**
     * Global kick of all players
     * @param initiator The administrator who kicked everyone out
     * @param reason The reason for the global kick
     * @param announceKick Will it be reported in the global chat?
     */
    public void preformGlobalKick(@NotNull CommandSender initiator, @Nullable String reason, boolean announceKick) {
        String initiatorName = null;
        String finalReason = getGlobalVariables().getDefaultReason();
        if(initiator instanceof ConsoleCommandSender) {
            initiatorName = getGlobalVariables().getConsoleVariableName();
        } else {
            initiatorName = initiator.getName();
        }

        if(reason != null && !reason.equalsIgnoreCase("")) {
            finalReason = reason;
        }

        int count = 0;

        for(Player player : Bukkit.getOnlinePlayers()) {
            KickPreprocessEvent kickPreprocessEvent = new KickPreprocessEvent(true, player, initiator, finalReason, KickType.GLOBAL);
            if(getConfigSettings().isApiEnabled()) {
                Bukkit.getPluginManager().callEvent(kickPreprocessEvent);
            }

            if(kickPreprocessEvent.isCancelled()) return;

            if(reason == null || reason.equalsIgnoreCase(getGlobalVariables().getDefaultReason())) {
                if(!getConfigSettings().isKickAllowedWithoutReason() && !initiator.hasPermission("functionalservercontrol.use.no-reason")) {
                    initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.no-reason")));
                    kickPreprocessEvent.setCancelled(true);
                    return;
                }
            }

            if(!announceKick && initiator instanceof Player) {
                if(!initiator.hasPermission("functionalservercontrol.use.silently")) {
                    initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.flag-no-perms").replace("%1$f", "-s")));
                    kickPreprocessEvent.setCancelled(true);
                    return;
                }
            }

            if(player.getName().equalsIgnoreCase(initiator.getName())) {
                kickPreprocessEvent.setCancelled(true);
                continue;
            }

            if(player.hasPermission("functionalservercontrol.kick.bypass") && !initiator.hasPermission("functionalservercontrol.bypass-break")) {
                initiator.sendMessage(setColors(getFileAccessor().getLang().getString("commands.kick.target-bypass")));
                kickPreprocessEvent.setCancelled(true);
                return;
            }

            count = count + 1;

            String anotherFinalReason = finalReason;
            String finalInitiatorName = initiatorName;
            Bukkit.getScheduler().runTask(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class), () -> {
                CoreAdapter.getAdapter().kick(player, setColors(String.join("\n", getFileAccessor().getLang().getStringList("kick-format")).replace("%1$f", anotherFinalReason).replace("%2$f", finalInitiatorName)));
            });

            if(announceKick) {
                CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.kick.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", finalReason)));
            }
            switch (getConfigSettings().getStorageType()) {
                case SQLITE: {
                    getSQLiteManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.kick").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", reason == null ? getGlobalVariables().getDefaultReason() : reason).replace("%4$f", getDate() + ", " + getTime()));
                    if(initiator instanceof Player) getSQLiteManager().updateAdminStatsInfo((Player)initiator, StatsType.Administrator.STATS_KICKS);
                    getSQLiteManager().updatePlayerStatsInfo(player, StatsType.Player.STATS_KICKS);
                    break;
                }
                case H2: {}
            }
        }
        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("commands.kick-all.success")).replace("%1$f", String.valueOf(count)));
        return;
    }

    public void preformCrazyKick(@NotNull Player player, CommandSender initiator, ChatColor color, boolean announceKick) {

        String initiatorName = null;
        if(initiator instanceof Player) {
            initiatorName = ((Player) initiator).getName();
        } else {
            initiatorName = getGlobalVariables().getConsoleVariableName();
        }


        KickPreprocessEvent kickPreprocessEvent = new KickPreprocessEvent(player, initiator, KickType.CRAZY_KICK);

        if(getConfigSettings().isApiEnabled()) {
            Bukkit.getPluginManager().callEvent(kickPreprocessEvent);
        }

        if(kickPreprocessEvent.isCancelled()) return;

        if(getConfigSettings().isProhibitYourselfInteraction()) {
            if(initiator.getName().equalsIgnoreCase(player.getName())) {
                initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.no-yourself-actions")));
                kickPreprocessEvent.setCancelled(true);
                return;
            }
        }

        if(!announceKick && initiator instanceof Player) {
            if(!initiator.hasPermission("functionalservercontrol.use.silently")) {
                initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.flag-no-perms").replace("%1$f", "-s")));
                kickPreprocessEvent.setCancelled(true);
                return;
            }
        }



        if(player.hasPermission("functionalservercontrol.crazy-kick.bypass") && !initiator.hasPermission("functionalbans.bypass-break")) {
            initiator.sendMessage(setColors(getFileAccessor().getLang().getString("commands.kick.target-bypass")));
            kickPreprocessEvent.setCancelled(true);
            return;
        }

        CoreAdapter.getAdapter().kick(player, color + "鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁");
        if(announceKick) {
            CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.kick.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", "Crazy")));
        }

    }

}
