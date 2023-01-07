package by.alis.functionalservercontrol.spigot.Additional.Misc.Cooldowns;

import by.alis.functionalservercontrol.spigot.Additional.CoreAdapters.CoreAdapter;
import by.alis.functionalservercontrol.spigot.Expansions.Expansions;
import by.alis.functionalservercontrol.spigot.FunctionalServerControl;
import by.alis.functionalservercontrol.spigot.Managers.TimeManagers.TimeSettingsAccessor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

import static by.alis.functionalservercontrol.spigot.Additional.Misc.TextUtils.setColors;
import static by.alis.functionalservercontrol.spigot.Managers.Files.SFAccessor.getFileAccessor;

public class Cooldowns {

    private boolean functionEnabled;
    private boolean useGroups;

    private List<TrackedCommand> trackedCommands = new ArrayList<>();

    public void loadCooldowns() {
        Bukkit.getScheduler().runTaskAsynchronously(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class), () -> {
            this.functionEnabled = getFileAccessor().getCooldownsConfig().getBoolean("settings.enabled");
            if(functionEnabled) {
                this.useGroups = getFileAccessor().getCooldownsConfig().getBoolean("settings.use-groups");
                for(String cmd : getFileAccessor().getCooldownsConfig().getConfigurationSection("commands").getKeys(false)) {
                    boolean isContinue = true;
                    Map<String, Long> groups = new HashMap<>();
                    for(String group : getFileAccessor().getCooldownsConfig().getConfigurationSection("commands." + cmd + ".per-groups").getKeys(false)) {
                        long a = 3;
                        try {
                            a = Long.parseLong(getFileAccessor().getCooldownsConfig().getString("commands." + cmd + ".per-groups." + group));
                            if(a < 3) {
                                Bukkit.getConsoleSender().sendMessage(setColors("&c[FunctionalServerControl] Error in the 'global-cooldown.yml' file on the path 'commands -> " + cmd + " -> per-groups -> " + group + "' (Time cannot be lower than 3)"));
                                isContinue = false;
                                break;
                            }
                        } catch (NumberFormatException ignored) {
                            Bukkit.getConsoleSender().sendMessage(setColors("&c[FunctionalServerControl] Error in the 'global-cooldown.yml' file on the path 'commands -> " + cmd + " -> per-groups -> " + group + "' (NumberFormatException)"));
                            isContinue = false;
                            break;
                        }
                        groups.put(group, getFileAccessor().getCooldownsConfig().getLong("commands." + cmd + ".per-groups." + group));
                    }
                    if(!isContinue) continue;
                    long a = 3;
                    try {
                        a = Long.parseLong(getFileAccessor().getCooldownsConfig().getString("commands." + cmd + ".time"));
                        if(a < 3) {
                            Bukkit.getConsoleSender().sendMessage(setColors("&c[FunctionalServerControl] Error in the 'global-cooldown.yml' file on the path 'commands -> " + cmd + " -> time' (Time cannot be lower than 3)"));
                            continue;
                        }
                    } catch (NumberFormatException ignored) {
                        Bukkit.getConsoleSender().sendMessage(setColors("&c[FunctionalServerControl] Error in the 'global-cooldown.yml' file on the path 'commands -> " + cmd + " -> time' (NumberFormatException)"));
                        continue;
                    }
                    int b = 0;
                    try {
                        b = Integer.parseInt(getFileAccessor().getCooldownsConfig().getString("commands." + cmd + ".min-args"));
                        if(b < 0) {
                            Bukkit.getConsoleSender().sendMessage(setColors("&c[FunctionalServerControl] Error in the 'global-cooldown.yml' file on the path 'commands -> " + cmd + " -> min-args' (Minimum arguments cannot be lower than 0)"));
                            continue;
                        }
                    } catch (NumberFormatException ignored) {
                        Bukkit.getConsoleSender().sendMessage(setColors("&c[FunctionalServerControl] Error in the 'global-cooldown.yml' file on the path 'commands -> " + cmd + " -> min-args' (NumberFormatException)"));
                        continue;
                    }
                    TrackedCommand trackedCommand = new TrackedCommand(
                            cmd,
                            getFileAccessor().getCooldownsConfig().getString("commands." + cmd + ".permission"),
                            getFileAccessor().getCooldownsConfig().getBoolean("commands." + cmd + ".expire-message-as-title"),
                            getFileAccessor().getCooldownsConfig().getBoolean("commands." + cmd + ".deny-message-as-title"),
                            getFileAccessor().getCooldownsConfig().getBoolean("commands." + cmd + ".check-aliases"),
                            Arrays.asList(StringUtils.substringBetween(getFileAccessor().getCooldownsConfig().getString("commands." + cmd + ".aliases").replace(" ", ""), "[", "]").split(",")),
                            getFileAccessor().getCooldownsConfig().getString("commands." + cmd + ".deny-message"),
                            getFileAccessor().getCooldownsConfig().getBoolean("commands." + cmd + ".notify-on-expire"),
                            groups,
                            getFileAccessor().getCooldownsConfig().getString("commands." + cmd + ".expire-message"),
                            a,
                            b
                    );
                    this.trackedCommands.add(trackedCommand);
                }
            }
        });
    }

    public void reloadCooldowns(CommandSender sender) {
        if(functionEnabled) {
            this.useGroups = getFileAccessor().getCooldownsConfig().getBoolean("settings.use-groups");
            for(String cmd : getFileAccessor().getCooldownsConfig().getConfigurationSection("commands").getKeys(false)) {
                boolean isContinue = true;
                for(TrackedCommand tc : this.trackedCommands) {
                    boolean isContinue1 = true;
                    if(tc.getCommandName().equalsIgnoreCase(cmd)) {
                        Map<String, Long> groups = new HashMap<>();
                        for(String group : getFileAccessor().getCooldownsConfig().getConfigurationSection("commands." + cmd + ".per-groups").getKeys(false)) {
                            long a = 3;
                            try {
                                a = Long.parseLong(getFileAccessor().getCooldownsConfig().getString("commands." + cmd + ".per-groups." + group));
                                if(a < 3) {
                                    Bukkit.getConsoleSender().sendMessage(setColors("&c[FunctionalServerControl] Error in the 'global-cooldown.yml' file on the path 'commands -> " + cmd + " -> per-groups -> " + group + "' (Time cannot be lower than 3)"));
                                    isContinue = false;
                                    break;
                                }
                            } catch (NumberFormatException ignored) {
                                Bukkit.getConsoleSender().sendMessage(setColors("&c[FunctionalServerControl] Error in the 'global-cooldown.yml' file on the path 'commands -> " + cmd + " -> per-groups -> " + group + "' (NumberFormatException)"));
                                isContinue = false;
                                break;
                            }
                            groups.put(group, getFileAccessor().getCooldownsConfig().getLong("commands." + cmd + ".per-groups." + group));
                        }
                        long a = 3;
                        try {
                            a = Long.parseLong(getFileAccessor().getCooldownsConfig().getString("commands." + cmd + ".time"));
                            if(a < 3) {
                                Bukkit.getConsoleSender().sendMessage(setColors("&c[FunctionalServerControl] Error in the 'global-cooldown.yml' file on the path 'commands -> " + cmd + " -> time' (Time cannot be lower than 3)"));
                                continue;
                            }
                        } catch (NumberFormatException ignored) {
                            Bukkit.getConsoleSender().sendMessage(setColors("&c[FunctionalServerControl] Error in the 'global-cooldown.yml' file on the path 'commands -> " + cmd + " -> time' (NumberFormatException)"));
                            continue;
                        }
                        int b = 0;
                        try {
                            b = Integer.parseInt(getFileAccessor().getCooldownsConfig().getString("commands." + cmd + ".min-args"));
                            if(b < 0) {
                                Bukkit.getConsoleSender().sendMessage(setColors("&c[FunctionalServerControl] Error in the 'global-cooldown.yml' file on the path 'commands -> " + cmd + " -> min-args' (Minimum arguments cannot be lower than 0)"));
                                continue;
                            }
                        } catch (NumberFormatException ignored) {
                            Bukkit.getConsoleSender().sendMessage(setColors("&c[FunctionalServerControl] Error in the 'global-cooldown.yml' file on the path 'commands -> " + cmd + " -> min-args' (NumberFormatException)"));
                            continue;
                        }
                        if(!isContinue1) continue;
                        TrackedCommand trackedCommand = this.trackedCommands.get(this.trackedCommands.indexOf(tc));
                        trackedCommand.setPermission(getFileAccessor().getCooldownsConfig().getString("commands." + cmd + ".permission"));
                        trackedCommand.setExpireMessageAsTitle(getFileAccessor().getCooldownsConfig().getBoolean("commands." + cmd + ".expire-message-as-title"));
                        trackedCommand.setDenyMessageAsTitle(getFileAccessor().getCooldownsConfig().getBoolean("commands." + cmd + ".deny-message-as-title"));
                        trackedCommand.setCheckAliases(getFileAccessor().getCooldownsConfig().getBoolean("commands." + cmd + ".check-aliases"));
                        trackedCommand.setAliases(Arrays.asList(StringUtils.substringBetween(getFileAccessor().getCooldownsConfig().getString("commands." + cmd + ".aliases").replace(" ", ""), "[", "]").split(",")));
                        trackedCommand.setDenyMessage(getFileAccessor().getCooldownsConfig().getString("commands." + cmd + ".deny-message"));
                        trackedCommand.setSendExpireMessage(getFileAccessor().getCooldownsConfig().getBoolean("commands." + cmd + ".notify-on-expire"));
                        trackedCommand.setGroupsTime(groups);
                        trackedCommand.setExpireMessage(getFileAccessor().getCooldownsConfig().getString("commands." + cmd + ".expire-message"));
                        trackedCommand.setCooldownTime(a);
                        trackedCommand.setMinArgs(b);
                        isContinue = false;
                        continue;
                    }
                }
                if(!isContinue) continue;
                Map<String, Long> groups = new HashMap<>();
                for(String group : getFileAccessor().getCooldownsConfig().getConfigurationSection("commands." + cmd + ".per-groups").getKeys(false)) {
                    long a = 3;
                    try {
                        a = Long.parseLong(getFileAccessor().getCooldownsConfig().getString("commands." + cmd + ".per-groups." + group));
                        if(a < 3) {
                            Bukkit.getConsoleSender().sendMessage(setColors("&c[FunctionalServerControl] Error in the 'global-cooldown.yml' file on the path 'commands -> " + cmd + " -> per-groups -> " + group + "' (Time cannot be lower than 3)"));
                            isContinue = false;
                            break;
                        }
                    } catch (NumberFormatException ignored) {
                        Bukkit.getConsoleSender().sendMessage(setColors("&c[FunctionalServerControl] Error in the 'global-cooldown.yml' file on the path 'commands -> " + cmd + " -> per-groups -> " + group + "' (NumberFormatException)"));
                        isContinue = false;
                        break;
                    }
                    groups.put(group, getFileAccessor().getCooldownsConfig().getLong("commands." + cmd + ".per-groups." + group));
                }
                if(!isContinue) continue;
                long a = 3;
                try {
                    a = Long.parseLong(getFileAccessor().getCooldownsConfig().getString("commands." + cmd + ".time"));
                    if(a < 3) {
                        Bukkit.getConsoleSender().sendMessage(setColors("&c[FunctionalServerControl] Error in the 'global-cooldown.yml' file on the path 'commands -> " + cmd + " -> time' (Time cannot be lower than 3)"));
                        continue;
                    }
                } catch (NumberFormatException ignored) {
                    Bukkit.getConsoleSender().sendMessage(setColors("&c[FunctionalServerControl] Error in the 'global-cooldown.yml' file on the path 'commands -> " + cmd + " -> time' (NumberFormatException)"));
                    continue;
                }
                int b = 0;
                try {
                    b = Integer.parseInt(getFileAccessor().getCooldownsConfig().getString("commands." + cmd + ".min-args"));
                    if(b < 0) {
                        Bukkit.getConsoleSender().sendMessage(setColors("&c[FunctionalServerControl] Error in the 'global-cooldown.yml' file on the path 'commands -> " + cmd + " -> min-args' (Minimum arguments cannot be lower than 0)"));
                        continue;
                    }
                } catch (NumberFormatException ignored) {
                    Bukkit.getConsoleSender().sendMessage(setColors("&c[FunctionalServerControl] Error in the 'global-cooldown.yml' file on the path 'commands -> " + cmd + " -> min-args' (NumberFormatException)"));
                    continue;
                }
                TrackedCommand trackedCommand = new TrackedCommand(
                        cmd,
                        getFileAccessor().getCooldownsConfig().getString("commands." + cmd + ".permission"),
                        getFileAccessor().getCooldownsConfig().getBoolean("commands." + cmd + ".expire-message-as-title"),
                        getFileAccessor().getCooldownsConfig().getBoolean("commands." + cmd + ".deny-message-as-title"),
                        getFileAccessor().getCooldownsConfig().getBoolean("commands." + cmd + ".check-aliases"),
                        Arrays.asList(StringUtils.substringBetween(getFileAccessor().getCooldownsConfig().getString("commands." + cmd + ".aliases").replace(" ", ""), "[", "]").split(",")),
                        getFileAccessor().getCooldownsConfig().getString("commands." + cmd + ".deny-message"),
                        getFileAccessor().getCooldownsConfig().getBoolean("commands." + cmd + ".notify-on-expire"),
                        groups,
                        getFileAccessor().getCooldownsConfig().getString("commands." + cmd + ".expire-message"),
                        a,
                        b
                );
                this.trackedCommands.add(trackedCommand);
            }
        } else {
            sender.sendMessage(setColors("&c[FunctionalServerControl] This function disabled in 'global-cooldowns.yml'"));
        }
    }

    public boolean playerHasCooldown(Player player, String command) {
        if(functionEnabled) {
            for (TrackedCommand tc : this.trackedCommands) {
                if (tc.getCommandName().equalsIgnoreCase(command) || tc.getAliases().contains(command)) {
                    if (tc.getPlayers().containsKey(player.getUniqueId())) return true;
                }
            }
            return false;
        }
        return false;
    }

    public void setUpCooldown(Player player, String command, int args) {
        if (functionEnabled) {
            Bukkit.getScheduler().runTaskAsynchronously(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class), () -> {
                if (player.hasPermission("functionalservercontrol.cooldowns.bypass") || player.hasPermission("functionalservercontrol.cooldowns." + command + ".bypass"))
                    return;
                for (TrackedCommand trackedCommand : this.trackedCommands) {
                    if (trackedCommand.isCheckAliases()) {
                        if (trackedCommand.getCommandName().equalsIgnoreCase(command) || trackedCommand.getAliases().contains(command)) {
                            TrackedCommand tc = this.trackedCommands.get(this.trackedCommands.indexOf(trackedCommand));
                            if (args >= tc.getMinArgs()) {
                                if (tc.getPermission() != null) {
                                    if(player.hasPermission(tc.getPermission())) tc.addPlayer(player, System.currentTimeMillis());
                                } else {
                                    tc.addPlayer(player, System.currentTimeMillis());
                                }
                            }
                        }
                    } else {
                        if (trackedCommand.getCommandName().equalsIgnoreCase(command)) {
                            TrackedCommand tc = this.trackedCommands.get(this.trackedCommands.indexOf(trackedCommand));
                            if (args >= tc.getMinArgs()) {
                                if (tc.getPermission() != null && player.hasPermission(tc.getPermission())) {
                                    tc.addPlayer(player, System.currentTimeMillis());
                                } else {
                                    tc.addPlayer(player, System.currentTimeMillis());
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    public void notifyAboutCooldown(Player player, String command) {
        if(functionEnabled) {
            Bukkit.getScheduler().runTaskAsynchronously(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class), () -> {
                for (TrackedCommand tc : this.trackedCommands) {
                    if ((tc.getCommandName().equalsIgnoreCase(command) || tc.getAliases().contains(command)) && tc.getPlayers().containsKey(player.getUniqueId())) {
                        TimeSettingsAccessor timeSettingsAccessor = new TimeSettingsAccessor();
                        if(tc.isDenyMessageAsTitle()) {
                            if(useGroups) {
                                if (Expansions.getVaultManager().isVaultSetuped() && tc.getGroupsTime().containsKey(Expansions.getVaultManager().getPlayerGroup(player))) {
                                    CoreAdapter.getAdapter().sendTitle(player, setColors(tc.getDenyMessage().replace("%1$f", timeSettingsAccessor.getTimeManager().convertFromMillis((tc.getPlayers().get(player.getUniqueId()) + (tc.getGroupsTime().get(Expansions.getVaultManager().getPlayerGroup(player)) * 1000)) - System.currentTimeMillis())).replace("%2$f", command)), "");
                                    return;
                                }
                                if (Expansions.getLuckPermsManager().isLuckPermsSetuped() && tc.getGroupsTime().containsKey(Expansions.getLuckPermsManager().getPlayerGroup(player))) {
                                    CoreAdapter.getAdapter().sendTitle(player, setColors(tc.getDenyMessage().replace("%1$f", timeSettingsAccessor.getTimeManager().convertFromMillis((tc.getPlayers().get(player.getUniqueId()) + (tc.getGroupsTime().get(Expansions.getLuckPermsManager().getPlayerGroup(player)) * 1000)) - System.currentTimeMillis())).replace("%2$f", command)), "");
                                    return;
                                }
                                CoreAdapter.getAdapter().sendTitle(player, setColors(tc.getDenyMessage()).replace("%1$f", timeSettingsAccessor.getTimeManager().convertFromMillis((tc.getPlayers().get(player.getUniqueId()) + (tc.getCooldownTime()) * 1000) - System.currentTimeMillis())).replace("%2$f", command), "");
                                return;
                            }
                            CoreAdapter.getAdapter().sendTitle(player, setColors(tc.getDenyMessage()).replace("%1$f", timeSettingsAccessor.getTimeManager().convertFromMillis((tc.getPlayers().get(player.getUniqueId()) + (tc.getCooldownTime()) * 1000) - System.currentTimeMillis())).replace("%2$f", command), "");
                        } else {
                            if(useGroups) {
                                if (Expansions.getVaultManager().isVaultSetuped() && tc.getGroupsTime().containsKey(Expansions.getVaultManager().getPlayerGroup(player))) {
                                    player.sendMessage(setColors(tc.getDenyMessage().replace("%1$f", timeSettingsAccessor.getTimeManager().convertFromMillis((tc.getPlayers().get(player.getUniqueId()) + (tc.getGroupsTime().get(Expansions.getVaultManager().getPlayerGroup(player)) * 1000)) - System.currentTimeMillis())).replace("%2$f", command)));
                                    return;
                                }
                                if (Expansions.getLuckPermsManager().isLuckPermsSetuped() && tc.getGroupsTime().containsKey(Expansions.getLuckPermsManager().getPlayerGroup(player))) {
                                    player.sendMessage(setColors(tc.getDenyMessage().replace("%1$f", timeSettingsAccessor.getTimeManager().convertFromMillis((tc.getPlayers().get(player.getUniqueId()) + (tc.getGroupsTime().get(Expansions.getLuckPermsManager().getPlayerGroup(player)) * 1000)) - System.currentTimeMillis())).replace("%2$f", command)));
                                    return;
                                }
                                player.sendMessage(setColors(tc.getDenyMessage()).replace("%1$f", timeSettingsAccessor.getTimeManager().convertFromMillis((tc.getPlayers().get(player.getUniqueId()) + (tc.getCooldownTime()) * 1000) - System.currentTimeMillis())).replace("%2$f", command));
                                return;
                            }
                            player.sendMessage(setColors(tc.getDenyMessage()).replace("%1$f", timeSettingsAccessor.getTimeManager().convertFromMillis((tc.getPlayers().get(player.getUniqueId()) + (tc.getCooldownTime()) * 1000) - System.currentTimeMillis())).replace("%2$f", command));
                        }
                    }
                }
            });
        }
    }

    public List<TrackedCommand> getTrackedCommands() {
        return trackedCommands;
    }

    private static final Cooldowns cooldowns = new Cooldowns();
    public static Cooldowns getCooldowns() {
        return cooldowns;
    }

    public boolean isFunctionEnabled() {
        return functionEnabled;
    }

    public boolean isUseGroups() {
        return useGroups;
    }
}
