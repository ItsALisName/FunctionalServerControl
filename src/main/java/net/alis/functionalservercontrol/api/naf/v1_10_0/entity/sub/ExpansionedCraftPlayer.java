package net.alis.functionalservercontrol.api.naf.v1_10_0.entity.sub;

import net.alis.functionalservercontrol.api.naf.v1_10_0.InternalAdapter;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.play.out.chat.WrappedPacketOutChat;
import net.alis.functionalservercontrol.libraries.net.md_5.bungee.api.ChatMessageType;
import net.alis.functionalservercontrol.libraries.net.md_5.bungee.api.chat.BaseComponent;
import net.alis.functionalservercontrol.libraries.net.md_5.bungee.api.chat.TextComponent;
import net.alis.functionalservercontrol.spigot.additional.misc.adapterutils.ExternalExpansionedPlayer;
import net.alis.functionalservercontrol.spigot.additional.textcomponents.Component;
import net.alis.functionalservercontrol.spigot.managers.TaskManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExpansionedCraftPlayer extends CommandSender.Spigot implements ExternalExpansionedPlayer {

    private final Player player;

    public ExpansionedCraftPlayer(Player player) {
        this.player = player;
    }
    
    public void message(ChatMessageType position, Component.SimplifiedComponent component) {
        message(position, new Component.SimplifiedComponent[] { component });
    }
    
    public void message(ChatMessageType position, Component.SimplifiedComponent... components) {
        TaskManager.preformAsync(() -> {
            switch (position) {
                case CHAT -> {
                    Component.SimplifiedComponent component = new Component.SimplifiedComponent("");
                    for (Component.SimplifiedComponent c : components) component.append(c);
                    InternalAdapter.sendPacketAsync(player, new WrappedPacketOutChat(component.get(), WrappedPacketOutChat.ChatPosition.CHAT, player.getUniqueId()));
                    break;
                }
                case ACTION_BAR -> {
                    Component.SimplifiedComponent component = new Component.SimplifiedComponent("");
                    for (Component.SimplifiedComponent c : components) component.append(c);
                    InternalAdapter.sendPacketAsync(player, new WrappedPacketOutChat(component.get(), WrappedPacketOutChat.ChatPosition.GAME_INFO, player.getUniqueId()));
                    break;
                }
                case SYSTEM -> {
                    Component.SimplifiedComponent component = new Component.SimplifiedComponent("");
                    for (Component.SimplifiedComponent c : components) component.append(c);
                    InternalAdapter.sendPacketAsync(player, new WrappedPacketOutChat(component.get(), WrappedPacketOutChat.ChatPosition.SYSTEM_MESSAGE, player.getUniqueId()));
                    break;
                }
            }
        });
    }

    
    public void message(ChatMessageType position, BaseComponent... components) {
        TaskManager.preformAsync(() -> {
            switch (position) {
                case CHAT -> {
                    BaseComponent component = new TextComponent("");
                    for (BaseComponent c : components) component.addExtra(c);
                    InternalAdapter.sendPacketAsync(player, new WrappedPacketOutChat(component, WrappedPacketOutChat.ChatPosition.CHAT, player.getUniqueId()));
                    break;
                }
                case ACTION_BAR -> {
                    BaseComponent component = new TextComponent("");
                    for (BaseComponent c : components) component.addExtra(c);
                    InternalAdapter.sendPacketAsync(player, new WrappedPacketOutChat(component, WrappedPacketOutChat.ChatPosition.GAME_INFO, player.getUniqueId()));
                    break;
                }
                case SYSTEM -> {
                    BaseComponent component = new TextComponent("");
                    for (BaseComponent c : components) component.addExtra(c);
                    InternalAdapter.sendPacketAsync(player, new WrappedPacketOutChat(component, WrappedPacketOutChat.ChatPosition.SYSTEM_MESSAGE, player.getUniqueId()));
                    break;
                }
            }
        });
    }

    
    public void message(Component.SimplifiedComponent... components) {
        TaskManager.preformAsync(() -> {
            Component.SimplifiedComponent component = new Component.SimplifiedComponent("");
            for(Component.SimplifiedComponent c : components) component.append(c);
            InternalAdapter.sendPacketAsync(player, new WrappedPacketOutChat(component.get(), player.getUniqueId()));
        });
    }

    
    public void message(BaseComponent... components) {
        TaskManager.preformAsync(() -> {
            BaseComponent component = new TextComponent("");
            for(BaseComponent c : components) component.addExtra(c);
            InternalAdapter.sendPacketAsync(player, new WrappedPacketOutChat(component, player.getUniqueId()));
        });
    }
    
    public void message(ChatMessageType position, String... strings) {
        TaskManager.preformAsync(() -> {
            switch (position) {
                case CHAT -> {
                    InternalAdapter.sendPacketAsync(player, new WrappedPacketOutChat(String.join(" ", strings), WrappedPacketOutChat.ChatPosition.CHAT, player.getUniqueId(), false));
                    break;
                }
                case ACTION_BAR -> {
                    InternalAdapter.sendPacketAsync(player, new WrappedPacketOutChat(String.join(" ", strings), WrappedPacketOutChat.ChatPosition.GAME_INFO, player.getUniqueId(), false));
                    break;
                }
                case SYSTEM -> {
                    InternalAdapter.sendPacketAsync(player, new WrappedPacketOutChat(String.join(" ", strings), WrappedPacketOutChat.ChatPosition.SYSTEM_MESSAGE, player.getUniqueId(), false));
                    break;
                }
            }
        });
    }

    
    public void message(ChatMessageType position, String string) {
        TaskManager.preformAsync(() -> {
            switch (position) {
                case CHAT -> {
                    InternalAdapter.sendPacketAsync(player, new WrappedPacketOutChat(string, WrappedPacketOutChat.ChatPosition.CHAT, player.getUniqueId(), false));
                    break;
                }
                case ACTION_BAR -> {
                    InternalAdapter.sendPacketAsync(player, new WrappedPacketOutChat(string, WrappedPacketOutChat.ChatPosition.GAME_INFO, player.getUniqueId(), false));
                    break;
                }
                case SYSTEM -> {
                    InternalAdapter.sendPacketAsync(player, new WrappedPacketOutChat(string, WrappedPacketOutChat.ChatPosition.SYSTEM_MESSAGE, player.getUniqueId(), false));
                    break;
                }
            }
        });
    }
    
}
