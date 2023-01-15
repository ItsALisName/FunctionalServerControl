package by.alis.functionalservercontrol.spigot.additional.tasks;

import by.alis.functionalservercontrol.spigot.additional.misc.device.DeviceInformation;

public class ServerInfoCollector implements Runnable {
    private transient long lastPoll = System.nanoTime();
    private short sec = 1;

    @Override
    public void run() {
        final long startTime = System.nanoTime();
        long timeSpent = (startTime - lastPoll) / 1000;
        if (timeSpent == 0) timeSpent = 1;
        double tps = 20 * 1000000.0 / timeSpent;
        if (tps <= 21) {
            DeviceInformation.ServerInfo.writeTps(sec, tps);
            sec = (short) (sec + 1);
            if(sec >= 300) sec = 1;
        }
        lastPoll = startTime;
    }
}
