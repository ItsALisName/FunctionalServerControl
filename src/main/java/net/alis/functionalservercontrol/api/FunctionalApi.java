package net.alis.functionalservercontrol.api;

import net.alis.functionalservercontrol.api.interfaces.FunctionalBanEntry;
import net.alis.functionalservercontrol.api.interfaces.FunctionalMuteEntry;
import net.alis.functionalservercontrol.spigot.coreadapters.Adapter;
import net.alis.functionalservercontrol.spigot.additional.misc.apiutils.FunctionalStatistics;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

public interface FunctionalApi {

    static FunctionalApi getFunctionalPlayer() {
        return ApiGetter.getApi();
    }

    Set<FunctionalBanEntry> getBans();

    Set<FunctionalMuteEntry> getMutes();

    //Soon
    //void ban(Plugin initiator, OfflinePlayer target, String reason, long time);

    Adapter getCoreAdapter();

    FunctionalStatistics getPlayerStatistics();

    /**
     * This API mechanics is taken from Chatty(Plugin by MrBrikster)
     */
    public static class ApiGetter {
        private static @Getter @Setter FunctionalApi api;
    }

}
