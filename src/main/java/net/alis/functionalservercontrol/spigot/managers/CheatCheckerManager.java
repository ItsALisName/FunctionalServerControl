package net.alis.functionalservercontrol.spigot.managers;

import net.alis.functionalservercontrol.api.interfaces.FunctionalPlayer;
import net.alis.functionalservercontrol.libraries.net.md_5.bungee.api.ChatMessageType;
import net.alis.functionalservercontrol.spigot.additional.misc.TemporaryCache;
import net.alis.functionalservercontrol.spigot.additional.misc.TextUtils;
import net.alis.functionalservercontrol.api.events.AsyncPlayerCheatCheckPreprocessEvent;
import net.alis.functionalservercontrol.spigot.managers.time.TimeSettingsAccessor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static net.alis.functionalservercontrol.spigot.additional.containers.StaticContainers.getCheckingCheatsPlayers;
import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;
import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getGlobalVariables;
import static net.alis.functionalservercontrol.spigot.additional.misc.WorldTimeAndDateClass.getDate;
import static net.alis.functionalservercontrol.spigot.additional.misc.WorldTimeAndDateClass.getTime;
import static net.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

public class CheatCheckerManager {

    private static final CheatCheckerManager cheatCheckerManager = new CheatCheckerManager();
    public static CheatCheckerManager getCheatCheckerManager() {
        return cheatCheckerManager;
    }

    private final TimeSettingsAccessor timeSettingsAccessor = new TimeSettingsAccessor();

    private final Map<FunctionalPlayer, Integer> countdown = new HashMap<>();

    public boolean isPlayerChecking(FunctionalPlayer player) {
        return getCheckingCheatsPlayers().getCheckingPlayers().contains(player);
    }

    public String getCheckReason(FunctionalPlayer player) {
        return getCheckingCheatsPlayers().getCheckReason().get(getCheckingCheatsPlayers().getCheckingPlayers().indexOf(player));
    }

    public CommandSender getCheckInitiator(FunctionalPlayer player) {
        return getCheckingCheatsPlayers().getInitiatorsAndHisPlayers().get(player);
    }

    public void startCheck(CommandSender initiator, FunctionalPlayer player, @Nullable String reason, int checkTime) {
        if(isPlayerChecking(player)) {
            initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.cheatcheck.player-already-checking")));
            return;
        }

        if(!TextUtils.isTextNotNull(reason)) {
            reason = getGlobalVariables().getDefaultReason();
        }

        AsyncPlayerCheatCheckPreprocessEvent cheatCheckPreprocessEvent = new AsyncPlayerCheatCheckPreprocessEvent(player, initiator, reason);
        if(getConfigSettings().isApiEnabled()) {
            Bukkit.getPluginManager().callEvent(cheatCheckPreprocessEvent);
        }

        if(cheatCheckPreprocessEvent.isCancelled()) return;

        if(reason.equalsIgnoreCase(getGlobalVariables().getDefaultReason())) {
            if(!getConfigSettings().isCheatsCheckAllowedWithoutReason() && !initiator.hasPermission("functionalservercontrol.use.no-reason")) {
                initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.no-reason")));
                cheatCheckPreprocessEvent.setCancelled(true);
                return;
            }
        }

        reason = TextUtils.isTextNotNull(cheatCheckPreprocessEvent.getReason()) ? cheatCheckPreprocessEvent.getReason() : reason;

        if(getConfigSettings().isProhibitYourselfInteraction()) {
            if(initiator.getName().equalsIgnoreCase(player.nickname())) {
                initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.no-yourself-actions")));
                cheatCheckPreprocessEvent.setCancelled(true);
                return;
            }
        }

        String initiatorName;
        if(initiator instanceof FunctionalPlayer) {
            initiatorName = (initiator).getName();
        } else if(initiator instanceof ConsoleCommandSender) {
            initiatorName = getGlobalVariables().getConsoleVariableName();
        } else {
            initiatorName = initiator.getName();
        }

        if(player.hasPermission("functionalservercontrol.cheatcheck.bypass") && !initiator.hasPermission("functionalservercontrol.bypass-break")) {
            initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.cheatcheck.target-bypass")));
            return;
        }

        getCheckingCheatsPlayers().setCheckingPlayers(player);
        getCheckingCheatsPlayers().setInitiatorsAndHisPlayers(player, initiator);
        getCheckingCheatsPlayers().setCheckReason(reason);
        initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.cheatcheck.success").replace("%1$f", player.nickname())));
        player.message(TextUtils.setColors(getFileAccessor().getLang().getString("commands.cheatcheck.chat-message-on-start").replace("%1$f", initiatorName).replace("%2$f", reason).replace("%3$f", String.valueOf(checkTime))));
        if(getConfigSettings().isSendTitleOnCheatCheck()) {
            player.title(TextUtils.setColors(getFileAccessor().getLang().getString("commands.cheatcheck.header-title-on-start")), TextUtils.setColors(String.join("\n", getFileAccessor().getLang().getStringList("commands.cheatcheck.footer-title-on-start"))));
        }
        this.countdown.put(player, checkTime);
        TemporaryCache.setCheckingPlayersNames(player.nickname());
        BaseManager.getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.cheatcheck").replace("%1$f", initiatorName).replace("%2$f", player.nickname()).replace("%3$f", reason).replace("%4$f", getDate() + ", " + getTime()));
        runCountdownTimer(player);
    }
    
    

    public void stopCheck(CommandSender initiator, FunctionalPlayer player) {
        if(isPlayerChecking(player)) {
            if(getCheckingCheatsPlayers().getInitiatorsAndHisPlayers().get(player) == initiator) {
                getCheckingCheatsPlayers().getInitiatorsAndHisPlayers().remove(player);
                getCheckingCheatsPlayers().getCheckReason().remove(getCheckingCheatsPlayers().getCheckingPlayers().indexOf(player));
                getCheckingCheatsPlayers().getCheckingPlayers().remove(player);
                player.message(TextUtils.setColors(getFileAccessor().getLang().getString("commands.cheatcheck.chat-message-on-stop")));
                if(getConfigSettings().isSendTitleOnCheatCheck()) {
                    player.title(TextUtils.setColors(getFileAccessor().getLang().getString("commands.cheatcheck.header-title-on-stop")), TextUtils.setColors(String.join("\n", getFileAccessor().getLang().getStringList("commands.cheatcheck.footer-title-on-stop"))));
                }
                TemporaryCache.unsetCheckingPlayersNames(player.nickname());
                initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.cheatcheck.check-stopped").replace("%1$f", player.nickname())));
            } else {
                if(!initiator.hasPermission("functionalservercontrol.cheatcheck.other")) {
                    initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.cheatcheck.not-your-player")));
                    return;
                }
                getCheckingCheatsPlayers().getInitiatorsAndHisPlayers().remove(player);
                getCheckingCheatsPlayers().getCheckReason().remove(getCheckingCheatsPlayers().getCheckingPlayers().indexOf(player));
                getCheckingCheatsPlayers().getCheckingPlayers().remove(player);
                player.message(TextUtils.setColors(getFileAccessor().getLang().getString("commands.cheatcheck.chat-message-on-stop")));
                if(getConfigSettings().isSendTitleOnCheatCheck()) {
                    player.title(TextUtils.setColors(getFileAccessor().getLang().getString("commands.cheatcheck.header-title-on-stop")), TextUtils.setColors(String.join("\n", getFileAccessor().getLang().getStringList("commands.cheatcheck.footer-title-on-stop"))));
                }
                TemporaryCache.unsetCheckingPlayersNames(player.nickname());
                initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.cheatcheck.check-stopped").replace("%1$f", player.nickname())));
            }
        } else {
            initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.cheatcheck.player-not-checking-now")));
        }
    }


    public void preformActionOnQuit(FunctionalPlayer player) {
        if (isPlayerChecking(player)) {
            if (countdown.containsKey(player)) {
                TemporaryCache.unsetCheckingPlayersNames(player.nickname());
                getCheckingCheatsPlayers().getInitiatorsAndHisPlayers().get(player).sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.cheatcheck.target-quit")));
                getCheckingCheatsPlayers().getInitiatorsAndHisPlayers().remove(player);
                getCheckingCheatsPlayers().getCheckReason().remove(getCheckingCheatsPlayers().getCheckingPlayers().indexOf(player));
                getCheckingCheatsPlayers().getCheckingPlayers().remove(player);
                for (String action : getConfigSettings().getActionIfQuitDuringCheatCheck()) {
                    TaskManager.preformSync(() -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action.replace("%1$f", player.nickname())));
                }
            }
        }
    }


    public void preformActionOnConfirm(CommandSender initiator, FunctionalPlayer player) {
        if(isPlayerChecking(player)) {

            if(getCheckingCheatsPlayers().getInitiatorsAndHisPlayers().get(player) != initiator) {
                if(!initiator.hasPermission("functionalservercontrol.cheatcheck.other")) {
                    initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.cheatcheck.not-your-player")));
                    return;
                }
            }

            TemporaryCache.unsetCheckingPlayersNames(player.nickname());
            getCheckingCheatsPlayers().getInitiatorsAndHisPlayers().get(player).sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.cheatcheck.target-quit")));
            getCheckingCheatsPlayers().getInitiatorsAndHisPlayers().remove(player);
            getCheckingCheatsPlayers().getCheckReason().remove(getCheckingCheatsPlayers().getCheckingPlayers().indexOf(player));
            getCheckingCheatsPlayers().getCheckingPlayers().remove(player);
            for (String action : getConfigSettings().getActionIfValidCheatCheck()) {
                TaskManager.preformSync(() -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action.replace("%1$f", player.nickname())));
            }
        }
    }


    public void preformActionOnFail(CommandSender initiator, FunctionalPlayer player) {
        if(isPlayerChecking(player)) {

            if(getCheckingCheatsPlayers().getInitiatorsAndHisPlayers().get(player) != initiator) {
                if(!initiator.hasPermission("functionalservercontrol.cheatcheck.other")) {
                    initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.cheatcheck.not-your-player")));
                    return;
                }
            }

            TemporaryCache.unsetCheckingPlayersNames(player.nickname());
            getCheckingCheatsPlayers().getInitiatorsAndHisPlayers().get(player).sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.cheatcheck.target-quit")));
            getCheckingCheatsPlayers().getInitiatorsAndHisPlayers().remove(player);
            getCheckingCheatsPlayers().getCheckReason().remove(getCheckingCheatsPlayers().getCheckingPlayers().indexOf(player));
            getCheckingCheatsPlayers().getCheckingPlayers().remove(player);
            for (String action : getConfigSettings().getActionIfFailedCheatCheck()) {
                TaskManager.preformSync(() -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action.replace("%1$f", player.nickname())));
            }
        }
    }


    private void runCountdownTimer(FunctionalPlayer player) {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if(player != null && isPlayerChecking(player)) {
                    int tLeft = countdown.get(player) - 1;
                    countdown.replace(player, tLeft);
                    player.expansion().message(ChatMessageType.ACTION_BAR, TextUtils.setColors(getFileAccessor().getLang().getString("commands.cheatcheck.actionbar-message").replace("%1$f", timeSettingsAccessor.getTimeManager().convertFromMillis(timeSettingsAccessor.getTimeManager().convertFromSecToMillis(tLeft)))));
                    if(tLeft <= 0) {
                        countdown.remove(player);
                        TemporaryCache.unsetCheckingPlayersNames(player.nickname());
                        for(String action : getConfigSettings().getActionIfTimeLeftOnCheatCheck()) {
                            TaskManager.preformSync(() -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action.replace("%1$f", player.nickname())));
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
