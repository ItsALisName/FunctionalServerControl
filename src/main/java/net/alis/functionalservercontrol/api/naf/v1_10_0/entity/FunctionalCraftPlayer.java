package net.alis.functionalservercontrol.api.naf.v1_10_0.entity;

import net.alis.functionalservercontrol.api.interfaces.FunctionalPlayer;
import net.alis.functionalservercontrol.api.naf.v1_10_0.InternalAdapter;
import net.alis.functionalservercontrol.api.naf.v1_10_0.entity.sub.ExpansionedCraftPlayer;
import net.alis.functionalservercontrol.api.naf.v1_10_0.util.FID;
import net.alis.functionalservercontrol.api.naf.v1_10_0.util.FunctionalStatistics;
import net.alis.functionalservercontrol.api.naf.v1_10_0.util.RewritableCraftType;
import net.alis.functionalservercontrol.api.naf.v1_10_0.util.data.MetaRewritable;
import net.alis.functionalservercontrol.api.naf.v1_10_0.util.data.WritablePlayerMeta;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.play.out.kickdisconnect.WrappedPacketOutKickDisconnect;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.play.out.title.WrappedPacketOutTitle;
import net.alis.functionalservercontrol.spigot.managers.TaskManager;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getGlobalVariables;
import static net.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;
import static net.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

public class FunctionalCraftPlayer implements FunctionalPlayer, MetaRewritable {

    private final WritablePlayerMeta meta;

    public FunctionalCraftPlayer(WritablePlayerMeta meta) {
        this.meta = meta;
    }

    @Override
    public int protocolVersion() {
        return this.meta.getProtocolVersion();
    }

    @Override
    public String minecraftVersionName() {
        return this.meta.getMinecraftVersionName();
    }

    @Override
    public String clientBrandName() {
        return this.meta.getClientBrandName();
    }

    @Override
    public boolean isBanned() {
        return this.meta.isBanned();
    }

    @Override
    public boolean isMuted() {
        return this.meta.isMuted();
    }

    @Override
    public String nickname() {
        return this.meta.getNickname();
    }

    @Override
    public UUID getUniqueId() {
        return this.meta.getUuid();
    }

    @Override
    public FID getFunctionalId() {
        return this.meta.getFid();
    }

    @Override
    public FunctionalStatistics.PlayerStats getStatsAsPlayer() {
        return this.meta.getPlayerStats();
    }

    @Override
    public FunctionalStatistics.AdminStats getStatsAsAdmin() {
        return this.meta.getAdminStats();
    }

    @Override
    public ExpansionedCraftPlayer expansion() {
        return this.meta.getExpansionedPlayer();
    }

    @Override
    public Player getBukkitPlayer() {
        return this.meta.getPlayer();
    }

    @Override
    public OfflinePlayer getOfflineBukkitPlayer() {
        return this.meta.getPlayer();
    }

    @Override
    public @Nullable FunctionalPlayer getPlayer() {
        return this;
    }

    @Override
    public void message(String message) {
        TaskManager.preformAsync(() -> this.meta.getPlayer().sendMessage(message));
    }

    @Override
    public void title(String header) {
        InternalAdapter.sendPacketAsync(this.meta.getPlayer(), new WrappedPacketOutTitle(WrappedPacketOutTitle.TitleAction.TITLE, setColors(header), 10, 70, 20));
    }

    @Override
    public void title(String header, String footer) {
        WrappedPacketOutTitle[] title = {new WrappedPacketOutTitle(WrappedPacketOutTitle.TitleAction.TITLE, setColors(header), 10, 70, 20), new WrappedPacketOutTitle(WrappedPacketOutTitle.TitleAction.SUBTITLE, setColors(footer), 10, 70, 20)};
        InternalAdapter.sendPacketsAsync(this.meta.getPlayer(), title);
    }

    @Override
    public void title(String header, String footer, int fadeIn, int stay, int fadeOut) {
        WrappedPacketOutTitle[] title = {new WrappedPacketOutTitle(WrappedPacketOutTitle.TitleAction.TITLE, setColors(header), fadeIn, stay, fadeOut), new WrappedPacketOutTitle(WrappedPacketOutTitle.TitleAction.SUBTITLE, setColors(footer), fadeIn, stay, fadeOut)};
        InternalAdapter.sendPacketsAsync(this.meta.getPlayer(), title);
    }

    @Override
    public boolean isPermissionSet(@NotNull String string) {
        return this.meta.getPlayer().isPermissionSet(string);
    }

    @Override
    public boolean isPermissionSet(@NotNull Permission permission) {
        return this.meta.getPlayer().isPermissionSet(permission);
    }

    @Override
    public boolean hasPermission(@NotNull String permission) {
        return this.meta.getPlayer().hasPermission(permission);
    }

    @Override
    public boolean hasPermission(@NotNull Permission permission) {
        return this.meta.getPlayer().hasPermission(permission);
    }

    @Override
    public @NotNull PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String string, boolean bl) {
        return this.meta.getPlayer().addAttachment(plugin, string, bl);
    }

    @Override
    public @NotNull PermissionAttachment addAttachment(@NotNull Plugin plugin) {
        return this.meta.getPlayer().addAttachment(plugin);
    }

    @Override
    public @Nullable PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String string, boolean bl, int i) {
        return this.meta.getPlayer().addAttachment(plugin, string, bl, i);
    }

    @Override
    public @Nullable PermissionAttachment addAttachment(@NotNull Plugin plugin, int i) {
        return this.meta.getPlayer().addAttachment(plugin, i);
    }

    @Override
    public void removeAttachment(@NotNull PermissionAttachment permissionAttachment) {
        TaskManager.preformAsync(() -> this.meta.getPlayer().removeAttachment(permissionAttachment));
    }

    @Override
    public void recalculatePermissions() {
        TaskManager.preformAsync(() -> this.meta.getPlayer().recalculatePermissions());
    }

    @Override
    public @NotNull Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return this.meta.getPlayer().getEffectivePermissions();
    }

    @Override
    public String address() {
        return this.meta.getAddress();
    }

    @Override
    public World world() {
        return this.meta.getWorld();
    }

    @Override
    public int ping() {
        return this.meta.getPing();
    }

    @Override
    public int packetsCount() {
        return this.meta.getPacketsSent();
    }

    @Override
    public boolean isOnline() {
        return this.meta.getPlayer().isOnline();
    }

    @Override
    public void kill() {
        this.meta.getPlayer().setHealth(0);
    }

    @Override
    public void kick() {
        InternalAdapter.sendPacketAsync(this.meta.getPlayer(), new WrappedPacketOutKickDisconnect(setColors(String.join("\n", getFileAccessor().getLang().getStringList("kick-format")).replace("%1$f", getGlobalVariables().getDefaultReason()).replace("%2$f", getGlobalVariables().getConsoleVariableName()))));
    }

    @Override
    public void kick(String reason) {
        String r;
        if(reason != null) {
            r = setColors(String.join("\n", getFileAccessor().getLang().getStringList("kick-format")).replace("%1$f", reason).replace("%2$f", getGlobalVariables().getConsoleVariableName()));
        } else {
            r = setColors(String.join("\n", getFileAccessor().getLang().getStringList("kick-format")).replace("%1$f", getGlobalVariables().getDefaultReason()).replace("%2$f", getGlobalVariables().getConsoleVariableName()));

        }
        InternalAdapter.sendPacketAsync(this.meta.getPlayer(), new WrappedPacketOutKickDisconnect(r));
    }

    @Override
    public void sendMessage(@NotNull String string) {
        TaskManager.preformAsync(() -> this.meta.getPlayer().sendMessage(string));
    }

    @Override
    public void sendMessage(@NotNull String... strings) {
        TaskManager.preformAsync(() -> this.meta.getPlayer().sendMessage(strings));
    }

    @Override
    public void sendMessage(@Nullable UUID uuid, @NotNull String string) {
        TaskManager.preformAsync(() -> this.meta.getPlayer().sendMessage(uuid, string));
    }

    @Override
    public void sendMessage(@Nullable UUID uuid, @NotNull String... strings) {
        TaskManager.preformAsync(() -> this.meta.getPlayer().sendMessage(uuid, strings));
    }

    @Override
    public @NotNull Server getServer() {
        return this.meta.getServer();
    }

    @Override
    public @NotNull String getName() {
        return this.meta.getNickname();
    }

    @Override
    public @NotNull Spigot spigot() {
        return this.meta.getExpansionedPlayer();
    }

    @Override
    public @NotNull Component name() {
        return Component.text(this.meta.getNickname());
    }

    @Override
    public boolean isOp() {
        return this.meta.getPlayer().isOp();
    }

    @Override
    public void setOp(boolean bl) {
        this.meta.getPlayer().setOp(bl);
    }

    @Override
    public void sendPluginMessage(@NotNull Plugin plugin, @NotNull String string, @NotNull byte[] bs) {
        TaskManager.preformAsync(() -> this.meta.getPlayer().sendPluginMessage(plugin, string, bs));
    }

    @Override
    public @NotNull Set<String> getListeningPluginChannels() {
        return this.meta.getPlayer().getListeningPluginChannels();
    }

    @Override
    public WritablePlayerMeta getMeta() {
        return this.meta;
    }

    @Override
    public void rewrite(RewritableCraftType type, Object param) {
        TaskManager.preformAsync(() -> {
            switch (type) {
                case FID -> {
                    if (param instanceof FID) {
                        this.meta.rewrite(type, param);
                        break;
                    } else {
                        throw new IllegalArgumentException("Object '" + param.getClass().getName() + "' cant rewrite type of '" + type.o.getName() + "'");
                    }
                }
                case NICKNAME, MINECRAFT_VERSION, MINECRAFT_CLIENT_NAME, ADDRESS -> {
                    if (param instanceof String) {
                        this.meta.rewrite(type, param);
                        break;
                    } else {
                        throw new IllegalArgumentException("Object '" + param.getClass().getName() + "' cant rewrite type of '" + type.o.getName() + "'");
                    }
                }
                case UUID -> {
                    if (param instanceof UUID) {
                        this.meta.rewrite(type, param);
                        break;
                    } else {
                        throw new IllegalArgumentException("Object '" + param.getClass().getName() + "' cant rewrite type of '" + type.o.getName() + "'");
                    }
                }
                case PLAYER_STATS -> {
                    if (param instanceof FunctionalStatistics.PlayerStats) {
                        this.meta.rewrite(type, param);
                        break;
                    } else {
                        throw new IllegalArgumentException("Object '" + param.getClass().getName() + "' cant rewrite type of '" + type.o.getName() + "'");
                    }
                }
                case PROTOCOL_VERSION, PING, PACKETS_COUNT -> {
                    if (param instanceof Integer) {
                        this.meta.rewrite(type, param);
                        break;
                    } else {
                        throw new IllegalArgumentException("Object '" + param.getClass().getName() + "' cant rewrite type of '" + type.o.getName() + "'");
                    }
                }
                case BOOL_BANNED, BOOL_MUTED -> {
                    if (param instanceof Boolean) {
                        this.meta.rewrite(type, param);
                        break;
                    } else {
                        throw new IllegalArgumentException("Object '" + param.getClass().getName() + "' cant rewrite type of '" + type.o.getName() + "'");
                    }
                }
                case EXPANSION -> {
                    if (param instanceof ExpansionedCraftPlayer) {
                        this.meta.rewrite(type, param);
                        break;
                    } else {
                        throw new IllegalArgumentException("Object '" + param.getClass().getName() + "' cant rewrite type of '" + type.o.getName() + "'");
                    }
                }
                case SERVER -> {
                    if (param instanceof Server) {
                        this.meta.rewrite(type, param);
                        break;
                    } else {
                        throw new IllegalArgumentException("Object '" + param.getClass().getName() + "' cant rewrite type of '" + type.o.getName() + "'");
                    }
                }
                case PLAYER -> {
                    if (param instanceof Player) {
                        this.meta.rewrite(type, param);
                        break;
                    } else {
                        throw new IllegalArgumentException("Object '" + param.getClass().getName() + "' cant rewrite type of '" + type.o.getName() + "'");
                    }
                }
                case WORLD -> {
                    if (param instanceof World) {
                        this.meta.rewrite(type, param);
                        break;
                    } else {
                        throw new IllegalArgumentException("Object '" + param.getClass().getName() + "' cant rewrite type of '" + type.o.getName() + "'");
                    }
                }
                case ADMIN_STATS -> {
                    if (param instanceof FunctionalStatistics.AdminStats) {
                        this.meta.rewrite(type, param);
                        break;
                    } else {
                        throw new IllegalArgumentException("Object '" + param.getClass().getName() + "' cant rewrite type of '" + type.o.getName() + "'");
                    }
                }
            }
        });
    }

}
