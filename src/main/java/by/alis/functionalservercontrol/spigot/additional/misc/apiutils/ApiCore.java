package by.alis.functionalservercontrol.spigot.additional.misc.apiutils;

import by.alis.functionalservercontrol.api.FunctionalApi;
import by.alis.functionalservercontrol.api.interfaces.FunctionalBanEntry;
import by.alis.functionalservercontrol.api.interfaces.FunctionalMuteEntry;
import by.alis.functionalservercontrol.spigot.additional.coreadapters.Adapter;
import by.alis.functionalservercontrol.spigot.additional.coreadapters.CoreAdapter;

import java.util.*;

import static by.alis.functionalservercontrol.spigot.additional.containers.StaticContainers.getBannedPlayersContainer;
import static by.alis.functionalservercontrol.spigot.additional.containers.StaticContainers.getMutedPlayersContainer;
import static by.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;
import static by.alis.functionalservercontrol.spigot.managers.BaseManager.getBaseManager;

public class ApiCore implements FunctionalApi {

    @Override
    public Set<FunctionalBanEntry> getBans() {
        Set<FunctionalBanEntry> entries = new HashSet<>();
        if(getConfigSettings().isAllowedUseRamAsContainer()) {
            return getBannedPlayersContainer().getBanEntries();
        } else {
            for(String id : getBaseManager().getBannedIds()) {
                int i = getBaseManager().getBannedIds().indexOf(id);
                BanEntry banEntry = new BanEntry(getBaseManager().getBannedPlayersNames().get(i), id, getBaseManager().getBannedIps().get(i), getBaseManager().getBanInitiators().get(i), getBaseManager().getBanReasons().get(i), getBaseManager().getBanTypes().get(i), getBaseManager().getBansDates().get(i), getBaseManager().getBansTimes().get(i), UUID.fromString(getBaseManager().getBannedUUIDs().get(i)), getBaseManager().getUnbanTimes().get(i));
                entries.add(banEntry);
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
            for(String id : getBaseManager().getBannedIds()) {
                int i = getBaseManager().getBannedIds().indexOf(id);
                MuteEntry muteEntry = new MuteEntry(getBaseManager().getMutedPlayersNames().get(i), id, getBaseManager().getMutedIps().get(i), getBaseManager().getMuteInitiators().get(i), getBaseManager().getMuteReasons().get(i), getBaseManager().getMuteTypes().get(i), getBaseManager().getMuteDates().get(i), getBaseManager().getMuteTimes().get(i), UUID.fromString(getBaseManager().getMutedUUIDs().get(i)), getBaseManager().getUnmuteTimes().get(i));
                entries.add(muteEntry);
            }
        }
        return entries;
    }

    @Override
    public FunctionalStatistics getPlayerStatistics() {
        return new FunctionalStatistics();
    }
}
