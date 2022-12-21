package by.alis.functionalbans.spigot.Managers.Kick;

import by.alis.functionalbans.API.Enums.KickType;
import by.alis.functionalbans.API.Spigot.Events.KickPreprocessEvent;
import by.alis.functionalbans.spigot.FunctionalBansSpigot;
import by.alis.functionalbans.spigot.Managers.CooldownsManager;
import by.alis.functionalbans.spigot.Managers.Files.FileAccessor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static by.alis.functionalbans.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalbans.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getGlobalVariables;
import static by.alis.functionalbans.spigot.Additional.Other.TextUtils.setColors;
import static by.alis.functionalbans.spigot.Managers.Files.SFAccessor.getFileAccessor;

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
            initiatorName = ((Player) initiator).getPlayerListName();
        } else {
            initiatorName = getGlobalVariables().getConsoleVariableName();
        }

        if(reason == null || reason.equalsIgnoreCase("")) {
            finalReason = getGlobalVariables().getDefaultReason();
        } else {
            finalReason = reason;
        }

        KickPreprocessEvent kickPreprocessEvent = new KickPreprocessEvent(false, player, initiator, finalReason, KickType.SINGLE, getConfigSettings().isApiEnabled());
        if(getConfigSettings().isApiEnabled()) {
            if(getConfigSettings().isApiProtectedByPassword()) {
                if(kickPreprocessEvent.getApiPassword() != null && kickPreprocessEvent.getApiPassword().equalsIgnoreCase(getFileAccessor().getGeneralConfig().getString("plugin-settings.api.spigot.password.password"))) {
                    Bukkit.getPluginManager().callEvent(kickPreprocessEvent);
                }
            } else {
                Bukkit.getPluginManager().callEvent(kickPreprocessEvent);
            }
        }

        if(kickPreprocessEvent.isCancelled()) return;

        if(finalReason.equalsIgnoreCase(getGlobalVariables().getDefaultReason())) {
            if(!getConfigSettings().isKickAllowedWithoutReason() && !initiator.hasPermission("functionalbans.use.no-reason")) {
                initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.no-reason")));
                kickPreprocessEvent.setCancelled(true);
                return;
            }
        }

        if(!announceKick && initiator instanceof Player) {
            if(!initiator.hasPermission("functionalbans.use.silently")) {
                initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.flag-no-perms").replace("%1$f", "-s")));
                kickPreprocessEvent.setCancelled(true);
                return;
            }
        }

        if(initiator instanceof Player) {
            if(CooldownsManager.playerHasCooldown(((Player) initiator).getPlayer(), "kick")) {
                CooldownsManager.notifyAboutCooldown(((Player) initiator).getPlayer(), "kick");
                kickPreprocessEvent.setCancelled(true);
                return;
            } else {
                CooldownsManager.setCooldown(((Player) initiator).getPlayer(), "kick");
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

        if(player.hasPermission("functionalbans.kick.bypass") && !initiator.hasPermission("functionalbans.bypass-break")) {
            initiator.sendMessage(setColors(getFileAccessor().getLang().getString("commands.kick.target-bypass")));
            kickPreprocessEvent.setCancelled(true);
            return;
        }

        player.kickPlayer(setColors(String.join("\n", getFileAccessor().getLang().getStringList("kick-format")).replace("%1$f", finalReason).replace("%2$f", initiatorName)));
        if(announceKick) {
            Bukkit.broadcastMessage(setColors(getFileAccessor().getLang().getString("commands.kick.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player.getPlayerListName()).replace("%3$f", finalReason)));
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
        if(initiator instanceof Player) {
            initiatorName = ((Player) initiator).getPlayerListName();
        } else {
            initiatorName = getGlobalVariables().getConsoleVariableName();
        }

        if(reason != null && !reason.equalsIgnoreCase("")) {
            finalReason = reason;
        }

        int count = 0;

        for(Player player : Bukkit.getOnlinePlayers()) {
            KickPreprocessEvent kickPreprocessEvent = new KickPreprocessEvent(true, player, initiator, finalReason, KickType.GLOBAL, getConfigSettings().isApiEnabled());
            if(getConfigSettings().isApiEnabled()) {
                if(getConfigSettings().isApiProtectedByPassword()) {
                    if(kickPreprocessEvent.getApiPassword() != null && kickPreprocessEvent.getApiPassword().equalsIgnoreCase(getFileAccessor().getGeneralConfig().getString("plugin-settings.api.spigot.password.password"))) {
                        Bukkit.getPluginManager().callEvent(kickPreprocessEvent);
                    }
                } else {
                    Bukkit.getPluginManager().callEvent(kickPreprocessEvent);
                }
            }

            if(kickPreprocessEvent.isCancelled()) return;

            if(reason.equalsIgnoreCase(getGlobalVariables().getDefaultReason())) {
                if(!getConfigSettings().isKickAllowedWithoutReason() && !initiator.hasPermission("functionalbans.use.no-reason")) {
                    initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.no-reason")));
                    kickPreprocessEvent.setCancelled(true);
                    return;
                }
            }

            if(!announceKick && initiator instanceof Player) {
                if(!initiator.hasPermission("functionalbans.use.silently")) {
                    initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.flag-no-perms").replace("%1$f", "-s")));
                    kickPreprocessEvent.setCancelled(true);
                    return;
                }
            }

            if(initiator instanceof Player) {
                if(CooldownsManager.playerHasCooldown(((Player) initiator).getPlayer(), "kick")) {
                    CooldownsManager.notifyAboutCooldown(((Player) initiator).getPlayer(), "kick");
                    kickPreprocessEvent.setCancelled(true);
                    return;
                } else {
                    CooldownsManager.setCooldown(((Player) initiator).getPlayer(), "kick");
                }
            }

            if(player.getName().equalsIgnoreCase(initiator.getName())) {
                kickPreprocessEvent.setCancelled(true);
                continue;
            }

            if(player.hasPermission("functionalbans.kick.bypass") && !initiator.hasPermission("functionalbans.bypass-break")) {
                initiator.sendMessage(setColors(getFileAccessor().getLang().getString("commands.kick.target-bypass")));
                kickPreprocessEvent.setCancelled(true);
                return;
            }

            count = count + 1;

            String anotherFinalReason = finalReason;
            String finalInitiatorName = initiatorName;
            Bukkit.getScheduler().runTask(FunctionalBansSpigot.getProvidingPlugin(FunctionalBansSpigot.class), () -> {
                player.kickPlayer(setColors(String.join("\n", getFileAccessor().getLang().getStringList("kick-format")).replace("%1$f", anotherFinalReason).replace("%2$f", finalInitiatorName)));
            });

            if(announceKick) {
                Bukkit.broadcastMessage(setColors(getFileAccessor().getLang().getString("commands.kick.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player.getPlayerListName()).replace("%3$f", finalReason)));
            }

        }

        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("commands.kick-all.success")).replace("%1$f", String.valueOf(count)));
        return;
    }

    public void preformCrazyKick(@NotNull Player player, CommandSender initiator, ChatColor color, boolean announceKick) {

        String initiatorName = null;
        if(initiator instanceof Player) {
            initiatorName = ((Player) initiator).getPlayerListName();
        } else {
            initiatorName = getGlobalVariables().getConsoleVariableName();
        }


        KickPreprocessEvent kickPreprocessEvent = new KickPreprocessEvent(player, initiator, KickType.CRAZY_KICK, getConfigSettings().isApiEnabled());

        if(getConfigSettings().isApiEnabled()) {
            if(getConfigSettings().isApiProtectedByPassword()) {
                if(kickPreprocessEvent.getApiPassword() != null && kickPreprocessEvent.getApiPassword().equalsIgnoreCase(getFileAccessor().getGeneralConfig().getString("plugin-settings.api.spigot.password.password"))) {
                    Bukkit.getPluginManager().callEvent(kickPreprocessEvent);
                }
            } else {
                Bukkit.getPluginManager().callEvent(kickPreprocessEvent);
            }
        }

        if(kickPreprocessEvent.isCancelled()) return;
        if(initiator instanceof Player) {
            if(CooldownsManager.playerHasCooldown(((Player) initiator).getPlayer(), "crazykick")) {
                CooldownsManager.notifyAboutCooldown(((Player) initiator).getPlayer(), "crazykick");
                kickPreprocessEvent.setCancelled(true);
                return;
            } else {
                CooldownsManager.setCooldown(((Player) initiator).getPlayer(), "crazykick");
            }
        }

        if(getConfigSettings().isProhibitYourselfInteraction()) {
            if(initiator.getName().equalsIgnoreCase(player.getName())) {
                initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.no-yourself-actions")));
                kickPreprocessEvent.setCancelled(true);
                return;
            }
        }

        if(!announceKick && initiator instanceof Player) {
            if(!initiator.hasPermission("functionalbans.use.silently")) {
                initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.flag-no-perms").replace("%1$f", "-s")));
                kickPreprocessEvent.setCancelled(true);
                return;
            }
        }



        if(player.hasPermission("functionalbans.crazy-kick.bypass") && !initiator.hasPermission("functionalbans.bypass-break")) {
            initiator.sendMessage(setColors(getFileAccessor().getLang().getString("commands.kick.target-bypass")));
            kickPreprocessEvent.setCancelled(true);
            return;
        }

        player.kickPlayer(color + "鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶鸵郦剜哪婀弱能陶粵躲遞鸲鸧闾稂税粵躲遞鸲鸧闾稂税遞粵逾鸺獀阔遁魁");
        if(announceKick) {
            Bukkit.broadcastMessage(setColors(getFileAccessor().getLang().getString("commands.kick.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player.getPlayerListName()).replace("%3$f", "Crazy")));
        }

    }

}
