package by.alis.functionalservercontrol.spigot.Additional.Misc;

import by.alis.functionalservercontrol.spigot.Managers.TimeManagers.TimeSettingsAccessor;
import by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor;

public class TimeLangGlobal {

    public String getTimeLang(String arg) {
        TimeSettingsAccessor timeSettingsAccessor = new TimeSettingsAccessor();
        if(String.valueOf(timeSettingsAccessor.getTimeChecker().getArgNumber(arg)).endsWith("1")) {
            switch (timeSettingsAccessor.getTimeChecker().getTimeUnit(arg)) {
                case SECONDS: {
                    return StaticSettingsAccessor.getGlobalVariables().getMultiVariableSecond()[0];
                }
                case MINUTES: {
                    return StaticSettingsAccessor.getGlobalVariables().getMultiVariableMinutes()[0];
                }
                case HOURS: {
                    return StaticSettingsAccessor.getGlobalVariables().getMultiVariableHours()[0];
                }
                case DAYS: {
                    return StaticSettingsAccessor.getGlobalVariables().getMultiVariableDays()[0];
                }
                case MONTHS: {
                    return StaticSettingsAccessor.getGlobalVariables().getMultiVariableMonths()[0];
                }
                case YEARS: {
                    return StaticSettingsAccessor.getGlobalVariables().getMultiVariableYears()[0];
                }
            }
        }

        if(String.valueOf(timeSettingsAccessor.getTimeChecker().getArgNumber(arg)).endsWith("2") || String.valueOf(timeSettingsAccessor.getTimeChecker().getArgNumber(arg)).endsWith("3") || String.valueOf(timeSettingsAccessor.getTimeChecker().getArgNumber(arg)).endsWith("4")) {
            switch (timeSettingsAccessor.getTimeChecker().getTimeUnit(arg)) {
                case SECONDS: {
                    return StaticSettingsAccessor.getGlobalVariables().getMultiVariableSecond()[1];
                }
                case MINUTES: {
                    return StaticSettingsAccessor.getGlobalVariables().getMultiVariableMinutes()[1];
                }
                case HOURS: {
                    return StaticSettingsAccessor.getGlobalVariables().getMultiVariableHours()[1];
                }
                case DAYS: {
                    return StaticSettingsAccessor.getGlobalVariables().getMultiVariableDays()[1];
                }
                case MONTHS: {
                    return StaticSettingsAccessor.getGlobalVariables().getMultiVariableMonths()[1];
                }
                case YEARS: {
                    return StaticSettingsAccessor.getGlobalVariables().getMultiVariableYears()[1];
                }
            }
        }

        switch (timeSettingsAccessor.getTimeChecker().getTimeUnit(arg)) {
            case SECONDS: {
                return StaticSettingsAccessor.getGlobalVariables().getMultiVariableSecond()[2];
            }
            case MINUTES: {
                return StaticSettingsAccessor.getGlobalVariables().getMultiVariableMinutes()[2];
            }
            case HOURS: {
                return StaticSettingsAccessor.getGlobalVariables().getMultiVariableHours()[2];
            }
            case DAYS: {
                return StaticSettingsAccessor.getGlobalVariables().getMultiVariableDays()[2];
            }
            case MONTHS: {
                return StaticSettingsAccessor.getGlobalVariables().getMultiVariableMonths()[2];
            }
            case YEARS: {
                return StaticSettingsAccessor.getGlobalVariables().getMultiVariableYears()[2];
            }
        }

        return null;
    }

}
