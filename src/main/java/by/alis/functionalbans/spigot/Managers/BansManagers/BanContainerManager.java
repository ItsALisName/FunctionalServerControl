package by.alis.functionalbans.spigot.Managers.BansManagers;

import by.alis.functionalbans.spigot.Additional.Enums.BanType;
import by.alis.functionalbans.spigot.FunctionalBansSpigot;
import org.bukkit.Bukkit;

import static by.alis.functionalbans.databases.StaticBases.getSQLiteManager;
import static by.alis.functionalbans.spigot.Additional.Containers.StaticContainers.getBannedPlayersContainer;

public class BanContainerManager {

    public void loadBansIntoRAM() {
        Bukkit.getScheduler().runTaskAsynchronously(FunctionalBansSpigot.getProvidingPlugin(FunctionalBansSpigot.class), () -> {
            getBannedPlayersContainer().addToBansContainer(
                    getSQLiteManager().getBannedIds(),
                    getSQLiteManager().getBannedIps(),
                    getSQLiteManager().getBannedPlayersNames(),
                    getSQLiteManager().getBanInitiators(),
                    getSQLiteManager().getBanReasons(),
                    getSQLiteManager().getBanTypes(),
                    getSQLiteManager().getBansDates(),
                    getSQLiteManager().getBansTimes(),
                    getSQLiteManager().getBannedUUIDs(),
                    getSQLiteManager().getUnbanTimes()
            );
        });
    }

    public void addToBanContainer(String id, String ip, String playerName, String initiatorName, String reason, BanType banType, String realBanDate, String realBanTime, String uuid, Long time) {
        getBannedPlayersContainer().addToBansContainer(
                id,
                ip,
                playerName,
                initiatorName,
                reason,
                banType,
                realBanDate,
                realBanTime,
                uuid,
                time
        );
    }

    public void removeFromBanContainer(String expression, String param) {
        if(expression.equalsIgnoreCase("-n")) {
            if(getBannedPlayersContainer().getNameContainer().contains(param)) {
                int indexOf = getBannedPlayersContainer().getNameContainer().indexOf(param);
                getBannedPlayersContainer().getIdsContainer().remove(indexOf);
                getBannedPlayersContainer().getIpContainer().remove(indexOf);
                getBannedPlayersContainer().getNameContainer().remove(indexOf);
                getBannedPlayersContainer().getInitiatorNameContainer().remove(indexOf);
                getBannedPlayersContainer().getReasonContainer().remove(indexOf);
                getBannedPlayersContainer().getBanTypesContainer().remove(indexOf);
                getBannedPlayersContainer().getRealBanDateContainer().remove(indexOf);
                getBannedPlayersContainer().getRealBanTimeContainer().remove(indexOf);
                getBannedPlayersContainer().getUUIDContainer().remove(indexOf);
                getBannedPlayersContainer().getBanTimeContainer().remove(indexOf);
                return;
            }
            return;
        }
        if(expression.equalsIgnoreCase("-ip")) {
            if(getBannedPlayersContainer().getIpContainer().contains(param)) {
                int indexOf = getBannedPlayersContainer().getIpContainer().indexOf(param);
                getBannedPlayersContainer().getIdsContainer().remove(indexOf);
                getBannedPlayersContainer().getIpContainer().remove(indexOf);
                getBannedPlayersContainer().getNameContainer().remove(indexOf);
                getBannedPlayersContainer().getInitiatorNameContainer().remove(indexOf);
                getBannedPlayersContainer().getReasonContainer().remove(indexOf);
                getBannedPlayersContainer().getBanTypesContainer().remove(indexOf);
                getBannedPlayersContainer().getRealBanDateContainer().remove(indexOf);
                getBannedPlayersContainer().getRealBanTimeContainer().remove(indexOf);
                getBannedPlayersContainer().getUUIDContainer().remove(indexOf);
                getBannedPlayersContainer().getBanTimeContainer().remove(indexOf);
                return;
            }
            return;
        }
        if(expression.equalsIgnoreCase("-id")) {
            Bukkit.getConsoleSender().sendMessage("LOL2");
            if(getBannedPlayersContainer().getIdsContainer().contains(param)) {
                Bukkit.getConsoleSender().sendMessage("LOL1");
                int indexOf = getBannedPlayersContainer().getIdsContainer().indexOf(param);
                getBannedPlayersContainer().getIdsContainer().remove(indexOf);
                getBannedPlayersContainer().getIpContainer().remove(indexOf);
                getBannedPlayersContainer().getNameContainer().remove(indexOf);
                getBannedPlayersContainer().getInitiatorNameContainer().remove(indexOf);
                getBannedPlayersContainer().getReasonContainer().remove(indexOf);
                getBannedPlayersContainer().getBanTypesContainer().remove(indexOf);
                getBannedPlayersContainer().getRealBanDateContainer().remove(indexOf);
                getBannedPlayersContainer().getRealBanTimeContainer().remove(indexOf);
                getBannedPlayersContainer().getUUIDContainer().remove(indexOf);
                getBannedPlayersContainer().getBanTimeContainer().remove(indexOf);
                Bukkit.getConsoleSender().sendMessage("LOL3");
                return;
            }
            Bukkit.getConsoleSender().sendMessage("LOL4");
            return;
        }
        if(expression.equalsIgnoreCase("-u")) {
            if(getBannedPlayersContainer().getUUIDContainer().contains(param)) {
                int indexOf = getBannedPlayersContainer().getUUIDContainer().indexOf(param);
                getBannedPlayersContainer().getIdsContainer().remove(indexOf);
                getBannedPlayersContainer().getIpContainer().remove(indexOf);
                getBannedPlayersContainer().getNameContainer().remove(indexOf);
                getBannedPlayersContainer().getInitiatorNameContainer().remove(indexOf);
                getBannedPlayersContainer().getReasonContainer().remove(indexOf);
                getBannedPlayersContainer().getBanTypesContainer().remove(indexOf);
                getBannedPlayersContainer().getRealBanDateContainer().remove(indexOf);
                getBannedPlayersContainer().getRealBanTimeContainer().remove(indexOf);
                getBannedPlayersContainer().getUUIDContainer().remove(indexOf);
                getBannedPlayersContainer().getBanTimeContainer().remove(indexOf);
                return;
            }
            return;
        }
    }

}
