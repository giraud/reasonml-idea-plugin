package com.reason.bs;

import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.reason.Platform;
import com.reason.Streams;
import com.reason.ide.RmlNotification;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class BscQueryTypesServiceComponent implements BscQueryTypesService {

    @Nullable
    private String m_bscBin;

    public BscQueryTypesServiceComponent() {
        m_bscBin = Platform.getBinary("REASON_BSB_BIN", "reasonBsb", "node_modules/bs-platform/bin/bsb.exe");
        if (m_bscBin != null) {
            // Use bsc, not bsb
            m_bscBin = m_bscBin.replace("bsb.exe", "bsc.exe");
        }
    }

    @Override
    public Map<String, String> types(Project project, VirtualFile file) {
        Map<String, String> result = new HashMap<>();

        VirtualFile baseDir = Platform.findBaseRoot(project);
        String bscPath = Platform.getBinaryPath(project, m_bscBin); // in project aware component ?

        if (bscPath != null && baseDir != null) {
            // Find corresponding cmi file... wip
            String filePath = file.getCanonicalPath();
            String replace = filePath.substring(baseDir.getPath().length()).replace(file.getPresentableName(), file.getNameWithoutExtension() + ".cmi");
            VirtualFile cmiFile = baseDir.findFileByRelativePath("lib/bs" + replace);
            if (cmiFile == null) {
                return result;
            }

            ProcessBuilder m_bscProcessBuilder = new ProcessBuilder(bscPath, "-dtypedtree", cmiFile.getCanonicalPath());
            m_bscProcessBuilder.directory(new File(baseDir.getCanonicalPath()));

            Process bsc = null;
            try {
                bsc = m_bscProcessBuilder.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(bsc.getInputStream()));
                BufferedReader errReader = new BufferedReader(new InputStreamReader(bsc.getErrorStream()));

                Streams.waitUntilReady(reader, errReader);
                StringBuilder msgBuffer = new StringBuilder();
                if (errReader.ready()) {
                    errReader.lines().forEach(line -> msgBuffer.append(line).append(System.lineSeparator()));
                    Notifications.Bus.notify(new RmlNotification("Reformat", msgBuffer.toString(), NotificationType.ERROR));
                } else {
                    reader.lines().forEach(line -> {
                        msgBuffer.append(line).append('\n');
                        if (line.startsWith("val")) {
                            System.out.println(line);
                            // first level let detected
                            String[] tokens = line.split(":", 1);
                            if (tokens.length == 2) {
                                result.put(tokens[0].substring(4, tokens[0].length() - 1), tokens[1]);
                            }
                        }
                    });
                    String newText = msgBuffer.toString();
                    System.out.println(newText);
                }
            } catch (Exception e) {
                e.printStackTrace(); // no ! nothing in fact
            } finally {
                if (bsc != null) {
                    bsc.destroy();
                }
            }
        }

        return result;
    }
}
