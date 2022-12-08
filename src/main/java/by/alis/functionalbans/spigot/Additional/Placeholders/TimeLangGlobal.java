package by.alis.functionalbans.spigot.Additional.Placeholders;

import by.alis.functionalbans.spigot.Additional.Enums.TimeUnit;
import by.alis.functionalbans.spigot.Managers.TimeManagers.TimeSettingsAccessor;
import by.alis.functionalbans.spigot.Additional.GlobalSettings.StaticSettingsAccessor;

public class TimeLangGlobal {

    TimeSettingsAccessor timeSettingsAccessor = new TimeSettingsAccessor();

    public String getTimeLang(String arg) {
        if(String.valueOf(this.timeSettingsAccessor.getTimeChecker().getArgNumber(arg)).endsWith("1")) {
            if(this.timeSettingsAccessor.getTimeChecker().getTimeUnit(arg) == TimeUnit.SECONDS) {
                return StaticSettingsAccessor.getGlobalVariables().getMultiVariableSecond()[0];
            }
            if(this.timeSettingsAccessor.getTimeChecker().getTimeUnit(arg) == TimeUnit.MINUTES) {
                return StaticSettingsAccessor.getGlobalVariables().getMultiVariableMinutes()[0];
            }
            if(this.timeSettingsAccessor.getTimeChecker().getTimeUnit(arg) == TimeUnit.HOURS) {
                return StaticSettingsAccessor.getGlobalVariables().getMultiVariableHours()[0];
            }
            if(this.timeSettingsAccessor.getTimeChecker().getTimeUnit(arg) == TimeUnit.DAYS) {
                return StaticSettingsAccessor.getGlobalVariables().getMultiVariableDays()[0];
            }
            if(this.timeSettingsAccessor.getTimeChecker().getTimeUnit(arg) == TimeUnit.MONTHS) {
                return StaticSettingsAccessor.getGlobalVariables().getMultiVariableMonths()[0];
            }
            if(this.timeSettingsAccessor.getTimeChecker().getTimeUnit(arg) == TimeUnit.YEARS) {
                return StaticSettingsAccessor.getGlobalVariables().getMultiVariableYears()[0];
            }
        }

        if(String.valueOf(this.timeSettingsAccessor.getTimeChecker().getArgNumber(arg)).endsWith("2") || String.valueOf(this.timeSettingsAccessor.getTimeChecker().getArgNumber(arg)).endsWith("3") || String.valueOf(this.timeSettingsAccessor.getTimeChecker().getArgNumber(arg)).endsWith("4")) {
            if(this.timeSettingsAccessor.getTimeChecker().getTimeUnit(arg) == TimeUnit.SECONDS) {
                return StaticSettingsAccessor.getGlobalVariables().getMultiVariableSecond()[1];
            }
            if(this.timeSettingsAccessor.getTimeChecker().getTimeUnit(arg) == TimeUnit.MINUTES) {
                return StaticSettingsAccessor.getGlobalVariables().getMultiVariableMinutes()[1];
            }
            if(this.timeSettingsAccessor.getTimeChecker().getTimeUnit(arg) == TimeUnit.HOURS) {
                return StaticSettingsAccessor.getGlobalVariables().getMultiVariableHours()[1];
            }
            if(this.timeSettingsAccessor.getTimeChecker().getTimeUnit(arg) == TimeUnit.DAYS) {
                return StaticSettingsAccessor.getGlobalVariables().getMultiVariableDays()[1];
            }
            if(this.timeSettingsAccessor.getTimeChecker().getTimeUnit(arg) == TimeUnit.MONTHS) {
                return StaticSettingsAccessor.getGlobalVariables().getMultiVariableMonths()[1];
            }
            if(this.timeSettingsAccessor.getTimeChecker().getTimeUnit(arg) == TimeUnit.YEARS) {
                return StaticSettingsAccessor.getGlobalVariables().getMultiVariableYears()[1];
            }
        }

        if(this.timeSettingsAccessor.getTimeChecker().getTimeUnit(arg) == TimeUnit.SECONDS) {
            return StaticSettingsAccessor.getGlobalVariables().getMultiVariableSecond()[2];
        }
        if(this.timeSettingsAccessor.getTimeChecker().getTimeUnit(arg) == TimeUnit.MINUTES) {
            return StaticSettingsAccessor.getGlobalVariables().getMultiVariableMinutes()[2];
        }
        if(this.timeSettingsAccessor.getTimeChecker().getTimeUnit(arg) == TimeUnit.HOURS) {
            return StaticSettingsAccessor.getGlobalVariables().getMultiVariableHours()[2];
        }
        if(this.timeSettingsAccessor.getTimeChecker().getTimeUnit(arg) == TimeUnit.DAYS) {
            return StaticSettingsAccessor.getGlobalVariables().getMultiVariableDays()[2];
        }
        if(this.timeSettingsAccessor.getTimeChecker().getTimeUnit(arg) == TimeUnit.MONTHS) {
            return StaticSettingsAccessor.getGlobalVariables().getMultiVariableMonths()[2];
        }
        if(this.timeSettingsAccessor.getTimeChecker().getTimeUnit(arg) == TimeUnit.YEARS) {
            return StaticSettingsAccessor.getGlobalVariables().getMultiVariableYears()[2];
        }

        return null;
    }

}
