package net.alis.functionalservercontrol.spigot.additional.globalsettings;

import net.alis.functionalservercontrol.spigot.managers.TaskManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static net.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

public class ProtectionSettings {

    private @Getter @Setter boolean packetLimiterEnabled;
    private @Getter @Setter int maxPlayerPackets;
    private @Getter @Setter boolean notifyAdminsAboutOverPackets;
    private @Getter @Setter int packetsCheckInterval;
    private final @Getter List<String> overPacketPunish = new ArrayList<>();


    private @Getter @Setter boolean pingLimiterEnabled;
    private @Getter @Setter int maxAllowedPing;
    private final @Getter List<String> pingLimiterActions = new ArrayList<>();


    private @Getter @Setter boolean accountProtectionEnabled;
    private @Getter @Setter boolean notifyAdminsAboutProtectedAccount;
    private final @Getter HashMap<String, String> protectedAccounts = new HashMap<>();

    private @Getter @Setter boolean itemFixerEnabled;
    private @Getter @Setter boolean removeInvalidEnchants;
    private @Getter @Setter boolean checkEnchants;
    private @Getter @Setter boolean checkItemsOnJoin;
    private final @Getter List<String> itemFixerIgnoredTags = new ArrayList<>();
    private final @Getter List<String> itemFixerIgnoredWorlds = new ArrayList<>();


    private @Getter @Setter boolean fixLecternCrash;
    private @Getter @Setter boolean notifyAboutLecternCrash;


    public void setItemFixerIgnoredTags(List<String> itemFixerIgnoredTags) {
        this.itemFixerIgnoredTags.clear();
        this.itemFixerIgnoredTags.addAll(itemFixerIgnoredTags);
    }

    public void setItemFixerIgnoredWorlds(List<String> itemFixerIgnoredWorlds) {
        this.itemFixerIgnoredWorlds.clear();
        this.itemFixerIgnoredWorlds.addAll(itemFixerIgnoredWorlds);
    }

    public void setProtectedAccounts(String name, String ip) {
        this.protectedAccounts.put(name, ip);
    }

    private void setOverPacketPunish(List<String> overPacketPunish) {
        this.overPacketPunish.clear();
        this.overPacketPunish.addAll(overPacketPunish);
    }

    public void setPingLimiterActions(List<String> pingLimiterActions) {
        this.pingLimiterActions.clear();
        this.pingLimiterActions.addAll(pingLimiterActions);
    }

    public void loadProtectionSettings() {
        setPacketLimiterEnabled(getFileAccessor().getProtectionConfig().getBoolean("packet-limiter.enabled"));
        if(isPacketLimiterEnabled()) {
            setNotifyAdminsAboutOverPackets(getFileAccessor().getProtectionConfig().getBoolean("packet-limiter.notify-admins"));
            try {
                setMaxPlayerPackets(getFileAccessor().getProtectionConfig().getInt("packet-limiter.max-player-packets"));
            } catch (RuntimeException ignored) {
                Bukkit.getConsoleSender().sendMessage("Failed to get value 'packet-limiter -> max-player-packets' from config 'protection.yml'. I use '250'");
                setMaxPlayerPackets(250);
            }
            try {
                setPacketsCheckInterval(getFileAccessor().getProtectionConfig().getInt("packet-limiter.check-interval"));
            } catch (RuntimeException ignored) {
                Bukkit.getConsoleSender().sendMessage("Failed to get value 'packet-limiter -> check-interval' from config 'protection.yml'. I use '5'");
                setPacketsCheckInterval(5);
            }
            setOverPacketPunish(getFileAccessor().getProtectionConfig().getStringList("packet-limiter.punishments"));
        }
        TaskManager.preformAsync(() -> {
            setFixLecternCrash(getFileAccessor().getProtectionConfig().getBoolean("lectern-crash-fixer.enabled"));
            if(isFixLecternCrash()) {
                setNotifyAboutLecternCrash(getFileAccessor().getProtectionConfig().getBoolean("lectern-crash-fixer.notify-admins"));
            }
            setItemFixerEnabled(getFileAccessor().getProtectionConfig().getBoolean("item-fixer.enabled"));
            if(isItemFixerEnabled()) {
                setCheckEnchants(getFileAccessor().getProtectionConfig().getBoolean("item-fixer.check-enchants"));
                setRemoveInvalidEnchants(getFileAccessor().getProtectionConfig().getBoolean("item-fixer.check-invalid-item-enchants"));
                setItemFixerIgnoredTags(getFileAccessor().getProtectionConfig().getStringList("item-fixer.ignored-tags"));
                setItemFixerIgnoredWorlds(getFileAccessor().getProtectionConfig().getStringList("item-fixer.ignored-worlds"));
                setCheckItemsOnJoin(getFileAccessor().getProtectionConfig().getBoolean("item-fixer.check-items-on-join"));
            }
            setAccountProtectionEnabled(getFileAccessor().getProtectionConfig().getBoolean("accounts-protection.enabled"));
            if(isAccountProtectionEnabled()) {
                setNotifyAdminsAboutProtectedAccount(getFileAccessor().getProtectionConfig().getBoolean("accounts-protection.notify-admins"));
                for(String name : getFileAccessor().getProtectionConfig().getConfigurationSection("accounts-protection.accounts").getKeys(false)) {
                    setProtectedAccounts(name, getFileAccessor().getProtectionConfig().getString("accounts-protection.accounts." + name));
                }
            }
            setPingLimiterEnabled(getFileAccessor().getProtectionConfig().getBoolean("ping-limiter.enabled"));
            if(isPingLimiterEnabled()) {
                setMaxAllowedPing(getFileAccessor().getProtectionConfig().getInt("ping-limiter.max-ping"));
                setPingLimiterActions(getFileAccessor().getProtectionConfig().getStringList("ping-limiter.punishments"));
            }
        });
    }

    public void reloadProtectionSettings() {
        setPacketLimiterEnabled(getFileAccessor().getProtectionConfig().getBoolean("packet-limiter.enabled"));
        if(isPacketLimiterEnabled()) {
            setNotifyAdminsAboutOverPackets(getFileAccessor().getProtectionConfig().getBoolean("packet-limiter.notify-admins"));
            try {
                setMaxPlayerPackets(getFileAccessor().getProtectionConfig().getInt("packet-limiter.max-player-packets"));
            } catch (RuntimeException ignored) {
                Bukkit.getConsoleSender().sendMessage("Failed to get value 'packet-limiter -> max-player-packets' from config 'protection.yml'. I use '500'");
                setMaxPlayerPackets(500);
            }
            try {
                setPacketsCheckInterval(getFileAccessor().getProtectionConfig().getInt("packet-limiter.check-interval"));
            } catch (RuntimeException ignored) {
                Bukkit.getConsoleSender().sendMessage("Failed to get value 'packet-limiter -> check-interval' from config 'protection.yml'. I use '5'");
                setPacketsCheckInterval(5);
            }
            setOverPacketPunish(getFileAccessor().getProtectionConfig().getStringList("packet-limiter.punishments"));
        }
        setFixLecternCrash(getFileAccessor().getProtectionConfig().getBoolean("lectern-crash-fixer.enabled"));
        if(isFixLecternCrash()) {
            setNotifyAboutLecternCrash(getFileAccessor().getProtectionConfig().getBoolean("lectern-crash-fixer.notify-admins"));
        }
        setItemFixerEnabled(getFileAccessor().getProtectionConfig().getBoolean("item-fixer.enabled"));
        if(isItemFixerEnabled()) {
            setCheckEnchants(getFileAccessor().getProtectionConfig().getBoolean("item-fixer.check-enchants"));
            setRemoveInvalidEnchants(getFileAccessor().getProtectionConfig().getBoolean("item-fixer.check-invalid-item-enchants"));
            setItemFixerIgnoredTags(getFileAccessor().getProtectionConfig().getStringList("item-fixer.ignored-tags"));
            setItemFixerIgnoredWorlds(getFileAccessor().getProtectionConfig().getStringList("item-fixer.ignored-worlds"));
            setCheckItemsOnJoin(getFileAccessor().getProtectionConfig().getBoolean("item-fixer.check-items-on-join"));
        }

        setAccountProtectionEnabled(getFileAccessor().getProtectionConfig().getBoolean("accounts-protection.enabled"));
        if(isAccountProtectionEnabled()) {
            this.protectedAccounts.clear();
            setNotifyAdminsAboutProtectedAccount(getFileAccessor().getProtectionConfig().getBoolean("accounts-protection.notify-admins"));
            for(String name : getFileAccessor().getProtectionConfig().getConfigurationSection("accounts-protection.accounts").getKeys(false)) {
                setProtectedAccounts(name, getFileAccessor().getProtectionConfig().getString("accounts-protection.accounts." + name));
            }
        }

        setPingLimiterEnabled(getFileAccessor().getProtectionConfig().getBoolean("ping-limiter.enabled"));
        if(isPingLimiterEnabled()) {
            setMaxAllowedPing(getFileAccessor().getProtectionConfig().getInt("ping-limiter.max-ping"));
            setPingLimiterActions(getFileAccessor().getProtectionConfig().getStringList("ping-limiter.punishments"));
        }
    }

}
