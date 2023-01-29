package net.alis.functionalservercontrol.api.naf.v1_10_0.entries;

import net.alis.functionalservercontrol.api.interfaces.FunctionalMuteEntry;
import net.alis.functionalservercontrol.api.enums.MuteType;
import net.alis.functionalservercontrol.api.naf.v1_10_0.util.FID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static net.alis.functionalservercontrol.spigot.additional.containers.StaticContainers.getMutedPlayersContainer;
import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;
import static net.alis.functionalservercontrol.spigot.managers.BaseManager.getBaseManager;
import static net.alis.functionalservercontrol.spigot.managers.mute.MuteManager.getMuteContainerManager;

public class MuteEntry implements FunctionalMuteEntry {

    private final @Nullable String name;
    private final @NotNull String id;
    private final @Nullable String address;
    private final @NotNull String initiator;
    private final @NotNull String reason;
    private final @NotNull MuteType muteType;
    private final @NotNull String muteDate;
    private final @NotNull String muteTime;
    private final @Nullable UUID uuid;
    private final @Nullable FID fid;
    private final long unmuteTime;

    public MuteEntry(@Nullable String name, @NotNull String id, @Nullable String address, @NotNull String initiator, @NotNull String reason, @NotNull MuteType muteType, @NotNull String muteDate, @NotNull String muteTime, @Nullable UUID uuid, long unmuteTime, FID fid) {
        this.name = name;
        this.id = id;
        this.address = address;
        this.initiator = initiator;
        this.reason = reason;
        this.muteType = muteType;
        this.muteDate = muteDate;
        this.muteTime = muteTime;
        this.uuid = uuid;
        this.unmuteTime = unmuteTime;
        this.fid = fid;
    }

    @Override
    public @Nullable String getName() {
        return name;
    }

    @Override
    public @NotNull String getId() {
        return id;
    }

    @Override
    public @Nullable String getAddress() {
        return address;
    }

    @Override
    public @NotNull MuteType getMuteType() {
        return muteType;
    }

    @Override
    public @NotNull String getInitiator() {
        return initiator;
    }

    @Override
    public @NotNull String getReason() {
        return reason;
    }

    @Override
    public @NotNull String getMuteDate() {
        return muteDate;
    }

    @Override
    public @NotNull String getMuteTime() {
        return muteTime;
    }

    @Override
    public @Nullable UUID getUniqueId() {
        return uuid;
    }

    @Override
    public @Nullable FID getFunctionalId() {
        return null;
    }

    @Override
    public long getUnmuteTime() {
        return unmuteTime;
    }

    @Override
    public void unmute() {
        getBaseManager().deleteFromMutedPlayers("-id", getId());
        getBaseManager().deleteFromNullMutedPlayers("-id", getId());
        if(getConfigSettings().isAllowedUseRamAsContainer()) {
            getMuteContainerManager().removeFromMuteContainer("-id", getId());
            getMutedPlayersContainer().getMuteEntries().remove(this);
        }
    }
}
