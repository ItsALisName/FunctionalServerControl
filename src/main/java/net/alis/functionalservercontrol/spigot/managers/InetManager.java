package net.alis.functionalservercontrol.spigot.managers;

import org.bukkit.command.CommandSender;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;

import static net.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;
import static net.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

public class InetManager {

    public void preformInetTest(CommandSender initiator) {
        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("commands.inetspeed.started")));
        Runnable runnable = () -> {
            double[] result = runTest(); assert result != null;
            initiator.sendMessage(setColors(getFileAccessor().getLang().getString("commands.inetspeed.success")
                    .replace("%1$f", (result[0] / 1048576.0D) + " mb")
                    .replace("%2$f", String.valueOf((int)result[1]))
                    .replace("%3$f", String.valueOf((int)result[2]))
                    .replace("%4$f", String.valueOf((int)result[3]))
            ));
            File testFolder = new File("plugins/FunctionalServerControlSpigot/inetspeedtests");
            if (testFolder.exists())
                try {
                    for (File testFile : testFolder.listFiles())
                        Files.deleteIfExists(testFile.toPath());
                    Files.deleteIfExists(testFolder.toPath());
                } catch (IOException iOException) {
                    iOException.printStackTrace();
                }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    private double[] runTest() {
        String testUrl = "https://storage.googleapis.com/google-code-archive-downloads/v2/code.google.com/jquery-speedtest/100MB.txt";
        try {
            ReadableByteChannel readableByteChannel = Channels.newChannel(runChannel(testUrl));
            File testFolder = new File("plugins/FunctionalServerControlSpigot/inetspeedtests");
            if (!testFolder.exists())
                testFolder.mkdir();
            File testFile = new File(testFolder, "inet_speed_test.txt");
            testFile.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(testFile);
            long start = System.currentTimeMillis();
            fileOutputStream.getChannel().transferFrom(readableByteChannel, 0L, Long.MAX_VALUE);
            long stop = System.currentTimeMillis();
            long length = testFile.length();
            double v = (stop - start) / 1000.0D;
            double v1 = length / v;
            double v2 = v1 / 1024.0D;
            double v3 = v2 / 1024.0D;
            double[] results = new double[4];
            results[0] = length;
            results[1] = v;
            results[2] = v3;
            results[3] = v3 * 10.0D;
            fileOutputStream.close();
            readableByteChannel.close();
            return results;
        } catch (IOException iOException) {
            iOException.printStackTrace();
            return null;
        }
    }

    private InputStream runChannel(String paramString) {
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection)(new URL(paramString)).openConnection();
            httpURLConnection.addRequestProperty("User-Agent", "Mozilla/4.0");
            return httpURLConnection.getInputStream();
        } catch (IOException iOException) {
            throw new RuntimeException(iOException);
        }
    }

}
