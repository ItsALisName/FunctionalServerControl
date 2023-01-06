package by.alis.functionalservercontrol.spigot.Managers.Mute;

import by.alis.functionalservercontrol.API.Enums.MuteType;
import org.bukkit.Bukkit;

import static by.alis.functionalservercontrol.databases.DataBases.getSQLiteManager;
import static by.alis.functionalservercontrol.spigot.Additional.Containers.StaticContainers.getMutedPlayersContainer;
import static by.alis.functionalservercontrol.spigot.Additional.Misc.TextUtils.setColors;

public class MuteContainerManager {

    public void loadMutesIntoRAM() {
        getMutedPlayersContainer().addToMuteContainer(
                getSQLiteManager().getMutedIds(),
                getSQLiteManager().getMutedIps(),
                getSQLiteManager().getMutedPlayersNames(),
                getSQLiteManager().getMuteInitiators(),
                getSQLiteManager().getMuteReasons(),
                getSQLiteManager().getMuteTypes(),
                getSQLiteManager().getMuteDates(),
                getSQLiteManager().getMuteTimes(),
                getSQLiteManager().getMutedUUIDs(),
                getSQLiteManager().getUnmuteTimes()
        );
        Bukkit.getConsoleSender().sendMessage(setColors("&a[FunctionalServerControl] Mutes loaded into RAM(Total: %count%)".replace("%count%", String.valueOf(getMutedPlayersContainer().getIdsContainer().size()))));
    }

    public void addToMuteContainer(String id, String ip, String playerName, String initiatorName, String reason, MuteType muteType, String realBanDate, String realBanTime, String uuid, Long time) {
        getMutedPlayersContainer().addToMuteContainer(
                id,
                ip,
                playerName,
                initiatorName,
                reason,
                muteType,
                realBanDate,
                realBanTime,
                uuid,
                time
        );
    }

    public void removeFromMuteContainer(String expression, String param) {
        if(expression.equalsIgnoreCase("-n")) {
            if(getMutedPlayersContainer().getNameContainer().contains(param)) {
                int indexOf = getMutedPlayersContainer().getNameContainer().indexOf(param);
                getMutedPlayersContainer().getIdsContainer().remove(indexOf);
                getMutedPlayersContainer().getIpContainer().remove(indexOf);
                getMutedPlayersContainer().getNameContainer().remove(indexOf);
                getMutedPlayersContainer().getInitiatorNameContainer().remove(indexOf);
                getMutedPlayersContainer().getReasonContainer().remove(indexOf);
                getMutedPlayersContainer().getMuteTypesContainer().remove(indexOf);
                getMutedPlayersContainer().getRealMuteDateContainer().remove(indexOf);
                getMutedPlayersContainer().getRealMuteTimeContainer().remove(indexOf);
                getMutedPlayersContainer().getUUIDContainer().remove(indexOf);
                getMutedPlayersContainer().getMuteTimeContainer().remove(indexOf);
                return;
            }
            return;
        }
        if(expression.equalsIgnoreCase("-ip")) {
            if(getMutedPlayersContainer().getIpContainer().contains(param)) {
                int indexOf = getMutedPlayersContainer().getIpContainer().indexOf(param);
                getMutedPlayersContainer().getIdsContainer().remove(indexOf);
                getMutedPlayersContainer().getIpContainer().remove(indexOf);
                getMutedPlayersContainer().getNameContainer().remove(indexOf);
                getMutedPlayersContainer().getInitiatorNameContainer().remove(indexOf);
                getMutedPlayersContainer().getReasonContainer().remove(indexOf);
                getMutedPlayersContainer().getMuteTypesContainer().remove(indexOf);
                getMutedPlayersContainer().getRealMuteDateContainer().remove(indexOf);
                getMutedPlayersContainer().getRealMuteTimeContainer().remove(indexOf);
                getMutedPlayersContainer().getUUIDContainer().remove(indexOf);
                getMutedPlayersContainer().getMuteTimeContainer().remove(indexOf);
                return;
            }
            return;
        }
        if(expression.equalsIgnoreCase("-id")) {
            if(getMutedPlayersContainer().getIdsContainer().contains(param)) {
                int indexOf = getMutedPlayersContainer().getIdsContainer().indexOf(param);
                getMutedPlayersContainer().getIdsContainer().remove(indexOf);
                getMutedPlayersContainer().getIpContainer().remove(indexOf);
                getMutedPlayersContainer().getNameContainer().remove(indexOf);
                getMutedPlayersContainer().getInitiatorNameContainer().remove(indexOf);
                getMutedPlayersContainer().getReasonContainer().remove(indexOf);
                getMutedPlayersContainer().getMuteTypesContainer().remove(indexOf);
                getMutedPlayersContainer().getRealMuteDateContainer().remove(indexOf);
                getMutedPlayersContainer().getRealMuteTimeContainer().remove(indexOf);
                getMutedPlayersContainer().getUUIDContainer().remove(indexOf);
                getMutedPlayersContainer().getMuteTimeContainer().remove(indexOf);
                return;
            }
            return;
        }
        if(expression.equalsIgnoreCase("-u")) {
            if(getMutedPlayersContainer().getUUIDContainer().contains(param)) {
                int indexOf = getMutedPlayersContainer().getUUIDContainer().indexOf(param);
                getMutedPlayersContainer().getIdsContainer().remove(indexOf);
                getMutedPlayersContainer().getIpContainer().remove(indexOf);
                getMutedPlayersContainer().getNameContainer().remove(indexOf);
                getMutedPlayersContainer().getInitiatorNameContainer().remove(indexOf);
                getMutedPlayersContainer().getReasonContainer().remove(indexOf);
                getMutedPlayersContainer().getMuteTypesContainer().remove(indexOf);
                getMutedPlayersContainer().getRealMuteDateContainer().remove(indexOf);
                getMutedPlayersContainer().getRealMuteTimeContainer().remove(indexOf);
                getMutedPlayersContainer().getUUIDContainer().remove(indexOf);
                getMutedPlayersContainer().getMuteTimeContainer().remove(indexOf);
                return;
            }
            return;
        }
    }
    
}
