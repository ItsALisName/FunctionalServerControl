# FunctionalServerControl
A multifunctional plugin that will allow you to control the players/server in different directions!
Restriction of commands (or rather the functionality of the PlHidePro plugin), chat control, mutas, bans and much more!

Available versions of Minecraft server: from 1.8.x to 1.19.x

Supported kernels: Spigot and its forks


# API

The API is located in the package: net.alis.functionalservercontrol.api.FunctionalApi;
The events are located in the package: net.alis.functionalservercontrol.api.events;

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

    import net.alis.functionalservercontrol.api.FunctionalApi;
    
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

    import net.alis.functionalservercontrol.api.FunctionalApi;

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

Example of working with the FunctionalApi#getCoreAdapter method:
```
package your.custom.pack;

public class YourCustomClass {
  
    import net.alis.functionalservercontrol.api.FunctionalApi;
  
    public String getPlayerVersionAndBrand(Player player) {
        FunctionalApi api = FunctionalApi.get();
        if(api != null) {
            Adapter coreAdapter = api.getCoreAdapter();
            String adaptName = coreAdapter.getAdapterName(); //We get the name of the adapter(Spigot or Paper)
            int playerProtocolVersion = coreAdapter.getPlayerProtocolVersion(player); //We get the version of the player's protocol
            String playerVersion = coreAdapter.getPlayerVersion(player).toString(); //Get the minecraft version of the player
            String playerBrand = coreAdapter.getPlayerMinecraftBrand(player); //We get the name of the player's client
            return "Player Protocol Version: " + playerProtocolVersion
                + "; Player version: " + playerVersion
                + "; Player client name: " + playerBrand;
        } else {
            Bukkit.getConsoleSender().sendMessage("Failed to get FunctionalApi!");
            return null;
        }
    }
}
```

Example of working with Component.SimplifiedComponent (simplified versions of components from md_5)
```
package your.custom.pack;

import net.alis.functionalservercontrol.api.FunctionalApi;
import net.alis.functionalservercontrol.libraries.net.md_5.bungee.api.chat.ClickEvent;
import net.alis.functionalservercontrol.libraries.net.md_5.bungee.api.chat.HoverEvent;
import net.alis.functionalservercontrol.libraries.net.md_5.bungee.api.chat.TextComponent;
import net.alis.functionalservercontrol.spigot.additional.textcomponents.Component;

public class Example {

    public void sendCoolMessage(Player player) {
        FunctionalApi api = FunctionalApi.get();
        if(api != null) {
            Component.SimplifiedComponent[] components = new Component.SimplifiedComponent[]{new Component.SimplifiedComponent("")};
            TextComponent[] components2 = new TextComponent[]{new TextComponent(""), new TextComponent("")};
            Component.SimplifiedComponent simplifiedComponent = new Component.SimplifiedComponent(); //Creating a lightweight version of TextComponent from md_5
            simplifiedComponent
                    .append(new TextComponent("your "))
                    .append("custom ")
                    .append(" ", components) // " " - delimiter
                    .append(" ", components2)
                    .setHoverEvent(HoverEvent.Action.SHOW_TEXT, "Some hover text")
                    .setClickEvent(ClickEvent.Action.OPEN_URL, "https://vk.com/alphatwo")
                    .appendOnStart(new Component.SimplifiedComponent("re "))
                    .appendOnStart("He")
                    .translateDefaultColorCodes(); //Correctly replaces color codes (usually when switching to a new line, colors using '§' and '&' do not work)
            String content = simplifiedComponent.getString(); //We get the text content of the component
            TextComponent component = simplifiedComponent.get();  //We get the original component from md_5
            api.getCoreAdapter().expansion().sendMessage(player, simplifiedComponent); //We send the Simplified Component to the player
            api.getCoreAdapter().expansion().sendMessage(player, component); //We send the original component to the player

            player.spigot().sendMessage(simplifiedComponent); // <- Does not work
            player.spigot().sendMessage(component); // <- Does not work
        }
    }
}
```
