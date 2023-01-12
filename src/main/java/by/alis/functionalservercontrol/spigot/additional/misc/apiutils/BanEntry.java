package by.alis.functionalservercontrol.spigot.additional.misc.apiutils;

import by.alis.functionalservercontrol.api.enums.BanType;
import by.alis.functionalservercontrol.api.interfaces.FunctionalBanEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static by.alis.functionalservercontrol.spigot.additional.containers.StaticContainers.getBanContainerManager;
import static by.alis.functionalservercontrol.spigot.additional.containers.StaticContainers.getBannedPlayersContainer;
import static by.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;
import static by.alis.functionalservercontrol.spigot.managers.BaseManager.getBaseManager;

public class BanEntry implements FunctionalBanEntry {

    private final @Nullable String name;
    private final @NotNull String id;
    private final @Nullable String address;
    private final @NotNull String initiator;
    private final @NotNull String reason;
    private final @NotNull BanType banType;
    private final @NotNull String banDate;
    private final @NotNull String banTime;
    private final @Nullable UUID uuid;
    private final long unbanTime;

    public BanEntry(@Nullable String name, @NotNull String id, @Nullable String address, @NotNull String initiator, @NotNull String reason, @NotNull BanType banType, @NotNull String banDate, @NotNull String banTime, @Nullable UUID uuid, long unbanTime) {
        this.name = name;
        this.id = id;
        this.address = address;
        this.initiator = initiator;
        this.reason = reason;
        this.banType = banType;
        this.banDate = banDate;
        this.banTime = banTime;
        this.uuid = uuid;
        this.unbanTime = unbanTime;
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
    public @NotNull String getInitiator() {
        return initiator;
    }

    @Override
    public @NotNull String getReason() {
        return reason;
    }

    @Override
    public @NotNull String getBanDate() {
        return banDate;
    }

    @Override
    public @NotNull BanType getBanType() {
        return banType;
    }

    @Override
    public @NotNull String getBanTime() {
        return banTime;
    }

    @Override
    public @Nullable UUID getUniqueId() {
        return uuid;
    }

    @Override
    public long getUnbanTime() {
        return unbanTime;
    }

    @Override
    public void unban() {
        getBaseManager().deleteFromBannedPlayers("-id", getId());
        getBaseManager().deleteFromNullBannedPlayers("-id", getId());
        if(getConfigSettings().isAllowedUseRamAsContainer()) {
            getBanContainerManager().removeFromBanContainer("-id", getId());
            getBannedPlayersContainer().getBanEntries().remove(this);
        }
    }
}
