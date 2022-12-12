package by.alis.functionalbans.spigot.Commands;

import by.alis.functionalbans.spigot.Additional.Enums.BanType;
import by.alis.functionalbans.spigot.Additional.GlobalSettings.GeneralConfigSettings;
import by.alis.functionalbans.spigot.Additional.GlobalSettings.GlobalVariables;
import by.alis.functionalbans.spigot.Additional.Other.OtherUtils;
import by.alis.functionalbans.spigot.Additional.Other.TemporaryCache;
import by.alis.functionalbans.spigot.Additional.Placeholders.TimeLangGlobal;
import by.alis.functionalbans.spigot.FunctionalBansSpigot;
import by.alis.functionalbans.spigot.Managers.BansManagers.BanManager;
import by.alis.functionalbans.spigot.Managers.CooldownsManager;
import by.alis.functionalbans.spigot.Managers.FilesManagers.FileAccessor;
import by.alis.functionalbans.spigot.Managers.TimeManagers.TimeSettingsAccessor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static by.alis.functionalbans.spigot.Additional.Containers.StaticContainers.getBannedPlayersContainer;
import static by.alis.functionalbans.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalbans.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getGlobalVariables;
import static by.alis.functionalbans.spigot.Additional.Other.TextUtils.getReason;

public class KickCommand implements CommandExecutor {

    FunctionalBansSpigot plugin;
    public KickCommand(FunctionalBansSpigot plugin) {
        this.plugin = plugin;
        plugin.getCommand("kick").setExecutor(this);
    }
    TimeSettingsAccessor timeSettingsAccessor = new TimeSettingsAccessor();
    BanManager banManager = new BanManager();
    GeneralConfigSettings generalConfigSettings = new GeneralConfigSettings();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        /*
        sender.sendMessage(this.timeSettingsAccessor.getTimeManager().convertFromMillis(Long.parseLong(args[0])));
        return true;
        */

        if(args[0].equalsIgnoreCase("checktime")) {
            if(this.timeSettingsAccessor.getTimeChecker().checkInputTimeArgument(args[1])) {
                sender.sendMessage("true");
                sender.sendMessage("UNIT: " + this.timeSettingsAccessor.getTimeChecker().getTimeUnit(args[1]));
                sender.sendMessage("NUM: " + this.timeSettingsAccessor.getTimeChecker().getArgNumber(args[1]));
                sender.sendMessage("MILLIS: " + this.timeSettingsAccessor.getTimeManager().convertToMillis(args[1]));
                sender.sendMessage("STRING_MIL: " + this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().convertToMillis(args[1]) - System.currentTimeMillis()));
                return true;
            }
        }


        if(args[0].equalsIgnoreCase("data")) {
            int a = 0,b = 0,c = 0,d = 0,e = 0,f = 0,g = 0,s = 0,l = 0, k = 0;
            for(String id : getBannedPlayersContainer().getIdsContainer()) {
                a = a+1;
                sender.sendMessage("IDS - " + a + ": " + id);
            }
            for(String ip : getBannedPlayersContainer().getIpContainer()) {
                b = b+1;
                sender.sendMessage("IPS - " + b + ": " + ip);
            }
            for(String name : getBannedPlayersContainer().getNameContainer()) {
                c = c+1;
                sender.sendMessage("NAMES - " + c + ": " + name);
            }
            for(String iName : getBannedPlayersContainer().getInitiatorNameContainer()) {
                d = d+1;
                sender.sendMessage("iNames - " + d + ": " + iName);
            }

            for(String reason : getBannedPlayersContainer().getReasonContainer()) {
                e = e+1;
                sender.sendMessage("REASONS - " + e + ": " + reason);
            }

            for(BanType banType : getBannedPlayersContainer().getBanTypesContainer()) {
                f = f+1;
                sender.sendMessage("BAN_TYPES - " + f + ": " + banType);
            }

            for(String rBanDate : getBannedPlayersContainer().getRealBanDateContainer()) {
                g = g+1;
                sender.sendMessage("R_BAN_DATES - " + g + ": " + rBanDate);
            }

            for(String rBanTime : getBannedPlayersContainer().getRealBanTimeContainer()) {
                s = s+1;
                sender.sendMessage("R_BAN_TIMES - " + s + ": " + rBanTime);
            }

            for(String uuid : getBannedPlayersContainer().getUUIDContainer()) {
                l = l+1;
                sender.sendMessage("UUIDS - " + l + ": " + uuid);
            }

            for(long unbanTime : getBannedPlayersContainer().getBanTimeContainer()) {
                k = k+1;
                sender.sendMessage("UNBAN_TIMES - " + k + ": " + String.valueOf(unbanTime));
            }

            return true;

        }

        if(args[0].equalsIgnoreCase("groups")) {
            for(String g : getConfigSettings().getPossibleGroups()) {
                sender.sendMessage(g);
            }
            return true;
        }


        if(args[0].equalsIgnoreCase("ban")) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
            if(player == null) {
                String reason = getReason(args, 3);
                this.banManager.preformBan(args[1], BanType.PERMANENT_NOT_IP, reason, ((Player)sender), -1, true);
                return true;
            }
            String reason = getReason(args, 3);
            this.banManager.preformBan(player, BanType.PERMANENT_NOT_IP, reason, ((Player)sender), -1, true);
        }

        if(args[0].equalsIgnoreCase("banip")) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
            if(player == null) {
                String reason = getReason(args, 3);
                String p = args[1];
                this.banManager.preformBan(p, BanType.PERMANENT_IP, reason, ((Player)sender), -1, true);
                return true;
            }
            String reason = getReason(args, 3);
            this.banManager.preformBan(player, BanType.PERMANENT_IP, reason, ((Player)sender), -1, true);
        }

        if(args[0].equalsIgnoreCase("tempban")) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
            long time = this.timeSettingsAccessor.getTimeManager().convertToMillis(args[2]);
            String reason = getReason(args, 3);
            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                this.banManager.preformBan(player, BanType.TIMED_NOT_IP, reason, ((Player)sender), time, true);
            });
        }

        if(args[0].equalsIgnoreCase("tempbanip")) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
            if(player == null) {
                long time = this.timeSettingsAccessor.getTimeManager().convertToMillis(args[2]);
                String reason = getReason(args, 3);
                Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                    this.banManager.preformBan(args[1], BanType.TIMED_IP, reason, ((Player)sender), time, true);
                });
                return true;
            }
            long time = this.timeSettingsAccessor.getTimeManager().convertToMillis(args[2]);
            String reason = getReason(args, 3);
            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                this.banManager.preformBan(player, BanType.TIMED_IP, reason, ((Player)sender), time, true);
            });
            return true;
        }

        if(args[0].equalsIgnoreCase("async")) {
            TemporaryCache cache = new TemporaryCache();
            sender.sendMessage(String.valueOf(TemporaryCache.getUnsafeBannedPlayers().size()));
            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                this.banManager.preformBan("azaza", BanType.PERMANENT_NOT_IP, "AZAZA", sender, 1000, true);
            });
            return true;
        }

        if(args[0].equalsIgnoreCase("getip")) {
            Player player = (Player) sender;
            sender.sendMessage(String.valueOf(player.getAddress().getAddress().getHostAddress()));
            return true;
        }

        if(args[0].equalsIgnoreCase("cd")) {
            sender.sendMessage("COUNT: " + CooldownsManager.cooldowns.size());
            sender.sendMessage("VAL: " + CooldownsManager.cooldowns.values());
            return true;
        }

        if(args[0].equalsIgnoreCase("unban")) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
            if(OtherUtils.isNotNullPlayer(args[1])) {
                Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                    this.banManager.preformUnban(player, sender, getReason(args, 1), true);
                });
            } else {
                Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                    this.banManager.preformUnban(args[1], sender, getReason(args, 1), true);
                });
            }
            return true;
        }

        if(args[0].equalsIgnoreCase("sett")) {
            sender.sendMessage("LANG " + generalConfigSettings.getConsoleLanguageMode());
            sender.sendMessage("LANG2 " + generalConfigSettings.getGlobalLanguage());
            FileAccessor fileAccessor = new FileAccessor();
            sender.sendMessage("LANG TRUE " + fileAccessor.getGeneralConfig().getString("plugin-settings.global-language"));
            sender.sendMessage("PURGE: " + String.valueOf(getConfigSettings().isPurgeConfirmation()));
            return true;
        }

        /*
        sender.sendMessage(this.timeSettingsAccessor.getTimeChecker().getArgNumber(args[0]) + " " + timeLang.getTimeLang(args[0]));
        sender.sendMessage(this.timeSettingsAccessor.getTimeManager().convertToMillis(args[0]) + " миллисекунд");
        */
        return true;

    }
}
