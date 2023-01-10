package by.alis.functionalservercontrol.spigot.Listeners.Old;

import by.alis.functionalservercontrol.api.Enums.MuteType;
import by.alis.functionalservercontrol.spigot.Managers.AdvertiseManager;
import by.alis.functionalservercontrol.spigot.Managers.Mute.MuteManager;
import by.alis.functionalservercontrol.spigot.Managers.TimeManagers.TimeSettingsAccessor;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.EventExecutor;
import org.jetbrains.annotations.NotNull;

import static by.alis.functionalservercontrol.databases.DataBases.getSQLiteManager;
import static by.alis.functionalservercontrol.spigot.Additional.Containers.StaticContainers.getMutedPlayersContainer;
import static by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor.*;
import static by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getChatSettings;
import static by.alis.functionalservercontrol.spigot.Managers.ChatManager.getChatManager;
import static by.alis.functionalservercontrol.spigot.Managers.Mute.MuteChecker.isPlayerMuted;

public class OldAsyncChatListener implements Listener, EventExecutor {
    private final TimeSettingsAccessor timeSettingsAccessor = new TimeSettingsAccessor();
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
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
                    getChatManager().notifyAdminsOnTryChat(player, message, translatedTime);
                } else {
                    switch (getConfigSettings().getStorageType()) {
                        case SQLITE: {
                            MuteType muteType = getSQLiteManager().getMuteTypes().get(getSQLiteManager().getMutedUUIDs().indexOf(String.valueOf(player.getUniqueId())));
                            long unmuteTime = getSQLiteManager().getUnmuteTimes().get(getSQLiteManager().getMutedUUIDs().indexOf(String.valueOf(player.getUniqueId())));
                            event.setCancelled(true);
                            MuteManager muteManager = new MuteManager();
                            muteManager.notifyAboutMuteOnChat(player);
                            String translatedTime = getGlobalVariables().getVariableNever();
                            if (muteType != MuteType.PERMANENT_IP && muteType != MuteType.PERMANENT_NOT_IP) {
                                translatedTime = this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(unmuteTime));
                            }
                            getChatManager().notifyAdminsOnTryChat(player, message, translatedTime);
                        }
                        case H2: {}
                    }
                }
            }
            return;
        }
        if(getChatSettings().isFunctionEnabled()) {
            if(getChatManager().playerHasChatDelay(player)) {
                event.setCancelled(true);
                return;
            } else {
                getChatManager().setupChatDelay(player);
            }
            if(AdvertiseManager.isMessageContainsIp(player, message)) {
                event.setCancelled(true);
                return;
            }
            if(AdvertiseManager.isMessageContainsDomain(player, message)) {
                event.setCancelled(true);
                return;
            }
            if(getChatManager().isMessageContainsBlockedWord(player, message)) {
                event.setCancelled(true);
                return;
            }
            if(getChatManager().isRepeatingMessage(player, message)) {
                event.setCancelled(true);
                return;
            } else {
                getChatManager().setRepeatingMessage(player, message);
            }
            if(getChatSettings().isMessagesReplacerEnabled()) {
                event.setMessage(getChatManager().replaceMessageIfNeed(player, message));
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
