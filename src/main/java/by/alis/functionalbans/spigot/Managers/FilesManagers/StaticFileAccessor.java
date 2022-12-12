package by.alis.functionalbans.spigot.Managers.FilesManagers;

public class StaticFileAccessor {

    public static FileAccessor fileAccessor = new FileAccessor();
    public static FileAccessor getFileAccessor() {
        return fileAccessor;
    }
}
