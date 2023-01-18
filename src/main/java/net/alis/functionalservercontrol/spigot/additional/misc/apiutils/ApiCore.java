package net.alis.functionalservercontrol.spigot.additional.misc.apiutils;

import net.alis.functionalservercontrol.api.interfaces.FunctionalBanEntry;
import net.alis.functionalservercontrol.api.interfaces.FunctionalMuteEntry;
import net.alis.functionalservercontrol.spigot.additional.coreadapters.Adapter;
import net.alis.functionalservercontrol.spigot.additional.coreadapters.CoreAdapter;
import net.alis.functionalservercontrol.spigot.managers.BaseManager;
import net.alis.functionalservercontrol.api.FunctionalApi;

import java.util.*;

import static net.alis.functionalservercontrol.spigot.additional.containers.StaticContainers.getBannedPlayersContainer;
import static net.alis.functionalservercontrol.spigot.additional.containers.StaticContainers.getMutedPlayersContainer;
import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;

public class ApiCore implements FunctionalApi {

    @Override
    public Set<FunctionalBanEntry> getBans() {
        Set<FunctionalBanEntry> entries = new HashSet<>();
        if(getConfigSettings().isAllowedUseRamAsContainer()) {
            return getBannedPlayersContainer().getBanEntries();
        } else {
            for(String id : BaseManager.getBaseManager().getBannedIds()) {
                int i = BaseManager.getBaseManager().getBannedIds().indexOf(id);
                BanEntry banEntry = new BanEntry(BaseManager.getBaseManager().getBannedPlayersNames().get(i), id, BaseManager.getBaseManager().getBannedIps().get(i), BaseManager.getBaseManager().getBanInitiators().get(i), BaseManager.getBaseManager().getBanReasons().get(i), BaseManager.getBaseManager().getBanTypes().get(i), BaseManager.getBaseManager().getBansDates().get(i), BaseManager.getBaseManager().getBansTimes().get(i), UUID.fromString(BaseManager.getBaseManager().getBannedUUIDs().get(i)), BaseManager.getBaseManager().getUnbanTimes().get(i));
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
            for(String id : BaseManager.getBaseManager().getBannedIds()) {
                int i = BaseManager.getBaseManager().getBannedIds().indexOf(id);
                MuteEntry muteEntry = new MuteEntry(BaseManager.getBaseManager().getMutedPlayersNames().get(i), id, BaseManager.getBaseManager().getMutedIps().get(i), BaseManager.getBaseManager().getMuteInitiators().get(i), BaseManager.getBaseManager().getMuteReasons().get(i), BaseManager.getBaseManager().getMuteTypes().get(i), BaseManager.getBaseManager().getMuteDates().get(i), BaseManager.getBaseManager().getMuteTimes().get(i), UUID.fromString(BaseManager.getBaseManager().getMutedUUIDs().get(i)), BaseManager.getBaseManager().getUnmuteTimes().get(i));
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
