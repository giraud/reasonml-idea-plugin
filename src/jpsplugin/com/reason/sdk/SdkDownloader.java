package jpsplugin.com.reason.sdk;

import com.intellij.notification.*;
import com.intellij.openapi.progress.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.util.*;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.*;
import com.intellij.util.io.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.util.zip.*;

import static com.intellij.notification.NotificationType.*;

public class SdkDownloader {
    private static final Condition<String> KEEP_OCAML_SOURCES = s ->
            s.endsWith(".ml") || s.endsWith(".mli") || s.endsWith(".ml4") || s.endsWith(".mll") || s.endsWith(".mly");

    private final @NotNull String m_sdk;
    private final @NotNull String m_major;
    private final @NotNull File m_sdkHome;

    public SdkDownloader(@NotNull String major, @NotNull String minor, @NotNull String patch, @NotNull VirtualFile sdkHome) {
        String canonicalPath = sdkHome.getCanonicalPath();
        assert canonicalPath != null;
        m_sdkHome = new File(canonicalPath);
        m_sdk = major + "." + minor + "." + patch;
        m_major = major + "." + minor;
    }

    public static @NotNull Task modalTask(@NotNull String major, @NotNull String minor, @NotNull String patch, @NotNull VirtualFile sdkHome, @Nullable Project project) {
        return new Task.Modal(project, "Download SDK", false) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                new SdkDownloader(major, minor, patch, sdkHome).run(project, indicator);
            }
        };
    }

    public void run(@Nullable Project project, @NotNull ProgressIndicator indicator) {
        String sdkFilename = "ocaml-" + m_sdk + ".tar.gz";
        File targetSdkLocation = new File(m_sdkHome, sdkFilename);
        String sdkUrl = "http://caml.inria.fr/pub/distrib/ocaml-" + m_major + "/" + sdkFilename;

        indicator.setIndeterminate(true);
        indicator.setText("Download sdk from " + sdkUrl);

        boolean isDownloaded = WGet.apply(sdkUrl, targetSdkLocation, indicator, -1.0);
        if (isDownloaded) {
            try {
                indicator.setText("Uncompress SDK");
                File tarPath = uncompress(targetSdkLocation);
                FileUtil.delete(targetSdkLocation);
                indicator.setText("Untar SDK");
                new Decompressor.Tar(tarPath).filter(KEEP_OCAML_SOURCES).extract(m_sdkHome);
                FileUtil.delete(tarPath);
            } catch (IOException e) {
                Notifications.Bus.notify(
                        new ORNotification("Sdk", "Cannot download sdk, error: " + e.getMessage(), ERROR, null),
                        project);
            }
        }
    }

    @NotNull
    private File uncompress(@NotNull File source) throws IOException {
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
