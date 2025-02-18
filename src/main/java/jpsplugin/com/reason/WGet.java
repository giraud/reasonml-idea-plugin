package jpsplugin.com.reason;

import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.util.SystemInfo;

import java.io.*;
import java.net.*;
import java.nio.file.StandardCopyOption;

import org.jetbrains.annotations.NotNull;

public class WGet {
    private static final Log LOG = Log.create("wget");

    private static final int BUFFER_SIZE = 1024;

    private WGet() {
    }

    public static boolean apply(@NotNull String urlString, @NotNull File targetFile, @NotNull ProgressIndicator indicator, double totalBytes) {
        try {
            // some code
            File partFile = new File(targetFile.getPath() + ".part");

            if (partFile.exists()) {
                //noinspection ResultOfMethodCallIgnored
                partFile.delete();
            }

            //noinspection ResultOfMethodCallIgnored
            partFile.createNewFile();

            FileOutputStream partFileOut = new FileOutputStream(partFile);

            java.net.URL url = URI.create(urlString).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);

            connection.setConnectTimeout(240 * 1000);
            connection.setReadTimeout(240 * 1000);

            InputStream inputStream = connection.getInputStream();

            double totalBytesDownloaded = 0.0;

            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead = inputStream.read(buffer);
            while (bytesRead >= 0) {
                if (totalBytes > 0.0) {
                    indicator.setFraction(totalBytesDownloaded / totalBytes);
                }
                totalBytesDownloaded += bytesRead;

                partFileOut.write(buffer, 0, bytesRead);
                bytesRead = inputStream.read(buffer);
            }

            connection.disconnect();
            partFileOut.close();
            inputStream.close();

            java.nio.file.Files.move(partFile.toPath(), targetFile.toPath(), StandardCopyOption.ATOMIC_MOVE);
            if (!SystemInfo.isWindows) {
                //noinspection ResultOfMethodCallIgnored
                targetFile.setExecutable(true);
            }

            LOG.info(targetFile.getName() + " downloaded to " + targetFile.toPath().getParent());
            Notifications.Bus.notify(new ORNotification("Reason", "Downloaded " + targetFile, NotificationType.INFORMATION));

            return true;
        } catch (IOException e) {
            Notifications.Bus.notify(new ORNotification("Reason", "Can't download " + targetFile + "\n" + e, NotificationType.ERROR));
            return false;
        }
    }
}
