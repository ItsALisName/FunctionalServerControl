package net.alis.functionalservercontrol.spigot.additional.containers;

import net.alis.functionalservercontrol.api.enums.MuteType;
import net.alis.functionalservercontrol.api.interfaces.FunctionalMuteEntry;
import net.alis.functionalservercontrol.api.naf.v1_10_0.util.FID;
import net.alis.functionalservercontrol.api.naf.v1_10_0.entries.MuteEntry;

import java.util.*;

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
    private final List<FID> fids = new ArrayList<>();
    Set<FunctionalMuteEntry> muteEntries = new HashSet<>();

    public void addToMuteContainer(List<String> id, List<String> ip, List<String> playerName, List<String> initiatorName, List<String> reason, List<MuteType> muteType, List<String> realMuteDate, List<String> realMuteTime, List<String> uuid, List<Long> time, List<FID> fids) {
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
        this.fids.addAll(fids);
        for(String bId : id) {
            int i = id.indexOf(bId);
            MuteEntry muteEntry = new MuteEntry(playerName.get(i), bId, ip.get(i), initiatorName.get(i), reason.get(i), muteType.get(i), realMuteDate.get(i), realMuteTime.get(i), UUID.fromString(uuid.get(i)), time.get(i), fids.get(i));
            muteEntries.add(muteEntry);
        }
    }

    public void addToMuteContainer(String id, String ip, String playerName, String initiatorName, String reason, MuteType muteType, String realMuteDate, String realMuteTime, String uuid, Long time, FID fid) {
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
        this.fids.add(fid);
        MuteEntry muteEntry = new MuteEntry(playerName, id, ip, initiatorName, reason, muteType, realMuteDate, realMuteTime, UUID.fromString(uuid), time, fid);
        muteEntries.add(muteEntry);
    }

    public Set<FunctionalMuteEntry> getMuteEntries() {
        return muteEntries;
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

    public List<MuteType> getMuteTypesContainer() {
        return muteTypesContainer;
    }

    public List<String> getRealMuteDateContainer() {
        return realMuteDateContainer;
    }

    public List<String> getUUIDContainer() {
        return uuidContainer;
    }

    public List<String> getRealMuteTimeContainer() {
        return realMuteTimeContainer;
    }

    public List<Long> getMuteTimeContainer() {
        return muteTimeContainer;
    }

    public List<FID> getFids() {
        return fids;
    }
}
