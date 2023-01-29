package net.alis.functionalservercontrol.spigot.additional.globalsettings;

import net.alis.functionalservercontrol.libraries.org.apache.commons.lang3.StringUtils;
import net.alis.functionalservercontrol.spigot.managers.TaskManager;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.*;

import static net.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;
import static net.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

public class CommandLimiterSettings {

    private boolean functionEnabled;
    private boolean notifyAdmins;
    private final Collection<String> limitedWorlds = new ArrayList<>();
    private final HashMap<String, List<String>> globalBlockedCommands = new HashMap<>();
    private String globalDenyMessage;
    private boolean globalUseAsWhiteList;
    private boolean disableSpigotReloadCommand;
    private final List<String> perWorldGroups = new ArrayList<>();
    private final List<World> perGroupWorlds = new ArrayList<>();
    private final List<List<String>> perGroupCommands = new ArrayList<>();
    private String perWorldDenyMessage;
    private boolean perWorldUseAsWhiteList;
    private boolean blockSyntaxCommand;
    private String syntaxDenyMessage;
    private final List<String> whitelistedSyntaxCommands = new ArrayList<>();
    private boolean useGroups;
    private boolean modifyTabCompletions;
    private final HashMap<String, List<String>> globalCompletions = new HashMap<>();
    private final HashMap<String, HashMap<String, List<String>>> perGroupCompletions =  new HashMap<>();
    private boolean hideCompletionsFully;
    private final List<String> consoleBlockedCommands = new ArrayList<>();
    private boolean consoleBlockedCommandsUseAsWhiteList;
    private String consoleCommandsDenyMessage;


    public boolean isNotifyAdmins() {
        return notifyAdmins;
    }
    private void setNotifyAdmins(boolean notifyAdmins) {
        this.notifyAdmins = notifyAdmins;
    }
    private void setDisableSpigotReloadCommand(boolean disableSpigotReloadCommand) {
        this.disableSpigotReloadCommand = disableSpigotReloadCommand;
    }
    public boolean isDisableSpigotReloadCommand() {
        return disableSpigotReloadCommand;
    }
    public List<String> getPerWorldGroups() {
        return perWorldGroups;
    }
    public List<List<String>> getPerGroupCommands() {
        return perGroupCommands;
    }
    public List<World> getPerGroupWorlds() {
        return perGroupWorlds;
    }

    public boolean isConsoleBlockedCommandsUseAsWhiteList() {
        return consoleBlockedCommandsUseAsWhiteList;
    }
    private void setConsoleBlockedCommandsUseAsWhiteList(boolean consoleBlockedCommandsUseAsWhiteList) {
        this.consoleBlockedCommandsUseAsWhiteList = consoleBlockedCommandsUseAsWhiteList;
    }
    public List<String> getConsoleBlockedCommands() {
        return consoleBlockedCommands;
    }
    public void setConsoleBlockedCommands(List<String> consoleBlockedCommands) {
        this.consoleBlockedCommands.addAll(consoleBlockedCommands);
    }
    public String getConsoleCommandsDenyMessage() {
        return consoleCommandsDenyMessage;
    }
    private void setConsoleCommandsDenyMessage(String consoleCommandsDenyMessage) {
        this.consoleCommandsDenyMessage = consoleCommandsDenyMessage;
    }
    private void setFunctionEnabled(boolean functionEnabled) {
        this.functionEnabled = functionEnabled;
    }
    public boolean isFunctionEnabled() {
        return functionEnabled;
    }
    public Collection<String> getLimitedWorlds() {
        return limitedWorlds;
    }
    private void setLimitedWorlds(Collection<String> limitedWorlds) {
        this.limitedWorlds.clear();
        this.limitedWorlds.addAll(limitedWorlds);
    }
    public String getPerWorldDenyMessage() {
        return perWorldDenyMessage;
    }
    private void setPerWorldDenyMessage(String perWorldDenyMessage) {
        this.perWorldDenyMessage = perWorldDenyMessage;
    }
    public boolean isPerWorldUseAsWhiteList() {
        return perWorldUseAsWhiteList;
    }
    private void setPerWorldUseAsWhiteList(boolean perWorldUseAsWhiteList) {
        this.perWorldUseAsWhiteList = perWorldUseAsWhiteList;
    }
    public HashMap<String, List<String>> getGlobalBlockedCommands() {
        return globalBlockedCommands;
    }
    public String getGlobalDenyMessage() {
        return globalDenyMessage;
    }
    private void setGlobalDenyMessage(String globalDenyMessage) {
        this.globalDenyMessage = globalDenyMessage;
    }
    public boolean isGlobalUseAsWhiteList() {
        return globalUseAsWhiteList;
    }
    private void setGlobalUseAsWhiteList(boolean globalUseAsWhiteList) {
        this.globalUseAsWhiteList = globalUseAsWhiteList;
    }
    public boolean isBlockSyntaxCommand() {
        return blockSyntaxCommand;
    }
    private void setBlockSyntaxCommand(boolean blockSyntaxCommand) {
        this.blockSyntaxCommand = blockSyntaxCommand;
    }
    public String getSyntaxDenyMessage() {
        return syntaxDenyMessage;
    }
    private void setSyntaxDenyMessage(String syntaxDenyMessage) {
        this.syntaxDenyMessage = syntaxDenyMessage;
    }
    public List<String> getWhitelistedSyntaxCommands() {
        return whitelistedSyntaxCommands;
    }
    private void setWhitelistedSyntaxCommands(List<String> whitelistedSyntaxCommands) {
        this.whitelistedSyntaxCommands.clear();
        this.whitelistedSyntaxCommands.addAll(whitelistedSyntaxCommands);
    }
    private void setUseGroups(boolean useGroups) {
        this.useGroups = useGroups;
    }
    public boolean isUseGroups() {
        return useGroups;
    }
    private void setModifyTabCompletions(boolean modifyTabCompletions) {
        this.modifyTabCompletions = modifyTabCompletions;
    }
    public boolean isModifyTabCompletions() {
        return modifyTabCompletions;
    }
    public HashMap<String, HashMap<String, List<String>>> getPerGroupCompletions() {
        return perGroupCompletions;
    }
    public HashMap<String, List<String>> getGlobalCompletions() {
        return globalCompletions;
    }
    public boolean isHideCompletionsFully() {
        return hideCompletionsFully;
    }

    private void setHideCompletionsFully(boolean hideCompletionsFully) {
        this.hideCompletionsFully = hideCompletionsFully;
    }

    public void loadCommandLimiterSettings() {
        setFunctionEnabled(getFileAccessor().getCommandsLimiterConfig().getBoolean("settings.enabled"));
        if (isFunctionEnabled()) {
            TaskManager.preformAsync(() -> {
                setNotifyAdmins(getFileAccessor().getCommandsLimiterConfig().getBoolean("settings.notify-admins"));
                setDisableSpigotReloadCommand(getFileAccessor().getCommandsLimiterConfig().getBoolean("settings.disable-reload-command"));
                setUseGroups(getFileAccessor().getCommandsLimiterConfig().getBoolean("settings.use-groups"));
                setGlobalDenyMessage(getFileAccessor().getCommandsLimiterConfig().getString("blocked-commands.global.deny-message"));
                setGlobalUseAsWhiteList(getFileAccessor().getCommandsLimiterConfig().getBoolean("blocked-commands.global.use-as-whitelist"));
                for(String groupName : getFileAccessor().getCommandsLimiterConfig().getConfigurationSection("blocked-commands.global.group").getKeys(false)) {
                    this.globalBlockedCommands.put(groupName, getFileAccessor().getCommandsLimiterConfig().getStringList("blocked-commands.global.group." + groupName));
                }
                this.globalBlockedCommands.put("global", getFileAccessor().getCommandsLimiterConfig().getStringList("blocked-commands.global.global"));
                setPerWorldDenyMessage(getFileAccessor().getCommandsLimiterConfig().getString("blocked-commands.per-world.deny-message"));
                setPerWorldUseAsWhiteList(getFileAccessor().getCommandsLimiterConfig().getBoolean("blocked-commands.per-world.use-as-whitelist"));
                setLimitedWorlds(getFileAccessor().getCommandsLimiterConfig().getConfigurationSection("blocked-commands.per-world").getKeys(false));
                for (String worldName : getLimitedWorlds()) {
                    if (!worldName.equalsIgnoreCase("deny-message") && !worldName.equalsIgnoreCase("use-as-whitelist")) {
                        World world = Bukkit.getWorld(worldName);
                        if (world != null) {
                            for (String groupName : getFileAccessor().getCommandsLimiterConfig().getConfigurationSection("blocked-commands.per-world." + worldName + ".group").getKeys(false)) {
                                this.perGroupWorlds.add(world);
                                this.perWorldGroups.add(groupName);
                                this.perGroupCommands.add(getFileAccessor().getCommandsLimiterConfig().getStringList("blocked-commands.per-world." + worldName + ".group." + groupName));
                            }
                            this.perWorldGroups.add("global");
                            this.perGroupWorlds.add(world);
                            this.perGroupCommands.add(getFileAccessor().getCommandsLimiterConfig().getStringList("blocked-commands.per-world." + worldName + ".global"));
                        } else {
                            Bukkit.getConsoleSender().sendMessage(setColors("&c[FunctionalServerControl] World '%world%' not found, check file 'commands-limiter.yml'".replace("%world%", worldName)));
                        }
                    }
                }

                setBlockSyntaxCommand(getFileAccessor().getCommandsLimiterConfig().getBoolean("blocked-commands.syntax-commands.block"));
                if(isBlockSyntaxCommand()) {
                    setSyntaxDenyMessage(getFileAccessor().getCommandsLimiterConfig().getString("blocked-commands.syntax-commands.deny-message"));
                    setWhitelistedSyntaxCommands(getFileAccessor().getCommandsLimiterConfig().getStringList("blocked-commands.syntax-commands.whitelisted-syntax-commands"));
                }
                setConsoleBlockedCommands(getFileAccessor().getCommandsLimiterConfig().getStringList("blocked-commands.console.commands"));
                setConsoleBlockedCommandsUseAsWhiteList(getFileAccessor().getCommandsLimiterConfig().getBoolean("blocked-commands.console.use-as-whitelist"));
                setConsoleCommandsDenyMessage(getFileAccessor().getCommandsLimiterConfig().getString("blocked-commands.console.deny-message"));
            });
        }
        TaskManager.preformAsync(() -> {
            setModifyTabCompletions(getFileAccessor().getCommandsLimiterConfig().getBoolean("settings.modify-tab-completions"));
            if(isModifyTabCompletions()) {
                for(String command : getFileAccessor().getCommandsLimiterConfig().getConfigurationSection("tab-completions.per-command.global").getKeys(false)) {
                    this.globalCompletions.put("/" + command, Arrays.asList(StringUtils.substringBetween(getFileAccessor().getCommandsLimiterConfig().getString("tab-completions.per-command.global." + command), "[", "]").split(", ")));
                }
                for(String groupName : getFileAccessor().getCommandsLimiterConfig().getConfigurationSection("tab-completions.per-command.group").getKeys(false)) {
                    for(String command : getFileAccessor().getCommandsLimiterConfig().getConfigurationSection("tab-completions.per-command.group." + groupName).getKeys(false)) {
                        HashMap<String, List<String>> a = new HashMap<>();
                        a.put("/" + command, Arrays.asList(StringUtils.substringBetween(getFileAccessor().getCommandsLimiterConfig().getString("tab-completions.per-command.group." + groupName + "." + command), "[", "]").split(", ")));
                        this.perGroupCompletions.put(groupName, a);
                    }
                }
                setHideCompletionsFully(getFileAccessor().getCommandsLimiterConfig().getBoolean("tab-completions.hide-fully"));
            }
        });
    }

    public void reloadCommandLimiterSettings() {
        setFunctionEnabled(getFileAccessor().getCommandsLimiterConfig().getBoolean("settings.enabled"));
        setDisableSpigotReloadCommand(getFileAccessor().getCommandsLimiterConfig().getBoolean("settings.disable-reload-command"));
        setNotifyAdmins(getFileAccessor().getCommandsLimiterConfig().getBoolean("settings.notify-admins"));
        if (isFunctionEnabled()) {
            setUseGroups(getFileAccessor().getCommandsLimiterConfig().getBoolean("settings.use-groups"));
            setGlobalDenyMessage(getFileAccessor().getCommandsLimiterConfig().getString("blocked-commands.global.deny-message"));
            setGlobalUseAsWhiteList(getFileAccessor().getCommandsLimiterConfig().getBoolean("blocked-commands.global.use-as-whitelist"));
            for(String groupName : getFileAccessor().getCommandsLimiterConfig().getConfigurationSection("blocked-commands.global.group").getKeys(false)) {
                this.globalBlockedCommands.put(groupName, getFileAccessor().getCommandsLimiterConfig().getStringList("blocked-commands.global.group." + groupName));
            }
            this.globalBlockedCommands.put("global", getFileAccessor().getCommandsLimiterConfig().getStringList("blocked-commands.global.global"));
            setPerWorldDenyMessage(getFileAccessor().getCommandsLimiterConfig().getString("blocked-commands.per-world.deny-message"));
            setPerWorldUseAsWhiteList(getFileAccessor().getCommandsLimiterConfig().getBoolean("blocked-commands.per-world.use-as-whitelist"));
            setLimitedWorlds(getFileAccessor().getCommandsLimiterConfig().getConfigurationSection("blocked-commands.per-world").getKeys(false));
            this.perGroupCommands.clear();
            this.perWorldGroups.clear();
            this.perGroupWorlds.clear();
            for (String worldName : getLimitedWorlds()) {
                if (!worldName.equalsIgnoreCase("deny-message") && !worldName.equalsIgnoreCase("use-as-whitelist")) {
                    World world = Bukkit.getWorld(worldName);
                    if (world != null) {
                        for (String groupName : getFileAccessor().getCommandsLimiterConfig().getConfigurationSection("blocked-commands.per-world." + worldName + ".group").getKeys(false)) {
                            this.perGroupWorlds.add(world);
                            this.perWorldGroups.add(groupName);
                            this.perGroupCommands.add(getFileAccessor().getCommandsLimiterConfig().getStringList("blocked-commands.per-world." + worldName + ".group." + groupName));
                        }
                        if(!this.perWorldGroups.contains("global")) {
                            this.perWorldGroups.add("global");
                            this.perGroupWorlds.add(world);
                            this.perGroupCommands.add(getFileAccessor().getCommandsLimiterConfig().getStringList("blocked-commands.per-world." + worldName + ".global"));
                        }
                    } else {
                        Bukkit.getConsoleSender().sendMessage(setColors("&c[FunctionalServerControl] World '%world%' not found, check file 'commands-limiter.yml'".replace("%world%", worldName)));
                    }
                }
            }
            setBlockSyntaxCommand(getFileAccessor().getCommandsLimiterConfig().getBoolean("blocked-commands.syntax-commands.block"));
            if(isBlockSyntaxCommand()) {
                setSyntaxDenyMessage(getFileAccessor().getCommandsLimiterConfig().getString("blocked-commands.syntax-commands.deny-message"));
                setWhitelistedSyntaxCommands(getFileAccessor().getCommandsLimiterConfig().getStringList("blocked-commands.syntax-commands.whitelisted-syntax-commands"));
            }
            setConsoleBlockedCommands(getFileAccessor().getCommandsLimiterConfig().getStringList("blocked-commands.console.commands"));
            setConsoleBlockedCommandsUseAsWhiteList(getFileAccessor().getCommandsLimiterConfig().getBoolean("blocked-commands.console.use-as-whitelist"));
            setConsoleCommandsDenyMessage(getFileAccessor().getCommandsLimiterConfig().getString("blocked-commands.console.deny-message"));
        }
        setModifyTabCompletions(getFileAccessor().getCommandsLimiterConfig().getBoolean("settings.modify-tab-completions"));
        if(isModifyTabCompletions()) {
            for(String command : getFileAccessor().getCommandsLimiterConfig().getConfigurationSection("tab-completions.per-command.global").getKeys(false)) {
                this.globalCompletions.put("/" + command, Arrays.asList(StringUtils.substringBetween(getFileAccessor().getCommandsLimiterConfig().getString("tab-completions.per-command.global." + command), "[", "]").split(", ")));
            }
            for(String groupName : getFileAccessor().getCommandsLimiterConfig().getConfigurationSection("tab-completions.per-command.group").getKeys(false)) {
                for(String command : getFileAccessor().getCommandsLimiterConfig().getConfigurationSection("tab-completions.per-command.group." + groupName).getKeys(false)) {
                    HashMap<String, List<String>> a = new HashMap<>();
                    a.put("/" + command, Arrays.asList(StringUtils.substringBetween(getFileAccessor().getCommandsLimiterConfig().getString("tab-completions.per-command.group." + groupName + "." + command), "[", "]").split(", ")));
                    this.perGroupCompletions.put(groupName, a);
                }
            }
            setHideCompletionsFully(getFileAccessor().getCommandsLimiterConfig().getBoolean("tab-completions.hide-fully"));
        }
    }

}
