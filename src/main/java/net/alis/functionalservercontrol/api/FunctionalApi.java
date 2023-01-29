package net.alis.functionalservercontrol.api;

import net.alis.functionalservercontrol.api.interfaces.FunctionalBanEntry;
import net.alis.functionalservercontrol.api.interfaces.FunctionalMuteEntry;
import net.alis.functionalservercontrol.api.interfaces.FunctionalPlayer;
import net.alis.functionalservercontrol.api.interfaces.OfflineFunctionalPlayer;
import net.alis.functionalservercontrol.api.naf.Incore;
import net.alis.functionalservercontrol.api.naf.v1_10_0.util.FunctionalStatistics;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;

public interface FunctionalApi {

    static @Nullable FunctionalApi get() {
        return Incore.api;
    }

    Set<FunctionalBanEntry> getBans();

    Set<FunctionalMuteEntry> getMutes();

    static @Nullable Collection<FunctionalPlayer> getOnlinePlayers() {
        return Incore.api == null ? null : Incore.players;
    }

    static @Nullable Collection<OfflineFunctionalPlayer> getOfflinePlayers() {
        return Incore.api == null ? null : Incore.offlinePlayers;
    }

    FunctionalStatistics getPlayerStatistics();

}
