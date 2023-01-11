package by.alis.functionalservercontrol.spigot.additional.containers;

import by.alis.functionalservercontrol.api.enums.BanType;
import by.alis.functionalservercontrol.spigot.additional.misc.apiutils.BanEntry;
import by.alis.functionalservercontrol.spigot.additional.misc.apiutils.FunctionalBanEntry;

import java.util.*;

public class BannedPlayersContainer {

    private final List<String> idsContainer = new ArrayList<>();
    private final List<String> ipContainer = new ArrayList<>();
    private final List<String> nameContainer = new ArrayList<>();
    private final List<String> initiatorNameContainer = new ArrayList<>();
    private final List<String> reasonContainer = new ArrayList<>();
    private final List<BanType> banTypesContainer = new ArrayList<>();
    private final List<String> realBanDateContainer = new ArrayList<>();
    private final List<String> realBanTimeContainer = new ArrayList<>();
    private final List<String> uuidContainer = new ArrayList<>();
    private final List<Long> banTimeContainer = new ArrayList<>();
    private final Set<FunctionalBanEntry> banEntries = new HashSet<>();

    public void addToBansContainer(List<String> id, List<String> ip, List<String> playerName, List<String> initiatorName, List<String> reason, List<BanType> banType, List<String> realBanDate, List<String> realBanTime, List<String> uuid, List<Long> time) {
        this.idsContainer.addAll(id);
        this.ipContainer.addAll(ip);
        this.nameContainer.addAll(playerName);
        this.initiatorNameContainer.addAll(initiatorName);
        this.reasonContainer.addAll(reason);
        this.banTypesContainer.addAll(banType);
        this.realBanDateContainer.addAll(realBanDate);
        this.realBanTimeContainer.addAll(realBanTime);
        this.uuidContainer.addAll(uuid);
        this.banTimeContainer.addAll(time);
        for(String bId : id) {
            int i = id.indexOf(bId);
            BanEntry banEntry = new BanEntry(playerName.get(i), bId, ip.get(i), initiatorName.get(i), reason.get(i), banType.get(i), realBanDate.get(i), realBanTime.get(i), UUID.fromString(uuid.get(i)), time.get(i));
            banEntries.add(banEntry);
        }
    }

    public void addToBansContainer(String id, String ip, String playerName, String initiatorName, String reason, BanType banType, String realBanDate, String realBanTime, String uuid, Long time) {
        this.idsContainer.add(id);
        this.ipContainer.add(ip);
        this.nameContainer.add(playerName);
        this.initiatorNameContainer.add(initiatorName);
        this.reasonContainer.add(reason);
        this.banTypesContainer.add(banType);
        this.realBanDateContainer.add(realBanDate);
        this.realBanTimeContainer.add(realBanTime);
        this.uuidContainer.add(uuid);
        this.banTimeContainer.add(time);
        BanEntry banEntry = new BanEntry(playerName, id, ip, initiatorName, reason, banType, realBanDate, realBanTime, UUID.fromString(uuid), time);
        banEntries.add(banEntry);
    }

    public List<String> getIdsContainer() {
        return idsContainer;
    }

    public List<String> getIpContainer() {
        return ipContainer;
    }

    public List<String> getNameContainer() {
        return nameContainer;
    }

    public List<String> getInitiatorNameContainer() {
        return initiatorNameContainer;
    }

    public List<String> getReasonContainer() {
        return reasonContainer;
    }

    public List<BanType> getBanTypesContainer() {
        return banTypesContainer;
    }

    public List<String> getRealBanDateContainer() {
        return realBanDateContainer;
    }

    public List<String> getUUIDContainer() {
        return uuidContainer;
    }

    public List<String> getRealBanTimeContainer() {
        return realBanTimeContainer;
    }

    public List<Long> getBanTimeContainer() {
        return banTimeContainer;
    }

    public Set<FunctionalBanEntry> getBanEntries() {
        return banEntries;
    }
}
