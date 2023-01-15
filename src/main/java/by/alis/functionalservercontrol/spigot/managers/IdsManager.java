package by.alis.functionalservercontrol.spigot.managers;

import static by.alis.functionalservercontrol.spigot.additional.containers.StaticContainers.getBannedPlayersContainer;
import static by.alis.functionalservercontrol.spigot.additional.containers.StaticContainers.getMutedPlayersContainer;
import static by.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;
import static by.alis.functionalservercontrol.spigot.managers.BaseManager.getBaseManager;

public class IdsManager {

    private int generateId() {
        return (int) (Math.random() * (100000 - 500) + 500);
    }

    private boolean isIdFree(int id) {
        if(getConfigSettings().isAllowedUseRamAsContainer()) {
            return !getBannedPlayersContainer().getIdsContainer().contains(String.valueOf(Math.round(id))) && !getMutedPlayersContainer().getIdsContainer().contains(String.valueOf(Math.round(id)));
        } else {
            return !getBaseManager().getBannedIds().contains(String.valueOf(Math.round(id))) && !getBaseManager().getMutedIds().contains(String.valueOf(Math.round(id)));
        }
    }

    public boolean isBannedId(String id) {
        if (getConfigSettings().isAllowedUseRamAsContainer()) {
            return getBannedPlayersContainer().getIdsContainer().contains(id);
        } else {
            return getBaseManager().getBannedIds().contains(id);
        }
    }

    public boolean isMutedId(String id) {
        if(getConfigSettings().isAllowedUseRamAsContainer()) {
            return getMutedPlayersContainer().getIdsContainer().contains(id);
        } else {
            return getBaseManager().getMutedIds().contains(id);
        }
    }

    public String getId() {
        String b = "ID_ERROR";
        for(int a = 0; a < 50; a++) {
            int id = generateId();
            if(isIdFree(id)) {
                b = String.valueOf(Math.round(id));
                return b;
            }
        }
        return b;
    }

}
