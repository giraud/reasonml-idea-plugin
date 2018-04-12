package com.reason.ide.format;


import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.reason.Platform;
import com.reason.Streams;
import com.reason.ide.settings.ReasonSettings;
import org.jetbrains.annotations.NotNull;

import java.io.*;

class RefmtProcess {

    private final Logger m_log;
    private String m_refmtBin;

    RefmtProcess() {
        m_log = Logger.getInstance("ReasonML.refmt");
    }

    String run(Project project, String format, String code) {
        String refmtPath = getRefmtBin(project);
        if (refmtPath == null) {
            return code;
        }

        String columnsWidth = ReasonSettings.getInstance().getRefmtWidth();
        ProcessBuilder processBuilder = new ProcessBuilder(refmtPath, "--parse", format, "--print", format, "-w", columnsWidth);

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
            if (errReader.ready()) {
                errReader.lines().forEach(line -> msgBuffer.append(line).append(System.lineSeparator()));
                m_log.warn(msgBuffer.toString());
            } else {
                reader.lines().forEach(line -> msgBuffer.append(line).append('\n'));
                String newText = msgBuffer.toString();
                if (!code.isEmpty() && !newText.isEmpty()) { // additional protection
                    return newText;
                }
            }
        } catch (IOException | RuntimeException e) {
            m_log.error(e.getMessage());
        } finally {
            if (refmt != null) {
                refmt.destroyForcibly();
            }
        }

        // Something bad happened, do nothing
        return code;
    }

    private String getRefmtBin(Project project) {
        if (m_refmtBin != null) {
            return m_refmtBin;
        }

        m_refmtBin = Platform.getBinary("REASON_REFMT_BIN", "reasonRefmt");
        if (m_refmtBin == null) {
            m_refmtBin = getRefmtBin(project, "/lib");
            if (m_refmtBin == null) {
                m_refmtBin = getRefmtBin(project, "/bin");
            }
        }

        return m_refmtBin;
    }

    private String getRefmtBin(Project project, @NotNull String root) {
        String BS_PATH = "node_modules/bs-platform";
        String binary = Platform.getBinaryPath(project, BS_PATH + root + "/refmt3.exe");
        if (binary == null) {
            binary = Platform.getBinaryPath(project, BS_PATH + root + "/refmt.exe");
        }
        return binary;
    }
}
