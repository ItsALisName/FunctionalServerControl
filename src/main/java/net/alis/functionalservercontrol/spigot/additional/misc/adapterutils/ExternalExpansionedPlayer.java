package net.alis.functionalservercontrol.spigot.additional.misc.adapterutils;

import net.alis.functionalservercontrol.libraries.net.md_5.bungee.api.ChatMessageType;
import net.alis.functionalservercontrol.libraries.net.md_5.bungee.api.chat.BaseComponent;
import net.alis.functionalservercontrol.spigot.additional.textcomponents.Component;

public interface ExternalExpansionedPlayer {

    void message(ChatMessageType position, Component.SimplifiedComponent component);

    void message(ChatMessageType position, Component.SimplifiedComponent... components);

    void message(ChatMessageType position, BaseComponent... components);

    void message(Component.SimplifiedComponent... components);

    void message(BaseComponent... components);

    void message(ChatMessageType position, String... strings);

    void message(ChatMessageType position, String string);

}
