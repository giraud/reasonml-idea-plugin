package com.reason.bs;


import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.reason.Log;
import com.reason.Streams;
import com.reason.ide.settings.ReasonSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;

import static com.intellij.openapi.vfs.StandardFileSystems.FILE_PROTOCOL_PREFIX;
import static com.reason.Platform.*;

public class RefmtProcess {

    private static final Log LOG = Log.create("refmt");

    private final Project m_project;

    public static RefmtProcess getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, RefmtProcess.class);
    }

    public RefmtProcess(Project project) {
        m_project = project;
    }

    @NotNull
    public String run(@NotNull VirtualFile sourceFile, boolean isInterface, @NotNull String format, @NotNull String code) {
        return convert(sourceFile, isInterface, format, format, code);
    }

    @NotNull
    public String convert(@NotNull VirtualFile sourceFile, boolean isInterface, @NotNull String fromFormat, @NotNull String toFormat, @NotNull String code) {
        String refmtPath = getRefmtPath(m_project, sourceFile);
        if (refmtPath == null) {
            LOG.debug("No refmt binary found, reformat cancelled");
            return code;
        }

        String columnsWidth = ReasonSettings.getInstance(m_project).getRefmtWidth();
        ProcessBuilder processBuilder = new ProcessBuilder(refmtPath, "-i", Boolean.toString(isInterface), "--parse=" + fromFormat, "-p", toFormat, "-w", columnsWidth);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Reformating " + sourceFile.getPath() + " (" + fromFormat + " -> " + toFormat + ") using " + columnsWidth + " cols for project [" + m_project + "]");
        }

        Process refmt = null;
        try {
            refmt = processBuilder.start();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(refmt.getOutputStream(), UTF8));
            BufferedReader reader = new BufferedReader(new InputStreamReader(refmt.getInputStream(), UTF8));
            BufferedReader errReader = new BufferedReader(new InputStreamReader(refmt.getErrorStream(), UTF8));

            writer.write(code);
            writer.flush();
            writer.close();

            Streams.waitUntilReady(reader, errReader);

            StringBuilder msgBuffer = new StringBuilder();
            if (!errReader.ready()) {
                final boolean[] empty = {true};
                reader.lines().forEach(line -> {
                    if (empty[0]) {
                        empty[0] = false;
                    } else {
                        msgBuffer.append('\n');
                    }
                    msgBuffer.append(line);
                });
                String newText = msgBuffer.toString();
                if (!code.isEmpty() && !newText.isEmpty()) { // additional protection
                    return newText;
                }
            }
        } catch (@NotNull IOException | RuntimeException e) {
            LOG.warn(e);
        } finally {
            if (refmt != null) {
                refmt.destroyForcibly();
            }
        }

        // Something bad happened, do nothing
        return code;
    }

    @Nullable
    private String getRefmtPath(@NotNull Project project, @NotNull VirtualFile sourceFile) {
        String workingDir = ReasonSettings.getInstance(project).getWorkingDir(sourceFile);

        String result = getRefmtBin(FILE_PROTOCOL_PREFIX + workingDir + LOCAL_BS_PLATFORM + "/lib");

        if (result == null) {
            result = getRefmtBin(FILE_PROTOCOL_PREFIX + workingDir + LOCAL_BS_PLATFORM + "/bin");
            if (result == null) {
                result = getRefmtBin(FILE_PROTOCOL_PREFIX + workingDir + LOCAL_NODE_MODULES_BIN);
            }
        }

        return result;
    }

    @Nullable
    private String getRefmtBin(@NotNull String path) {
        VirtualFileManager vfManager = VirtualFileManager.getInstance();

        VirtualFile binary = vfManager.findFileByUrl(path + "/refmt.exe");

        if (binary == null) {
            binary = vfManager.findFileByUrl(path + "/refmt3.exe");
            if (binary == null) {
                binary = vfManager.findFileByUrl(path + "/bsrefmt");
            }
        }

        return binary == null ? null : binary.getCanonicalPath();
    }
}
