package net.alis.functionalservercontrol.spigot.additional.misc;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class WorldTimeAndDateClass {

    public static String getDate() {
        final Calendar dating = Calendar.getInstance();
        return new SimpleDateFormat("dd/MM/yyyy").format(dating.getTime());
    }

    public static String getTime() {
        final Calendar dating = Calendar.getInstance();
        return new SimpleDateFormat("HH:mm:ss").format(dating.getTime());
    }

    public static String getDateA() {
        final Calendar dating = Calendar.getInstance();
        return new SimpleDateFormat("dd-MM-yyyy").format(dating.getTime());
    }

    public static String getTimeA() {
        final Calendar dating = Calendar.getInstance();
        return new SimpleDateFormat("HH-mm").format(dating.getTime());
    }

    public static String getDate(Date date) {
        return new SimpleDateFormat("dd/MM/yyyy").format(date);
    }

    public static String getTime(Date date) {
        return new SimpleDateFormat("HH:mm:ss").format(date);
    }

}
