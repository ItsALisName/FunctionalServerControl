package by.alis.functionalservercontrol.spigot.listeners;

import by.alis.functionalservercontrol.api.enums.MuteType;
import by.alis.functionalservercontrol.spigot.additional.misc.AdventureApiUtils;
import by.alis.functionalservercontrol.spigot.managers.AdvertiseManager;
import by.alis.functionalservercontrol.spigot.managers.mute.MuteManager;
import by.alis.functionalservercontrol.spigot.managers.time.TimeSettingsAccessor;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.jetbrains.annotations.NotNull;

import static by.alis.functionalservercontrol.databases.DataBases.getSQLiteManager;
import static by.alis.functionalservercontrol.spigot.additional.containers.StaticContainers.getMutedPlayersContainer;
import static by.alis.functionalservercontrol.spigot.additional.globalsettings.StaticSettingsAccessor.*;
import static by.alis.functionalservercontrol.spigot.managers.ChatManager.getChatManager;
import static by.alis.functionalservercontrol.spigot.managers.mute.MuteChecker.isPlayerMuted;

public class AsyncChatListener implements Listener, EventExecutor {

    private final TimeSettingsAccessor timeSettingsAccessor = new TimeSettingsAccessor();
    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        Component message = event.message();
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
                    getChatManager().notifyAdminsOnTryChat(player, AdventureApiUtils.componentToString(message), translatedTime);
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
                            getChatManager().notifyAdminsOnTryChat(player, AdventureApiUtils.componentToString(message), translatedTime);
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
            if(AdvertiseManager.isMessageContainsIp(player, AdventureApiUtils.componentToString(message))) {
                event.setCancelled(true);
                return;
            }
            if(AdvertiseManager.isMessageContainsDomain(player, AdventureApiUtils.componentToString(message))) {
                event.setCancelled(true);
                return;
            }
            if(getChatManager().isMessageContainsBlockedWord(player, AdventureApiUtils.componentToString(message))) {
                event.setCancelled(true);
                return;
            }
            if(getChatManager().isRepeatingMessage(player, AdventureApiUtils.componentToString(message))) {
                event.setCancelled(true);
                return;
            } else {
                getChatManager().setRepeatingMessage(player, AdventureApiUtils.componentToString(message));
            }
            if(getChatSettings().isMessagesReplacerEnabled()) {
                event.message(AdventureApiUtils.stringToComponent(getChatManager().replaceMessageIfNeed(player, AdventureApiUtils.componentToString(message))));
            }
        }

    }

    @Override
    public void execute(@NotNull Listener listener, @NotNull Event event) {
        if (listener == this && event instanceof AsyncChatEvent) {
            if (((AsyncChatEvent)event).isCancelled())
                return;
            onPlayerChat((AsyncChatEvent) event);
        }
    }
}
