package by.alis.functionalservercontrol.spigot.Managers.Bans;

import by.alis.functionalservercontrol.API.Enums.BanType;
import org.bukkit.Bukkit;

import static by.alis.functionalservercontrol.databases.DataBases.getSQLiteManager;
import static by.alis.functionalservercontrol.spigot.Additional.Containers.StaticContainers.getBannedPlayersContainer;
import static by.alis.functionalservercontrol.spigot.Additional.Misc.TextUtils.setColors;

public class BanContainerManager {

    public void loadBansIntoRAM() {
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
        Bukkit.getConsoleSender().sendMessage(setColors("&a[FunctionalServerControl] Bans loaded into RAM(Total: %count%)".replace("%count%", String.valueOf(getSQLiteManager().getBannedIds().size()))));
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
            if(getBannedPlayersContainer().getIdsContainer().contains(param)) {
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
                return;
            }
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
