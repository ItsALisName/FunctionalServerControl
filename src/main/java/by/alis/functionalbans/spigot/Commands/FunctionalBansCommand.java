package by.alis.functionalbans.spigot.Commands;

import by.alis.functionalbans.spigot.Additional.Other.TemporaryCache;
import by.alis.functionalbans.spigot.Commands.Completers.FunctionalBansCompleter;
import by.alis.functionalbans.spigot.FunctionalBansSpigot;
import by.alis.functionalbans.spigot.Managers.FilesManagers.FileAccessor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static by.alis.functionalbans.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalbans.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getGlobalVariables;
import static by.alis.functionalbans.spigot.Additional.Other.TextUtils.setColors;

public class FunctionalBansCommand implements CommandExecutor {

    FunctionalBansSpigot plugin;
    public FunctionalBansCommand(FunctionalBansSpigot plugin) {
        this.plugin = plugin;
        plugin.getCommand("functionalbans").setExecutor(this);
        plugin.getCommand("functionalbans").setTabCompleter(new FunctionalBansCompleter());
    }


    private boolean purgeConfirmation = false;


    private final FileAccessor accessor = new FileAccessor();
    private final TemporaryCache cache = new TemporaryCache();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {


        if(args.length == 0) {
            if(sender.hasPermission("functionalbans.help")) {
                sender.sendMessage(setColors(String.join("\n", this.accessor.getLang().getStringList("commands.help"))));
                return true;
            } else {
                sender.sendMessage(setColors(this.accessor.getLang().getString("other.no-permissions")));
                return true;
            }
        }


        if(args[0].equalsIgnoreCase("reload")) {
            if(!sender.hasPermission("functionalbans.reload"))  {
                sender.sendMessage(setColors(this.accessor.getLang().getString("other.no-permissions")));
                return true;
            }
            if(args.length != 2) {
                if(getConfigSettings().showDescription()) { sender.sendMessage(setColors(this.accessor.getLang().getString("commands.reload.description").replace("%1$f", command.getName()))); }
                sender.sendMessage(setColors(this.accessor.getLang().getString("commands.reload.usage").replace("%1$f", command.getName())));
                if(getConfigSettings().showExamples()) { sender.sendMessage(setColors(this.accessor.getLang().getString("commands.reload.example").replace("%1$f", command.getName())));}
                return true;
            }

            if(args[1].equalsIgnoreCase("all")) {
                try {
                    this.accessor.reloadGeneralConfig();
                    this.accessor.reloadLang();
                    getConfigSettings().reloadConfig();
                    getGlobalVariables().reloadGlobalVariables();
                    sender.sendMessage(setColors(this.accessor.getLang().getString("commands.reload.done").replace("%1$f", getGlobalVariables().getVariableAll())));
                    return true;
                } catch (RuntimeException ignored) {
                    sender.sendMessage(this.accessor.getLang().getString("commands.reload.failed"));
                    return true;
                }
            }

            if(args[1].equalsIgnoreCase("globalvariables")) {
                try{
                    this.accessor.reloadLang();
                    getGlobalVariables().reloadGlobalVariables();
                    sender.sendMessage(setColors(this.accessor.getLang().getString("commands.reload.done").replace("%1$f", args[1])));
                    return true;
                } catch (RuntimeException ingored) {
                    sender.sendMessage(setColors(this.accessor.getLang().getString("commands.reload.failed")));
                    return true;
                }
            }

            if(args[1].equalsIgnoreCase("settings")) {
                try{
                    this.accessor.reloadGeneralConfig();
                    this.accessor.reloadLang();
                    getConfigSettings().reloadConfig();
                    sender.sendMessage(setColors(this.accessor.getLang().getString("commands.reload.done").replace("%1$f", args[1])));
                    return true;
                } catch (RuntimeException ingored) {
                    sender.sendMessage(setColors(this.accessor.getLang().getString("commands.reload.failed")));
                    return true;
                }
            }

            sender.sendMessage(setColors(this.accessor.getLang().getString("commands.reload.unknown-type").replace("%1$f", args[1])));
            return true;

        }


        if(args[0].equalsIgnoreCase("purge")) {
            if(sender.hasPermission("functionalbans.purge")) {

                if(args.length != 2) {
                    if(getConfigSettings().showDescription()) { sender.sendMessage(setColors(this.accessor.getLang().getString("commands.purge.description").replace("%1$f",command.getName() + " purge"))); }
                    sender.sendMessage(setColors(this.accessor.getLang().getString("commands.purge.usage").replace("%1$f", command.getName() + " purge")));
                    if(getConfigSettings().showExamples()) { sender.sendMessage(setColors(this.accessor.getLang().getString("commands.purge.example").replace("%1$f", command.getName() + " purge"))); }
                    return true;
                }

                if(args[1].equalsIgnoreCase("cache")) {
                    if(this.cache.getUnsafeBannedPlayers().isEmpty() && this.cache.getUnsafeMutedPlayers().isEmpty()) {
                        sender.sendMessage(setColors(this.accessor.getLang().getString("commands.purge.cache.empty")));
                        return true;
                    } else {
                        if(getConfigSettings().isPurgeConfirmation()) {
                            if(!purgeConfirmation) {
                                purgeConfirmation = true;
                                sender.sendMessage(setColors(this.accessor.getLang().getString("commands.purge.confirm")));
                                return true;
                            }
                        }
                        purgeConfirmation = false;
                        this.cache.getUnsafeBannedPlayers().clear();
                        this.cache.getUnsafeMutedPlayers().clear();
                        sender.sendMessage(setColors(this.accessor.getLang().getString("commands.purge.cache.cleared")));
                        return true;
                    }
                } else {
                    sender.sendMessage(setColors(this.accessor.getLang().getString("commands.purge.unknown-type").replace("%1$f", "cache")));
                    return true;
                }
            } else {
                sender.sendMessage(setColors(this.accessor.getLang().getString("other.no-permissions")));
                return true;
            }
        }


        if(sender.hasPermission("functionalbans.help")) {
            sender.sendMessage(setColors(this.accessor.getLang().getString("other.unknown-subcommand").replace("%1$f", args[0] == null ? "" : args[0])));
        } else {
            sender.sendMessage(setColors(this.accessor.getLang().getString("other.no-permissions")));
        }
        return true;
    }
}
