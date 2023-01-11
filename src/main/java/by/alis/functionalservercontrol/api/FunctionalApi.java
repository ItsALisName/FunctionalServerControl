package by.alis.functionalservercontrol.api;

import by.alis.functionalservercontrol.spigot.additional.coreadapters.Adapter;
import by.alis.functionalservercontrol.spigot.additional.misc.apiutils.FunctionalBanEntry;
import by.alis.functionalservercontrol.spigot.additional.misc.apiutils.FunctionalMuteEntry;
import by.alis.functionalservercontrol.spigot.additional.misc.apiutils.FunctionalStatistics;
import lombok.Getter;

import java.util.Set;

public interface FunctionalApi {

    static FunctionalApi get() {
        return ApiGetter.getApi();
    }

    Set<FunctionalBanEntry> getBans();

    Set<FunctionalMuteEntry> getMutes();

    Adapter getCoreAdapter();

    FunctionalStatistics getPlayerStatistics();

    /**
     * This API mechanics is taken from Chatty(Plugin by MrBrikster)
     */
    class ApiGetter {
        private static @Getter FunctionalApi api;
        public static void setApi(FunctionalApi api) {
            ApiGetter.api = api;
        }
    }

}
