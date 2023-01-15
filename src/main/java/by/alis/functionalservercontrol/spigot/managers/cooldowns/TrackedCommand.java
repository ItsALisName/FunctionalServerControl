package by.alis.functionalservercontrol.spigot.managers.cooldowns;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static by.alis.functionalservercontrol.spigot.additional.misc.TextUtils.isTextNotNull;

public class TrackedCommand {

    private final @NotNull String commandName;

    private boolean checkAliases;

    private final TreeMap<UUID, Long> players = new TreeMap<>();

    private @Nullable List<String> aliases;

    private @Nullable String denyMessage;

    private boolean denyMessageAsTitle;

    private boolean expireMessageAsTitle;

    private boolean sendExpireMessage;

    private @Nullable String expireMessage;

    private Map<String, Long> groupsTime;

    private String permission;

    private long cooldownTime;

    private int minArgs;

    public TrackedCommand(@NotNull String commandName, String permission, boolean expireMessageAsTitle, boolean denyMessageAsTitle, boolean checkAliases, @Nullable List<String> aliases, @Nullable String denyMessage, boolean sendExpireMessage, Map<String, Long> groupsTime, @Nullable String expireMessage, long cooldownTime, int minArgs) {
        this.commandName = commandName;
        this.checkAliases = checkAliases;
        this.expireMessageAsTitle = expireMessageAsTitle;

        if(groupsTime == null) {
            groupsTime = new HashMap<>();
        }
        this.groupsTime = groupsTime;

        if(isTextNotNull(permission)) {
            this.permission = permission;
        } else {
            this.permission = null;
        }


        if(aliases == null) {
            aliases = new ArrayList<>();
        }
        this.aliases = aliases;
        this.denyMessage = denyMessage;
        this.denyMessageAsTitle = denyMessageAsTitle;
        this.sendExpireMessage = sendExpireMessage;
        this.expireMessage = expireMessage;
        this.cooldownTime = cooldownTime;
        this.minArgs = minArgs;
    }

    public @Nullable String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        if(isTextNotNull(permission)) {
            this.permission = permission;
        } else {
            this.permission = null;
        }
    }

    public @NotNull String getCommandName() {
        return commandName;
    }

    public boolean isCheckAliases() {
        return checkAliases;
    }

    public boolean isDenyMessageAsTitle() {
        return denyMessageAsTitle;
    }

    public @Nullable List<String> getAliases() {
        return aliases;
    }

    public @Nullable String getDenyMessage() {
        return denyMessage;
    }

    public boolean isSendExpireMessage() {
        return sendExpireMessage;
    }

    public @Nullable String getExpireMessage() {
        return expireMessage;
    }

    public long getCooldownTime() {
        return cooldownTime;
    }

    public int getMinArgs() {
        return minArgs;
    }

    public Map<String, Long> getGroupsTime() {
        return groupsTime;
    }

    public void addPlayer(Player player, long start) {
        this.players.put(player.getUniqueId(), start);
    }

    public void removePlayer(Player player) {
        this.players.remove(player.getUniqueId());
    }

    public TreeMap<UUID, Long> getPlayers() {
        return players;
    }

    public boolean isExpireMessageAsTitle() {
        return expireMessageAsTitle;
    }

    public void setCheckAliases(boolean checkAliases) {
        this.checkAliases = checkAliases;
    }

    public void setAliases(@Nullable List<String> aliases) {
        if(aliases == null) aliases = new ArrayList<>();
        this.aliases = aliases;
    }

    public void setDenyMessage(@Nullable String denyMessage) {
        this.denyMessage = denyMessage;
    }

    public void setDenyMessageAsTitle(boolean denyMessageAsTitle) {
        this.denyMessageAsTitle = denyMessageAsTitle;
    }

    public void setExpireMessageAsTitle(boolean expireMessageAsTitle) {
        this.expireMessageAsTitle = expireMessageAsTitle;
    }

    public void setSendExpireMessage(boolean sendExpireMessage) {
        this.sendExpireMessage = sendExpireMessage;
    }

    public void setExpireMessage(@Nullable String expireMessage) {
        this.expireMessage = expireMessage;
    }

    public void setGroupsTime(Map<String, Long> groupsTime) {
        if(groupsTime == null) {
            groupsTime = new HashMap<>();
        }
        this.groupsTime = groupsTime;
    }

    public void setCooldownTime(long cooldownTime) {
        this.cooldownTime = cooldownTime;
    }

    public void setMinArgs(int minArgs) {
        this.minArgs = minArgs;
    }

}
