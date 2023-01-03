package by.alis.functionalservercontrol.spigot.Additional.GlobalSettings;

import by.alis.functionalservercontrol.spigot.FunctionalServerControl;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;

import static by.alis.functionalservercontrol.spigot.Additional.SomeUtils.TextUtils.setColors;
import static by.alis.functionalservercontrol.spigot.Managers.Files.SFAccessor.getFileAccessor;

public class CommandLimiterSettings {

    private boolean functionEnabled = true;
    private String checkMode;
    private final Collection<String> limitedWorlds = new ArrayList<>();
    private final HashMap<String, List<String>> globalBlockedCommands = new HashMap<>();
    private String globalDenyMessage;
    private boolean globalUseAsWhiteList;
    private final HashMap<World, HashMap<String, List<String>>> perWorldBlockedCommands = new HashMap<>();
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
    public HashMap<World, HashMap<String, List<String>>> getPerWorldBlockedCommands() {
        return perWorldBlockedCommands;
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
    private void setCheckMode(String checkMode) {
        this.checkMode = checkMode;
    }
    public String getCheckMode() {
        return checkMode;
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
            Bukkit.getScheduler().runTaskAsynchronously(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class), () -> {
                setCheckMode(getFileAccessor().getCommandsLimiterConfig().getString("settings.commands-check-mode"));
                setUseGroups(getFileAccessor().getCommandsLimiterConfig().getBoolean("settings.use-groups"));
                if(!getCheckMode().equalsIgnoreCase("first_arg") && !getCheckMode().equalsIgnoreCase("all_args")) {
                    Bukkit.getConsoleSender().sendMessage(setColors("&c[FunctionalServerControl] Unknown command validation method(%method%), using 'first_arg'".replace("%method%", getCheckMode())));
                    setCheckMode("first_arg");
                }
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
                                HashMap<String, List<String>> a = new HashMap<>();
                                a.put(groupName, getFileAccessor().getCommandsLimiterConfig().getStringList("blocked-commands.per-world." + worldName + ".group." + groupName));
                                this.perWorldBlockedCommands.put(world, a);
                            }
                            HashMap<String, List<String>> b = new HashMap<>();
                            b.put("global", getFileAccessor().getCommandsLimiterConfig().getStringList("blocked-commands.per-world." + worldName + ".global"));
                            this.perWorldBlockedCommands.put(world, b);
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
            });
        }
        Bukkit.getScheduler().runTaskAsynchronously(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class), () -> {
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
        if (isFunctionEnabled()) {
            setCheckMode(getFileAccessor().getCommandsLimiterConfig().getString("settings.commands-check-mode"));
            setUseGroups(getFileAccessor().getCommandsLimiterConfig().getBoolean("settings.use-groups"));
            if(!getCheckMode().equalsIgnoreCase("first_arg") && !getCheckMode().equalsIgnoreCase("all_args")) {
                Bukkit.getConsoleSender().sendMessage(setColors("&c[FunctionalServerControl] Unknown command validation method(%method%), using 'first_arg'".replace("%method%", getCheckMode())));
                setCheckMode("first_arg");
            }
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
                            HashMap<String, List<String>> a = new HashMap<>();
                            a.put(groupName, getFileAccessor().getCommandsLimiterConfig().getStringList("blocked-commands.per-world." + worldName + ".group." + groupName));
                            this.perWorldBlockedCommands.put(world, a);
                        }
                        HashMap<String, List<String>> b = new HashMap<>();
                        b.put("global", getFileAccessor().getCommandsLimiterConfig().getStringList("blocked-commands.per-world." + worldName + ".global"));
                        this.perWorldBlockedCommands.put(world, b);
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
        } else {
            this.limitedWorlds.clear();
            this.perWorldBlockedCommands.clear();
            this.limitedWorlds.clear();
            this.whitelistedSyntaxCommands.clear();
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
        } else {
            this.globalCompletions.clear();
            this.perGroupCompletions.clear();
        }
    }

}
