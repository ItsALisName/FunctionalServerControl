package by.alis.functionalbans.spigot.Managers;

import by.alis.functionalbans.spigot.Additional.Other.OtherUtils;
import by.alis.functionalbans.spigot.FunctionalBansSpigot;
import by.alis.functionalbans.spigot.Managers.FilesManagers.FileAccessor;
import by.alis.functionalbans.spigot.Managers.TimeManagers.TimeSettingsAccessor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static by.alis.functionalbans.databases.StaticBases.getSQLiteManager;
import static by.alis.functionalbans.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalbans.spigot.Additional.Other.TextUtils.setColors;

public class CooldownsManager {

    private static final FileAccessor accessor = new FileAccessor();
    private static final TimeSettingsAccessor timeSettingsAccessor = new TimeSettingsAccessor();
    public CooldownsManager() {}

    private static int BAN_COOLDOWN = 0;
    private static int MUTE_COOLDOWN = 0;
    private static int KICK_COOLDOWN = 0;
    private static int TEMPBAN_COOLDOWN = 0;
    public static final TreeMap<String, Long> cooldowns = new TreeMap<>();

    public static void setCooldown(Player player, String command) {
        if (getConfigSettings().isCooldownsEnabled()) {
            if (!player.hasPermission("functionalbans.cooldowns.bypass")) {
                switch (command) {
                    case "ban": {
                        cooldowns.put(player.getName() + ":" + command, System.currentTimeMillis() + timeSettingsAccessor.getTimeManager().convertFromSecToMillis(BAN_COOLDOWN));
                        break;
                    }
                    case "kick": {
                        cooldowns.put(player.getName() + ":" + command, System.currentTimeMillis() + timeSettingsAccessor.getTimeManager().convertFromSecToMillis(KICK_COOLDOWN));
                        break;
                    }
                    case "mute": {
                        cooldowns.put(player.getName() + ":" + command, System.currentTimeMillis() + timeSettingsAccessor.getTimeManager().convertFromSecToMillis(MUTE_COOLDOWN));
                        break;
                    }
                    case "tempban": {
                        cooldowns.put(player.getName() + ":" + command, System.currentTimeMillis() + timeSettingsAccessor.getTimeManager().convertFromSecToMillis(TEMPBAN_COOLDOWN));
                        break;
                    }
                }
            }
        }
    }

    public static boolean playerHasCooldown(Player player, String command) {
        boolean has = false;
        if(cooldowns.isEmpty()) return false;
        if(player.hasPermission("functionalbans.cooldowns.bypass")) return has;
        for(Map.Entry<String, Long> e : cooldowns.entrySet()) {
            if(e.getKey().equalsIgnoreCase(player.getName() + ":" + command)) {
                if(System.currentTimeMillis() > e.getValue()) {
                    cooldowns.remove(player.getName() + ":" + command);
                    has = false;
                    break;
                } else {
                    has = true;
                    break;
                }
            }
        }
        return has;
    }

    public static void notifyAboutCooldown(Player player, String command) {
        long currentTime = 0;
        for(Map.Entry<String, Long> e : cooldowns.entrySet()) {
            if(e.getKey().equalsIgnoreCase(player.getName() + ":" + command)) {
                currentTime = e.getValue() - System.currentTimeMillis();
                player.sendMessage(setColors(accessor.getLang().getString("other.cooldown").replace("%1$f", command).replace("%2$f", timeSettingsAccessor.getTimeManager().convertFromMillis(currentTime))));
                break;
            }
        }
    }

    public static void saveCooldowns() {
        if(getConfigSettings().isSaveCooldowns()) {
            switch (getConfigSettings().getStorageType()) {
                case SQLITE: {
                    getSQLiteManager().saveCooldowns(cooldowns);
                    break;
                }
                case MYSQL: {
                    break;
                }
                case H2: {
                    break;
                }
                default: {
                    getSQLiteManager().saveCooldowns(cooldowns);
                    break;
                }
            }
        }
    }

    public static void loadCooldowns() {
        Bukkit.getScheduler().runTaskAsynchronously(FunctionalBansSpigot.getProvidingPlugin(FunctionalBansSpigot.class), () -> {
            if(getConfigSettings().isSaveCooldowns()) {
                cooldowns.clear();
                for(String pac : getSQLiteManager().getPlayersAndCommandsFromCooldowns()) {
                    int indexOf = getSQLiteManager().getPlayersAndCommandsFromCooldowns().indexOf(pac);
                    cooldowns.put(pac, getSQLiteManager().getTimesFromCooldowns().get(indexOf));
                }
            }
        });
    }

    public static void setupCooldowns() {
        if (getConfigSettings().isCooldownsEnabled()) {
            if (accessor.getGeneralConfig().contains("plugin-settings.cooldowns.command.ban") && OtherUtils.isNumber(accessor.getGeneralConfig().getString("plugin-settings.cooldowns.command.ban"))) {
                BAN_COOLDOWN = Integer.parseInt(accessor.getGeneralConfig().getString("plugin-settings.cooldowns.command.ban"));
            }
            if (accessor.getGeneralConfig().contains("plugin-settings.cooldowns.command.kick") && OtherUtils.isNumber(accessor.getGeneralConfig().getString("plugin-settings.cooldowns.command.kick"))) {
                KICK_COOLDOWN = Integer.parseInt(accessor.getGeneralConfig().getString("plugin-settings.cooldowns.command.kick"));
            }
            if (accessor.getGeneralConfig().contains("plugin-settings.cooldowns.command.mute") && OtherUtils.isNumber(accessor.getGeneralConfig().getString("plugin-settings.cooldowns.command.mute"))) {
                MUTE_COOLDOWN = Integer.parseInt(accessor.getGeneralConfig().getString("plugin-settings.cooldowns.command.mute"));
            }
            if (accessor.getGeneralConfig().contains("plugin-settings.cooldowns.command.tempban") && OtherUtils.isNumber(accessor.getGeneralConfig().getString("plugin-settings.cooldowns.command.tempban"))) {
                TEMPBAN_COOLDOWN = Integer.parseInt(accessor.getGeneralConfig().getString("plugin-settings.cooldowns.command.tempban"));
            }
        }
    }
}
