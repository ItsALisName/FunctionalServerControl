package by.alis.functionalservercontrol.spigot.additional.misc.apiutils;

import by.alis.functionalservercontrol.api.FunctionalApi;
import by.alis.functionalservercontrol.spigot.additional.coreadapters.Adapter;
import by.alis.functionalservercontrol.spigot.additional.coreadapters.CoreAdapter;

import java.util.*;

import static by.alis.functionalservercontrol.databases.DataBases.getSQLiteManager;
import static by.alis.functionalservercontrol.spigot.additional.containers.StaticContainers.getBannedPlayersContainer;
import static by.alis.functionalservercontrol.spigot.additional.containers.StaticContainers.getMutedPlayersContainer;
import static by.alis.functionalservercontrol.spigot.additional.globalsettings.StaticSettingsAccessor.getConfigSettings;

public class ApiCore implements FunctionalApi {

    @Override
    public Set<FunctionalBanEntry> getBans() {
        Set<FunctionalBanEntry> entries = new HashSet<>();
        if(getConfigSettings().isAllowedUseRamAsContainer()) {
            return getBannedPlayersContainer().getBanEntries();
        } else {
            switch (getConfigSettings().getStorageType()) {
                case SQLITE: {
                    for(String id : getSQLiteManager().getBannedIds()) {
                        int i = getSQLiteManager().getBannedIds().indexOf(id);
                        BanEntry banEntry = new BanEntry(getSQLiteManager().getBannedPlayersNames().get(i), id, getSQLiteManager().getBannedIps().get(i), getSQLiteManager().getBanInitiators().get(i), getSQLiteManager().getBanReasons().get(i), getSQLiteManager().getBanTypes().get(i), getSQLiteManager().getBansDates().get(i), getSQLiteManager().getBansTimes().get(i), UUID.fromString(getSQLiteManager().getBannedUUIDs().get(i)), getSQLiteManager().getUnbanTimes().get(i));
                        entries.add(banEntry);
                    }
                }
                case H2: {}
            }
        }
        return entries;
    }

    @Override
    public Adapter getCoreAdapter() {
        return CoreAdapter.getAdapter();
    }

    @Override
    public Set<FunctionalMuteEntry> getMutes() {
        Set<FunctionalMuteEntry> entries = new HashSet<>();
        if(getConfigSettings().isAllowedUseRamAsContainer()) {
            return getMutedPlayersContainer().getMuteEntries();
        } else {
            switch (getConfigSettings().getStorageType()) {
                case SQLITE: {
                    for(String id : getSQLiteManager().getBannedIds()) {
                        int i = getSQLiteManager().getBannedIds().indexOf(id);
                        MuteEntry muteEntry = new MuteEntry(getSQLiteManager().getMutedPlayersNames().get(i), id, getSQLiteManager().getMutedIps().get(i), getSQLiteManager().getMuteInitiators().get(i), getSQLiteManager().getMuteReasons().get(i), getSQLiteManager().getMuteTypes().get(i), getSQLiteManager().getMuteDates().get(i), getSQLiteManager().getMuteTimes().get(i), UUID.fromString(getSQLiteManager().getMutedUUIDs().get(i)), getSQLiteManager().getUnmuteTimes().get(i));
                        entries.add(muteEntry);
                    }
                }
                case H2: {}
            }
        }
        return entries;
    }

    @Override
    public FunctionalStatistics getPlayerStatistics() {
        return new FunctionalStatistics();
    }
}
