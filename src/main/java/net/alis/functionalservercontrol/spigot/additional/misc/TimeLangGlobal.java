package net.alis.functionalservercontrol.spigot.additional.misc;

import net.alis.functionalservercontrol.spigot.managers.time.TimeSettingsAccessor;
import net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor;
import net.alis.functionalservercontrol.api.enums.TimeUnit;

public class TimeLangGlobal {

    public String getTimeLang(String arg) {
        TimeSettingsAccessor timeSettingsAccessor = new TimeSettingsAccessor();
        if(String.valueOf(timeSettingsAccessor.getTimeChecker().getArgNumber(arg)).endsWith("1")) {
            switch (timeSettingsAccessor.getTimeChecker().getTimeUnit(arg)) {
                case SECONDS: {
                    return SettingsAccessor.getGlobalVariables().getMultiVariableSecond()[0];
                }
                case MINUTES: {
                    return SettingsAccessor.getGlobalVariables().getMultiVariableMinutes()[0];
                }
                case HOURS: {
                    return SettingsAccessor.getGlobalVariables().getMultiVariableHours()[0];
                }
                case DAYS: {
                    return SettingsAccessor.getGlobalVariables().getMultiVariableDays()[0];
                }
                case MONTHS: {
                    return SettingsAccessor.getGlobalVariables().getMultiVariableMonths()[0];
                }
                case YEARS: {
                    return SettingsAccessor.getGlobalVariables().getMultiVariableYears()[0];
                }
            }
        }

        if(String.valueOf(timeSettingsAccessor.getTimeChecker().getArgNumber(arg)).endsWith("2") || String.valueOf(timeSettingsAccessor.getTimeChecker().getArgNumber(arg)).endsWith("3") || String.valueOf(timeSettingsAccessor.getTimeChecker().getArgNumber(arg)).endsWith("4")) {
            switch (timeSettingsAccessor.getTimeChecker().getTimeUnit(arg)) {
                case SECONDS: {
                    return SettingsAccessor.getGlobalVariables().getMultiVariableSecond()[1];
                }
                case MINUTES: {
                    return SettingsAccessor.getGlobalVariables().getMultiVariableMinutes()[1];
                }
                case HOURS: {
                    return SettingsAccessor.getGlobalVariables().getMultiVariableHours()[1];
                }
                case DAYS: {
                    return SettingsAccessor.getGlobalVariables().getMultiVariableDays()[1];
                }
                case MONTHS: {
                    return SettingsAccessor.getGlobalVariables().getMultiVariableMonths()[1];
                }
                case YEARS: {
                    return SettingsAccessor.getGlobalVariables().getMultiVariableYears()[1];
                }
            }
        }

        switch (timeSettingsAccessor.getTimeChecker().getTimeUnit(arg)) {
            case SECONDS: {
                return SettingsAccessor.getGlobalVariables().getMultiVariableSecond()[2];
            }
            case MINUTES: {
                return SettingsAccessor.getGlobalVariables().getMultiVariableMinutes()[2];
            }
            case HOURS: {
                return SettingsAccessor.getGlobalVariables().getMultiVariableHours()[2];
            }
            case DAYS: {
                return SettingsAccessor.getGlobalVariables().getMultiVariableDays()[2];
            }
            case MONTHS: {
                return SettingsAccessor.getGlobalVariables().getMultiVariableMonths()[2];
            }
            case YEARS: {
                return SettingsAccessor.getGlobalVariables().getMultiVariableYears()[2];
            }
        }

        return null;
    }

}
