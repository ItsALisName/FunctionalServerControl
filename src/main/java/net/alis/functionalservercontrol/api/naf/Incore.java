package net.alis.functionalservercontrol.api.naf;

import lombok.Setter;
import net.alis.functionalservercontrol.api.FunctionalApi;
import net.alis.functionalservercontrol.api.interfaces.FunctionalPlayer;
import net.alis.functionalservercontrol.api.interfaces.OfflineFunctionalPlayer;
import net.alis.functionalservercontrol.api.naf.v1_10_0.util.FID;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class Incore {

    public static @Setter FunctionalApi api;
    public static Collection<FunctionalPlayer> players = new ArrayList<>();
    public static Collection<OfflineFunctionalPlayer> offlinePlayers = new ArrayList<>();

    public static abstract class Player {

        protected abstract void register();

        protected void unregister() { }

        @Nullable
        public static FunctionalPlayer getByName(String name) {
            FID fid = new FID(name);
            for(FunctionalPlayer p : players) {
                if(p.getFunctionalId().equals(fid)) return p;
            }
            return null;
        }

        @Deprecated
        @Nullable
        public static FunctionalPlayer getByUniqueId(UUID uuid) {
            for(FunctionalPlayer p : players) {
                if(p.getUniqueId() == uuid) return p;
            }
            return null;
        }

        @Nullable
        public static FunctionalPlayer getByFunctionalId(FID fid) {
            for(FunctionalPlayer p : players) {
                if(p.getFunctionalId().equals(fid)) return p;
            }
            return null;
        }

        @Nullable
        public static OfflineFunctionalPlayer getOfflineByName(String name) {
            FID fid = new FID(name);
            for(OfflineFunctionalPlayer p : offlinePlayers) {
                if(p.getFunctionalId().equals(fid)) return p;
            }
            return null;
        }

        @Deprecated
        @Nullable
        public static OfflineFunctionalPlayer getOfflineByUniqueId(UUID uuid) {
            for(OfflineFunctionalPlayer p : offlinePlayers) {
                if(p.getUniqueId() == uuid) return p;
            }
            return null;
        }

        @Nullable
        public static OfflineFunctionalPlayer getOfflineByFunctionalId(FID fid) {
            for(OfflineFunctionalPlayer p : offlinePlayers) {
                if(p.getFunctionalId() == fid) return p;
            }
            //new FunctionalPlayerNotFound("Offline player with FID " + fid + " not founded.").printStackTrace();
            return null;
        }

    }

}
