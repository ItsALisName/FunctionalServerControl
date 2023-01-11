package by.alis.functionalservercontrol.spigot.additional.containers;

import by.alis.functionalservercontrol.spigot.managers.ban.BanContainerManager;

public class StaticContainers {

    private static final HidedMessagesContainer hidedMessagesContainer = new HidedMessagesContainer();
    private static final ReplacedMessagesContainer replacedMessagesContainer = new ReplacedMessagesContainer();
    private static final BannedPlayersContainer bannedPlayersContainer = new BannedPlayersContainer();
    private static final BanContainerManager banContainerManager = new BanContainerManager();
    private static final DupeIpReports dupeIpReports = new DupeIpReports();
    private static final CheatsCheckingPlayers checkingCheatsPlayers = new CheatsCheckingPlayers();
    private static final MutedPlayersContainer mutedPlayersContainer = new MutedPlayersContainer();

    public static HidedMessagesContainer getHidedMessagesContainer() {
        return hidedMessagesContainer;
    }
    public static ReplacedMessagesContainer getReplacedMessagesContainer() {
        return replacedMessagesContainer;
    }
    public static BannedPlayersContainer getBannedPlayersContainer() {
        return bannedPlayersContainer;
    }
    public static CheatsCheckingPlayers getCheckingCheatsPlayers() {
        return checkingCheatsPlayers;
    }
    public static DupeIpReports getDupeIpReports() {
        return dupeIpReports;
    }
    public static BanContainerManager getBanContainerManager() {
        return banContainerManager;
    }

    public static MutedPlayersContainer getMutedPlayersContainer() {
        return mutedPlayersContainer;
    }
}
