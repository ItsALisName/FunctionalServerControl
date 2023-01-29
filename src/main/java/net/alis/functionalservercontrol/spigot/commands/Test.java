package net.alis.functionalservercontrol.spigot.commands;

import net.alis.functionalservercontrol.api.FunctionalApi;
import net.alis.functionalservercontrol.api.interfaces.FunctionalPlayer;
import net.alis.functionalservercontrol.api.interfaces.OfflineFunctionalPlayer;
import net.alis.functionalservercontrol.spigot.FunctionalServerControlSpigot;
import net.alis.functionalservercontrol.spigot.additional.textcomponents.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class Test implements CommandExecutor {

    private final String TEST_COMMAND = "JUST TEST COMMAND, FOR SOME TESTS";
    public Test(FunctionalServerControlSpigot plugin) {
        plugin.getCommand("test").setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(args[0].equalsIgnoreCase("fp")) {
            for(FunctionalPlayer player : FunctionalApi.getOnlinePlayers()) sender.sendMessage(player.nickname());
            sender.sendMessage("===============");
            FunctionalPlayer player = FunctionalPlayer.get(sender.getName());
            sender.sendMessage("NAME: " + player.nickname());
            player.expansion().message(new Component.SimplifiedComponent("&cTEST============================================gytyrxcfrygvuybhiunjokpiubvyutycopiouigyftcgvubhnjkm").translateDefaultColorCodes());
            return true;
        }

        if(args[0].equalsIgnoreCase("ofc")) {
            for(OfflineFunctionalPlayer player : FunctionalApi.getOfflinePlayers()) {
                sender.sendMessage("NAME is: " + player.nickname());
                sender.sendMessage("fid is: " + player.getFunctionalId());
                sender.sendMessage("uuid is: " + player.getUniqueId());
            }
        }
        return true;
    }

}
