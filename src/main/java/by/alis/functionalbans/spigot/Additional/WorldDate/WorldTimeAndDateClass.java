package by.alis.functionalbans.spigot.Additional.WorldDate;

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

}
