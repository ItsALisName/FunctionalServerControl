package by.alis.functionalservercontrol.spigot.Managers;

import static by.alis.functionalservercontrol.databases.DataBases.getSQLiteManager;
import static by.alis.functionalservercontrol.spigot.Additional.Containers.StaticContainers.getBannedPlayersContainer;
import static by.alis.functionalservercontrol.spigot.Additional.Containers.StaticContainers.getMutedPlayersContainer;
import static by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;

public class IdsManager {

    private double generateId() {
        return Math.random() * (100000 - 500) + 500;
    }

    private boolean isIdFree(double id) {
        if(getConfigSettings().isAllowedUseRamAsContainer()) {
            return !getBannedPlayersContainer().getIdsContainer().contains(String.valueOf((int)Math.round(id))) && !getMutedPlayersContainer().getIdsContainer().contains(String.valueOf((int)Math.round(id)));
        } else {
            switch (getConfigSettings().getStorageType()) {
                case SQLITE: return !getSQLiteManager().getBannedIds().contains(String.valueOf((int)Math.round(id))) && !getSQLiteManager().getMutedIds().contains(String.valueOf((int)Math.round(id)));
                case H2: return true;
                case MYSQL: return true;
            }
        }
        return true;
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
