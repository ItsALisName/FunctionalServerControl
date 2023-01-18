package net.alis.functionalservercontrol.spigot.managers.file;

public class SFAccessor {

    private static FileAccessor fileAccessor = new FileAccessor();

    public static void reloadFiles() {
        fileAccessor = new FileAccessor();
    }

    public static FileAccessor getFileAccessor() {
        return fileAccessor;
    }
}
