package by.alis.functionalbans.spigot.Managers;

import static by.alis.functionalbans.spigot.Additional.Containers.StaticContainers.getBannedPlayersContainer;

public class IdsManager {

    private double generateId() {
        return Math.random() * (50000 - 500) + 500;
    }

    private boolean isIdFree(double id) {
        return !getBannedPlayersContainer().getIdsContainer().contains((int)Math.round(id));
    }

    public String getId() {
        String sId = "ID_ERROR";
        for(int a = 0; a < 25; a++) {
            double id = generateId();
            if(isIdFree(id)) {
                sId = String.valueOf((int)Math.round(id));
                return sId;
            }
        }
        return sId;
    }

}
