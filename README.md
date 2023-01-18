# FunctionalServerControl
A multifunctional plugin that will allow you to control the players/server in different directions!
Restriction of commands (or rather the functionality of the PlHidePro plugin), chat control, mutas, bans and much more!

Available versions of Minecraft server: from 1.8.x to 1.19.x

Supported kernels: Spigot and its forks


# API

The API is located in the package: by.alis.functionalservercontrol.api.FunctionalApi;
The events are located in the package: by.alis.functionalservercontrol.api.events;

Example of getting the API:

```
package your.some.class;

public class SomeYourClass {

    import net.alis.functionalservercontrol.api.FunctionalApi;

    private FunctionalApi api; //Creating a global API variable for further obtaining it

    public boolean setupFunctionalApi() {
        api = FunctionalApi.get(); //Getting API
        return api != null ? true : false; //Сheck whether the api was obtained and if so, we return true
        //It is mandatory to check for null, since the API can be disabled by the config
    }

    //Creating getter for FunctionalApi
    public FunctionalApi getFunctionalApi() {
        return this.api;
    }
    
}
```

Example of working with bans(with mutes also, only FunctionalMuteEntry and api.getMutes(); ):
```
public class YourSomeClass {

    import by.alis.functionalservercontrol.api.FunctionalApi;
    
    public void unbanPlayerIfHeALis() {
        FunctionalApi api = FunctionalApi.get(); //Getting FunctionalApi
        if(api != null) { //Checking whether the api is equal to null
            for(FunctionalBanEntry banEntry : api.getBans()) { //Going through the list of bans
                if(banEntry.getName().equalsIgnoreCase("ALis")) { //Check whether the name of the banned player in this FunctionalBanEntry is ALis
                    banEntry.unban(); //And if it is, we remove the ban
                    return;
                }
            }
        } else {
            getConsoleSender().sendMessage("Failed to get Functional Api!"); //We output a message that it was not possible to get a FunctionalApi
            return;
        }
    }
}
```

Example of working with statistics:

```
public class YourSomeClass {

    import by.alis.functionalservercontrol.api.FunctionalApi;

    public String sendStatisctic(Player player) {
        FunctionalApi api = FunctionalApi.get();
        if(api != null) {
            String wasBanned = api.getPlayerStatistics().getAsPlayer(player).get(StatsType.Player.STATS_BANS);
            String preformBan = api.getPlayerStatistics().getAsAdmin(player).get(StatsType.Administrator.STATS_BANS)
            String advertiseAttempts = api.getPlayerStatistics().getAsPlayer(player).get(StatsType.Player.ADVERTISE_ATTEMPTS)
            return "Was banned " + wasBanned + " times; Preform bans: " + preformBan + " times; Tried to advertise: " + advertiseAttempts + " times;"
            //These are not all types of statistics, I just don't think it makes sense to list everything here
        } else {
            getConsoleSender().sendMessage("Failed to get Functional Api");
            return;
        }
    }
}
```

There is also a FunctionalApi#getCoreAdapter() method in the interface;
But, I think there is not much sense from it, it just does not add anything new, it is rather needed by the plugin itself, 
it allows you to use some functions that are not present in the Spigot / Paper API on very low server versions
