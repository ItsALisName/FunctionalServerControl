package by.alis.functionalbans.spigot.Managers.TimeManagers;

import by.alis.functionalbans.spigot.Additional.Enums.TimeRestrictionType;
import by.alis.functionalbans.spigot.Additional.Enums.TimeUnit;
import by.alis.functionalbans.spigot.Expansions.StaticExpansions;
import by.alis.functionalbans.spigot.Managers.FilesManagers.FileAccessor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static by.alis.functionalbans.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalbans.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getGlobalVariables;


public class TimeManager {

    InputTimeChecker checker = new InputTimeChecker();
    FileAccessor fileAccessor = new FileAccessor();


    public long convertToMillis(String arg) {
        long finalTime = 0;
        if(this.checker.checkInputTimeArgument(arg)) {
            long num = this.checker.getArgNumber(arg);
            if(this.checker.getTimeUnit(arg) == TimeUnit.SECONDS) {
                finalTime = System.currentTimeMillis() + (num * 1000);
                return finalTime;
            }
            if(this.checker.getTimeUnit(arg) == TimeUnit.MINUTES) {
                finalTime = System.currentTimeMillis() + (num * 1000 * 60);
                return finalTime;
            }
            if(this.checker.getTimeUnit(arg) == TimeUnit.HOURS) {
                finalTime = System.currentTimeMillis() + (num * 1000 * 60 * 60);
                return finalTime;
            }
            if(this.checker.getTimeUnit(arg) == TimeUnit.DAYS) {
                finalTime = System.currentTimeMillis() + (num * 1000 * 60 * 60 * 24);
                return finalTime;
            }
            if(this.checker.getTimeUnit(arg) == TimeUnit.MONTHS) {
                finalTime = (System.currentTimeMillis() + (num * 1000 * 60 * 60 * 24 * 30));
                return finalTime;
            }
            if(this.checker.getTimeUnit(arg) == TimeUnit.YEARS) {
                finalTime = (System.currentTimeMillis() + (num * 1000 * 60 * 60 * 24 * 365));
                return finalTime;
            }
        } else {
            return finalTime;
        }
        return finalTime;
    }

    public String convertFromMillis(long time) {
        if(time <= 60000) {
            if(String.valueOf(time/1000).endsWith("1")) {
                return (long)Math.round(time/1000) + " " + getGlobalVariables().getMultiVariableSecond()[0];
            }
            if(String.valueOf(time/1000).endsWith("2") || String.valueOf(time).endsWith("3") || String.valueOf(time).endsWith("4")) {
                return (long)Math.round(time/1000) + " " + getGlobalVariables().getMultiVariableSecond()[1];
            }
            return (long)Math.round(time/1000) + " " + getGlobalVariables().getMultiVariableSecond()[2];
        }

        if(time > 60000 && time <= 3600000) {
            long min = time/1000/60;
            long timeInSecFromCurrentMinutes = (time/1000) - (min * 60);
            String a = null; String b = null;
            if(String.valueOf(min).endsWith("1")) {
                a = min + " " + getGlobalVariables().getMultiVariableMinutes()[0] + " ";
            } else if(String.valueOf(min).endsWith("2") || String.valueOf(min).endsWith("3") || String.valueOf(min).endsWith("4")) {
                a = min + " " + getGlobalVariables().getMultiVariableMinutes()[1] + " ";
            } else {
                a = min + " " + getGlobalVariables().getMultiVariableMinutes()[2] + " ";
            }
            if(String.valueOf(timeInSecFromCurrentMinutes).endsWith("1")) {
                b = timeInSecFromCurrentMinutes + " " + getGlobalVariables().getMultiVariableSecond()[0];
            } else if(String.valueOf(timeInSecFromCurrentMinutes).endsWith("2") || String.valueOf(timeInSecFromCurrentMinutes).endsWith("3") || String.valueOf(timeInSecFromCurrentMinutes).endsWith("4")) {
                b = timeInSecFromCurrentMinutes + " " + getGlobalVariables().getMultiVariableSecond()[1];
            } else {
                b = timeInSecFromCurrentMinutes + " " + getGlobalVariables().getMultiVariableSecond()[2];
            }
            if(timeInSecFromCurrentMinutes == 0) b = "";
            return a + b;
        }

        if(time > 3600000 && time <= 86400000) {
            long timeInHours = time / 1000 / 60 / 60;
            long timeInMinutesFromCurrentHours = (time / 1000 / 60) - (timeInHours * 60);
            long timeInSecondsFromCurrentMinutes = (time / 1000) - ((time / 1000 / 60) * 60);
            String a = null; String b = null; String c = null;
            if(String.valueOf(timeInHours).endsWith("1")) {
                a = timeInHours + " " + getGlobalVariables().getMultiVariableHours()[0] + " ";
            } else if(String.valueOf(timeInHours).endsWith("2") || String.valueOf(timeInHours).endsWith("3") || String.valueOf(timeInHours).endsWith("4")) {
                a =  timeInHours + " " + getGlobalVariables().getMultiVariableHours()[1] + " ";
            } else {
                a = timeInHours + " " + getGlobalVariables().getMultiVariableHours()[2] + " ";
            }
            if(String.valueOf(timeInMinutesFromCurrentHours).endsWith("1")) {
                b = timeInMinutesFromCurrentHours + " " + getGlobalVariables().getMultiVariableMinutes()[0] + " ";
            } else if(String.valueOf(timeInMinutesFromCurrentHours).endsWith("2") || String.valueOf(timeInMinutesFromCurrentHours).endsWith("3") || String.valueOf(timeInMinutesFromCurrentHours).endsWith("4")) {
                b = timeInMinutesFromCurrentHours + " " + getGlobalVariables().getMultiVariableMinutes()[1] + " ";
            } else {
                b = timeInMinutesFromCurrentHours + " " + getGlobalVariables().getMultiVariableMinutes()[2] + " ";
            }
            if(String.valueOf(timeInSecondsFromCurrentMinutes).endsWith("1")) {
                c = timeInSecondsFromCurrentMinutes + " " + getGlobalVariables().getMultiVariableSecond()[0];
            } else if(String.valueOf(timeInSecondsFromCurrentMinutes).endsWith("2") || String.valueOf(timeInSecondsFromCurrentMinutes).endsWith("3") || String.valueOf(timeInSecondsFromCurrentMinutes).endsWith("4")) {
                c = timeInSecondsFromCurrentMinutes + " " + getGlobalVariables().getMultiVariableSecond()[1];
            } else {
                c = timeInSecondsFromCurrentMinutes + " " + getGlobalVariables().getMultiVariableSecond()[2];
            }
            if(timeInSecondsFromCurrentMinutes == 0) c = "";
            if(timeInMinutesFromCurrentHours == 0) b = "";
            return a + b + c;
        }

        if(time > 86400000) {
            long timeInDays = time / 1000 / 60 / 60 / 24;
            long timeInHoursFromCurrentDays = (time / 1000 / 60 / 60) - (timeInDays * 24);
            long timeInMinutesFromCurrentHours = (time / 1000 / 60) - ((time / 1000 / 60 / 60) * 60);
            long timeInSecondsFromCurrentMinutes = (time / 1000) - ((time / 1000 / 60) * 60);
            String a = null; String b = null; String c = null; String d = null;
            if(String.valueOf(timeInDays).endsWith("1")) {
                a = timeInDays + " " + getGlobalVariables().getMultiVariableDays()[0] + " ";
            } else if(String.valueOf(timeInDays).endsWith("2") || String.valueOf(timeInDays).endsWith("3") || String.valueOf(timeInDays).endsWith("4")) {
                a = timeInDays + " " + getGlobalVariables().getMultiVariableDays()[1] + " ";
            } else {
                a = timeInDays + " " + getGlobalVariables().getMultiVariableDays()[2] + " ";
            }
            if(String.valueOf(timeInHoursFromCurrentDays).endsWith("1")) {
                b = timeInHoursFromCurrentDays + " " + getGlobalVariables().getMultiVariableHours()[0] + " ";
            } else if(String.valueOf(timeInHoursFromCurrentDays).endsWith("2") || String.valueOf(timeInHoursFromCurrentDays).endsWith("3") || String.valueOf(timeInHoursFromCurrentDays).endsWith("4")) {
                b = timeInHoursFromCurrentDays + " " + getGlobalVariables().getMultiVariableHours()[1] + " ";
            } else {
                b = timeInHoursFromCurrentDays + " " + getGlobalVariables().getMultiVariableHours()[2] + " ";
            }
            if(String.valueOf(timeInMinutesFromCurrentHours).endsWith("1")) {
                c = timeInMinutesFromCurrentHours + " " + getGlobalVariables().getMultiVariableMinutes()[0] + " ";
            } else if(String.valueOf(timeInMinutesFromCurrentHours).endsWith("2") || String.valueOf(timeInMinutesFromCurrentHours).endsWith("3") || String.valueOf(timeInMinutesFromCurrentHours).endsWith("4")) {
                c = timeInMinutesFromCurrentHours + " " + getGlobalVariables().getMultiVariableMinutes()[1] + " ";
            } else {
                c = timeInMinutesFromCurrentHours + " " + getGlobalVariables().getMultiVariableMinutes()[2] + " ";
            }
            if(String.valueOf(timeInSecondsFromCurrentMinutes).endsWith("1")) {
                d = timeInSecondsFromCurrentMinutes + " " + getGlobalVariables().getMultiVariableSecond()[0];
            } else if(String.valueOf(timeInSecondsFromCurrentMinutes).endsWith("2") || String.valueOf(timeInSecondsFromCurrentMinutes).endsWith("3") || String.valueOf(timeInSecondsFromCurrentMinutes).endsWith("4")) {
                d = timeInSecondsFromCurrentMinutes + " " + getGlobalVariables().getMultiVariableSecond()[1];
            } else {
                d = timeInSecondsFromCurrentMinutes + " " + getGlobalVariables().getMultiVariableSecond()[2];
            }
            if(timeInSecondsFromCurrentMinutes <= 0) d = "";
            if(timeInMinutesFromCurrentHours <= 0) c = "";
            if(timeInHoursFromCurrentDays <= 0) b = "";
            return a + b + c + d;
        }

        return null;
    }

    public boolean isTimeBiggerThanMax(String argTime) {
        return convertToMillis(argTime) > getMaxPossiblePunishTime();
    }

    public long getMaxPossiblePunishTime() {
        return convertToMillis(this.fileAccessor.getGeneralConfig().getString("plugin-settings.time-settings.max-possible-time").replace("|", ""));
    }

    public long getMaxPlayerPunishTime(Player player) {
        if(playerRestrictionType(player) == TimeRestrictionType.GROUP) {
            if (StaticExpansions.getVaultManager().isVaultSetuped()) {
                if(this.fileAccessor.getGeneralConfig().contains("plugin-settings.time-settings.per-groups." + StaticExpansions.getVaultManager().getPlayerGroup(player) + ".ban")) {
                    long maxTime = this.convertToMillis(this.fileAccessor.getGeneralConfig().getString("plugin-settings.time-settings.per-groups." + StaticExpansions.getVaultManager().getPlayerGroup(player) + ".ban").replace("|", ""));
                    return maxTime;
                } else {
                    return getMaxPossiblePunishTime() + System.currentTimeMillis();
                }
            }
            if (StaticExpansions.getLuckPermsManager().isLuckPermsSetuped()) {
                if(this.fileAccessor.getGeneralConfig().contains("plugin-settings.time-settings.per-groups." + StaticExpansions.getLuckPermsManager().getPlayerGroup(player, getConfigSettings().getPossibleGroups()) + ".ban")) {
                    long maxTime = this.convertToMillis(this.fileAccessor.getGeneralConfig().getString("plugin-settings.time-settings.per-groups." + StaticExpansions.getLuckPermsManager().getPlayerGroup(player, getConfigSettings().getPossibleGroups()) + ".ban").replace("|", ""));
                    return maxTime;
                } else {
                    return getMaxPossiblePunishTime() + System.currentTimeMillis();
                }
            }
        } else if(playerRestrictionType(player) == TimeRestrictionType.DEFAULT) {
            return System.currentTimeMillis() + getMaxPossiblePunishTime();
        }
        return System.currentTimeMillis() + getMaxPossiblePunishTime();
    }

    public boolean isBanTimeBiggerThanAllowedByGroup(Player player, String argTime) {
        if(playerRestrictionType(player) == TimeRestrictionType.GROUP) {
            long maxTime = 0;
            if (StaticExpansions.getVaultManager().isVaultSetuped()) {
                maxTime = this.convertToMillis(this.fileAccessor.getGeneralConfig().getString("plugin-settings.time-settings.per-groups." + StaticExpansions.getVaultManager().getPlayerGroup(player) + ".ban").replace("|", ""));
                return this.convertToMillis(argTime) > maxTime;
            }
            if (StaticExpansions.getLuckPermsManager().isLuckPermsSetuped()) {
                maxTime = this.convertToMillis(this.fileAccessor.getGeneralConfig().getString("plugin-settings.time-settings.per-groups." + StaticExpansions.getLuckPermsManager().getPlayerGroup(player, getConfigSettings().getPossibleGroups()) + ".ban").replace("|", ""));
                return this.convertToMillis(argTime) > maxTime;
            }
        }
        return false;
    }

    private TimeRestrictionType playerRestrictionType(Player player) {
        if (StaticExpansions.getVaultManager().isVaultSetuped()) {
            if (this.fileAccessor.getGeneralConfig().contains("plugin-settings.time-settings.per-groups." + StaticExpansions.getVaultManager().getPlayerGroup(player))) {
                return TimeRestrictionType.GROUP;
            }
            return TimeRestrictionType.DEFAULT;
        } else {
            if (StaticExpansions.getLuckPermsManager().isLuckPermsSetuped()) {
                if (this.fileAccessor.getGeneralConfig().contains("plugin-settings.time-settings.per-groups." + StaticExpansions.getLuckPermsManager().getPlayerGroup(player, getConfigSettings().getPossibleGroups()))) {
                    return TimeRestrictionType.GROUP;
                }
                return TimeRestrictionType.DEFAULT;
            }
            return TimeRestrictionType.DEFAULT;
        }
    }

    public long getBanTime(long a) {
        return a - System.currentTimeMillis();
    }

}
