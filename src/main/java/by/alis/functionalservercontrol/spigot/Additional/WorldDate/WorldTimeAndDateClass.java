package by.alis.functionalservercontrol.spigot.Additional.WorldDate;

import java.text.SimpleDateFormat;
import java.util.Calendar;

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

}