package by.alis.functionalservercontrol.spigot.managers;

import static by.alis.functionalservercontrol.spigot.additional.containers.StaticContainers.getBannedPlayersContainer;
import static by.alis.functionalservercontrol.spigot.additional.containers.StaticContainers.getMutedPlayersContainer;
import static by.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;
import static by.alis.functionalservercontrol.spigot.managers.BaseManager.getBaseManager;

public class IdsManager {

    private double generateId() {
        return Math.random() * (100000 - 500) + 500;
    }

    private boolean isIdFree(double id) {
        if(getConfigSettings().isAllowedUseRamAsContainer()) {
            return !getBannedPlayersContainer().getIdsContainer().contains(String.valueOf((int)Math.round(id))) && !getMutedPlayersContainer().getIdsContainer().contains(String.valueOf((int)Math.round(id)));
        } else {
            return !getBaseManager().getBannedIds().contains(String.valueOf((int)Math.round(id))) && !getBaseManager().getMutedIds().contains(String.valueOf((int)Math.round(id)));
        }
    }

    public String getId() {
        String b = "ID_ERROR";
        for(int a = 0; a < 50; a++) {
            double id = generateId();
            if(isIdFree(id)) {
                b = String.valueOf((int)Math.round(id));
                return b;
            }
        }
        return b;
    }

}
