package net.alis.functionalservercontrol.spigot.managers;

import net.alis.functionalservercontrol.spigot.FunctionalServerControlSpigot;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Arrays;

public class CommandsRegistrationManager extends Command implements PluginIdentifiableCommand {

    protected FunctionalServerControlSpigot plugin;
    protected CommandExecutor owner;
    protected Object registeredWith;

    public CommandsRegistrationManager(String[] aliases, String desc, String usage, CommandExecutor owner, Object registeredWith, FunctionalServerControlSpigot plugin) {
        super(aliases[0], desc, usage, Arrays.asList(aliases));
        this.owner = owner;
        this.plugin = plugin;
        this.registeredWith = registeredWith;
    }

    @Override
    public @NotNull Plugin getPlugin() {
        return this.plugin;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }
        if (this.owner.onCommand(sender, this, label, args)) {
            return true;
        } else {
            sender.sendMessage(this.usageMessage);
            return false;
        }
    }

    public static void registerCommand(FunctionalServerControlSpigot plugin, CommandExecutor commandExecutor, String[] aliases, String description, String usage) {
        try {
            CommandsRegistrationManager reg = new CommandsRegistrationManager(aliases, description, usage, commandExecutor, new Object(), plugin);
            Field field = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            field.setAccessible(true);
            CommandMap map = (CommandMap) field.get(Bukkit.getServer());
            map.register(plugin.getDescription().getName(), reg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
