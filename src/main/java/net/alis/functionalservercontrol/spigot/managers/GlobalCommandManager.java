package net.alis.functionalservercontrol.spigot.managers;

import net.alis.functionalservercontrol.api.FunctionalApi;
import net.alis.functionalservercontrol.api.enums.StatsType;
import net.alis.functionalservercontrol.api.interfaces.FunctionalPlayer;
import net.alis.functionalservercontrol.libraries.org.apache.commons.lang3.StringUtils;
import net.alis.functionalservercontrol.spigot.additional.misc.OtherUtils;
import net.alis.functionalservercontrol.spigot.additional.textcomponents.Component;
import net.alis.functionalservercontrol.spigot.dependencies.Expansions;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.util.ArrayList;
import java.util.List;

import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.*;
import static net.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;
import static net.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

public class GlobalCommandManager {
    public boolean isPlayerCanUseCommand(FunctionalPlayer player, String command) {
        String finalCommand = command.split(" ")[0];
        if(command.contains(":")) {
            if(getCommandLimiterSettings().isBlockSyntaxCommand()) {
                if(!getCommandLimiterSettings().getWhitelistedSyntaxCommands().contains(finalCommand)) {
                    if(player.hasPermission("functionalservercontrol.commands.bypass") || player.hasPermission("functionalservercontrol.commands.syntax.bypass")){
                        return true;
                    } else {
                        player.message(setColors(getCommandLimiterSettings().getSyntaxDenyMessage()));
                        TaskManager.preformAsync(() -> notifyAdminsAboutBlockedCommand(player, finalCommand));
                        return false;
                    }
                }
            }
        }
        if(player.hasPermission("functionalservercontrol.commands.bypass") || player.hasPermission("functionalservercontrol.commands." + finalCommand.replace("/", "") + ".bypass")) return true;
        if(getCommandLimiterSettings().isUseGroups()) {
            if (Expansions.getVaultManager().isVaultSetuped()) {
                String playerGroup = Expansions.getVaultManager().getPlayerGroup(player);
                if (getCommandLimiterSettings().getGlobalBlockedCommands().containsKey(playerGroup)) {
                    World world = player.world();
                    if (getCommandLimiterSettings().getPerWorldGroups().contains(playerGroup) && getCommandLimiterSettings().getPerGroupWorlds().get(getCommandLimiterSettings().getPerWorldGroups().indexOf(playerGroup)) == world) {
                        int indexOf = getCommandLimiterSettings().getPerWorldGroups().indexOf(playerGroup);
                        if(getCommandLimiterSettings().isPerWorldUseAsWhiteList()) {
                            for(String cmd : getCommandLimiterSettings().getPerGroupCommands().get(indexOf)) {
                                if(OtherUtils.getCommandCheckMode(cmd).equalsIgnoreCase("first_arg")) {
                                    if(!cmd.split("\\[")[0].equalsIgnoreCase(finalCommand)) {
                                        player.message(setColors(getCommandLimiterSettings().getPerWorldDenyMessage()));
                                        TaskManager.preformAsync(() -> notifyAdminsAboutBlockedCommand(player, finalCommand));
                                        return false;
                                    }
                                } else {
                                    List<String> l = new ArrayList<>();
                                    for(String c : getCommandLimiterSettings().getPerGroupCommands().get(indexOf)) {
                                        l.add(c.replace("[" + StringUtils.substringBetween(c, "[", "]") + "]", ""));
                                    }
                                    Bukkit.getConsoleSender().sendMessage("AAL: " + l);
                                    Bukkit.getConsoleSender().sendMessage("CMD: " + command);
                                    if(!l.contains(command)) {
                                        player.message(setColors(getCommandLimiterSettings().getPerWorldDenyMessage()));
                                        TaskManager.preformAsync(() -> notifyAdminsAboutBlockedCommand(player, command));
                                        return false;
                                    }
                                }
                            }
                        } else {
                            for(String cmd : getCommandLimiterSettings().getPerGroupCommands().get(indexOf)) {
                                if(OtherUtils.getCommandCheckMode(cmd).equalsIgnoreCase("first_arg")) {
                                    if(cmd.split("\\[")[0].equalsIgnoreCase(finalCommand)) {
                                        player.message(setColors(getCommandLimiterSettings().getPerWorldDenyMessage()));
                                        TaskManager.preformAsync(() -> notifyAdminsAboutBlockedCommand(player, finalCommand));
                                        return false;
                                    }
                                } else {
                                    List<String> l = new ArrayList<>();
                                    for(String c : getCommandLimiterSettings().getPerGroupCommands().get(indexOf)) {
                                        l.add(c.replace("[" + StringUtils.substringBetween(c, "[", "]") + "]", ""));
                                    }
                                    Bukkit.getConsoleSender().sendMessage("AAL: " + l);
                                    Bukkit.getConsoleSender().sendMessage("CMD: " + command);
                                    if(l.contains(command)) {
                                        player.message(setColors(getCommandLimiterSettings().getPerWorldDenyMessage()));
                                        TaskManager.preformAsync(() -> notifyAdminsAboutBlockedCommand(player, command));
                                        return false;
                                    }
                                }
                            }
                        }
                    }
                    if(getCommandLimiterSettings().isGlobalUseAsWhiteList()) {
                        for(String cmd : getCommandLimiterSettings().getGlobalBlockedCommands().get(playerGroup)) {
                            if(OtherUtils.getCommandCheckMode(cmd).equalsIgnoreCase("first_arg")) {
                                if(!cmd.split("\\[")[0].equalsIgnoreCase(finalCommand)) {
                                    player.message(setColors(getCommandLimiterSettings().getGlobalDenyMessage()));
                                    TaskManager.preformAsync(() -> notifyAdminsAboutBlockedCommand(player, finalCommand));
                                    return false;
                                }
                            } else {
                                List<String> l = new ArrayList<>();
                                for(String c : getCommandLimiterSettings().getGlobalBlockedCommands().get(playerGroup)) {
                                    l.add(c.replace("[" + StringUtils.substringBetween(c, "[", "]") + "]", ""));
                                }
                                Bukkit.getConsoleSender().sendMessage("AAL: " + l);
                                    Bukkit.getConsoleSender().sendMessage("CMD: " + command);
                                if(!l.contains(command)) {
                                    player.message(setColors(getCommandLimiterSettings().getPerWorldDenyMessage()));
                                    TaskManager.preformAsync(() -> notifyAdminsAboutBlockedCommand(player, command));
                                    return false;
                                }
                            }
                        }
                    } else {
                        for(String cmd : getCommandLimiterSettings().getGlobalBlockedCommands().get(playerGroup)) {
                            if(OtherUtils.getCommandCheckMode(cmd).equalsIgnoreCase("first_arg")) {
                                if(cmd.split("\\[")[0].equalsIgnoreCase(finalCommand)) {
                                    player.message(setColors(getCommandLimiterSettings().getGlobalDenyMessage()));
                                    TaskManager.preformAsync(() -> notifyAdminsAboutBlockedCommand(player, finalCommand));
                                    return false;
                                }
                            } else {
                                List<String> l = new ArrayList<>();
                                for(String c : getCommandLimiterSettings().getGlobalBlockedCommands().get(playerGroup)) {
                                    l.add(c.replace("[" + StringUtils.substringBetween(c, "[", "]") + "]", ""));
                                }
                                Bukkit.getConsoleSender().sendMessage("AAL: " + l);
                                    Bukkit.getConsoleSender().sendMessage("CMD: " + command);
                                if(l.contains(command)) {
                                    player.message(setColors(getCommandLimiterSettings().getPerWorldDenyMessage()));
                                    TaskManager.preformAsync(() -> notifyAdminsAboutBlockedCommand(player, command));
                                    return false;
                                }
                            }
                        }
                    }
                    return true;
                }
            }
            if (Expansions.getLuckPermsManager().isLuckPermsSetuped()) {
                if (getCommandLimiterSettings().getGlobalBlockedCommands().containsKey(Expansions.getLuckPermsManager().getPlayerGroup(player))) {
                    String playerGroup = Expansions.getLuckPermsManager().getPlayerGroup(player);
                    if (getCommandLimiterSettings().getGlobalBlockedCommands().containsKey(playerGroup)) {
                        World world = player.world();
                        if (getCommandLimiterSettings().getPerWorldGroups().contains(playerGroup) && getCommandLimiterSettings().getPerGroupWorlds().get(getCommandLimiterSettings().getPerWorldGroups().indexOf(playerGroup)) == world) {
                            int indexOf = getCommandLimiterSettings().getPerWorldGroups().indexOf(playerGroup);
                            if(getCommandLimiterSettings().isPerWorldUseAsWhiteList()) {
                                for(String cmd : getCommandLimiterSettings().getPerGroupCommands().get(indexOf)) {
                                    if(OtherUtils.getCommandCheckMode(cmd).equalsIgnoreCase("first_arg")) {
                                        if(!cmd.split("\\[")[0].equalsIgnoreCase(finalCommand)) {
                                            player.message(setColors(getCommandLimiterSettings().getPerWorldDenyMessage()));
                                            TaskManager.preformAsync(() -> notifyAdminsAboutBlockedCommand(player, finalCommand));
                                            return false;
                                        }
                                    } else {
                                        List<String> l = new ArrayList<>();
                                        for(String c : getCommandLimiterSettings().getPerGroupCommands().get(indexOf)) {
                                            l.add(c.replace("[" + StringUtils.substringBetween(c, "[", "]") + "]", ""));
                                        }
                                        Bukkit.getConsoleSender().sendMessage("AAL: " + l);
                                    Bukkit.getConsoleSender().sendMessage("CMD: " + command);
                                        if(!l.contains(command)) {
                                            player.message(setColors(getCommandLimiterSettings().getPerWorldDenyMessage()));
                                            TaskManager.preformAsync(() -> notifyAdminsAboutBlockedCommand(player, command));
                                            return false;
                                        }
                                    }
                                }
                            } else {
                                for(String cmd : getCommandLimiterSettings().getPerGroupCommands().get(indexOf)) {
                                    if(OtherUtils.getCommandCheckMode(cmd).equalsIgnoreCase("first_arg")) {
                                        if(cmd.split("\\[")[0].equalsIgnoreCase(finalCommand)) {
                                            player.message(setColors(getCommandLimiterSettings().getPerWorldDenyMessage()));
                                            TaskManager.preformAsync(() -> notifyAdminsAboutBlockedCommand(player, finalCommand));
                                            return false;
                                        }
                                    } else {
                                        List<String> l = new ArrayList<>();
                                        for(String c : getCommandLimiterSettings().getPerGroupCommands().get(indexOf)) {
                                            l.add(c.replace("[" + StringUtils.substringBetween(c, "[", "]") + "]", ""));
                                        }
                                        Bukkit.getConsoleSender().sendMessage("AAL: " + l);
                                    Bukkit.getConsoleSender().sendMessage("CMD: " + command);
                                        if(l.contains(command)) {
                                            player.message(setColors(getCommandLimiterSettings().getPerWorldDenyMessage()));
                                            TaskManager.preformAsync(() -> notifyAdminsAboutBlockedCommand(player, command));
                                            return false;
                                        }
                                    }
                                }
                            }
                        }
                        if(getCommandLimiterSettings().isGlobalUseAsWhiteList()) {
                            for(String cmd : getCommandLimiterSettings().getGlobalBlockedCommands().get(playerGroup)) {
                                if(OtherUtils.getCommandCheckMode(cmd).equalsIgnoreCase("first_arg")) {
                                    if(!cmd.split("\\[")[0].equalsIgnoreCase(finalCommand)) {
                                        player.message(setColors(getCommandLimiterSettings().getGlobalDenyMessage()));
                                        TaskManager.preformAsync(() -> notifyAdminsAboutBlockedCommand(player, finalCommand));
                                        return false;
                                    }
                                } else {
                                    List<String> l = new ArrayList<>();
                                    for(String c : getCommandLimiterSettings().getGlobalBlockedCommands().get(playerGroup)) {
                                        l.add(c.replace("[" + StringUtils.substringBetween(c, "[", "]") + "]", ""));
                                    }
                                    Bukkit.getConsoleSender().sendMessage("AAL: " + l);
                                    Bukkit.getConsoleSender().sendMessage("CMD: " + command);
                                    if(!l.contains(command)) {
                                        player.message(setColors(getCommandLimiterSettings().getPerWorldDenyMessage()));
                                        TaskManager.preformAsync(() -> notifyAdminsAboutBlockedCommand(player, command));
                                        return false;
                                    }
                                }
                            }
                        } else {
                            for(String cmd : getCommandLimiterSettings().getGlobalBlockedCommands().get(playerGroup)) {
                                if(OtherUtils.getCommandCheckMode(cmd).equalsIgnoreCase("first_arg")) {
                                    if(cmd.split("\\[")[0].equalsIgnoreCase(finalCommand)) {
                                        player.message(setColors(getCommandLimiterSettings().getGlobalDenyMessage()));
                                        TaskManager.preformAsync(() -> notifyAdminsAboutBlockedCommand(player, finalCommand));
                                        return false;
                                    }
                                } else {
                                    List<String> l = new ArrayList<>();
                                    for(String c : getCommandLimiterSettings().getGlobalBlockedCommands().get(playerGroup)) {
                                        l.add(c.replace("[" + StringUtils.substringBetween(c, "[", "]") + "]", ""));
                                    }
                                    Bukkit.getConsoleSender().sendMessage("AAL: " + l);
                                    Bukkit.getConsoleSender().sendMessage("CMD: " + command);
                                    if(l.contains(command)) {
                                        player.message(setColors(getCommandLimiterSettings().getPerWorldDenyMessage()));
                                        TaskManager.preformAsync(() -> notifyAdminsAboutBlockedCommand(player, command));
                                        return false;
                                    }
                                }
                            }
                        }
                        return true;
                    }
                }
            }
        }
        World world = player.world();
        if(getCommandLimiterSettings().getPerWorldGroups().contains("global") && getCommandLimiterSettings().getPerGroupWorlds().get(getCommandLimiterSettings().getPerWorldGroups().indexOf("global")) == world) {
            int indexOf = getCommandLimiterSettings().getPerGroupWorlds().indexOf(world);
            if(getCommandLimiterSettings().isPerWorldUseAsWhiteList()) {
                for(String cmd : getCommandLimiterSettings().getPerGroupCommands().get(indexOf)) {
                    if(OtherUtils.getCommandCheckMode(cmd).equalsIgnoreCase("first_arg")) {
                        if(!cmd.split("\\[")[0].equalsIgnoreCase(finalCommand)) {
                            player.message(setColors(getCommandLimiterSettings().getPerWorldDenyMessage()));
                            TaskManager.preformAsync(() -> notifyAdminsAboutBlockedCommand(player, finalCommand));
                            return false;
                        }
                    } else {
                        List<String> l = new ArrayList<>();
                        for(String c : getCommandLimiterSettings().getPerGroupCommands().get(indexOf)) {
                            l.add(c.replace("[" + StringUtils.substringBetween(c, "[", "]") + "]", ""));
                        }
                        Bukkit.getConsoleSender().sendMessage("AAL: " + l);
                                    Bukkit.getConsoleSender().sendMessage("CMD: " + command);
                        if(!l.contains(command)) {
                            player.message(setColors(getCommandLimiterSettings().getPerWorldDenyMessage()));
                            TaskManager.preformAsync(() -> notifyAdminsAboutBlockedCommand(player, command));
                            return false;
                        }
                    }
                }
            } else {
                for(String cmd : getCommandLimiterSettings().getPerGroupCommands().get(indexOf)) {
                    if(OtherUtils.getCommandCheckMode(cmd).equalsIgnoreCase("first_arg")) {
                        if(cmd.split("\\[")[0].equalsIgnoreCase(finalCommand)) {
                            player.message(setColors(getCommandLimiterSettings().getPerWorldDenyMessage()));
                            TaskManager.preformAsync(() -> notifyAdminsAboutBlockedCommand(player, finalCommand));
                            return false;
                        }
                    } else {
                        List<String> l = new ArrayList<>();
                        for(String c : getCommandLimiterSettings().getPerGroupCommands().get(indexOf)) {
                            l.add(c.replace("[" + StringUtils.substringBetween(c, "[", "]") + "]", ""));
                        }
                        Bukkit.getConsoleSender().sendMessage("AAL: " + l);
                                    Bukkit.getConsoleSender().sendMessage("CMD: " + command);
                        if(l.contains(command)) {
                            player.message(setColors(getCommandLimiterSettings().getPerWorldDenyMessage()));
                            TaskManager.preformAsync(() -> notifyAdminsAboutBlockedCommand(player, command));
                            return false;
                        }
                    }
                }
            }
        }
        if(getCommandLimiterSettings().isGlobalUseAsWhiteList()) {
            for(String cmd : getCommandLimiterSettings().getGlobalBlockedCommands().get("global")) {
                if(OtherUtils.getCommandCheckMode(cmd).equalsIgnoreCase("first_arg")) {
                    if(!cmd.split("\\[")[0].equalsIgnoreCase(finalCommand)) {
                        player.message(setColors(getCommandLimiterSettings().getGlobalDenyMessage()));
                        TaskManager.preformAsync(() -> notifyAdminsAboutBlockedCommand(player, finalCommand));
                        return false;
                    }
                } else {
                    List<String> l = new ArrayList<>();
                    for(String c : getCommandLimiterSettings().getGlobalBlockedCommands().get("global")) {
                        l.add(c.replace("[" + StringUtils.substringBetween(c, "[", "]") + "]", ""));
                    }
                    Bukkit.getConsoleSender().sendMessage("AAL: " + l);
                                    Bukkit.getConsoleSender().sendMessage("CMD: " + command);
                    if(!l.contains(command)) {
                        player.message(setColors(getCommandLimiterSettings().getPerWorldDenyMessage()));
                        TaskManager.preformAsync(() -> notifyAdminsAboutBlockedCommand(player, command));
                        return false;
                    }
                }
            }
        } else {
            for(String cmd : getCommandLimiterSettings().getGlobalBlockedCommands().get("global")) {
                if(OtherUtils.getCommandCheckMode(cmd).equalsIgnoreCase("first_arg")) {
                    if(cmd.split("\\[")[0].equalsIgnoreCase(finalCommand)) {
                        player.message(setColors(getCommandLimiterSettings().getGlobalDenyMessage()));
                        TaskManager.preformAsync(() -> notifyAdminsAboutBlockedCommand(player, finalCommand));
                        return false;
                    }
                } else {
                    List<String> l = new ArrayList<>();
                    for(String c : getCommandLimiterSettings().getGlobalBlockedCommands().get("global")) {
                        l.add(c.replace("[" + StringUtils.substringBetween(c, "[", "]") + "]", ""));
                    }
                    Bukkit.getConsoleSender().sendMessage("AAL: " + l);
                                    Bukkit.getConsoleSender().sendMessage("CMD: " + command);
                    if(l.contains(command)) {
                        player.message(setColors(getCommandLimiterSettings().getPerWorldDenyMessage()));
                        TaskManager.preformAsync(() -> notifyAdminsAboutBlockedCommand(player, command));
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
                String playerGroup = Expansions.getVaultManager().getPlayerGroup((FunctionalPlayer) player);
                if (getCommandLimiterSettings().getPerGroupCompletions().containsKey(playerGroup) && getCommandLimiterSettings().getPerGroupCompletions().get(playerGroup).containsKey(command)) {
                    return getCommandLimiterSettings().getPerGroupCompletions().get(playerGroup).get(command);
                }
                if(getCommandLimiterSettings().getGlobalCompletions().containsKey(command)) {
                    return getCommandLimiterSettings().getGlobalCompletions().get(command);
                }
            }
            if(Expansions.getLuckPermsManager().isLuckPermsSetuped()) {
                String playerGroup = Expansions.getLuckPermsManager().getPlayerGroup((FunctionalPlayer) player);
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

    public List<String> getCommandsToFullyHide(FunctionalPlayer player, List<String> trueCompletions) {
        List<String> newCompletions = new ArrayList<>(trueCompletions);
        if (getConfigSettings().hideMainCommand()) {
            newCompletions.removeIf(cmd -> cmd.split("\\[")[0].equalsIgnoreCase("fsc") || cmd.split("\\[")[0].equalsIgnoreCase("functionalservercontrol") || cmd.split("\\[")[0].equalsIgnoreCase("fscontrol"));
        }
        if(getCommandLimiterSettings().isBlockSyntaxCommand()) {
            newCompletions.removeIf(cmd -> cmd.split("\\[")[0].equalsIgnoreCase(":"));
        }
        if (getCommandLimiterSettings().isUseGroups()) {
            if (Expansions.getVaultManager().isVaultSetuped()) {
                String playerGroup = Expansions.getVaultManager().getPlayerGroup(player);
                if (getCommandLimiterSettings().getGlobalBlockedCommands().containsKey(playerGroup)) {
                    World world = player.world();
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
                    World world = player.world();
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
        World world = player.world();
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
            if(sender instanceof FunctionalPlayer) {
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

    private void notifyAdminsAboutBlockedCommand(FunctionalPlayer player, String command) {
        BaseManager.getBaseManager().updatePlayerStatsInfo(player.getFunctionalId(), StatsType.Player.BLOCKED_COMMANDS_USED);
        if(getCommandLimiterSettings().isNotifyAdmins()) {
            Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.blocked-command").replace("%1$f", player.nickname()).replace("%2$f", command)));
            for(FunctionalPlayer admin : FunctionalApi.getOnlinePlayers()) {
                if(admin.hasPermission("functionalservercontrol.notification.blocked-command")) {
                    if(getConfigSettings().isButtonsOnNotifications()) {
                        admin.expansion().message(
                                        Component.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.blocked-command").replace("%1$f", player.nickname()).replace("%2$f", command)), player)
                                                .append(Component.addPunishmentButtons(admin, player.nickname())).translateDefaultColorCodes()
                        );
                        continue;
                    } else {
                        admin.expansion().message(
                                Component.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.blocked-command").replace("%1$f", player.nickname()).replace("%2$f", command)), player).translateDefaultColorCodes());
                        continue;
                    }
                }
            }
        }
    }
}
