package com.reason.hints;


import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.reason.Log;
import com.reason.ide.ORNotification;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.StandardCopyOption;

public class RincewindDownloader extends Task.Backgroundable {

    private static final double TOTAL_BYTES = 5_000_000.0;
    private static final int BUFFER_SIZE = 1024;
    private static final String DOWNLOAD_URL = "https://dl.bintray.com/giraud/ocaml/";
    private static final Log LOG = Log.create("hints");

    private static RincewindDownloader INSTANCE;
    private final VirtualFile m_sourceFile;

    public static RincewindDownloader getInstance(@NotNull Project project, @NotNull VirtualFile sourceFile) {
        if (INSTANCE == null) {
            INSTANCE = new RincewindDownloader(project, sourceFile);
        }
        return INSTANCE;
    }

    private RincewindDownloader(@Nullable Project project, @NotNull VirtualFile sourceFile) {
        //noinspection DialogTitleCapitalization
        super(project, "Downloading Rincewind binary");
        m_sourceFile = sourceFile;
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        if (myProject.isDisposed()) {
            LOG.debug("Project is disposed, can't download rincewind");
            return;
        }

        InsightManagerImpl insightManager = (InsightManagerImpl) ServiceManager.getService(myProject, InsightManager.class);
        if (!insightManager.isDownloaded.get() && !insightManager.isDownloading.compareAndSet(false, true)) {
            // We are already in the process of downloading
            LOG.debug("Already downloading, abort");
            return;
        }

        try {
            File targetFile = insightManager.getRincewindFile(m_sourceFile);
            if (targetFile == null) {
                LOG.debug("No target file, abort downloading");
                return;
            }

            LOG.info("Downloading " + targetFile.getName() + "...");
            indicator.setIndeterminate(false);
            indicator.setFraction(0.0);

            boolean downloaded = WGet.apply(DOWNLOAD_URL + insightManager.getRincewindFilename(m_sourceFile), targetFile, indicator, TOTAL_BYTES);
            if (downloaded) {
                insightManager.isDownloaded.set(true);

                Application application = ApplicationManager.getApplication();
                application.invokeLater(() -> application.runWriteAction(() -> {
                    VirtualFileManager.getInstance().syncRefresh();
                }));
            }

            indicator.setFraction(1.0);
        } finally {
            insightManager.isDownloading.set(false);
        }
    }
}
