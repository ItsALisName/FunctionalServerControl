package by.alis.functionalservercontrol.spigot.Managers;

import by.alis.functionalservercontrol.spigot.Additional.Misc.TemporaryCache;
import by.alis.functionalservercontrol.spigot.Additional.Misc.TextUtils;
import by.alis.functionalservercontrol.spigot.Expansions.Expansions;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getCommandLimiterSettings;
import static by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalservercontrol.spigot.Additional.Misc.TextUtils.setColors;

public class PlayerCommandManager {
    public boolean isPlayerCanUseCommand(Player player, String command) {
        String finalCommand = command.split(" ")[0];
        if(command.contains(":")) {
            if(getCommandLimiterSettings().isBlockSyntaxCommand()) {
                if(!getCommandLimiterSettings().getWhitelistedSyntaxCommands().contains(finalCommand)) {
                    if(player.hasPermission("functionalservercontrol.commands.bypass") || player.hasPermission("functionalservercontrol.commands.syntax.bypass")){
                        return true;
                    } else {
                        player.sendMessage(setColors(getCommandLimiterSettings().getSyntaxDenyMessage()));
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
                        if (getCommandLimiterSettings().getPerWorldBlockedCommands().containsKey(world)) {
                            if (getCommandLimiterSettings().isPerWorldUseAsWhiteList() ? !getCommandLimiterSettings().getPerWorldBlockedCommands().get(world).get(playerGroup).contains(finalCommand) : getCommandLimiterSettings().getPerWorldBlockedCommands().get(world).get(playerGroup).contains(finalCommand)) {
                                if(player.hasPermission("functionalservercontrol.commands.bypass") || player.hasPermission("functionalservercontrol.commands." + command + ".bypass")){
                                    return true;
                                } else {
                                    player.sendMessage(setColors(getCommandLimiterSettings().getGlobalDenyMessage()));
                                    return false;
                                }
                            }
                        }
                        if (getCommandLimiterSettings().isGlobalUseAsWhiteList() ? !getCommandLimiterSettings().getGlobalBlockedCommands().get(playerGroup).contains(finalCommand) : getCommandLimiterSettings().getGlobalBlockedCommands().get(playerGroup).contains(finalCommand)) {
                            if(player.hasPermission("functionalservercontrol.commands.bypass") || player.hasPermission("functionalservercontrol.commands." + command + ".bypass")){
                                return true;
                            } else {
                                player.sendMessage(setColors(getCommandLimiterSettings().getGlobalDenyMessage()));
                                return false;
                            }
                        }
                    }
                    if (getCommandLimiterSettings().getCheckMode().equalsIgnoreCase("all_args")) {
                        if (getCommandLimiterSettings().getPerWorldBlockedCommands().containsKey(player.getWorld())) {
                            World world = player.getWorld();
                            if (getCommandLimiterSettings().isPerWorldUseAsWhiteList() ? !getCommandLimiterSettings().getPerWorldBlockedCommands().get(world).get(playerGroup).contains(command) : getCommandLimiterSettings().getPerWorldBlockedCommands().get(world).get(playerGroup).contains(command)) {
                                if(player.hasPermission("functionalservercontrol.commands.bypass") || player.hasPermission("functionalservercontrol.commands." + command + ".bypass")){
                                    return true;
                                } else {
                                    player.sendMessage(setColors(getCommandLimiterSettings().getPerWorldDenyMessage()));
                                    return false;
                                }
                            }
                        }
                        if (getCommandLimiterSettings().isGlobalUseAsWhiteList() ? !getCommandLimiterSettings().getGlobalBlockedCommands().get(playerGroup).contains(command) : getCommandLimiterSettings().getGlobalBlockedCommands().get(playerGroup).contains(command)) {
                            if(player.hasPermission("functionalservercontrol.commands.bypass") || player.hasPermission("functionalservercontrol.commands." + command + ".bypass")){
                                return true;
                            } else {
                                player.sendMessage(setColors(getCommandLimiterSettings().getGlobalDenyMessage()));
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
                        if (getCommandLimiterSettings().getPerWorldBlockedCommands().containsKey(world)) {
                            if (getCommandLimiterSettings().isPerWorldUseAsWhiteList() ? !getCommandLimiterSettings().getPerWorldBlockedCommands().get(world).get(playerGroup).contains(finalCommand) : getCommandLimiterSettings().getPerWorldBlockedCommands().get(world).get(playerGroup).contains(finalCommand)) {
                                if(player.hasPermission("functionalservercontrol.commands.bypass") || player.hasPermission("functionalservercontrol.commands." + command + ".bypass")) {
                                    return true;
                                } else {
                                    player.sendMessage(setColors(getCommandLimiterSettings().getGlobalDenyMessage()));
                                    return false;
                                }
                            }
                        }
                        if (getCommandLimiterSettings().isGlobalUseAsWhiteList() ? !getCommandLimiterSettings().getGlobalBlockedCommands().get(playerGroup).contains(finalCommand) : getCommandLimiterSettings().getGlobalBlockedCommands().get(playerGroup).contains(finalCommand)) {
                            if(player.hasPermission("functionalservercontrol.commands.bypass") || player.hasPermission("functionalservercontrol.commands." + command + ".bypass")){
                                return true;
                            } else {
                                player.sendMessage(setColors(getCommandLimiterSettings().getGlobalDenyMessage()));
                                return false;
                            }
                        }
                    }
                    if (getCommandLimiterSettings().getCheckMode().equalsIgnoreCase("all_args")) {
                        if (getCommandLimiterSettings().isGlobalUseAsWhiteList() ? !getCommandLimiterSettings().getGlobalBlockedCommands().get(playerGroup).contains(command) : getCommandLimiterSettings().getGlobalBlockedCommands().get(playerGroup).contains(command)) {
                            if(player.hasPermission("functionalservercontrol.commands.bypass") || player.hasPermission("functionalservercontrol.commands." + command + ".bypass")){
                                return true;
                            } else {
                                player.sendMessage(setColors(getCommandLimiterSettings().getGlobalDenyMessage()));
                                return false;
                            }
                        }
                        if (getCommandLimiterSettings().getPerWorldBlockedCommands().containsKey(player.getWorld())) {
                            World world = player.getWorld();
                            if (getCommandLimiterSettings().isPerWorldUseAsWhiteList() ? !getCommandLimiterSettings().getPerWorldBlockedCommands().get(world).get(playerGroup).contains(command) : getCommandLimiterSettings().getPerWorldBlockedCommands().get(world).get(playerGroup).contains(command)) {
                                if(player.hasPermission("functionalservercontrol.commands.bypass") || player.hasPermission("functionalservercontrol.commands." + command + ".bypass")){
                                    return true;
                                } else {
                                    player.sendMessage(setColors(getCommandLimiterSettings().getPerWorldDenyMessage()));
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        }
        if(getCommandLimiterSettings().getCheckMode().equalsIgnoreCase("first_arg")) {
            if(getCommandLimiterSettings().getPerWorldBlockedCommands().containsKey(player.getWorld())) {
                World world = player.getWorld();
                if(getCommandLimiterSettings().isPerWorldUseAsWhiteList()) {
                    if(!getCommandLimiterSettings().getPerWorldBlockedCommands().get(world).get("global").contains(finalCommand)) {
                        if (player.hasPermission("functionalservercontrol.commands.bypass") || player.hasPermission("functionalservercontrol.commands." + command + ".bypass")) {
                            return true;
                        } else {
                            player.sendMessage(setColors(getCommandLimiterSettings().getGlobalDenyMessage()));
                            return false;
                        }
                    }
                } else {
                    if(getCommandLimiterSettings().getPerWorldBlockedCommands().get(world).get("global").contains(finalCommand)) {
                        if (player.hasPermission("functionalservercontrol.commands.bypass") || player.hasPermission("functionalservercontrol.commands." + command + ".bypass")) {
                            return true;
                        } else {
                            player.sendMessage(setColors(getCommandLimiterSettings().getGlobalDenyMessage()));
                            return false;
                        }
                    }
                }
            }
            if(getCommandLimiterSettings().isGlobalUseAsWhiteList()) {
                if(!getCommandLimiterSettings().getGlobalBlockedCommands().get("global").contains(finalCommand)) {
                    if (player.hasPermission("functionalservercontrol.commands.bypass") || player.hasPermission("functionalservercontrol.commands." + command + ".bypass")) {
                        return true;
                    } else {
                        player.sendMessage(setColors(getCommandLimiterSettings().getGlobalDenyMessage()));
                        return false;
                    }
                }
            } else {
                if(getCommandLimiterSettings().getGlobalBlockedCommands().get("global").contains(finalCommand)) {
                    if (player.hasPermission("functionalservercontrol.commands.bypass") || player.hasPermission("functionalservercontrol.commands." + command + ".bypass")) {
                        return true;
                    } else {
                        player.sendMessage(setColors(getCommandLimiterSettings().getGlobalDenyMessage()));
                        return false;
                    }
                }
            }
        }
        if(getCommandLimiterSettings().getCheckMode().equalsIgnoreCase("all_args")) {
            if(getCommandLimiterSettings().getPerWorldBlockedCommands().containsKey(player.getWorld())) {
                World world = player.getWorld();
                if (getCommandLimiterSettings().isPerWorldUseAsWhiteList()) {
                    if(!getCommandLimiterSettings().getPerWorldBlockedCommands().get(world).get("global").contains(command)) {
                        if (player.hasPermission("functionalservercontrol.commands.bypass") || player.hasPermission("functionalservercontrol.commands." + command + ".bypass")) {
                            return true;
                        } else {
                            player.sendMessage(setColors(getCommandLimiterSettings().getPerWorldDenyMessage()));
                            return false;
                        }
                    }
                } else {
                    if(getCommandLimiterSettings().getPerWorldBlockedCommands().get(world).get("global").contains(command)) {
                        if (player.hasPermission("functionalservercontrol.commands.bypass") || player.hasPermission("functionalservercontrol.commands." + command + ".bypass")) {
                            return true;
                        } else {
                            player.sendMessage(setColors(getCommandLimiterSettings().getPerWorldDenyMessage()));
                            return false;
                        }
                    }
                }
            }
            if (getCommandLimiterSettings().isGlobalUseAsWhiteList()) {
                if(!getCommandLimiterSettings().getGlobalBlockedCommands().get("global").contains(command)) {
                    if (player.hasPermission("functionalservercontrol.commands.bypass") || player.hasPermission("functionalservercontrol.commands." + command + ".bypass")) {
                        return true;
                    } else {
                        player.sendMessage(setColors(getCommandLimiterSettings().getGlobalDenyMessage()));
                        return false;
                    }
                }
            } else {
                if(getCommandLimiterSettings().getGlobalBlockedCommands().get("global").contains(command)) {
                    if (player.hasPermission("functionalservercontrol.commands.bypass") || player.hasPermission("functionalservercontrol.commands." + command + ".bypass")) {
                        return true;
                    } else {
                        player.sendMessage(setColors(getCommandLimiterSettings().getGlobalDenyMessage()));
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public List<String> getNewCompletions(CommandSender player, String command, List<String> trueCompletions) {
        if(player.hasPermission("functionalservercontrol.tab-complete.bypass") || player.hasPermission("functionalservercontrol.tab-complete." + command + ".bypass")) return trueCompletions;
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
                    if (getCommandLimiterSettings().getPerWorldBlockedCommands().containsKey(world)) {
                        if (getCommandLimiterSettings().isPerWorldUseAsWhiteList()) {
                            return getCommandLimiterSettings().getPerWorldBlockedCommands().get(world).get(playerGroup);
                        } else {
                            newCompletions.removeAll(getCommandLimiterSettings().getPerWorldBlockedCommands().get(world).get(playerGroup));
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
                    if (getCommandLimiterSettings().getPerWorldBlockedCommands().containsKey(world)) {
                        if (getCommandLimiterSettings().isPerWorldUseAsWhiteList()) {
                            return getCommandLimiterSettings().getPerWorldBlockedCommands().get(world).get(playerGroup);
                        } else {
                            newCompletions.removeAll(getCommandLimiterSettings().getPerWorldBlockedCommands().get(world).get(playerGroup));
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
        if (getCommandLimiterSettings().getPerWorldBlockedCommands().containsKey(world)) {
            if (getCommandLimiterSettings().isPerWorldUseAsWhiteList()) {
                return getCommandLimiterSettings().getPerWorldBlockedCommands().get(world).get("global");
            } else {
                newCompletions.removeAll(getCommandLimiterSettings().getPerWorldBlockedCommands().get(world).get("global"));
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

}
