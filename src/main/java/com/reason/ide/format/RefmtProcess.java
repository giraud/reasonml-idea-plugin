package com.reason.ide.format;


import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.reason.Platform;
import com.reason.Streams;

import java.io.*;

class RefmtProcess {

    private final String BS_PATH = "node_modules/bs-platform";

    private final String m_refmtBin;
    private final Logger m_log;

    RefmtProcess() {
        m_refmtBin = Platform.getBinary("REASON_REFMT_BIN", "reasonRefmt", BS_PATH + "/lib/refmt.exe");
        m_log = Logger.getInstance("ReasonML.refmt");
    }

    String run(Project project, String format, String code) {
        String refmtPath = Platform.getBinaryPath(project, m_refmtBin);
        if (refmtPath == null) {
            // Test old versions
            refmtPath = Platform.getBinaryPath(project, BS_PATH + "/lib/refmt3.exe");
            if (refmtPath == null) {
                refmtPath = Platform.getBinaryPath(project, BS_PATH + "/bin/refmt3.exe");
                if (refmtPath == null) {
                    // Use a watcher ?
                    return code;
                }
            }
        }

        ProcessBuilder processBuilder = new ProcessBuilder(refmtPath, "--parse", format, "--print", format);

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
            if (refmt != null && refmt.isAlive()) {
                refmt.destroy();
            }
        }

        // Something bad happened, do nothing
        return code;
    }
}
