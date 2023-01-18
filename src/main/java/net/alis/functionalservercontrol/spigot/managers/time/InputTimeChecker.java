package net.alis.functionalservercontrol.spigot.managers.time;

import net.alis.functionalservercontrol.api.enums.TimeUnit;

public class InputTimeChecker {

    public boolean checkInputTimeArgument(String inputArgument) {
        if(inputArgument.replaceAll("\\d", "").equalsIgnoreCase("s")
                || inputArgument.replaceAll("\\d", "").equalsIgnoreCase("seconds")
                || inputArgument.replaceAll("\\d", "").equalsIgnoreCase("sec")
                || inputArgument.replaceAll("\\d", "").equalsIgnoreCase("second"))
        {
            try {
                if(inputArgument.replaceAll("\\d", "").equalsIgnoreCase("seconds")) {
                    Long.parseLong(inputArgument.substring(0, inputArgument.length() - 7));
                } else if(inputArgument.replaceAll("\\d", "").equalsIgnoreCase("sec")) {
                    Long.parseLong(inputArgument.substring(0, inputArgument.length() - 3));
                } else if(inputArgument.replaceAll("\\d", "").equalsIgnoreCase("second")) {
                    Long.parseLong(inputArgument.substring(0, inputArgument.length() - 6));
                } else {
                    Long.parseLong(inputArgument.substring(0, inputArgument.length() - 1));
                }
                return true;
            } catch (NumberFormatException ignored) {
                return false;
            }
        }

        if(inputArgument.replaceAll("\\d", "").equalsIgnoreCase("minutes")
                || inputArgument.replaceAll("\\d", "").equalsIgnoreCase("minute")
                || inputArgument.replaceAll("\\d", "").equalsIgnoreCase("min")
                || inputArgument.replaceAll("\\d", "").equalsIgnoreCase("m"))
        {
            try {
                if(inputArgument.replaceAll("\\d", "").equalsIgnoreCase("minutes")) {
                    Long.parseLong(inputArgument.substring(0, inputArgument.length() - 7));
                    return true;
                } else if(inputArgument.replaceAll("\\d", "").equalsIgnoreCase("min")) {
                    Long.parseLong(inputArgument.substring(0, inputArgument.length() - 3));
                    return true;
                } else if(inputArgument.replaceAll("\\d", "").equalsIgnoreCase("minute")) {
                    Long.parseLong(inputArgument.substring(0, inputArgument.length() - 6));
                    return true;
                } else {
                    Long.parseLong(inputArgument.substring(0, inputArgument.length() - 1));
                }
                return true;
            } catch (NumberFormatException ignored) {
                return false;
            }
        }

        if(inputArgument.replaceAll("\\d", "").equalsIgnoreCase("hours")
                || inputArgument.replaceAll("\\d", "").equalsIgnoreCase("hour")
                || inputArgument.replaceAll("\\d", "").equalsIgnoreCase("h"))
        {
            try {
                if(inputArgument.replaceAll("\\d", "").equalsIgnoreCase("hours")) {
                    Long.parseLong(inputArgument.substring(0, inputArgument.length() - 5));
                } else if(inputArgument.replaceAll("\\d", "").equalsIgnoreCase("hour")) {
                    Long.parseLong(inputArgument.substring(0, inputArgument.length() - 4));
                } else {
                    Long.parseLong(inputArgument.substring(0, inputArgument.length() - 1));
                }
                return true;
            } catch (NumberFormatException ignored) {
                return false;
            }
        }

        if(inputArgument.replaceAll("\\d", "").equalsIgnoreCase("days")
                || inputArgument.replaceAll("\\d", "").equalsIgnoreCase("day")
                || inputArgument.replaceAll("\\d", "").equalsIgnoreCase("d"))
        {
            try {
                if(inputArgument.replaceAll("\\d", "").equalsIgnoreCase("days")) {
                    Long.parseLong(inputArgument.substring(0, inputArgument.length() - 4));
                } else if(inputArgument.replaceAll("\\d", "").equalsIgnoreCase("day")) {
                    Long.parseLong(inputArgument.substring(0, inputArgument.length() - 3));
                } else {
                    Long.parseLong(inputArgument.substring(0, inputArgument.length() - 1));
                }
                return true;
            } catch (NumberFormatException ignored) {
                return false;
            }
        }

        if(inputArgument.replaceAll("\\d", "").equalsIgnoreCase("months")
                || inputArgument.replaceAll("\\d", "").equalsIgnoreCase("month")
                || inputArgument.replaceAll("\\d", "").equalsIgnoreCase("mon"))
        {
            try {
                if(inputArgument.replaceAll("\\d", "").equalsIgnoreCase("months")) {
                    Long.parseLong(inputArgument.substring(0, inputArgument.length() - 6));
                } else if(inputArgument.replaceAll("\\d", "").equalsIgnoreCase("month")) {
                    Long.parseLong(inputArgument.substring(0, inputArgument.length() - 5));
                } else {
                    Long.parseLong(inputArgument.substring(0, inputArgument.length() - 3));
                }
                return true;
            } catch (NumberFormatException ignored) {
                return false;
            }
        }

        if(inputArgument.replaceAll("\\d", "").equalsIgnoreCase("years")
                || inputArgument.replaceAll("\\d", "").equalsIgnoreCase("year")
                || inputArgument.replaceAll("\\d", "").equalsIgnoreCase("y"))
        {
            try {
                if(inputArgument.replaceAll("\\d", "").equalsIgnoreCase("years")) {
                    Long.parseLong(inputArgument.substring(0, inputArgument.length() - 5));
                } else if(inputArgument.replaceAll("\\d", "").equalsIgnoreCase("year")) {
                    Long.parseLong(inputArgument.substring(0, inputArgument.length() - 4));
                } else {
                    Long.parseLong(inputArgument.substring(0, inputArgument.length() - 1));
                }
                return true;
            } catch (NumberFormatException ignored) {
                return false;
            }
        }

        try {
            Long.parseLong(inputArgument);
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

    public TimeUnit getTimeUnit(String inputArgument) {

        if(inputArgument.replaceAll("\\d", "").equalsIgnoreCase("s")
                || inputArgument.replaceAll("\\d", "").equalsIgnoreCase("seconds")
                || inputArgument.replaceAll("\\d", "").equalsIgnoreCase("sec")
                || inputArgument.replaceAll("\\d", "").equalsIgnoreCase("second")) {
            return TimeUnit.SECONDS;
        }

        if(inputArgument.replaceAll("\\d", "").equalsIgnoreCase("minutes")
                || inputArgument.replaceAll("\\d", "").equalsIgnoreCase("minute")
                || inputArgument.replaceAll("\\d", "").equalsIgnoreCase("min")
                || inputArgument.replaceAll("\\d", "").equalsIgnoreCase("m"))
        {
            return TimeUnit.MINUTES;
        }

        if(inputArgument.replaceAll("\\d", "").equalsIgnoreCase("hours")
                || inputArgument.replaceAll("\\d", "").equalsIgnoreCase("hour")
                || inputArgument.replaceAll("\\d", "").equalsIgnoreCase("h"))
        {
            return TimeUnit.HOURS;
        }

        if(inputArgument.replaceAll("\\d", "").equalsIgnoreCase("months")
                || inputArgument.replaceAll("\\d", "").equalsIgnoreCase("month")
                || inputArgument.replaceAll("\\d", "").equalsIgnoreCase("mon"))
        {
            return TimeUnit.MONTHS;
        }

        if(inputArgument.replaceAll("\\d", "").equalsIgnoreCase("days")
                || inputArgument.replaceAll("\\d", "").equalsIgnoreCase("day")
                || inputArgument.replaceAll("\\d", "").equalsIgnoreCase("d"))
        {
            return TimeUnit.DAYS;
        }

        if(inputArgument.replaceAll("\\d", "").equalsIgnoreCase("years")
                || inputArgument.replaceAll("\\d", "").equalsIgnoreCase("year")
                || inputArgument.replaceAll("\\d", "").equalsIgnoreCase("y"))
        {
            return TimeUnit.YEARS;
        }
        return null;
    }

    public long getArgNumber(String arg) {
        return Long.parseLong(arg.replaceAll("[^\\d]", ""));
    }

}
