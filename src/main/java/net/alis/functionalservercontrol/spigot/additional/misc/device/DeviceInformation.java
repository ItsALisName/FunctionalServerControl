package net.alis.functionalservercontrol.spigot.additional.misc.device;

import net.alis.functionalservercontrol.spigot.managers.time.TimeSettingsAccessor;
import com.sun.management.OperatingSystemMXBean;
import lombok.Setter;

import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;
import java.util.DoubleSummaryStatistics;
import java.util.LinkedList;

import static net.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;

public class DeviceInformation {

    public static class ServerInfo {
        private static @Setter double tpsLastSecond = 20.0, tpsLastFiveSeconds = 20.0, tpsLastThirtySeconds = 20.0, tpsLastMinute = 20.0, tpsLastFiveMinutes = 20.0, tpsLastTenMinutes = 20.0;
        private static final LinkedList<Double> serverTps = new LinkedList<>();
        private static final TimeSettingsAccessor timeSettingsAccessor = new TimeSettingsAccessor();
        private static final DecimalFormat format = new DecimalFormat("##.###");

        private ServerInfo() {}

        public static void writeTps(short sec, double tps) {
            serverTps.add(tps);
            setTpsLastSecond(tps);
            if(sec % 5 == 0) setTpsLastFiveSeconds(tps);
            if(sec % 30 == 0) setTpsLastThirtySeconds(tps);
            if(sec % 60 == 0) setTpsLastMinute(tps);
            if(sec % 300 == 0) setTpsLastFiveMinutes(tps);
            if(sec % 600 == 0) {
                setTpsLastTenMinutes(tps);
                serverTps.clear();
            }
        }

        public static String getTpsLastSecond() {
            if(tpsLastSecond > 20) return setColors("&a*" + Math.round(tpsLastSecond) + " &7(" + format.format(tpsLastSecond) + ")");
            if(tpsLastSecond >= 18) return setColors("&a" + Math.round(tpsLastSecond) + " &7(" + format.format(tpsLastSecond) + ")");
            if(tpsLastSecond >= 13) return setColors("&e*" + Math.round(tpsLastSecond) + " &7(" + format.format(tpsLastSecond) + ")");
            if(tpsLastSecond >= 8) return setColors("&c" + Math.round(tpsLastSecond) + " &7(" + format.format(tpsLastSecond) + ")");
            return setColors("&4" + Math.round(tpsLastSecond) + " &7(" + format.format(tpsLastSecond) + ")");
        }

        public static String getTpsLastFiveSeconds() {
            if(tpsLastFiveSeconds > 20) return setColors("&a*" + Math.round(tpsLastFiveSeconds) + " &7(" + format.format(tpsLastFiveSeconds) + ")");
            if(tpsLastFiveSeconds >= 18) return setColors("&a" + Math.round(tpsLastFiveSeconds) + " &7(" + format.format(tpsLastFiveSeconds) + ")");
            if(tpsLastFiveSeconds >= 13) return setColors("&e*" + Math.round(tpsLastFiveSeconds) + " &7(" + format.format(tpsLastFiveSeconds) + ")");
            if(tpsLastFiveSeconds >= 8) return setColors("&c" + Math.round(tpsLastFiveSeconds) + " &7(" + format.format(tpsLastFiveSeconds) + ")");
            return setColors("&4" + Math.round(tpsLastFiveSeconds) + " &7(" + format.format(tpsLastFiveSeconds) + ")");
        }

        public static String getTpsLastThirtySeconds() {
            if(tpsLastThirtySeconds > 20) return setColors("&a*" + Math.round(tpsLastThirtySeconds) + " &7(" + format.format(tpsLastThirtySeconds) + ")");
            if(tpsLastThirtySeconds >= 18) return setColors("&a" + Math.round(tpsLastThirtySeconds) + " &7(" + format.format(tpsLastThirtySeconds) + ")");
            if(tpsLastThirtySeconds >= 13) return setColors("&e*" + Math.round(tpsLastThirtySeconds) + " &7(" + format.format(tpsLastThirtySeconds) + ")");
            if(tpsLastThirtySeconds >= 8) return setColors("&c" + Math.round(tpsLastThirtySeconds) + " &7(" + format.format(tpsLastThirtySeconds) + ")");
            return setColors("&4" + Math.round(tpsLastThirtySeconds) + " &7(" + format.format(tpsLastThirtySeconds) + ")");
        }

        public static String getTpsLastMinute() {
            if(tpsLastMinute > 20) return setColors("&a*" + Math.round(tpsLastMinute) + " &7(" + format.format(tpsLastMinute) + ")");
            if(tpsLastMinute >= 18) return setColors("&a" + Math.round(tpsLastMinute) + " &7(" + format.format(tpsLastMinute) + ")");
            if(tpsLastMinute >= 13) return setColors("&e*" + Math.round(tpsLastMinute) + " &7(" + format.format(tpsLastMinute) + ")");
            if(tpsLastMinute >= 8) return setColors("&c" + Math.round(tpsLastMinute) + " &7(" + format.format(tpsLastMinute) + ")");
            return setColors("&4" + Math.round(tpsLastMinute) + " &7(" + format.format(tpsLastMinute) + ")");
        }

        public static String getTpsLastFiveMinutes() {
            if(tpsLastFiveMinutes > 20) return setColors("&a*" + Math.round(tpsLastFiveMinutes) + " &7(" + format.format(tpsLastFiveMinutes) + ")");
            if(tpsLastFiveMinutes >= 18) return setColors("&a" + Math.round(tpsLastFiveMinutes) + " &7(" + format.format(tpsLastFiveMinutes) + ")");
            if(tpsLastFiveMinutes >= 13) return setColors("&e*" + Math.round(tpsLastFiveMinutes) + " &7(" + format.format(tpsLastFiveMinutes) + ")");
            if(tpsLastFiveMinutes >= 8) return setColors("&c" + Math.round(tpsLastFiveMinutes) + " &7(" + format.format(tpsLastFiveMinutes) + ")");
            return setColors("&4" + Math.round(tpsLastFiveMinutes) + " &7(" + format.format(tpsLastFiveMinutes) + ")");
        }

        public static String getTpsLastTenMinutes() {
            if(tpsLastTenMinutes > 20) return setColors("&a*" + Math.round(tpsLastTenMinutes) + " &7(" + format.format(tpsLastTenMinutes) + ")");
            if(tpsLastTenMinutes >= 18) return setColors("&a" + Math.round(tpsLastTenMinutes) + " &7(" + format.format(tpsLastTenMinutes) + ")");
            if(tpsLastTenMinutes >= 13) return setColors("&e*" + Math.round(tpsLastTenMinutes) + " &7(" + format.format(tpsLastTenMinutes) + ")");
            if(tpsLastTenMinutes >= 8) return setColors("&c" + Math.round(tpsLastTenMinutes) + " &7(" + format.format(tpsLastTenMinutes) + ")");
            return setColors("&4" + Math.round(tpsLastTenMinutes) + " &7(" + format.format(tpsLastTenMinutes) + ")");
        }

        public static String getMaxMemory() {
            return String.valueOf(Runtime.getRuntime().maxMemory() / 1024 / 1024);
        }

        public static String getAvailableMemory() {
            return String.valueOf(Runtime.getRuntime().totalMemory() / 1024 / 1024);
        }

        public static String getUsedMemory() {
            return String.valueOf((Runtime.getRuntime().totalMemory() / 1024 / 1024) - (Runtime.getRuntime().freeMemory() / 1024 / 1024));
        }

        public static String getFreeMemory() {
            return String.valueOf(Runtime.getRuntime().freeMemory() / 1024 / 1024);
        }

        public static String getCpuSystemUsage() {
            return Math.round(((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getSystemCpuLoad() * 1000) / 10D + "%";
        }

        public static String getCpuJavaUsage() {
            return Math.round(((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getProcessCpuLoad() * 1000) / 10D + "%";
        }

        public static String getAverageCpuUsage() {
            double average = (Math.round(((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getProcessCpuLoad() * 1000) / 10D + Math.round(((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getSystemCpuLoad() * 1000) / 10D) / 2;
            return average + "%";
        }

        public static String getAverageTps() {
            DoubleSummaryStatistics sum = serverTps.stream().mapToDouble(Double::doubleValue).summaryStatistics();
            double averageTps = sum.getAverage();
            if(averageTps > 20) return setColors("&a*" + Math.round(averageTps) + " &7(" + format.format(averageTps) + ")");
            if(averageTps >= 18) return setColors("&a" + Math.round(averageTps) + " &7(" + format.format(averageTps) + ")");
            if(averageTps >= 13) return setColors("&e" + Math.round(averageTps) + " &7(" + format.format(averageTps) + ")" );
            if(averageTps >= 8) return setColors("&c" + Math.round(averageTps) + " &7(" + format.format(averageTps) + ")");
            return setColors("&4" + Math.round(averageTps) + " &7(" + format.format(averageTps) + ")");
        }

        public static String getUptime() {
            return timeSettingsAccessor.getTimeManager().convertFromMillis(ManagementFactory.getRuntimeMXBean().getUptime());
        }

    }

    public static class MachineInfo {

        public static String getJavaVersion() {
            return setColors(System.getProperty("java.version") + " &7(" + System.getProperty("java.version.date") + ")");
        }

        public static String getJDKVersion() {
            return System.getProperty("java.runtime.version");
        }

        public static String getAvailableCores() {
            return String.valueOf(ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors());
        }

        public static String getJavaRuntimeVersion() {
            return System.getProperty("java.runtime.version") + " (Class version: " + System.getProperty("java.class.version") + ")";
        }

        public static String getOSName() {
            return System.getProperty("os.name");
        }

        public static String getOSVersion() {
            return System.getProperty("os.version");
        }

        public static String getOSArch() {
            return System.getProperty("os.arch");
        }

    }

}
