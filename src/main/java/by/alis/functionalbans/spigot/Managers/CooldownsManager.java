package by.alis.functionalbans.spigot.Managers;

import by.alis.functionalbans.spigot.Additional.Other.OtherUtils;
import by.alis.functionalbans.spigot.FunctionalBansSpigot;
import by.alis.functionalbans.spigot.Managers.Files.FileAccessor;
import by.alis.functionalbans.spigot.Managers.TimeManagers.TimeSettingsAccessor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.TreeMap;

import static by.alis.functionalbans.databases.DataBases.getSQLiteManager;
import static by.alis.functionalbans.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalbans.spigot.Additional.Other.TextUtils.setColors;
import static by.alis.functionalbans.spigot.Managers.Files.SFAccessor.getFileAccessor;

public class CooldownsManager {
    private static final TimeSettingsAccessor timeSettingsAccessor = new TimeSettingsAccessor();
    public CooldownsManager() {}

    private static int BAN_COOLDOWN = 0;
    private static int MUTE_COOLDOWN = 0;
    private static int KICK_COOLDOWN = 0;
    private static int UNBAN_COOLDOWN = 0;
    private static int UNBANALL_COOLDOWN = 0;
    private static int CRAZYKICK_COOLDOWN = 0;
    private static int DUPEIP_COOLDOWN = 0;
    private static int CHEATCHECK_COOLDOWN = 0;
    public static final TreeMap<String, Long> cooldowns = new TreeMap<>();

    public static void setCooldown(Player player, String command) {
        if (getConfigSettings().isCooldownsEnabled()) {
            if (!player.hasPermission("functionalbans.cooldowns.bypass")) {
                switch (command) {
                    case "ban": {
                        if(BAN_COOLDOWN > 0) {
                            cooldowns.put(player.getName() + ":" + command, System.currentTimeMillis() + timeSettingsAccessor.getTimeManager().convertFromSecToMillis(BAN_COOLDOWN));
                        }
                        break;
                    }
                    case "kick": {
                        if(KICK_COOLDOWN > 0) {
                            cooldowns.put(player.getName() + ":" + command, System.currentTimeMillis() + timeSettingsAccessor.getTimeManager().convertFromSecToMillis(KICK_COOLDOWN));
                        }
                        break;
                    }
                    case "mute": {
                        if(MUTE_COOLDOWN > 0) {
                            cooldowns.put(player.getName() + ":" + command, System.currentTimeMillis() + timeSettingsAccessor.getTimeManager().convertFromSecToMillis(MUTE_COOLDOWN));
                        }
                        break;
                    }
                    case "unban": {
                        if(UNBAN_COOLDOWN > 0) {
                            cooldowns.put(player.getName() + ":" + command, System.currentTimeMillis() + timeSettingsAccessor.getTimeManager().convertFromSecToMillis(UNBAN_COOLDOWN));
                        }
                        break;
                    }
                    case "unbanall": {
                        if(UNBANALL_COOLDOWN > 0) {
                            cooldowns.put(player.getName() + ":" + command, System.currentTimeMillis() + timeSettingsAccessor.getTimeManager().convertFromSecToMillis(UNBANALL_COOLDOWN));
                        }
                    }
                    case "crazykick": {
                        if(CRAZYKICK_COOLDOWN > 0) {
                            cooldowns.put(player.getName() + ":" + command, System.currentTimeMillis() + timeSettingsAccessor.getTimeManager().convertFromSecToMillis(CRAZYKICK_COOLDOWN));
                        }
                    }
                    case "dupeip": {
                        if(DUPEIP_COOLDOWN > 0) {
                            cooldowns.put(player.getName() + ":" + command, System.currentTimeMillis() + timeSettingsAccessor.getTimeManager().convertFromSecToMillis(DUPEIP_COOLDOWN));
                        }
                    }
                    case "cheatcheck": {
                        if(CHEATCHECK_COOLDOWN > 0) {
                            cooldowns.put(player.getName() + ":" + command, System.currentTimeMillis() + timeSettingsAccessor.getTimeManager().convertFromSecToMillis(CHEATCHECK_COOLDOWN));
                        }
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
                player.sendMessage(setColors(getFileAccessor().getLang().getString("other.cooldown").replace("%1$f", command).replace("%2$f", timeSettingsAccessor.getTimeManager().convertFromMillis(currentTime))));
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
        Bukkit.getScheduler().runTaskAsynchronously(FunctionalBansSpigot.getProvidingPlugin(FunctionalBansSpigot.class), () -> {
            FileAccessor accessor = new FileAccessor();
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
                if(accessor.getGeneralConfig().contains("plugin-settings.cooldowns.command.unban") && OtherUtils.isNumber(accessor.getGeneralConfig().getString("plugin-settings.cooldowns.command.unban"))) {
                    UNBAN_COOLDOWN = Integer.parseInt(accessor.getGeneralConfig().getString("plugin-settings.cooldowns.command.unban"));
                }
                if(accessor.getGeneralConfig().contains("plugin-settings.cooldowns.command.unbanall") && OtherUtils.isNumber(accessor.getGeneralConfig().getString("plugin-settings.cooldowns.command.unbanall"))) {
                    UNBAN_COOLDOWN = Integer.parseInt(accessor.getGeneralConfig().getString("plugin-settings.cooldowns.command.unbanall"));
                }
                if(accessor.getGeneralConfig().contains("plugin-settings.cooldowns.command.crazykick") && OtherUtils.isNumber(accessor.getGeneralConfig().getString("plugin-settings.cooldowns.command.crazykick"))) {
                    CRAZYKICK_COOLDOWN = Integer.parseInt(accessor.getGeneralConfig().getString("plugin-settings.cooldowns.command.crazykick"));
                }
                if(accessor.getGeneralConfig().contains("plugin-settings.cooldowns.command.dupeip") && OtherUtils.isNumber(accessor.getGeneralConfig().getString("plugin-settings.cooldowns.command.dupeip"))) {
                    DUPEIP_COOLDOWN = Integer.parseInt(accessor.getGeneralConfig().getString("plugin-settings.cooldowns.command.dupeip"));
                }
                if(accessor.getGeneralConfig().contains("plugin-settings.cooldowns.command.cheatcheck") && OtherUtils.isNumber(accessor.getGeneralConfig().getString("plugin-settings.cooldowns.command.cheatcheck"))) {
                    CHEATCHECK_COOLDOWN = Integer.parseInt(accessor.getGeneralConfig().getString("plugin-settings.cooldowns.command.cheatcheck"));
                }
            }
        });
    }
}
