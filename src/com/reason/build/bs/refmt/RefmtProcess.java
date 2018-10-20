package com.reason.build.bs.refmt;


import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.reason.Streams;
import com.reason.build.bs.ModuleConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.*;

public class RefmtProcess implements ProjectComponent {

    private final static Logger LOG = Logger.getInstance("ReasonML.refmt");
    private final Project m_project;

    public RefmtProcess(Project project) {
        m_project = project;
    }

    public static RefmtProcess getInstance(Project project) {
        return project.getComponent(RefmtProcess.class);
    }

    public boolean isOnSaveEnabled() {
        return ModuleConfiguration.isOnSaveEnabled(m_project);
    }

    public String run(@NotNull VirtualFile sourceFile, @NotNull String format, @NotNull String code) {
        return convert(sourceFile, format, format, code);
    }

    public String convert(@NotNull VirtualFile sourceFile, @NotNull String fromFormat, @NotNull String toFormat, @NotNull String code) {
        String refmtPath = ModuleConfiguration.getRefmtPath(m_project, sourceFile);
        if (refmtPath == null) {
            LOG.debug("No refmt binary found, reformat cancelled");
            return code;
        }

        String columnsWidth = ModuleConfiguration.getRefmtWidth(m_project);
        ProcessBuilder processBuilder = new ProcessBuilder(refmtPath, "--parse", fromFormat, "--print", toFormat, "-w", columnsWidth);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Reformating " + sourceFile.getPath() + " (" + fromFormat + " -> " + toFormat + ") using " + columnsWidth + "cols for project [" + m_project + "]");
        }

        Process refmt = null;
        try {
            refmt = processBuilder.start();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(refmt.getOutputStream()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(refmt.getInputStream()));
            BufferedReader errReader = new BufferedReader(new InputStreamReader(refmt.getErrorStream()));

            writer.write(code);
            writer.close();
            Streams.waitUntilReady(reader, errReader);

            StringBuilder msgBuffer = new StringBuilder();
            if (!errReader.ready()) {
                reader.lines().forEach(line -> msgBuffer.append(line).append('\n'));
                String newText = msgBuffer.toString();
                if (!code.isEmpty() && !newText.isEmpty()) { // additional protection
                    return newText;
                }
            }
        } catch (IOException | RuntimeException e) {
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
