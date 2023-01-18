package net.alis.functionalservercontrol.spigot.managers;

import net.alis.functionalservercontrol.api.enums.StatsType;
import net.alis.functionalservercontrol.spigot.additional.misc.MD5TextUtils;
import net.alis.functionalservercontrol.spigot.additional.misc.AdventureApiUtils;
import net.alis.functionalservercontrol.spigot.dependencies.Expansions;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.*;
import static net.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;
import static net.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

public class GlobalCommandManager {
    public boolean isPlayerCanUseCommand(Player player, String command) {
        String finalCommand = command.split(" ")[0];
        if(command.contains(":")) {
            if(getCommandLimiterSettings().isBlockSyntaxCommand()) {
                if(!getCommandLimiterSettings().getWhitelistedSyntaxCommands().contains(finalCommand)) {
                    if(player.hasPermission("functionalservercontrol.commands.bypass") || player.hasPermission("functionalservercontrol.commands.syntax.bypass")){
                        return true;
                    } else {
                        player.sendMessage(setColors(getCommandLimiterSettings().getSyntaxDenyMessage()));
                        TaskManager.preformAsync(() -> notifyAdminsAboutBlockedCommand(player, finalCommand));
                        return false;
                    }
                }
            }
        }
        if(getCommandLimiterSettings().isUseGroups()) {
            if (Expansions.getVaultManager().isVaultSetuped()) {
                String playerGroup = Expansions.getVaultManager().getPlayerGroup(player);
                if (getCommandLimiterSettings().getGlobalBlockedCommands().containsKey(playerGroup)) {
                    if (getCommandLimiterSettings().getCheckMode().equalsIgnoreCase("first_arg")) {
                        World world = player.getWorld();
                        if (getCommandLimiterSettings().getPerWorldGroups().contains(playerGroup) && getCommandLimiterSettings().getPerGroupWorlds().get(getCommandLimiterSettings().getPerWorldGroups().indexOf(playerGroup)) == world) {
                            int indexOf = getCommandLimiterSettings().getPerWorldGroups().indexOf(playerGroup);
                            if (getCommandLimiterSettings().isPerWorldUseAsWhiteList() ? !getCommandLimiterSettings().getPerGroupCommands().get(indexOf).contains(finalCommand) : getCommandLimiterSettings().getPerGroupCommands().get(indexOf).contains(finalCommand)) {
                                if(player.hasPermission("functionalservercontrol.commands.bypass") || player.hasPermission("functionalservercontrol.commands." + command.replace("/", "") + ".bypass")){
                                    return true;
                                } else {
                                    player.sendMessage(setColors(getCommandLimiterSettings().getGlobalDenyMessage()));
                                    TaskManager.preformAsync(() -> notifyAdminsAboutBlockedCommand(player, finalCommand));
                                    return false;
                                }
                            }
                        }
                        if (getCommandLimiterSettings().isGlobalUseAsWhiteList() ? !getCommandLimiterSettings().getGlobalBlockedCommands().get(playerGroup).contains(finalCommand) : getCommandLimiterSettings().getGlobalBlockedCommands().get(playerGroup).contains(finalCommand)) {
                            if(player.hasPermission("functionalservercontrol.commands.bypass") || player.hasPermission("functionalservercontrol.commands." + command.replace("/", "") + ".bypass")){
                                return true;
                            } else {
                                player.sendMessage(setColors(getCommandLimiterSettings().getGlobalDenyMessage()));
                                TaskManager.preformAsync(() -> notifyAdminsAboutBlockedCommand(player, finalCommand));
                                return false;
                            }
                        }
                    }
                    if (getCommandLimiterSettings().getCheckMode().equalsIgnoreCase("all_args")) {
                        World world = player.getWorld();
                        
                        
                        if (getCommandLimiterSettings().getPerWorldGroups().contains(playerGroup) && getCommandLimiterSettings().getPerGroupWorlds().get(getCommandLimiterSettings().getPerWorldGroups().indexOf(playerGroup)) == world) {
                            int indexOf = getCommandLimiterSettings().getPerWorldGroups().indexOf(playerGroup);
                            if (getCommandLimiterSettings().isPerWorldUseAsWhiteList() ? !getCommandLimiterSettings().getPerGroupCommands().get(indexOf).contains(command) : getCommandLimiterSettings().getPerGroupCommands().get(indexOf).contains(command)) {
                                if(player.hasPermission("functionalservercontrol.commands.bypass") || player.hasPermission("functionalservercontrol.commands." + command.replace("/", "") + ".bypass")){
                                    return true;
                                } else {
                                    player.sendMessage(setColors(getCommandLimiterSettings().getPerWorldDenyMessage()));
                                    TaskManager.preformAsync(() -> notifyAdminsAboutBlockedCommand(player, finalCommand));
                                    return false;
                                }
                            }
                        }
                        if (getCommandLimiterSettings().isGlobalUseAsWhiteList() ? !getCommandLimiterSettings().getGlobalBlockedCommands().get(playerGroup).contains(command) : getCommandLimiterSettings().getGlobalBlockedCommands().get(playerGroup).contains(command)) {
                            if(player.hasPermission("functionalservercontrol.commands.bypass") || player.hasPermission("functionalservercontrol.commands." + command.replace("/", "") + ".bypass")){
                                return true;
                            } else {
                                player.sendMessage(setColors(getCommandLimiterSettings().getGlobalDenyMessage()));
                                TaskManager.preformAsync(() -> notifyAdminsAboutBlockedCommand(player, finalCommand));
                                return false;
                            }
                        }
                    }
                }
            }
            if (Expansions.getLuckPermsManager().isLuckPermsSetuped()) {
                if (getCommandLimiterSettings().getGlobalBlockedCommands().containsKey(Expansions.getLuckPermsManager().getPlayerGroup(player))) {
                    String playerGroup = Expansions.getLuckPermsManager().getPlayerGroup(player);
                    if (getCommandLimiterSettings().getCheckMode().equalsIgnoreCase("first_arg")) {
                        World world = player.getWorld();
                        
                        
                        if (getCommandLimiterSettings().getPerWorldGroups().contains(playerGroup) && getCommandLimiterSettings().getPerGroupWorlds().get(getCommandLimiterSettings().getPerWorldGroups().indexOf(playerGroup)) == world) {
                            int indexOf = getCommandLimiterSettings().getPerWorldGroups().indexOf(playerGroup);
                            if (getCommandLimiterSettings().isPerWorldUseAsWhiteList() ? !getCommandLimiterSettings().getPerGroupCommands().get(indexOf).contains(finalCommand) : getCommandLimiterSettings().getPerGroupCommands().get(indexOf).contains(finalCommand)) {
                                if(player.hasPermission("functionalservercontrol.commands.bypass") || player.hasPermission("functionalservercontrol.commands." + command.replace("/", "") + ".bypass")) {
                                    return true;
                                } else {
                                    player.sendMessage(setColors(getCommandLimiterSettings().getGlobalDenyMessage()));
                                    TaskManager.preformAsync(() -> notifyAdminsAboutBlockedCommand(player, finalCommand));
                                    return false;
                                }
                            }
                        }
                        if (getCommandLimiterSettings().isGlobalUseAsWhiteList() ? !getCommandLimiterSettings().getGlobalBlockedCommands().get(playerGroup).contains(finalCommand) : getCommandLimiterSettings().getGlobalBlockedCommands().get(playerGroup).contains(finalCommand)) {
                            if(player.hasPermission("functionalservercontrol.commands.bypass") || player.hasPermission("functionalservercontrol.commands." + command.replace("/", "") + ".bypass")){
                                return true;
                            } else {
                                player.sendMessage(setColors(getCommandLimiterSettings().getGlobalDenyMessage()));
                                TaskManager.preformAsync(() -> notifyAdminsAboutBlockedCommand(player, finalCommand));
                                return false;
                            }
                        }
                    }
                    if (getCommandLimiterSettings().getCheckMode().equalsIgnoreCase("all_args")) {
                        if (getCommandLimiterSettings().isGlobalUseAsWhiteList() ? !getCommandLimiterSettings().getGlobalBlockedCommands().get(playerGroup).contains(command) : getCommandLimiterSettings().getGlobalBlockedCommands().get(playerGroup).contains(command)) {
                            if(player.hasPermission("functionalservercontrol.commands.bypass") || player.hasPermission("functionalservercontrol.commands." + command.replace("/", "") + ".bypass")){
                                return true;
                            } else {
                                player.sendMessage(setColors(getCommandLimiterSettings().getGlobalDenyMessage()));
                                TaskManager.preformAsync(() -> notifyAdminsAboutBlockedCommand(player, finalCommand));
                                return false;
                            }
                        }
                        World world = player.getWorld();
                        if (getCommandLimiterSettings().getPerWorldGroups().contains(playerGroup) && getCommandLimiterSettings().getPerGroupWorlds().get(getCommandLimiterSettings().getPerWorldGroups().indexOf(playerGroup)) == world) {
                            int indexOf = getCommandLimiterSettings().getPerWorldGroups().indexOf(playerGroup);
                            if (getCommandLimiterSettings().isPerWorldUseAsWhiteList() ? !getCommandLimiterSettings().getPerGroupCommands().get(indexOf).contains(command) : getCommandLimiterSettings().getPerGroupCommands().get(indexOf).contains(command)) {
                                if(player.hasPermission("functionalservercontrol.commands.bypass") || player.hasPermission("functionalservercontrol.commands." + command.replace("/", "") + ".bypass")){
                                    return true;
                                } else {
                                    player.sendMessage(setColors(getCommandLimiterSettings().getPerWorldDenyMessage()));
                                    TaskManager.preformAsync(() -> notifyAdminsAboutBlockedCommand(player, finalCommand));
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        }
        if(getCommandLimiterSettings().getCheckMode().equalsIgnoreCase("first_arg")) {
            World world = player.getWorld();
            
            
            if(getCommandLimiterSettings().getPerWorldGroups().contains("global") && getCommandLimiterSettings().getPerGroupWorlds().get(getCommandLimiterSettings().getPerWorldGroups().indexOf("global")) == world) {
                int indexOf = getCommandLimiterSettings().getPerGroupWorlds().indexOf(world);
                if(getCommandLimiterSettings().isPerWorldUseAsWhiteList()) {
                    if(!getCommandLimiterSettings().getPerGroupCommands().get(indexOf).contains(finalCommand)) {
                        if (player.hasPermission("functionalservercontrol.commands.bypass") || player.hasPermission("functionalservercontrol.commands." + command.replace("/", "") + ".bypass")) {
                            return true;
                        } else {
                            player.sendMessage(setColors(getCommandLimiterSettings().getGlobalDenyMessage()));
                            TaskManager.preformAsync(() -> notifyAdminsAboutBlockedCommand(player, finalCommand));
                            return false;
                        }
                    }
                } else {
                    if(getCommandLimiterSettings().getPerGroupCommands().get(indexOf).contains(finalCommand)) {
                        if (player.hasPermission("functionalservercontrol.commands.bypass") || player.hasPermission("functionalservercontrol.commands." + command.replace("/", "") + ".bypass")) {
                            return true;
                        } else {
                            player.sendMessage(setColors(getCommandLimiterSettings().getGlobalDenyMessage()));
                            TaskManager.preformAsync(() -> notifyAdminsAboutBlockedCommand(player, finalCommand));
                            return false;
                        }
                    }
                }
            }
            if(getCommandLimiterSettings().isGlobalUseAsWhiteList()) {
                if(!getCommandLimiterSettings().getGlobalBlockedCommands().get("global").contains(finalCommand)) {
                    if (player.hasPermission("functionalservercontrol.commands.bypass") || player.hasPermission("functionalservercontrol.commands." + command.replace("/", "") + ".bypass")) {
                        return true;
                    } else {
                        player.sendMessage(setColors(getCommandLimiterSettings().getGlobalDenyMessage()));
                        TaskManager.preformAsync(() -> notifyAdminsAboutBlockedCommand(player, finalCommand));
                        return false;
                    }
                }
            } else {
                if(getCommandLimiterSettings().getGlobalBlockedCommands().get("global").contains(finalCommand)) {
                    if (player.hasPermission("functionalservercontrol.commands.bypass") || player.hasPermission("functionalservercontrol.commands." + command.replace("/", "") + ".bypass")) {
                        return true;
                    } else {
                        player.sendMessage(setColors(getCommandLimiterSettings().getGlobalDenyMessage()));
                        TaskManager.preformAsync(() -> notifyAdminsAboutBlockedCommand(player, finalCommand));
                        return false;
                    }
                }
            }
        }
        if(getCommandLimiterSettings().getCheckMode().equalsIgnoreCase("all_args")) {
            World world = player.getWorld();
            
            
            if(getCommandLimiterSettings().getPerWorldGroups().contains("global") && getCommandLimiterSettings().getPerGroupWorlds().get(getCommandLimiterSettings().getPerWorldGroups().indexOf("global")) == world) {
                int indexOf = getCommandLimiterSettings().getPerGroupWorlds().indexOf(world);
                if (getCommandLimiterSettings().isPerWorldUseAsWhiteList()) {
                    if(!getCommandLimiterSettings().getPerGroupCommands().get(indexOf).contains(command)) {
                        if (player.hasPermission("functionalservercontrol.commands.bypass") || player.hasPermission("functionalservercontrol.commands." + command.replace("/", "") + ".bypass")) {
                            return true;
                        } else {
                            player.sendMessage(setColors(getCommandLimiterSettings().getPerWorldDenyMessage()));
                            TaskManager.preformAsync(() -> notifyAdminsAboutBlockedCommand(player, finalCommand));
                            return false;
                        }
                    }
                } else {
                    if(getCommandLimiterSettings().getPerGroupCommands().get(indexOf).contains(command)) {
                        if (player.hasPermission("functionalservercontrol.commands.bypass") || player.hasPermission("functionalservercontrol.commands." + command.replace("/", "") + ".bypass")) {
                            return true;
                        } else {
                            player.sendMessage(setColors(getCommandLimiterSettings().getPerWorldDenyMessage()));
                            TaskManager.preformAsync(() -> notifyAdminsAboutBlockedCommand(player, finalCommand));
                            return false;
                        }
                    }
                }
            }
            if (getCommandLimiterSettings().isGlobalUseAsWhiteList()) {
                if(!getCommandLimiterSettings().getGlobalBlockedCommands().get("global").contains(command)) {
                    if (player.hasPermission("functionalservercontrol.commands.bypass") || player.hasPermission("functionalservercontrol.commands." + command.replace("/", "") + ".bypass")) {
                        return true;
                    } else {
                        player.sendMessage(setColors(getCommandLimiterSettings().getGlobalDenyMessage()));
                        TaskManager.preformAsync(() -> notifyAdminsAboutBlockedCommand(player, finalCommand));
                        return false;
                    }
                }
            } else {
                if(getCommandLimiterSettings().getGlobalBlockedCommands().get("global").contains(command)) {
                    if (player.hasPermission("functionalservercontrol.commands.bypass") || player.hasPermission("functionalservercontrol.commands." + command.replace("/", "") + ".bypass")) {
                        return true;
                    } else {
                        player.sendMessage(setColors(getCommandLimiterSettings().getGlobalDenyMessage()));
                        TaskManager.preformAsync(() -> notifyAdminsAboutBlockedCommand(player, finalCommand));
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public List<String> getNewCompletions(CommandSender player, String command, List<String> trueCompletions) {
        if(player.hasPermission("functionalservercontrol.tab-complete.bypass") || player.hasPermission("functionalservercontrol.tab-complete." + command.replace("/", "") + ".bypass")) return trueCompletions;
        if(getCommandLimiterSettings().isUseGroups()) {
            if (Expansions.getVaultManager().isVaultSetuped()) {
                String playerGroup = Expansions.getVaultManager().getPlayerGroup((Player) player);
                if (getCommandLimiterSettings().getPerGroupCompletions().containsKey(playerGroup) && getCommandLimiterSettings().getPerGroupCompletions().get(playerGroup).containsKey(command)) {
                    return getCommandLimiterSettings().getPerGroupCompletions().get(playerGroup).get(command);
                }
                if(getCommandLimiterSettings().getGlobalCompletions().containsKey(command)) {
                    return getCommandLimiterSettings().getGlobalCompletions().get(command);
                }
            }
            if(Expansions.getLuckPermsManager().isLuckPermsSetuped()) {
                String playerGroup = Expansions.getLuckPermsManager().getPlayerGroup((Player) player);
                if (getCommandLimiterSettings().getPerGroupCompletions().containsKey(playerGroup) && getCommandLimiterSettings().getPerGroupCompletions().get(playerGroup).containsKey(command)) {
                    return getCommandLimiterSettings().getPerGroupCompletions().get(playerGroup).get(command);
                }
                if(getCommandLimiterSettings().getGlobalCompletions().containsKey(command)) {
                    return getCommandLimiterSettings().getGlobalCompletions().get(command);
                }
            }
        }
        if(getCommandLimiterSettings().getGlobalCompletions().containsKey(command)) {
            return getCommandLimiterSettings().getGlobalCompletions().get(command);
        }
        return trueCompletions;
    }

    public List<String> getCommandsToFullyHide(Player player, List<String> trueCompletions) {
        List<String> newCompletions = new ArrayList<>(trueCompletions);
        if (getConfigSettings().hideMainCommand()) {
            newCompletions.removeIf(cmd -> cmd.contains("fsc") || cmd.contains("functionalservercontrol") || cmd.contains("fscontrol"));
        }
        if(getCommandLimiterSettings().isBlockSyntaxCommand()) {
            newCompletions.removeIf(cmd -> cmd.contains(":"));
        }
        if (getCommandLimiterSettings().isUseGroups()) {
            if (Expansions.getVaultManager().isVaultSetuped()) {
                String playerGroup = Expansions.getVaultManager().getPlayerGroup(player);
                if (getCommandLimiterSettings().getGlobalBlockedCommands().containsKey(playerGroup)) {
                    World world = player.getWorld();
                    
                    
                    if (getCommandLimiterSettings().getPerWorldGroups().contains(playerGroup) && getCommandLimiterSettings().getPerGroupWorlds().get(getCommandLimiterSettings().getPerWorldGroups().indexOf(playerGroup)) == world) {
                        int indexOf = getCommandLimiterSettings().getPerWorldGroups().indexOf(playerGroup);
                        if (getCommandLimiterSettings().isPerWorldUseAsWhiteList()) {
                            return getCommandLimiterSettings().getPerGroupCommands().get(indexOf);
                        } else {
                            newCompletions.removeAll(getCommandLimiterSettings().getPerGroupCommands().get(indexOf));
                            return newCompletions;
                        }
                    }
                    if (getCommandLimiterSettings().isGlobalUseAsWhiteList()) {
                        return getCommandLimiterSettings().getGlobalBlockedCommands().get(playerGroup);
                    } else {
                        newCompletions.removeAll(getCommandLimiterSettings().getGlobalBlockedCommands().get(playerGroup));
                        return newCompletions;
                    }
                }
            }
            if (Expansions.getLuckPermsManager().isLuckPermsSetuped()) {
                if (getCommandLimiterSettings().getGlobalBlockedCommands().containsKey(Expansions.getLuckPermsManager().getPlayerGroup(player))) {
                    String playerGroup = Expansions.getLuckPermsManager().getPlayerGroup(player);
                    World world = player.getWorld();
                    
                    
                    if (getCommandLimiterSettings().getPerWorldGroups().contains(playerGroup) && getCommandLimiterSettings().getPerGroupWorlds().get(getCommandLimiterSettings().getPerWorldGroups().indexOf(playerGroup)) == world) {
                        int indexOf = getCommandLimiterSettings().getPerWorldGroups().indexOf(playerGroup);
                        if (getCommandLimiterSettings().isPerWorldUseAsWhiteList()) {
                            return getCommandLimiterSettings().getPerGroupCommands().get(indexOf);
                        } else {
                            newCompletions.removeAll(getCommandLimiterSettings().getPerGroupCommands().get(indexOf));
                            return newCompletions;
                        }
                    }
                    if (getCommandLimiterSettings().isGlobalUseAsWhiteList()) {
                        return getCommandLimiterSettings().getGlobalBlockedCommands().get(playerGroup);
                    } else {
                        newCompletions.removeAll(getCommandLimiterSettings().getGlobalBlockedCommands().get(playerGroup));
                        return newCompletions;
                    }
                }
            }
        }
        World world = player.getWorld();
        if (getCommandLimiterSettings().getPerWorldGroups().contains("global") && getCommandLimiterSettings().getPerGroupWorlds().get(getCommandLimiterSettings().getPerWorldGroups().indexOf("global")) == world) {
            int indexOf = getCommandLimiterSettings().getPerWorldGroups().indexOf("global");
            if (getCommandLimiterSettings().isPerWorldUseAsWhiteList()) {
                return getCommandLimiterSettings().getPerGroupCommands().get(indexOf);
            } else {
                newCompletions.removeAll(getCommandLimiterSettings().getPerGroupCommands().get(indexOf));
                return newCompletions;
            }
        }
        if (getCommandLimiterSettings().isGlobalUseAsWhiteList()) {
            return getCommandLimiterSettings().getGlobalBlockedCommands().get("global");
        } else {
            newCompletions.removeAll(getCommandLimiterSettings().getGlobalBlockedCommands().get("global"));
            return newCompletions;
        }
    }

    public String replaceMinecraftCommand(String command) {
        String[] args = command.replace("/", "").split(" ");
        if(args[0].equalsIgnoreCase("minecraft:ban")) return command.replace(args[0], "ban");
        if(args[0].equalsIgnoreCase("minecraft:kick")) return command.replace(args[0], "kick");
        if(args[0].equalsIgnoreCase("minecraft:ban-ip")) return command.replace(args[0], "banip");
        if(args[0].equalsIgnoreCase("minecraft:banlist")) return command.replace(args[0], "banlist");
        if(args[0].equalsIgnoreCase("minecraft:pardon")) return command.replace(args[0], "unban");
        if(args[0].equalsIgnoreCase("minecraft:pardon-ip")) return command.replace(args[0], "unbanip");
        return command.replace("/", "");
    }

    public boolean preventReloadCommand(CommandSender sender, String command) {
        if(getCommandLimiterSettings().isDisableSpigotReloadCommand()) {
            if(sender instanceof ConsoleCommandSender) {
                if(command.startsWith("reload")) {
                    sender.sendMessage(setColors("&4This command is disabled by the FunctionalServerControl plugin!"));
                    sender.sendMessage(setColors("&cIt is really unsafe to use it for the stability of the server!"));
                    sender.sendMessage(setColors("&cIf you still want to enable it, you can do it in the commands-limiter.yml file by setting 'disable-reload-command' to false"));
                    return true;
                }
            }
            if(sender instanceof Player) {
                if(sender.hasPermission("bukkit.command.reload")) {
                    if(command.replace("/", "").startsWith("reload")) {
                        sender.sendMessage(setColors("&4This command is disabled by the FunctionalServerControl plugin!"));
                        sender.sendMessage(setColors("&cIt is really unsafe to use it for the stability of the server!"));
                        sender.sendMessage(setColors("&cIf you still want to enable it, you can do it in the commands-limiter.yml file by setting 'disable-reload-command' to false"));
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void notifyAdminsAboutBlockedCommand(Player player, String command) {
        BaseManager.getBaseManager().updatePlayerStatsInfo(player, StatsType.Player.BLOCKED_COMMANDS_USED);
        if(getCommandLimiterSettings().isNotifyAdmins()) {
                Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.blocked-command").replace("%1$f", player.getName()).replace("%2$f", command)));
                for(Player admin : Bukkit.getOnlinePlayers()) {
                    if(admin.hasPermission("functionalservercontrol.notification.blocked-command")) {
                        if(getConfigSettings().isServerSupportsHoverEvents()) {
                            if(getConfigSettings().isButtonsOnNotifications()) {
                                if(getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("MD5")) {
                                    admin.spigot().sendMessage(
                                            MD5TextUtils.appendTwo(
                                                    MD5TextUtils.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.blocked-command").replace("%1$f", player.getName()).replace("%2$f", command)), player),
                                                    MD5TextUtils.addPunishmentButtons(admin, player.getName())));
                                    continue;
                                }
                                if(getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("ADVENTURE")) {
                                    admin.sendMessage(
                                            AdventureApiUtils.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.blocked-command").replace("%1$f", player.getName()).replace("%2$f", command)), player).append(AdventureApiUtils.addPunishmentButtons(admin, player.getName())));
                                    continue;
                                }
                            } else {
                                if(getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("MD5")) {
                                    admin.spigot().sendMessage(
                                            MD5TextUtils.createPlayerInfoHoverText( setColors(getFileAccessor().getLang().getString("other.notifications.blocked-command").replace("%1$f", player.getName()).replace("%2$f", command)), player));
                                    continue;
                                }
                                if(getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("ADVENTURE")) {
                                    admin.sendMessage(
                                            AdventureApiUtils.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.blocked-command").replace("%1$f", player.getName()).replace("%2$f", command)), player));
                                    continue;
                                }
                            }
                        } else {
                            admin.sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.blocked-command").replace("%1$f", player.getName()).replace("%2$f", command)));
                        }
                    }
                }
        }
    }

}
