package com.reason.ide.format;


import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.reason.Platform;
import com.reason.Streams;
import com.reason.ide.RmlNotification;

import java.io.*;

class RefmtProcess {

    private final String m_refmtBin;
    private final Logger m_log;

    RefmtProcess() {
        m_refmtBin = Platform.getBinary("REASON_REFMT_BIN", "reasonRefmt", "node_modules/bs-platform/bin/refmt3.exe");
        m_log = Logger.getInstance("ReasonML.refmt");
    }

    String run(Project project, String format, String code) {
        String refmtPath = Platform.getBinaryPath(project, m_refmtBin);
        if (refmtPath == null) {
            // Use a watcher ?
            return code;
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
                Notifications.Bus.notify(new RmlNotification("Reformat", msgBuffer.toString(), NotificationType.ERROR));
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
