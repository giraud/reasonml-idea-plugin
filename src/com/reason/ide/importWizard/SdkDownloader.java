package com.reason.ide.importWizard;

import com.intellij.notification.Notifications;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.util.io.Decompressor;
import com.reason.Platform;
import com.reason.hints.WGet;
import com.reason.ide.ORNotification;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

import static com.intellij.notification.NotificationType.ERROR;

class SdkDownloader extends Task.Modal {

    private static Condition<String> KEEP_OCAML_SOURCES = s -> s.endsWith(".ml") || s.endsWith(".mli") || s.endsWith(".ml4") || s.endsWith(".mll") || s.endsWith(".mly");

    private final String m_sdk;
    private final String m_major;

    SdkDownloader(@NotNull String major, @NotNull String minor, @Nullable Project project) {
        super(project, "Downloading SDK", true);
        m_sdk = major + "." + minor;
        m_major = major;
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        File pluginLocation = Platform.getPluginLocation();

        String sdkFilename = "ocaml-" + m_sdk + ".tar.gz";
        File targetSdkLocation = new File(pluginLocation, sdkFilename);
        String sdkUrl = "http://caml.inria.fr/pub/distrib/ocaml-" + m_major + "/" + sdkFilename;

        indicator.setIndeterminate(true);
        indicator.setText("Download sdk from " + sdkUrl);

        boolean isDownloaded = WGet.apply(sdkUrl, targetSdkLocation, indicator, -1.0);
        if (isDownloaded) {
            try {
                indicator.setText("uncompress sdk");
                File tarPath = uncompress(targetSdkLocation);
                FileUtil.delete(targetSdkLocation);
                indicator.setText("Untar sdk");
                new Decompressor.Tar(tarPath).filter(KEEP_OCAML_SOURCES).extract(pluginLocation);
                FileUtil.delete(tarPath);
            } catch (IOException e) {
                Notifications.Bus.notify(new ORNotification("Sdk", "Cannot download sdk, error: " + e.getMessage(), ERROR, null), getProject());
            }
        }
    }

    private File uncompress(File source) throws IOException {
        byte[] buffer = new byte[1024];

        String absolutePath = source.getAbsolutePath();
        String tarName = absolutePath.substring(0, absolutePath.length() - 3);

        try (GZIPInputStream is = new GZIPInputStream(new FileInputStream(source))) {
            try (FileOutputStream out = new FileOutputStream(tarName)) {
                int len;
                while ((len = is.read(buffer)) > 0) {
                    out.write(buffer, 0, len);
                }
            }
        }

        return new File(tarName);
    }

}
