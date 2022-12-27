package by.alis.functionalservercontrol.spigot.Managers;

import by.alis.functionalservercontrol.spigot.Additional.Other.TemporaryCache;
import by.alis.functionalservercontrol.spigot.FunctionalServerControlSpigot;
import by.alis.functionalservercontrol.spigot.Managers.TimeManagers.TimeSettingsAccessor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static by.alis.functionalservercontrol.spigot.Additional.Containers.StaticContainers.getCheckingCheatsPlayers;
import static by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getGlobalVariables;
import static by.alis.functionalservercontrol.spigot.Additional.Other.TextUtils.setColors;
import static by.alis.functionalservercontrol.spigot.Managers.Files.SFAccessor.getFileAccessor;

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

        if(reason == null || reason.equalsIgnoreCase("")) {
            reason = getGlobalVariables().getDefaultReason();
        }

        if(reason.equalsIgnoreCase(getGlobalVariables().getDefaultReason())) {
            if(!getConfigSettings().isCheatsCheckAllowedWithoutReason() && !initiator.hasPermission("functionalservercontrol.use.no-reason")) {
                initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.no-reason")));
                return;
            }
        }

        if(initiator instanceof Player) {
            Player i = ((Player) initiator).getPlayer();
            if(CooldownsManager.playerHasCooldown(i, "cheatcheck")) {
                CooldownsManager.notifyAboutCooldown(i, "cheatcheck");
                return;
            } else {
                CooldownsManager.setCooldown(i, "cheatcheck");
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
            initiatorName = ((Player) initiator).getPlayerListName();
        } else if(initiator instanceof ConsoleCommandSender) {
            initiatorName = getGlobalVariables().getConsoleVariableName();
        } else {
            initiatorName = initiator.getName();
        }

        if(player.hasPermission("functionalservercontrol.cheat-check.bypass") && !initiator.hasPermission("functionalservercontrol.bypass-break")) {
            initiator.sendMessage(setColors(getFileAccessor().getLang().getString("commands.cheatcheck.target-bypass")));
            return;
        }

        getCheckingCheatsPlayers().setCheckingPlayers(player);
        getCheckingCheatsPlayers().setInitiatorsAndHisPlayers(player, initiator);
        getCheckingCheatsPlayers().setCheckReason(reason);
        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("commands.cheatcheck.success").replace("%1$f", player.getPlayerListName())));
        player.sendMessage(setColors(getFileAccessor().getLang().getString("commands.cheatcheck.chat-message-on-start").replace("%1$f", initiatorName).replace("%2$f", reason).replace("%3$f", String.valueOf(checkTime))));
        if(getConfigSettings().isSendTitleOnCheck()) {
            player.sendTitle(setColors(getFileAccessor().getLang().getString("commands.cheatcheck.header-title-on-start")), setColors(String.join("\n", getFileAccessor().getLang().getStringList("commands.cheatcheck.footer-title-on-start"))), 10, 70,20);
        }
        this.countdown.put(player, checkTime);
        TemporaryCache.setCheckingPlayersNames(player.getName());
        runCountdownTimer(player);
    }
    
    

    public void stopCheck(CommandSender initiator, Player player) {
        if(isPlayerChecking(player)) {
            if(getCheckingCheatsPlayers().getInitiatorsAndHisPlayers().get(player) == initiator) {
                getCheckingCheatsPlayers().getInitiatorsAndHisPlayers().remove(player);
                getCheckingCheatsPlayers().getCheckReason().remove(getCheckingCheatsPlayers().getCheckingPlayers().indexOf(player));
                getCheckingCheatsPlayers().getCheckingPlayers().remove(player);
                player.sendMessage(setColors(getFileAccessor().getLang().getString("commands.cheatcheck.chat-message-on-stop")));
                if(getConfigSettings().isSendTitleOnCheck()) {
                    player.sendTitle(setColors(getFileAccessor().getLang().getString("commands.cheatcheck.header-title-on-stop")), setColors(String.join("\n", getFileAccessor().getLang().getStringList("commands.cheatcheck.footer-title-on-stop"))), 10, 70,20);
                }
                TemporaryCache.unsetCheckingPlayersNames(player.getName());
                initiator.sendMessage(setColors(getFileAccessor().getLang().getString("commands.cheatcheck.check-stopped").replace("%1$f", player.getPlayerListName())));
            } else {
                if(!initiator.hasPermission("functionalservercontrol.cheat-check.other")) {
                    initiator.sendMessage(setColors(getFileAccessor().getLang().getString("commands.cheatcheck.not-your-player")));
                    return;
                }
                getCheckingCheatsPlayers().getInitiatorsAndHisPlayers().remove(player);
                getCheckingCheatsPlayers().getCheckReason().remove(getCheckingCheatsPlayers().getCheckingPlayers().indexOf(player));
                getCheckingCheatsPlayers().getCheckingPlayers().remove(player);
                player.sendMessage(setColors(getFileAccessor().getLang().getString("commands.cheatcheck.chat-message-on-stop")));
                if(getConfigSettings().isSendTitleOnCheck()) {
                    player.sendTitle(setColors(getFileAccessor().getLang().getString("commands.cheatcheck.header-title-on-stop")), setColors(String.join("\n", getFileAccessor().getLang().getStringList("commands.cheatcheck.footer-title-on-stop"))), 10, 70,20);
                }
                TemporaryCache.unsetCheckingPlayersNames(player.getName());
                initiator.sendMessage(setColors(getFileAccessor().getLang().getString("commands.cheatcheck.check-stopped").replace("%1$f", player.getPlayerListName())));
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
                for (String action : getConfigSettings().getActionIfQuitDuringCheck()) {
                    Bukkit.getScheduler().runTask(FunctionalServerControlSpigot.getProvidingPlugin(FunctionalServerControlSpigot.class), () -> {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action.replace("%1$f", player.getName()));
                    });
                }
            }
        }
    }


    public void preformActionOnConfirm(CommandSender initiator, Player player) {
        if(isPlayerChecking(player)) {

            if(getCheckingCheatsPlayers().getInitiatorsAndHisPlayers().get(player) != initiator) {
                if(!initiator.hasPermission("functionalservercontrol.cheat-check.other")) {
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
                Bukkit.getScheduler().runTask(FunctionalServerControlSpigot.getProvidingPlugin(FunctionalServerControlSpigot.class), () -> {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action.replace("%1$f", player.getName()));
                });
            }
        }
    }


    public void preformActionOnFail(CommandSender initiator, Player player) {
        if(isPlayerChecking(player)) {

            if(getCheckingCheatsPlayers().getInitiatorsAndHisPlayers().get(player) != initiator) {
                if(!initiator.hasPermission("functionalservercontrol.cheat-check.other")) {
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
                Bukkit.getScheduler().runTask(FunctionalServerControlSpigot.getProvidingPlugin(FunctionalServerControlSpigot.class), () -> {
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
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(setColors(getFileAccessor().getLang().getString("commands.cheatcheck.actionbar-message").replace("%1$f", timeSettingsAccessor.getTimeManager().convertFromMillis(timeSettingsAccessor.getTimeManager().convertFromSecToMillis(tLeft))))));
                    if(tLeft <= 0) {
                        countdown.remove(player);
                        TemporaryCache.unsetCheckingPlayersNames(player.getName());
                        for(String action : getConfigSettings().getActionIfTimeLeft()) {
                            Bukkit.getScheduler().runTask(FunctionalServerControlSpigot.getProvidingPlugin(FunctionalServerControlSpigot.class), () -> {
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
