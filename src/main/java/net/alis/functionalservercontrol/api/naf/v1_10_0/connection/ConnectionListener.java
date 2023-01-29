package net.alis.functionalservercontrol.api.naf.v1_10_0.connection;

import net.alis.functionalservercontrol.api.FunctionalApi;
import net.alis.functionalservercontrol.api.interfaces.FunctionalPlayer;
import net.alis.functionalservercontrol.api.naf.v1_10_0.InternalAdapter;
import net.alis.functionalservercontrol.api.naf.v1_10_0.entity.FunctionalCraftPlayer;
import net.alis.functionalservercontrol.api.naf.v1_10_0.entity.sub.ExpansionedCraftPlayer;
import net.alis.functionalservercontrol.api.naf.v1_10_0.util.FID;
import net.alis.functionalservercontrol.api.naf.v1_10_0.util.FunctionalStatistics;
import net.alis.functionalservercontrol.api.naf.v1_10_0.util.RewritableCraftType;
import net.alis.functionalservercontrol.api.naf.v1_10_0.util.checkers.InternalBanChecker;
import net.alis.functionalservercontrol.api.naf.v1_10_0.util.checkers.InternalMuteChecker;
import net.alis.functionalservercontrol.api.naf.v1_10_0.util.data.WritablePlayerMeta;
import net.alis.functionalservercontrol.api.naf.v1_10_0.util.data.container.CraftPlayersContainer;
import net.alis.functionalservercontrol.api.naf.v1_10_0.util.registerer.PlayerRegisterer;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.PacketEvents;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.AbstractPacketListener;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packettype.PacketType;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.play.in.custompayload.WrappedPacketInCustomPayload;
import net.alis.functionalservercontrol.spigot.additional.misc.OtherUtils;
import net.alis.functionalservercontrol.spigot.additional.textcomponents.Component;
import net.alis.functionalservercontrol.spigot.managers.TaskManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;
import static net.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;
import static net.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

public class ConnectionListener extends AbstractPacketListener implements Listener {


    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        FID fid = new FID(player.getName());
        if (!PacketEvents.get().getSettings().shouldUseCompatibilityInjector()) PacketEvents.get().getInjector().injectPlayer(player);
        WritablePlayerMeta playerMeta = new WritablePlayerMeta(
                InternalAdapter.getPlayerProtocolVersion(player),
                OtherUtils.convertProtocolVersion(InternalAdapter.getPlayerProtocolVersion(player)).toString(),
                "not received yet, wait...",
                InternalBanChecker.isPlayerBanned(fid),
                InternalMuteChecker.isPlayerMuted(fid),
                player.getName(),
                player.getUniqueId(),
                fid,
                FunctionalStatistics.getAsPlayer(fid),
                FunctionalStatistics.getAsAdmin(fid),
                new ExpansionedCraftPlayer(player),
                Bukkit.getServer(),
                0,
                event.getAddress().getHostAddress(),
                player.getWorld(),
                player,
                InternalAdapter.getPlayerPing(player)
        );
        FunctionalCraftPlayer functionalCraftPlayer = new FunctionalCraftPlayer(playerMeta);
        CraftPlayersContainer.Online.In.add(functionalCraftPlayer);
        new PlayerRegisterer(functionalCraftPlayer).register();
    }

    @Override
    public void onPacketPlayReceive(PacketPlayReceiveEvent event) {
        if(event.getPacketId() == PacketType.Play.Client.CUSTOM_PAYLOAD) {
            WrappedPacketInCustomPayload packet = new WrappedPacketInCustomPayload(event.getNMSPacket());
            FunctionalCraftPlayer craftPlayer = CraftPlayersContainer.Online.Out.get(new FID(event.getPlayer().getName()));
            if(craftPlayer != null){
                if ((packet.getChannelName().contains("wdl|") || packet.getChannelName().contains("wdl:")) && getConfigSettings().isBlockWorldDownloader()) {
                    for (String action : getConfigSettings().getActionsOnWDL()) {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action.replace("%1$f", craftPlayer.nickname()));
                    }
                    for (FunctionalPlayer admin : FunctionalApi.getOnlinePlayers()) {
                        if (admin.hasPermission("functionalservercontrol.notification.clients")) {
                            admin.expansion().message(new Component.SimplifiedComponent(setColors(getFileAccessor().getLang().getString("other.notifications.client.world-downloader").replace("%1$f", craftPlayer.nickname())))
                                    .append(Component.addPunishmentButtons(admin, craftPlayer.nickname()))
                            );
                        }
                    }
                }
                if (packet.getChannelName().contains("brand")) {
                    String clientName = InternalAdapter.getPlayerClientName(packet);
                    craftPlayer.rewrite(RewritableCraftType.MINECRAFT_CLIENT_NAME, clientName);
                    TaskManager.preformAsync(() -> {
                        try {
                            if (!craftPlayer.hasPermission("functionalservercontrol.clients.bypass")) {
                                if (clientName.contains("vanilla")) {
                                    if (getConfigSettings().isBlockVanillaClient()) {
                                        for (String action : getConfigSettings().getVanillaClientActions()) {
                                            TaskManager.preformSync(() -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action.replace("%1$f", craftPlayer.nickname())));
                                        }
                                        return;
                                    }
                                }
                                if (clientName.contains("fml") || clientName.contains("forge")) {
                                    if (getConfigSettings().isBlockForgeClient()) {
                                        for (String action : getConfigSettings().getForgeClientActions()) {
                                            TaskManager.preformSync(() -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action.replace("%1$f", craftPlayer.nickname())));
                                        }
                                        return;
                                    }
                                }
                                if (clientName.contains("lunar")) {
                                    if (getConfigSettings().isBlockLunarClient()) {
                                        for (String action : getConfigSettings().getLunarClientActions()) {
                                            TaskManager.preformSync(() -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action.replace("%1$f", craftPlayer.nickname())));
                                        }
                                        return;
                                    }
                                }
                                if (clientName.contains("badlion")) {
                                    if (getConfigSettings().isBlockBadlionClient()) {
                                        for (String action : getConfigSettings().getBadlionClientActions()) {
                                            TaskManager.preformSync(() -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action.replace("%1$f", craftPlayer.nickname())));
                                        }
                                        return;
                                    }
                                }
                            }
                            if (getConfigSettings().isAnnounceConsoleAboutBrand()) {
                                Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.client.player-brand-notify").replace("%1$f", craftPlayer.nickname())).replace("%2$f", clientName).replace("%3$f", craftPlayer.minecraftVersionName()));
                            }
                            for (FunctionalPlayer admin : FunctionalApi.getOnlinePlayers()) {
                                if (admin.hasPermission("functionalservercontrol.notification.clients")) {
                                    admin.message(setColors(getFileAccessor().getLang().getString("other.notifications.client.player-brand-notify").replace("%1$f", craftPlayer.nickname())).replace("%2$f", clientName).replace("%3$f", craftPlayer.minecraftVersionName()));
                                }
                            }
                        } catch (Exception ignored) {
                            Bukkit.getConsoleSender().sendMessage(setColors("&c[FunctionalServerControl] Failed to get the %player% Minecraft brand".replace("%player%", craftPlayer.nickname())));
                        }
                    });
                }
            }
        }
    }
}
