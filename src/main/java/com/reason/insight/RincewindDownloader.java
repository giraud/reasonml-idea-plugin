package com.reason.insight;


import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.atomic.AtomicBoolean;

public class RincewindDownloader extends Task.Backgroundable {

    public static final int BUFFER_SIZE = 1024;
    public AtomicBoolean isDownloaded = new AtomicBoolean(false);

    private static RincewindDownloader INSTANCE;
    private final Logger m_log = Logger.getInstance("ReasonML");
    private final String m_osPrefix;

    public static RincewindDownloader getInstance(Project project, String osPrefix) {
        if (INSTANCE == null) {
            INSTANCE = new RincewindDownloader(project, osPrefix);
        }
        return INSTANCE;
    }

    private RincewindDownloader(@Nullable Project project, String osPrefix) {
        super(project, "Downloading rincewind binary");
        m_osPrefix = osPrefix;
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        indicator.setFraction(0.0);

        try {
            double totalBytes = 5_000_000.0;
            InsightManager insightManager = myProject.getComponent(InsightManager.class);

            // some code
            File targetFile = insightManager.getRincewindFile(m_osPrefix);
            File partFile = new File(targetFile.getPath() + ".part");

            if (partFile.exists()) {
                partFile.delete();
            }

            partFile.createNewFile();

            FileOutputStream partFileOut = new FileOutputStream(partFile);

            java.net.URL url = new URL("https://dl.bintray.com/giraud/ocaml/" + insightManager.getRincewindFilename(m_osPrefix));
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
                indicator.setFraction(totalBytesDownloaded / totalBytes);
                totalBytesDownloaded += bytesRead;

                partFileOut.write(buffer, 0, bytesRead);
                bytesRead = inputStream.read(buffer);
            }

            connection.disconnect();
            partFileOut.close();
            inputStream.close();

            java.nio.file.Files.move(partFile.toPath(), targetFile.toPath(), StandardCopyOption.ATOMIC_MOVE);

            m_log.info(targetFile.getName() + " downloaded to " + targetFile.toPath().getParent());
        } catch (IOException e) {
            m_log.error(e);
        }

        indicator.setFraction(1.0);
    }
}
