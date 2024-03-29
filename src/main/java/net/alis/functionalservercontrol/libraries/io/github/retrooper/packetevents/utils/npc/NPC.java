

package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.npc;

import java.util.List;

import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.PacketEvents;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.api.SendableWrapper;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.play.out.entity.WrappedPacketOutEntity;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.play.out.entitydestroy.WrappedPacketOutEntityDestroy;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.play.out.entityheadrotation.WrappedPacketOutEntityHeadRotation;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.play.out.entityteleport.WrappedPacketOutEntityTeleport;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.play.out.namedentityspawn.WrappedPacketOutNamedEntitySpawn;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.play.out.playerinfo.WrappedPacketOutPlayerInfo;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.player.GameMode;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.vector.Vector3d;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.CompletableFuture;

import org.bukkit.entity.Player;
import org.bukkit.Location;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.nms.NMSUtils;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.gameprofile.WrappedGameProfile;
import java.util.UUID;

public class NPC
{
    private final String name;
    private final int entityID;
    private final UUID uuid;
    private final WrappedGameProfile gameProfile;
    private final Map<UUID, Boolean> spawnedForPlayerMap;
    private Vector3d position;
    private float yaw;
    private float pitch;
    private boolean onGround;
    
    public NPC(final String name) {
        this.spawnedForPlayerMap = new ConcurrentHashMap<UUID, Boolean>();
        this.name = name;
        this.entityID = NMSUtils.generateEntityId();
        this.uuid = NMSUtils.generateUUID();
        this.gameProfile = new WrappedGameProfile(this.uuid, name);
        this.position = new Vector3d(0.0, 0.0, 0.0);
        this.yaw = 0.0f;
        this.pitch = 0.0f;
    }
    
    public NPC(final String name, final int entityID, final UUID uuid, final WrappedGameProfile gameProfile) {
        this.spawnedForPlayerMap = new ConcurrentHashMap<UUID, Boolean>();
        this.name = name;
        this.entityID = entityID;
        this.uuid = uuid;
        this.gameProfile = gameProfile;
        this.position = new Vector3d(0.0, 0.0, 0.0);
        this.yaw = 0.0f;
        this.pitch = 0.0f;
    }
    
    public NPC(final String name, final Vector3d position, final float yaw, final float pitch) {
        this.spawnedForPlayerMap = new ConcurrentHashMap<UUID, Boolean>();
        this.name = name;
        this.entityID = NMSUtils.generateEntityId();
        this.uuid = NMSUtils.generateUUID();
        this.gameProfile = new WrappedGameProfile(this.uuid, name);
        this.position = position;
        this.yaw = yaw;
        this.pitch = pitch;
    }
    
    public NPC(final String name, final Location location) {
        this(name, new Vector3d(location.getX(), location.getY(), location.getZ()), location.getYaw(), location.getPitch());
    }

    public void despawn(Player player) {
        boolean spawned = spawnedForPlayerMap.getOrDefault(player.getUniqueId(), false);
        spawnedForPlayerMap.remove(player.getUniqueId());
        if (spawned) {
            try {
                CompletableFuture.runAsync(() -> {
                    WrappedPacketOutPlayerInfo playerInfo = new WrappedPacketOutPlayerInfo(WrappedPacketOutPlayerInfo.PlayerInfoAction.REMOVE_PLAYER, new WrappedPacketOutPlayerInfo.PlayerInfo(name, gameProfile, GameMode.SURVIVAL, 0));
                    PacketEvents.get().getPlayerUtils().sendPacket(player, playerInfo);
                    WrappedPacketOutEntityDestroy wrappedPacketOutEntityDestroy = new WrappedPacketOutEntityDestroy(entityID);
                    PacketEvents.get().getPlayerUtils().sendPacket(player, wrappedPacketOutEntityDestroy);
                }).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }
    
    public boolean hasSpawned(final Player player) {
        return this.spawnedForPlayerMap.getOrDefault(player.getUniqueId(), false);
    }

    public void spawn(Player player) {
        try {
            if (!hasSpawned(player)) {
                CompletableFuture.runAsync(() -> {
                    WrappedPacketOutPlayerInfo playerInfo = new WrappedPacketOutPlayerInfo(WrappedPacketOutPlayerInfo.PlayerInfoAction.ADD_PLAYER, new WrappedPacketOutPlayerInfo.PlayerInfo(name, gameProfile, GameMode.SURVIVAL, 0));
                    PacketEvents.get().getPlayerUtils().sendPacket(player, playerInfo);
                    WrappedPacketOutNamedEntitySpawn wrappedPacketOutNamedEntitySpawn = new WrappedPacketOutNamedEntitySpawn(entityID, uuid, position, yaw, pitch);
                    PacketEvents.get().getPlayerUtils().sendPacket(player, wrappedPacketOutNamedEntitySpawn);
                    spawnedForPlayerMap.put(player.getUniqueId(), true);
                }).get();
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
    
    public String getName() {
        return this.name;
    }
    
    public int getEntityId() {
        return this.entityID;
    }
    
    public UUID getUUID() {
        return this.uuid;
    }
    
    public WrappedGameProfile getGameProfile() {
        return this.gameProfile;
    }
    
    public boolean isOnGround() {
        return this.onGround;
    }
    
    public void setOnGround(final boolean onGround) {
        this.onGround = onGround;
    }
    
    public Vector3d getPosition() {
        return this.position;
    }
    
    public float getPitch() {
        return this.pitch;
    }
    
    public float getYaw() {
        return this.yaw;
    }
    
    public void teleport(final Player player, final Vector3d targetPosition, final float yaw, final float pitch) {
        this.position = targetPosition;
        this.yaw = yaw;
        this.pitch = pitch;
        if (this.hasSpawned(player)) {
            PacketEvents.get().getPlayerUtils().sendPacket(player, new WrappedPacketOutEntityTeleport(this.entityID, this.position, yaw, pitch, this.onGround));
        }
    }
    
    public void move(final Player player, final Vector3d targetPosition) {
        this.position = targetPosition;
        final double distX = targetPosition.x - this.position.x;
        final double distY = targetPosition.y - this.position.y;
        final double distZ = targetPosition.z - this.position.z;
        final double dist = distX + distY + distZ;
        SendableWrapper sentPacket;
        if (dist > 8.0) {
            sentPacket = new WrappedPacketOutEntityTeleport(this.entityID, this.position, this.yaw, this.pitch, this.onGround);
        }
        else {
            sentPacket = new WrappedPacketOutEntity.WrappedPacketOutRelEntityMove(this.entityID, distX, distY, distZ, this.onGround);
        }
        if (this.hasSpawned(player)) {
            PacketEvents.get().getPlayerUtils().sendPacket(player, sentPacket);
        }
    }
    
    public void moveAndRotate(final Player player, final Vector3d targetPosition, final float yaw, final float pitch) {
        this.position = targetPosition;
        this.yaw = yaw;
        this.pitch = pitch;
        final double distX = targetPosition.x - this.position.x;
        final double distY = targetPosition.y - this.position.y;
        final double distZ = targetPosition.z - this.position.z;
        final double dist = distX + distY + distZ;
        SendableWrapper sentPacket;
        if (dist > 8.0) {
            sentPacket = new WrappedPacketOutEntityTeleport(this.entityID, this.position, yaw, pitch, this.onGround);
        }
        else {
            sentPacket = new WrappedPacketOutEntity.WrappedPacketOutRelEntityMoveLook(this.entityID, distX, distY, distZ, yaw, pitch, this.onGround);
        }
        if (this.hasSpawned(player)) {
            PacketEvents.get().getPlayerUtils().sendPacket(player, sentPacket);
        }
    }
    
    public void rotate(final Player player, final float yaw, final float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
        final WrappedPacketOutEntity.WrappedPacketOutEntityLook lookPacket = new WrappedPacketOutEntity.WrappedPacketOutEntityLook(this.entityID, (byte)(yaw * 256.0f / 360.0f), (byte)(pitch * 256.0f / 360.0f), this.onGround);
        final WrappedPacketOutEntityHeadRotation headRotationPacket = new WrappedPacketOutEntityHeadRotation(this.entityID, (byte)(yaw * 256.0f / 360.0f));
        if (this.hasSpawned(player)) {
            PacketEvents.get().getPlayerUtils().sendPacket(player, lookPacket);
            PacketEvents.get().getPlayerUtils().sendPacket(player, headRotationPacket);
        }
    }
    
    public void teleport(final List<Player> players, final Vector3d targetPosition, final float yaw, final float pitch) {
        this.position = targetPosition;
        this.yaw = yaw;
        this.pitch = pitch;
        for (final Player player : players) {
            if (this.hasSpawned(player)) {
                PacketEvents.get().getPlayerUtils().sendPacket(player, new WrappedPacketOutEntityTeleport(this.entityID, this.position, yaw, pitch, this.onGround));
            }
        }
    }
    
    public void move(final List<Player> players, final Vector3d targetPosition) {
        final double distX = targetPosition.x - this.position.x;
        final double distY = targetPosition.y - this.position.y;
        final double distZ = targetPosition.z - this.position.z;
        final double dist = distX + distY + distZ;
        this.position = targetPosition;
        SendableWrapper sentPacket;
        if (dist > 8.0) {
            sentPacket = new WrappedPacketOutEntityTeleport(this.entityID, this.position, this.yaw, this.pitch, this.onGround);
        }
        else {
            sentPacket = new WrappedPacketOutEntity.WrappedPacketOutRelEntityMove(this.entityID, distX, distY, distZ, this.onGround);
        }
        for (final Player player : players) {
            if (this.hasSpawned(player)) {
                PacketEvents.get().getPlayerUtils().sendPacket(player, sentPacket);
            }
        }
    }
    
    public void moveAndRotate(final List<Player> players, final Vector3d targetPosition, final float yaw, final float pitch) {
        final double distX = targetPosition.x - this.position.x;
        final double distY = targetPosition.y - this.position.y;
        final double distZ = targetPosition.z - this.position.z;
        final double dist = distX + distY + distZ;
        this.position = targetPosition;
        this.yaw = yaw;
        this.pitch = pitch;
        SendableWrapper sentPacket;
        if (dist > 8.0) {
            sentPacket = new WrappedPacketOutEntityTeleport(this.entityID, this.position, yaw, pitch, this.onGround);
        }
        else {
            sentPacket = new WrappedPacketOutEntity.WrappedPacketOutRelEntityMoveLook(this.entityID, distX, distY, distZ, yaw, pitch, this.onGround);
        }
        for (final Player player : players) {
            if (this.hasSpawned(player)) {
                PacketEvents.get().getPlayerUtils().sendPacket(player, sentPacket);
            }
        }
    }
    
    public void rotate(final List<Player> players, final float yaw, final float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
        final WrappedPacketOutEntity.WrappedPacketOutEntityLook lookPacket = new WrappedPacketOutEntity.WrappedPacketOutEntityLook(this.entityID, (byte)(yaw * 256.0f / 360.0f), (byte)(pitch * 256.0f / 360.0f), this.onGround);
        final WrappedPacketOutEntityHeadRotation headRotationPacket = new WrappedPacketOutEntityHeadRotation(this.entityID, (byte)(yaw * 256.0f / 360.0f));
        for (final Player player : players) {
            if (this.hasSpawned(player)) {
                PacketEvents.get().getPlayerUtils().sendPacket(player, lookPacket);
                PacketEvents.get().getPlayerUtils().sendPacket(player, headRotationPacket);
            }
        }
    }
    
    @Deprecated
    public void moveDelta(final List<Player> players, final Vector3d deltaPosition) {
        this.position = this.position.add(deltaPosition);
        final WrappedPacketOutEntityTeleport teleportPacket = new WrappedPacketOutEntityTeleport(this.entityID, this.position, this.yaw, this.pitch, this.onGround);
        for (final Player player : players) {
            if (this.hasSpawned(player)) {
                PacketEvents.get().getPlayerUtils().sendPacket(player, teleportPacket);
            }
        }
    }
    
    @Deprecated
    public void moveDeltaAndRotate(final List<Player> players, final Vector3d deltaPosition, final float yaw, final float pitch) {
        this.position = this.position.add(deltaPosition);
        this.yaw = yaw;
        this.pitch = pitch;
        final WrappedPacketOutEntityTeleport teleportPacket = new WrappedPacketOutEntityTeleport(this.entityID, this.position, yaw, pitch, this.onGround);
        for (final Player player : players) {
            if (this.hasSpawned(player)) {
                PacketEvents.get().getPlayerUtils().sendPacket(player, teleportPacket);
            }
        }
    }
}
