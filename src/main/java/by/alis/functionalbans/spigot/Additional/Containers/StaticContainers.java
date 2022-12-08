package by.alis.functionalbans.spigot.Additional.Containers;

public class StaticContainers {

    private static final HidedMessagesContainer hidedMessagesContainer = new HidedMessagesContainer();
    private static final ReplacedMessagesContainer replacedMessagesContainer = new ReplacedMessagesContainer();
    private static final BannedPlayersContainer bannedPlayersContainer = new BannedPlayersContainer();

    public static HidedMessagesContainer getHidedMessagesContainer() {
        return hidedMessagesContainer;
    }
    public static ReplacedMessagesContainer getReplacedMessagesContainer() { return replacedMessagesContainer; }

    public static BannedPlayersContainer getBannedPlayersContainer() { return bannedPlayersContainer; }
}
