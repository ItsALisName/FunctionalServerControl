package net.alis.functionalservercontrol.spigot.commands;

import net.alis.functionalservercontrol.api.FunctionalApi;
import net.alis.functionalservercontrol.api.enums.StatsType;
import net.alis.functionalservercontrol.spigot.FunctionalServerControlSpigot;
import net.alis.functionalservercontrol.api.interfaces.FunctionalBanEntry;
import net.alis.functionalservercontrol.spigot.additional.reflect.PingReflect;
import net.alis.functionalservercontrol.spigot.managers.ImportManager;
import net.alis.functionalservercontrol.spigot.managers.InetManager;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static net.alis.functionalservercontrol.spigot.additional.containers.StaticContainers.getBannedPlayersContainer;

public class Test implements CommandExecutor {

    private final String TEST_COMMAND = "JUST TEST COMMAND, FOR SOME TESTS";
    FunctionalServerControlSpigot plugin;
    public Test(FunctionalServerControlSpigot plugin) {
        this.plugin = plugin;
        plugin.getCommand("test").setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args[0].equalsIgnoreCase("bans")) {
            @NotNull Set<BanEntry> ipBans = Bukkit.getBanList(BanList.Type.NAME).getBanEntries();
            for (BanEntry entry : ipBans) {
                sender.sendMessage("NAME: " + entry.getTarget());
                sender.sendMessage("SOURCE: " + entry.getSource());
                sender.sendMessage("EXPIRATION: " + entry.getExpiration());
                sender.sendMessage("REASON:" + entry.getReason());
                sender.sendMessage("CREATED: " + entry.getCreated());
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("cmdlimit")) {

            return true;
        }

        if (args[0].equalsIgnoreCase("inetspeed")) {
            new InetManager().preformInetTest(sender);
            return true;
        }

        if (args[0].equalsIgnoreCase("ivanilla")) {
            ImportManager.importDataFromVanilla(sender);
            return true;
        }

        if (args[0].equalsIgnoreCase("banlist")) {
            for (String s : getBannedPlayersContainer().getNameContainer()) {
                sender.sendMessage("NAME: " + s);
            }
            for (String s : getBannedPlayersContainer().getIpContainer()) {
                sender.sendMessage("IP: " + s);
            }
            for (String s : getBannedPlayersContainer().getUUIDContainer()) {
                sender.sendMessage("UUID: " + s);
            }
            for (String s : getBannedPlayersContainer().getIdsContainer()) {
                sender.sendMessage("ID: " + s);
            }
            for (String s : getBannedPlayersContainer().getReasonContainer()) {
                sender.sendMessage("REASON: " + s);
            }
            for (String s : getBannedPlayersContainer().getInitiatorNameContainer()) {
                sender.sendMessage("INITIATOR: " + s);
            }
            for (long s : getBannedPlayersContainer().getBanTimeContainer()) {
                sender.sendMessage("TIME: " + s);
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("api")) {
            FunctionalApi api = FunctionalApi.getFunctionalPlayer();
            if (api != null) {
                for (FunctionalBanEntry banEntry : api.getBans()) {
                    sender.sendMessage("NAME: " + banEntry.getName());
                    sender.sendMessage("UUID: " + banEntry.getUniqueId());
                }
            } else {
                sender.sendMessage("API is null");
            }
        }

        if(args[0].equalsIgnoreCase("testping")) {
            sender.sendMessage("PING: " + PingReflect.getPing((Player) sender));
            return true;
        }

        if (args[0].equalsIgnoreCase("unban")) {
            FunctionalApi api = FunctionalApi.getFunctionalPlayer();
            if (api != null) {
                for (FunctionalBanEntry entry : api.getBans()) {
                    if (entry.getName().equalsIgnoreCase(args[1])) {
                        entry.unban();
                        sender.sendMessage(args[1] + " unbanned!");
                    }
                }
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("api-stats")) {
            FunctionalApi api = FunctionalApi.getFunctionalPlayer();
            if (api != null) {
                String s = api.getPlayerStatistics().getAsPlayer((Player) sender).get(StatsType.Player.STATS_BANS);
                sender.sendMessage(s);
                return true;
            }
        }
        return true;
    }
}
