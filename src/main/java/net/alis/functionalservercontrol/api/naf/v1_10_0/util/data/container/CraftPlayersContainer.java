package net.alis.functionalservercontrol.api.naf.v1_10_0.util.data.container;

import net.alis.functionalservercontrol.api.interfaces.FunctionalPlayer;
import net.alis.functionalservercontrol.api.interfaces.OfflineFunctionalPlayer;
import net.alis.functionalservercontrol.api.naf.v1_10_0.entity.FunctionalCraftPlayer;
import net.alis.functionalservercontrol.api.naf.v1_10_0.entity.OfflineFunctionalCraftPlayer;
import net.alis.functionalservercontrol.api.naf.v1_10_0.util.FID;

import java.util.ArrayList;
import java.util.Collection;

public class CraftPlayersContainer {

    public static class Online {
        private final Collection<net.alis.functionalservercontrol.api.naf.v1_10_0.entity.FunctionalCraftPlayer> craftPlayers = new ArrayList<>();
        protected static final Online CRAFT_CONTAINER = new Online();

        public static class In {
            public static void add(FunctionalCraftPlayer functionalCraftPlayer) {
                Online.CRAFT_CONTAINER.craftPlayers.add(functionalCraftPlayer);
            }
        }

        public static class Out {
            public static Collection<FunctionalCraftPlayer> get() {
                return Online.CRAFT_CONTAINER.craftPlayers;
            }
            public static FunctionalCraftPlayer get(FID fid) {
                for(FunctionalCraftPlayer craftPlayer : get()) {
                    if(craftPlayer.getFunctionalId().equals(fid)) return craftPlayer;
                }
                return null;
            }
            public static void remove(FunctionalPlayer player) {
                Online.CRAFT_CONTAINER.craftPlayers.removeIf((functionalCraftPlayer -> functionalCraftPlayer.getFunctionalId().equals(player.getFunctionalId())));
            }
        }
    }

    public static class Offline {
        private final Collection<OfflineFunctionalCraftPlayer> craftPlayers = new ArrayList<>();
        protected static final Offline CRAFT_CONTAINER = new Offline();
        public static class In {
            public static void add(net.alis.functionalservercontrol.api.naf.v1_10_0.entity.OfflineFunctionalCraftPlayer functionalCraftPlayer) {
                Offline.CRAFT_CONTAINER.craftPlayers.add(functionalCraftPlayer);
            }
        }

        public static class Out {
            public static Collection<net.alis.functionalservercontrol.api.naf.v1_10_0.entity.OfflineFunctionalCraftPlayer> get() {
                return Offline.CRAFT_CONTAINER.craftPlayers;
            }
            public static OfflineFunctionalCraftPlayer get(FID fid) {
                for(OfflineFunctionalCraftPlayer player : get()) {
                    if(player.getFunctionalId().equals(fid)) return player;
                }
                return null;
            }
            public static void remove(OfflineFunctionalPlayer player) {
                Online.CRAFT_CONTAINER.craftPlayers.removeIf((functionalCraftPlayer -> functionalCraftPlayer.getFunctionalId().equals(player.getFunctionalId())));
            }
        }
    }

}
