package by.alis.functionalservercontrol.spigot.Additional.Containers;

import by.alis.functionalservercontrol.api.Enums.MuteType;

import java.util.ArrayList;
import java.util.List;

public class MutedPlayersContainer {

    private final List<String> idsContainer = new ArrayList<>();
    private final List<String> ipContainer = new ArrayList<>();
    private final List<String> nameContainer = new ArrayList<>();
    private final List<String> initiatorNameContainer = new ArrayList<>();
    private final List<String> reasonContainer = new ArrayList<>();
    private final List<MuteType> muteTypesContainer = new ArrayList<>();
    private final List<String> realMuteDateContainer = new ArrayList<>();
    private final List<String> realMuteTimeContainer = new ArrayList<>();
    private final List<String> uuidContainer = new ArrayList<>();
    private final List<Long> muteTimeContainer = new ArrayList<>();

    public void addToMuteContainer(List<String> id, List<String> ip, List<String> playerName, List<String> initiatorName, List<String> reason, List<MuteType> muteType, List<String> realMuteDate, List<String> realMuteTime, List<String> uuid, List<Long> time) {
        this.idsContainer.addAll(id);
        this.ipContainer.addAll(ip);
        this.nameContainer.addAll(playerName);
        this.initiatorNameContainer.addAll(initiatorName);
        this.reasonContainer.addAll(reason);
        this.muteTypesContainer.addAll(muteType);
        this.realMuteDateContainer.addAll(realMuteDate);
        this.realMuteTimeContainer.addAll(realMuteTime);
        this.uuidContainer.addAll(uuid);
        this.muteTimeContainer.addAll(time);
    }

    public void addToMuteContainer(String id, String ip, String playerName, String initiatorName, String reason, MuteType muteType, String realMuteDate, String realMuteTime, String uuid, Long time) {
        this.idsContainer.add(id);
        this.ipContainer.add(ip);
        this.nameContainer.add(playerName);
        this.initiatorNameContainer.add(initiatorName);
        this.reasonContainer.add(reason);
        this.muteTypesContainer.add(muteType);
        this.realMuteDateContainer.add(realMuteDate);
        this.realMuteTimeContainer.add(realMuteTime);
        this.uuidContainer.add(uuid);
        this.muteTimeContainer.add(time);
    }

    public void addToMuteContainer(String id, String playerName, String initiatorName, String reason, MuteType muteType, String realMuteDate, String realMuteTime, Long time) {
        this.idsContainer.add(id);
        this.nameContainer.add(playerName);
        this.initiatorNameContainer.add(initiatorName);
        this.reasonContainer.add(reason);
        this.muteTypesContainer.add(muteType);
        this.realMuteDateContainer.add(realMuteDate);
        this.realMuteTimeContainer.add(realMuteTime);
        this.muteTimeContainer.add(time);
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

    public List<MuteType> getMuteTypesContainer() {
        return this.muteTypesContainer;
    }

    public List<String> getRealMuteDateContainer() {
        return this.realMuteDateContainer;
    }

    public List<String> getUUIDContainer() {
        return uuidContainer;
    }

    public List<String> getRealMuteTimeContainer() {
        return this.realMuteTimeContainer;
    }

    public List<Long> getMuteTimeContainer() {
        return this.muteTimeContainer;
    }

}
