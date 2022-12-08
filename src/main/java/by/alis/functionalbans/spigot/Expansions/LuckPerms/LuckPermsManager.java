package by.alis.functionalbans.spigot.Expansions.LuckPerms;

import by.alis.functionalbans.spigot.Additional.GlobalSettings.Languages.LangEnglish;
import by.alis.functionalbans.spigot.Additional.GlobalSettings.Languages.LangRussian;
import by.alis.functionalbans.spigot.Expansions.StaticExpansions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;

import java.util.Collection;

import static by.alis.functionalbans.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalbans.spigot.Additional.Other.TextUtils.setColors;
import static org.bukkit.Bukkit.getServer;

public class LuckPermsManager {

    private boolean isLuckPermsSetuped;

    LuckPerms luckPermsProvider;
    private boolean isLuckPermsInstalled() {
        return getServer().getPluginManager().isPluginEnabled("LuckPerms");
    }

    public void setupLuckPerms() {
        if (isLuckPermsInstalled() && !StaticExpansions.getVaultManager().isVaultSetuped()) {
            luckPermsProvider = LuckPermsProvider.get();
            switch (getConfigSettings().getConsoleLanguageMode()) {
                case "ru_RU":
                    Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.LUCK_PERMS_FINDED));
                    break;
                case "en_US":
                    Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.LUCK_PERMS_FINDED));
                    break;
                default:
                    Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.LUCK_PERMS_FINDED));
                    break;
            }
            if (luckPermsProvider != null) {
                this.isLuckPermsSetuped = true;
                switch (getConfigSettings().getConsoleLanguageMode()) {
                    case "ru_RU":
                        Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.LUCK_PERMS_HOOKED));
                        break;
                    case "en_US":
                        Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.LUCK_PERMS_HOOKED));
                        break;
                    default:
                        Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.LUCK_PERMS_HOOKED));
                        break;
                }
            } else {
                this.isLuckPermsSetuped = false;
                switch (getConfigSettings().getConsoleLanguageMode()) {
                    case "ru_RU":
                        Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.LUCK_PERMS_ERROR));
                        break;
                    case "en_US":
                        Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.LUCK_PERMS_ERROR));
                        break;
                    default:
                        Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.LUCK_PERMS_ERROR));
                        break;
                }
            }
        }
    }

    public boolean isLuckPermsSetuped() {
        return isLuckPermsSetuped;
    }

    public String getPlayerGroup(Player player, Collection<String> possibleGroups) {
        if (isLuckPermsSetuped()) {
            for (String group : possibleGroups) {
                if (player.hasPermission("group." + group)) {
                    return group;
                }
            }
        }
        return null;
    }

}
