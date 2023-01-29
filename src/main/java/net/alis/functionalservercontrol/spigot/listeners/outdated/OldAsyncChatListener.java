package net.alis.functionalservercontrol.spigot.listeners.outdated;

import net.alis.functionalservercontrol.api.interfaces.FunctionalPlayer;
import net.alis.functionalservercontrol.spigot.managers.AdvertiseManager;
import net.alis.functionalservercontrol.spigot.managers.BaseManager;
import net.alis.functionalservercontrol.spigot.managers.ChatManager;
import net.alis.functionalservercontrol.api.enums.MuteType;
import net.alis.functionalservercontrol.spigot.managers.mute.MuteManager;
import net.alis.functionalservercontrol.spigot.managers.time.TimeSettingsAccessor;
import org.bukkit.event.*;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.EventExecutor;
import org.jetbrains.annotations.NotNull;

import static net.alis.functionalservercontrol.spigot.additional.containers.StaticContainers.getMutedPlayersContainer;
import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.*;
import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getChatSettings;
import static net.alis.functionalservercontrol.spigot.managers.mute.MuteChecker.isPlayerMuted;

public class OldAsyncChatListener implements Listener, EventExecutor {
    private final TimeSettingsAccessor timeSettingsAccessor = new TimeSettingsAccessor();
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        FunctionalPlayer player = FunctionalPlayer.get(event.getPlayer().getName());
        String message = event.getMessage();
        if(isPlayerMuted(player)) {
            if(!event.isCancelled()) {
                if(getConfigSettings().isAllowedUseRamAsContainer()){
                    MuteType muteType = getMutedPlayersContainer().getMuteTypesContainer().get(getMutedPlayersContainer().getUUIDContainer().indexOf(String.valueOf(player.getUniqueId())));
                    long unmuteTime = getMutedPlayersContainer().getMuteTimeContainer().get(getMutedPlayersContainer().getUUIDContainer().indexOf(String.valueOf(player.getUniqueId())));
                    event.setCancelled(true);
                    MuteManager muteManager = new MuteManager();
                    muteManager.notifyAboutMuteOnChat(player);
                    String translatedTime = getGlobalVariables().getVariableNever();
                    if (muteType != MuteType.PERMANENT_IP && muteType != MuteType.PERMANENT_NOT_IP) {
                        translatedTime = this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(unmuteTime));
                    }
                    ChatManager.getChatManager().notifyAdminsOnTryChat(player, message, translatedTime);
                } else {
                    MuteType muteType = BaseManager.getBaseManager().getMuteTypes().get(BaseManager.getBaseManager().getMutedUUIDs().indexOf(String.valueOf(player.getUniqueId())));
                    long unmuteTime = BaseManager.getBaseManager().getUnmuteTimes().get(BaseManager.getBaseManager().getMutedUUIDs().indexOf(String.valueOf(player.getUniqueId())));
                    event.setCancelled(true);
                    MuteManager muteManager = new MuteManager();
                    muteManager.notifyAboutMuteOnChat(player);
                    String translatedTime = getGlobalVariables().getVariableNever();
                    if (muteType != MuteType.PERMANENT_IP && muteType != MuteType.PERMANENT_NOT_IP) {
                        translatedTime = this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(unmuteTime));
                    }
                    ChatManager.getChatManager().notifyAdminsOnTryChat(player, message, translatedTime);
                }
            }
            return;
        }
        if(getChatSettings().isFunctionEnabled()) {
            if(ChatManager.getChatManager().playerHasChatDelay(player)) {
                event.setCancelled(true);
                return;
            } else {
                ChatManager.getChatManager().setupChatDelay(player);
            }
            if(AdvertiseManager.isMessageContainsIp(player, message)) {
                event.setCancelled(true);
                return;
            }
            if(AdvertiseManager.isMessageContainsDomain(player, message)) {
                event.setCancelled(true);
                return;
            }
            if(ChatManager.getChatManager().isMessageContainsBlockedWord(player, message)) {
                event.setCancelled(true);
                return;
            }
            if(ChatManager.getChatManager().isRepeatingMessage(player, message)) {
                event.setCancelled(true);
                return;
            } else {
                ChatManager.getChatManager().setRepeatingMessage(player, message);
            }
            if(getChatSettings().isMessagesReplacerEnabled()) {
                event.setMessage(ChatManager.getChatManager().replaceMessageIfNeed(player, message));
            }
        }
    }

    @Override
    public void execute(@NotNull Listener listener, @NotNull Event event) {
        if (listener == this && event instanceof AsyncPlayerChatEvent) {
            if (((AsyncPlayerChatEvent)event).isCancelled())
                return;
            onPlayerChat((AsyncPlayerChatEvent)event);
        }
    }

}
