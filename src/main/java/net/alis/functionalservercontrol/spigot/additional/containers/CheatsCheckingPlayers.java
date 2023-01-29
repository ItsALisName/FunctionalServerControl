package net.alis.functionalservercontrol.spigot.additional.containers;

import net.alis.functionalservercontrol.api.interfaces.FunctionalPlayer;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheatsCheckingPlayers {


    private final List<FunctionalPlayer> checkingPlayers = new ArrayList<>();
    private final Map<FunctionalPlayer, CommandSender> initiatorsAndHisPlayers = new HashMap<>();
    private final List<String> checkReason = new ArrayList<>();



    public List<FunctionalPlayer> getCheckingPlayers() {
        return this.checkingPlayers;
    }
    public void setCheckingPlayers(FunctionalPlayer checkingPlayer) {
        this.checkingPlayers.add(checkingPlayer);
    }



    public void setInitiatorsAndHisPlayers(FunctionalPlayer player, CommandSender initiator) {
        this.initiatorsAndHisPlayers.put(player, initiator);
    }

    public Map<FunctionalPlayer, CommandSender> getInitiatorsAndHisPlayers() {
        return initiatorsAndHisPlayers;
    }

    public List<String> getCheckReason() {
        return checkReason;
    }
    public void setCheckReason(String checkReason) {
        this.checkReason.add(checkReason);
    }
}
