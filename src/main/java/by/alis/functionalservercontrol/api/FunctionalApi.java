package by.alis.functionalservercontrol.api;

import by.alis.functionalservercontrol.spigot.additional.coreadapters.Adapter;
import by.alis.functionalservercontrol.api.interfaces.FunctionalBanEntry;
import by.alis.functionalservercontrol.api.interfaces.FunctionalMuteEntry;
import by.alis.functionalservercontrol.spigot.additional.misc.apiutils.FunctionalStatistics;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

import java.util.Set;

public interface FunctionalApi {

    static FunctionalApi get() {
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
    class ApiGetter {
        private static @Getter @Setter FunctionalApi api;
    }

}
