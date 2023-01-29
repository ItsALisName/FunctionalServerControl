package net.alis.functionalservercontrol.api.naf.v1_10_0.util.data;

import net.alis.functionalservercontrol.api.naf.v1_10_0.entity.sub.ExpansionedCraftPlayer;
import net.alis.functionalservercontrol.api.naf.v1_10_0.util.FID;
import net.alis.functionalservercontrol.api.naf.v1_10_0.util.FunctionalStatistics;
import net.alis.functionalservercontrol.api.naf.v1_10_0.util.RewritableCraftType;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.UUID;

public class WritablePlayerMeta extends DefaultMeta {

    private int protocolVersion;
    private String minecraftVersionName;
    private String clientBrandName;
    private FunctionalStatistics.PlayerStats playerStats;
    private FunctionalStatistics.AdminStats adminStats;
    private ExpansionedCraftPlayer expansionedPlayer;
    private Server server;
    private int packetsSent;
    private String address;
    private World world;
    private Player player;
    private int ping;
    private boolean isBanned;
    private boolean isMuted;

    public WritablePlayerMeta(int protocolVersion, String minecraftVersionName, String clientBrandName, boolean isBanned, boolean isMuted, String nickname, UUID uuid, FID fid, FunctionalStatistics.PlayerStats playerStats, FunctionalStatistics.AdminStats adminStats, ExpansionedCraftPlayer expansionedPlayer, Server server, int packetsSent, String address, World world, Player player, int ping) {
        super(nickname, uuid, fid);
        this.protocolVersion = protocolVersion;
        this.minecraftVersionName = minecraftVersionName;
        this.clientBrandName = clientBrandName;
        this.playerStats = playerStats;
        this.adminStats = adminStats;
        this.expansionedPlayer = expansionedPlayer;
        this.server = server;
        this.packetsSent = packetsSent;
        this.address = address;
        this.world = world;
        this.player = player;
        this.ping = ping;
        this.isBanned = isBanned;
        this.isMuted = isMuted;
    }

    public int getProtocolVersion() {
        return protocolVersion;
    }

    public String getMinecraftVersionName() {
        return minecraftVersionName;
    }

    public String getClientBrandName() {
        return clientBrandName;
    }

    public FunctionalStatistics.PlayerStats getPlayerStats() {
        return playerStats;
    }

    public FunctionalStatistics.AdminStats getAdminStats() {
        return adminStats;
    }

    public ExpansionedCraftPlayer getExpansionedPlayer() {
        return expansionedPlayer;
    }

    public Server getServer() {
        return server;
    }

    public int getPacketsSent() {
        return packetsSent;
    }

    public String getAddress() {
        return address;
    }

    public World getWorld() {
        return world;
    }

    public Player getPlayer() {
        return player;
    }

    public int getPing() {
        return ping;
    }

    @Override
    public void rewrite(RewritableCraftType type, Object param) {
        switch (type) {
            case FID -> this.fid = (FID) param;
            case PING -> this.ping = (Integer) param;
            case UUID -> this.uuid = (UUID) param;
            case WORLD -> this.world = (World) param;
            case PLAYER -> this.player = (Player) param;
            case SERVER -> this.server = (Server) param;
            case ADDRESS -> this.address = (String) param;
            case NICKNAME -> this.nickname = (String) param;
            case EXPANSION -> this.expansionedPlayer = (ExpansionedCraftPlayer) param;
            case BOOL_MUTED -> this.isMuted = (Boolean) param;
            case ADMIN_STATS -> this.adminStats = (FunctionalStatistics.AdminStats) param;
            case BOOL_BANNED -> this.isBanned = (Boolean) param;
            case PACKETS_COUNT -> this.packetsSent = (Integer) param;
            case PLAYER_STATS -> this.playerStats = (FunctionalStatistics.PlayerStats) param;
            case PROTOCOL_VERSION -> this.protocolVersion = (Integer) param;
            case MINECRAFT_VERSION -> this.minecraftVersionName = (String) param;
            case MINECRAFT_CLIENT_NAME -> this.clientBrandName = (String) param;
        }
    }

    @Override
    public WritablePlayerMeta getMeta() {
        return this;
    }

    @Override
    public String getNickname() {
        return this.nickname;
    }

    @Override
    public UUID getUuid() {
        return this.uuid;
    }

    @Override
    public FID getFid() {
        return this.fid;
    }

    public boolean isBanned() {
        return isBanned;
    }

    public boolean isMuted() {
        return isMuted;
    }
}
