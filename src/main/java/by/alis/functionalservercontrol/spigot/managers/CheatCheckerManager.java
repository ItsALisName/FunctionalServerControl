package by.alis.functionalservercontrol.spigot.managers;

import by.alis.functionalservercontrol.spigot.additional.coreadapters.CoreAdapter;
import by.alis.functionalservercontrol.spigot.additional.misc.TemporaryCache;
import by.alis.functionalservercontrol.spigot.FunctionalServerControl;
import by.alis.functionalservercontrol.spigot.managers.time.TimeSettingsAccessor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static by.alis.functionalservercontrol.databases.DataBases.getSQLiteManager;
import static by.alis.functionalservercontrol.spigot.additional.containers.StaticContainers.getCheckingCheatsPlayers;
import static by.alis.functionalservercontrol.spigot.additional.globalsettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalservercontrol.spigot.additional.globalsettings.StaticSettingsAccessor.getGlobalVariables;
import static by.alis.functionalservercontrol.spigot.additional.misc.TextUtils.isTextNotNull;
import static by.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;
import static by.alis.functionalservercontrol.spigot.additional.misc.WorldTimeAndDateClass.getDate;
import static by.alis.functionalservercontrol.spigot.additional.misc.WorldTimeAndDateClass.getTime;
import static by.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

public class CheatCheckerManager {

    private static final CheatCheckerManager cheatCheckerManager = new CheatCheckerManager();
    public static CheatCheckerManager getCheatCheckerManager() {
        return cheatCheckerManager;
    }

    private final TimeSettingsAccessor timeSettingsAccessor = new TimeSettingsAccessor();

    private final Map<Player, Integer> countdown = new HashMap<>();

    public boolean isPlayerChecking(Player player) {
        return getCheckingCheatsPlayers().getCheckingPlayers().contains(player);
    }

    public String getCheckReason(Player player) {
        return getCheckingCheatsPlayers().getCheckReason().get(getCheckingCheatsPlayers().getCheckingPlayers().indexOf(player));
    }

    public CommandSender getCheckInitiator(Player player) {
        return getCheckingCheatsPlayers().getInitiatorsAndHisPlayers().get(player);
    }

    public void startCheck(CommandSender initiator, Player player, @Nullable String reason, int checkTime) {
        if(isPlayerChecking(player)) {
            initiator.sendMessage(setColors(getFileAccessor().getLang().getString("commands.cheatcheck.player-already-checking")));
            return;
        }

        if(!isTextNotNull(reason)) {
            reason = getGlobalVariables().getDefaultReason();
        }

        if(reason.equalsIgnoreCase(getGlobalVariables().getDefaultReason())) {
            if(!getConfigSettings().isCheatsCheckAllowedWithoutReason() && !initiator.hasPermission("functionalservercontrol.use.no-reason")) {
                initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.no-reason")));
                return;
            }
        }

        if(getConfigSettings().isProhibitYourselfInteraction()) {
            if(initiator.getName().equalsIgnoreCase(player.getName())) {
                initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.no-yourself-actions")));
                return;
            }
        }

        String initiatorName = null;
        if(initiator instanceof Player) {
            initiatorName = ((Player) initiator).getName();
        } else if(initiator instanceof ConsoleCommandSender) {
            initiatorName = getGlobalVariables().getConsoleVariableName();
        } else {
            initiatorName = initiator.getName();
        }

        if(player.hasPermission("functionalservercontrol.cheatcheck.bypass") && !initiator.hasPermission("functionalservercontrol.bypass-break")) {
            initiator.sendMessage(setColors(getFileAccessor().getLang().getString("commands.cheatcheck.target-bypass")));
            return;
        }

        getCheckingCheatsPlayers().setCheckingPlayers(player);
        getCheckingCheatsPlayers().setInitiatorsAndHisPlayers(player, initiator);
        getCheckingCheatsPlayers().setCheckReason(reason);
        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("commands.cheatcheck.success").replace("%1$f", player.getName())));
        player.sendMessage(setColors(getFileAccessor().getLang().getString("commands.cheatcheck.chat-message-on-start").replace("%1$f", initiatorName).replace("%2$f", reason).replace("%3$f", String.valueOf(checkTime))));
        if(getConfigSettings().isSendTitleOnCheatCheck()) {
            CoreAdapter.getAdapter().sendTitle(player, setColors(getFileAccessor().getLang().getString("commands.cheatcheck.header-title-on-start")), setColors(String.join("\n", getFileAccessor().getLang().getStringList("commands.cheatcheck.footer-title-on-start"))));
        }
        this.countdown.put(player, checkTime);
        TemporaryCache.setCheckingPlayersNames(player.getName());
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: getSQLiteManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.cheatcheck").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", reason).replace("%4$f", getDate() + ", " + getTime()));
            case H2: {}
        }
        runCountdownTimer(player);
    }
    
    

    public void stopCheck(CommandSender initiator, Player player) {
        if(isPlayerChecking(player)) {
            if(getCheckingCheatsPlayers().getInitiatorsAndHisPlayers().get(player) == initiator) {
                getCheckingCheatsPlayers().getInitiatorsAndHisPlayers().remove(player);
                getCheckingCheatsPlayers().getCheckReason().remove(getCheckingCheatsPlayers().getCheckingPlayers().indexOf(player));
                getCheckingCheatsPlayers().getCheckingPlayers().remove(player);
                player.sendMessage(setColors(getFileAccessor().getLang().getString("commands.cheatcheck.chat-message-on-stop")));
                if(getConfigSettings().isSendTitleOnCheatCheck()) {
                    CoreAdapter.getAdapter().sendTitle(player, setColors(getFileAccessor().getLang().getString("commands.cheatcheck.header-title-on-stop")), setColors(String.join("\n", getFileAccessor().getLang().getStringList("commands.cheatcheck.footer-title-on-stop"))));
                }
                TemporaryCache.unsetCheckingPlayersNames(player.getName());
                initiator.sendMessage(setColors(getFileAccessor().getLang().getString("commands.cheatcheck.check-stopped").replace("%1$f", player.getName())));
            } else {
                if(!initiator.hasPermission("functionalservercontrol.cheatcheck.other")) {
                    initiator.sendMessage(setColors(getFileAccessor().getLang().getString("commands.cheatcheck.not-your-player")));
                    return;
                }
                getCheckingCheatsPlayers().getInitiatorsAndHisPlayers().remove(player);
                getCheckingCheatsPlayers().getCheckReason().remove(getCheckingCheatsPlayers().getCheckingPlayers().indexOf(player));
                getCheckingCheatsPlayers().getCheckingPlayers().remove(player);
                player.sendMessage(setColors(getFileAccessor().getLang().getString("commands.cheatcheck.chat-message-on-stop")));
                if(getConfigSettings().isSendTitleOnCheatCheck()) {
                    CoreAdapter.getAdapter().sendTitle(player, setColors(getFileAccessor().getLang().getString("commands.cheatcheck.header-title-on-stop")), setColors(String.join("\n", getFileAccessor().getLang().getStringList("commands.cheatcheck.footer-title-on-stop"))));
                }
                TemporaryCache.unsetCheckingPlayersNames(player.getName());
                initiator.sendMessage(setColors(getFileAccessor().getLang().getString("commands.cheatcheck.check-stopped").replace("%1$f", player.getName())));
            }
        } else {
            initiator.sendMessage(setColors(getFileAccessor().getLang().getString("commands.cheatcheck.player-not-checking-now")));
        }
    }


    public void preformActionOnQuit(Player player) {
        if (isPlayerChecking(player)) {
            if (countdown.containsKey(player)) {
                TemporaryCache.unsetCheckingPlayersNames(player.getName());
                getCheckingCheatsPlayers().getInitiatorsAndHisPlayers().get(player).sendMessage(setColors(getFileAccessor().getLang().getString("commands.cheatcheck.target-quit")));
                getCheckingCheatsPlayers().getInitiatorsAndHisPlayers().remove(player);
                getCheckingCheatsPlayers().getCheckReason().remove(getCheckingCheatsPlayers().getCheckingPlayers().indexOf(player));
                getCheckingCheatsPlayers().getCheckingPlayers().remove(player);
                for (String action : getConfigSettings().getActionIfQuitDuringCheatCheck()) {
                    Bukkit.getScheduler().runTask(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class), () -> {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action.replace("%1$f", player.getName()));
                    });
                }
            }
        }
    }


    public void preformActionOnConfirm(CommandSender initiator, Player player) {
        if(isPlayerChecking(player)) {

            if(getCheckingCheatsPlayers().getInitiatorsAndHisPlayers().get(player) != initiator) {
                if(!initiator.hasPermission("functionalservercontrol.cheatcheck.other")) {
                    initiator.sendMessage(setColors(getFileAccessor().getLang().getString("commands.cheatcheck.not-your-player")));
                    return;
                }
            }

            TemporaryCache.unsetCheckingPlayersNames(player.getName());
            getCheckingCheatsPlayers().getInitiatorsAndHisPlayers().get(player).sendMessage(setColors(getFileAccessor().getLang().getString("commands.cheatcheck.target-quit")));
            getCheckingCheatsPlayers().getInitiatorsAndHisPlayers().remove(player);
            getCheckingCheatsPlayers().getCheckReason().remove(getCheckingCheatsPlayers().getCheckingPlayers().indexOf(player));
            getCheckingCheatsPlayers().getCheckingPlayers().remove(player);
            for (String action : getConfigSettings().getActionIfValidCheatCheck()) {
                Bukkit.getScheduler().runTask(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class), () -> {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action.replace("%1$f", player.getName()));
                });
            }
        }
    }


    public void preformActionOnFail(CommandSender initiator, Player player) {
        if(isPlayerChecking(player)) {

            if(getCheckingCheatsPlayers().getInitiatorsAndHisPlayers().get(player) != initiator) {
                if(!initiator.hasPermission("functionalservercontrol.cheatcheck.other")) {
                    initiator.sendMessage(setColors(getFileAccessor().getLang().getString("commands.cheatcheck.not-your-player")));
                    return;
                }
            }

            TemporaryCache.unsetCheckingPlayersNames(player.getName());
            getCheckingCheatsPlayers().getInitiatorsAndHisPlayers().get(player).sendMessage(setColors(getFileAccessor().getLang().getString("commands.cheatcheck.target-quit")));
            getCheckingCheatsPlayers().getInitiatorsAndHisPlayers().remove(player);
            getCheckingCheatsPlayers().getCheckReason().remove(getCheckingCheatsPlayers().getCheckingPlayers().indexOf(player));
            getCheckingCheatsPlayers().getCheckingPlayers().remove(player);
            for (String action : getConfigSettings().getActionIfFailedCheatCheck()) {
                Bukkit.getScheduler().runTask(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class), () -> {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action.replace("%1$f", player.getName()));
                });
            }
        }
    }


    private void runCountdownTimer(Player player) {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if(player != null && isPlayerChecking(player)) {
                    int tLeft = countdown.get(player) - 1;
                    countdown.replace(player, tLeft);
                    CoreAdapter.getAdapter().sendActionBar(player, setColors(getFileAccessor().getLang().getString("commands.cheatcheck.actionbar-message").replace("%1$f", timeSettingsAccessor.getTimeManager().convertFromMillis(timeSettingsAccessor.getTimeManager().convertFromSecToMillis(tLeft)))));
                    if(tLeft <= 0) {
                        countdown.remove(player);
                        TemporaryCache.unsetCheckingPlayersNames(player.getName());
                        for(String action : getConfigSettings().getActionIfTimeLeftOnCheatCheck()) {
                            Bukkit.getScheduler().runTask(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class), () -> {
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action.replace("%1$f", player.getName()));
                            });
                        }
                        this.cancel();
                    }
                } else {
                    countdown.remove(player);
                    this.cancel();
                }
            }
        };
        new Timer("countdownCheck").scheduleAtFixedRate(timerTask, 0, 1000L);
    }

}
