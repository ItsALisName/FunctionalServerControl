package by.alis.functionalservercontrol.spigot.Additional.Containers;

import by.alis.functionalservercontrol.API.Enums.BanType;

import java.util.ArrayList;
import java.util.List;

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
    }

    public void addToBansContainer(String id, String playerName, String initiatorName, String reason, BanType banType, String realBanDate, String realBanTime, Long time) {
        this.idsContainer.add(id);
        this.nameContainer.add(playerName);
        this.initiatorNameContainer.add(initiatorName);
        this.reasonContainer.add(reason);
        this.banTypesContainer.add(banType);
        this.realBanDateContainer.add(realBanDate);
        this.realBanTimeContainer.add(realBanTime);
        this.banTimeContainer.add(time);
    }

    public List<String> getIdsContainer() {
        return idsContainer;
    }

    public List<String> getIpContainer() {
        return this.ipContainer;
    }

    public List<String> getNameContainer() {
        return this.nameContainer;
    }

    public List<String> getInitiatorNameContainer() {
        return this.initiatorNameContainer;
    }

    public List<String> getReasonContainer() {
        return this.reasonContainer;
    }

    public List<BanType> getBanTypesContainer() {
        return this.banTypesContainer;
    }

    public List<String> getRealBanDateContainer() {
        return this.realBanDateContainer;
    }

    public List<String> getUUIDContainer() {
        return uuidContainer;
    }

    public List<String> getRealBanTimeContainer() {
        return this.realBanTimeContainer;
    }

    public List<Long> getBanTimeContainer() {
        return this.banTimeContainer;
    }
}
