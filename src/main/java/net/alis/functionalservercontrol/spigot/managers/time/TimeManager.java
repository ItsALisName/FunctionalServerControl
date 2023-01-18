package net.alis.functionalservercontrol.spigot.managers.time;

import net.alis.functionalservercontrol.api.enums.TimeRestrictionType;
import net.alis.functionalservercontrol.api.enums.TimeUnit;
import net.alis.functionalservercontrol.spigot.dependencies.Expansions;
import org.bukkit.entity.Player;

import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getGlobalVariables;
import static net.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;


public class TimeManager {

    InputTimeChecker checker = new InputTimeChecker();


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

    public boolean isBanTimeBiggerThanMax(String argTime) {
        return convertToMillis(argTime) > getMaxPossibleBanPunishTime();
    }

    public long getMaxPossibleMutePunishTime() {
        return convertToMillis(getFileAccessor().getGeneralConfig().getString("plugin-settings.time-settings.max-possible-mute-time").replace("|", ""));
    }

    public long getMaxPossibleBanPunishTime() {
        return convertToMillis(getFileAccessor().getGeneralConfig().getString("plugin-settings.time-settings.max-possible-ban-time").replace("|", ""));
    }

    public long getMaxPlayerBanPunishTime(Player player) {
        if(playerRestrictionType(player) == TimeRestrictionType.GROUP) {
            if (Expansions.getVaultManager().isVaultSetuped()) {
                if(getFileAccessor().getGeneralConfig().contains("plugin-settings.time-settings.per-groups." + Expansions.getVaultManager().getPlayerGroup(player) + ".ban")) {
                    long maxTime = this.convertToMillis(getFileAccessor().getGeneralConfig().getString("plugin-settings.time-settings.per-groups." + Expansions.getVaultManager().getPlayerGroup(player) + ".ban").replace("|", ""));
                    return maxTime;
                } else {
                    return getMaxPossibleBanPunishTime();
                }
            }
            if (Expansions.getLuckPermsManager().isLuckPermsSetuped()) {
                if(getFileAccessor().getGeneralConfig().contains("plugin-settings.time-settings.per-groups." + Expansions.getLuckPermsManager().getPlayerGroup(player) + ".ban")) {
                    long maxTime = this.convertToMillis(getFileAccessor().getGeneralConfig().getString("plugin-settings.time-settings.per-groups." + Expansions.getLuckPermsManager().getPlayerGroup(player) + ".ban").replace("|", ""));
                    return maxTime;
                } else {
                    return getMaxPossibleBanPunishTime();
                }
            }
        } else {
            return getMaxPossibleBanPunishTime();
        }
        return getMaxPossibleBanPunishTime();
    }

    public long getMaxPlayerMutePunishTime(Player player) {
        if(playerRestrictionType(player) == TimeRestrictionType.GROUP) {
            if (Expansions.getVaultManager().isVaultSetuped()) {
                if(getFileAccessor().getGeneralConfig().contains("plugin-settings.time-settings.per-groups." + Expansions.getVaultManager().getPlayerGroup(player) + ".mute")) {
                    long maxTime = this.convertToMillis(getFileAccessor().getGeneralConfig().getString("plugin-settings.time-settings.per-groups." + Expansions.getVaultManager().getPlayerGroup(player) + ".mute").replace("|", ""));
                    return maxTime;
                } else {
                    return getMaxPossibleMutePunishTime();
                }
            }
            if (Expansions.getLuckPermsManager().isLuckPermsSetuped()) {
                if(getFileAccessor().getGeneralConfig().contains("plugin-settings.time-settings.per-groups." + Expansions.getLuckPermsManager().getPlayerGroup(player) + ".mute")) {
                    long maxTime = this.convertToMillis(getFileAccessor().getGeneralConfig().getString("plugin-settings.time-settings.per-groups." + Expansions.getLuckPermsManager().getPlayerGroup(player) + ".mute").replace("|", ""));
                    return maxTime;
                } else {
                    return getMaxPossibleMutePunishTime();
                }
            }
        } else {
            return getMaxPossibleMutePunishTime();
        }
        return getMaxPossibleMutePunishTime();
    }

    public boolean isBanTimeBiggerThanAllowedByGroup(Player player, String argTime) {
        if(playerRestrictionType(player) == TimeRestrictionType.GROUP) {
            long maxTime = 0;
            if (Expansions.getVaultManager().isVaultSetuped()) {
                maxTime = this.convertToMillis(getFileAccessor().getGeneralConfig().getString("plugin-settings.time-settings.per-groups." + Expansions.getVaultManager().getPlayerGroup(player) + ".ban").replace("|", ""));
                return this.convertToMillis(argTime) > maxTime;
            }
            if (Expansions.getLuckPermsManager().isLuckPermsSetuped()) {
                maxTime = this.convertToMillis(getFileAccessor().getGeneralConfig().getString("plugin-settings.time-settings.per-groups." + Expansions.getLuckPermsManager().getPlayerGroup(player) + ".ban").replace("|", ""));
                return this.convertToMillis(argTime) > maxTime;
            }
        }
        return false;
    }

    public boolean isMuteTimeBiggerThanAllowedByGroup(Player player, String argTime) {
        if(playerRestrictionType(player) == TimeRestrictionType.GROUP) {
            long maxTime = 0;
            if (Expansions.getVaultManager().isVaultSetuped()) {
                maxTime = this.convertToMillis(getFileAccessor().getGeneralConfig().getString("plugin-settings.time-settings.per-groups." + Expansions.getVaultManager().getPlayerGroup(player) + ".mute").replace("|", ""));
                return convertToMillis(argTime) > maxTime;
            }
            if (Expansions.getLuckPermsManager().isLuckPermsSetuped()) {
                maxTime = this.convertToMillis(getFileAccessor().getGeneralConfig().getString("plugin-settings.time-settings.per-groups." + Expansions.getLuckPermsManager().getPlayerGroup(player) + ".mute").replace("|", ""));
                return convertToMillis(argTime) > maxTime;
            }
        }
        return false;
    }

    private TimeRestrictionType playerRestrictionType(Player player) {
        if (Expansions.getVaultManager().isVaultSetuped()) {
            if (getFileAccessor().getGeneralConfig().contains("plugin-settings.time-settings.per-groups." + Expansions.getVaultManager().getPlayerGroup(player))) {
                return TimeRestrictionType.GROUP;
            } else {
                return TimeRestrictionType.DEFAULT;
            }
        } else {
            if (Expansions.getLuckPermsManager().isLuckPermsSetuped()) {
                if (getFileAccessor().getGeneralConfig().contains("plugin-settings.time-settings.per-groups." + Expansions.getLuckPermsManager().getPlayerGroup(player))) {
                    return TimeRestrictionType.GROUP;
                } else {
                    return TimeRestrictionType.DEFAULT;
                }
            }
            return TimeRestrictionType.DEFAULT;
        }
    }

    public long getPunishTime(long a) {
        return a - System.currentTimeMillis();
    }

    public long convertFromSecToMillis(int param) {
        return param * 1000L;
    }

}
