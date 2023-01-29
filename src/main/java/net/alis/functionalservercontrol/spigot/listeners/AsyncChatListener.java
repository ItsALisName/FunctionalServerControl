package net.alis.functionalservercontrol.spigot.listeners;

import net.alis.functionalservercontrol.api.interfaces.FunctionalPlayer;
import net.alis.functionalservercontrol.spigot.managers.AdvertiseManager;
import net.alis.functionalservercontrol.spigot.managers.BaseManager;
import net.alis.functionalservercontrol.spigot.managers.ChatManager;
import net.alis.functionalservercontrol.api.enums.MuteType;
import net.alis.functionalservercontrol.spigot.managers.mute.MuteManager;
import net.alis.functionalservercontrol.spigot.managers.time.TimeSettingsAccessor;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.jetbrains.annotations.NotNull;

import static net.alis.functionalservercontrol.spigot.additional.containers.StaticContainers.getMutedPlayersContainer;
import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.*;
import static net.alis.functionalservercontrol.spigot.managers.mute.MuteChecker.isPlayerMuted;

public class AsyncChatListener implements Listener, EventExecutor {

    private final TimeSettingsAccessor timeSettingsAccessor = new TimeSettingsAccessor();
    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        FunctionalPlayer player = FunctionalPlayer.get(event.getPlayer().getName());
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
                    ChatManager.getChatManager().notifyAdminsOnTryChat(player, LegacyComponentSerializer.legacyAmpersand().serialize(message), translatedTime);
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
                    ChatManager.getChatManager().notifyAdminsOnTryChat(player, LegacyComponentSerializer.legacyAmpersand().serialize(message), translatedTime);
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
            if(AdvertiseManager.isMessageContainsIp(player, LegacyComponentSerializer.legacyAmpersand().serialize(message))) {
                event.setCancelled(true);
                return;
            }
            if(AdvertiseManager.isMessageContainsDomain(player, LegacyComponentSerializer.legacyAmpersand().serialize(message))) {
                event.setCancelled(true);
                return;
            }
            if(ChatManager.getChatManager().isMessageContainsBlockedWord(player, LegacyComponentSerializer.legacyAmpersand().serialize(message))) {
                event.setCancelled(true);
                return;
            }
            if(ChatManager.getChatManager().isRepeatingMessage(player, LegacyComponentSerializer.legacyAmpersand().serialize(message))) {
                event.setCancelled(true);
                return;
            } else {
                ChatManager.getChatManager().setRepeatingMessage(player, LegacyComponentSerializer.legacyAmpersand().serialize(message));
            }
            if(getChatSettings().isMessagesReplacerEnabled()) {
                event.message(Component.text((ChatManager.getChatManager().replaceMessageIfNeed(player, LegacyComponentSerializer.legacyAmpersand().serialize(message)))));
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
