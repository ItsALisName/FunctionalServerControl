package net.alis.functionalservercontrol.spigot.additional.containers;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheatsCheckingPlayers {


    private final List<Player> checkingPlayers = new ArrayList<>();
    private final Map<Player, CommandSender> initiatorsAndHisPlayers = new HashMap<>();
    private final List<String> checkReason = new ArrayList<>();



    public List<Player> getCheckingPlayers() {
        return this.checkingPlayers;
    }
    public void setCheckingPlayers(Player checkingPlayer) {
        this.checkingPlayers.add(checkingPlayer);
    }

    public void setInitiatorsAndHisPlayers(Player player, CommandSender initiator) {
        this.initiatorsAndHisPlayers.put(player, initiator);
    }

    public Map<Player, CommandSender> getInitiatorsAndHisPlayers() {
        return initiatorsAndHisPlayers;
    }

    public List<String> getCheckReason() {
        return checkReason;
    }
    public void setCheckReason(String checkReason) {
        this.checkReason.add(checkReason);
    }
}
