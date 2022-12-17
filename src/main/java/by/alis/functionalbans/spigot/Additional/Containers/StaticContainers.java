package by.alis.functionalbans.spigot.Additional.Containers;

import by.alis.functionalbans.spigot.Commands.DupeIpCommand;
import by.alis.functionalbans.spigot.Managers.Bans.BanContainerManager;

public class StaticContainers {

    private static final HidedMessagesContainer hidedMessagesContainer = new HidedMessagesContainer();
    private static final ReplacedMessagesContainer replacedMessagesContainer = new ReplacedMessagesContainer();
    private static final BannedPlayersContainer bannedPlayersContainer = new BannedPlayersContainer();
    private static final BanContainerManager banContainerManager = new BanContainerManager();
    private static final DupeIpReports dupeIpReports = new DupeIpReports();

    public static HidedMessagesContainer getHidedMessagesContainer() {
        return hidedMessagesContainer;
    }
    public static ReplacedMessagesContainer getReplacedMessagesContainer() {
        return replacedMessagesContainer;
    }
    public static BannedPlayersContainer getBannedPlayersContainer() {
        return bannedPlayersContainer;
    }
    public static DupeIpReports getDupeIpReports() {
        return dupeIpReports;
    }
    public static BanContainerManager getBanContainerManager() {
        return banContainerManager;
    }
}
