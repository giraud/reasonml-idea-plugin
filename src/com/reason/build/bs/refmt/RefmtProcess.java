package com.reason.build.bs.refmt;


import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.reason.Streams;
import com.reason.build.bs.ModuleConfiguration;
import com.reason.ide.settings.ReasonSettings;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.Charset;

public class RefmtProcess implements ProjectComponent {

    private static final Charset UTF8 = Charset.forName("UTF-8");
    private static final Logger LOG = Logger.getInstance("ReasonML.refmt");

    private final Project m_project;

    public RefmtProcess(Project project) {
        m_project = project;
    }

    public static RefmtProcess getInstance(Project project) {
        return project.getComponent(RefmtProcess.class);
    }

    @NotNull
    public String run(@NotNull VirtualFile sourceFile, boolean isInterface, @NotNull String format, @NotNull String code) {
        return convert(sourceFile, isInterface, format, format, code);
    }

    @NotNull
    public String convert(@NotNull VirtualFile sourceFile, boolean isInterface, @NotNull String fromFormat, @NotNull String toFormat, @NotNull String code) {
        String refmtPath = ModuleConfiguration.getRefmtPath(m_project, sourceFile);
        if (refmtPath == null) {
            LOG.debug("No refmt binary found, reformat cancelled");
            return code;
        }

        String columnsWidth = ReasonSettings.getInstance(m_project).getRefmtWidth();
        ProcessBuilder processBuilder = new ProcessBuilder(refmtPath, "--interface", Boolean.toString(isInterface), "--parse", fromFormat, "--print", toFormat, "-w", columnsWidth);
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
            LOG.error(e.getMessage());
        } finally {
            if (refmt != null) {
                refmt.destroyForcibly();
            }
        }

        // Something bad happened, do nothing
        return code;
    }

    //region Compatibility
    @Override
    public void initComponent() { // For compatibility with idea#143
    }

    @Override
    public void disposeComponent() { // For compatibility with idea#143
    }
    //endregion
}
