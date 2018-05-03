package com.reason.bs.refmt;


import com.intellij.openapi.diagnostic.Logger;
import com.reason.Streams;
import com.reason.bs.ModuleConfiguration;
import com.reason.ide.settings.ReasonSettings;

import java.io.*;

public class RefmtProcess {

    private final ModuleConfiguration m_moduleConfiguration;
    private final Logger m_log;

    public RefmtProcess(ModuleConfiguration moduleConfiguration) {
        m_moduleConfiguration = moduleConfiguration;
        m_log = Logger.getInstance("ReasonML.refmt");
    }

    public String run(String format, String code) {
        String refmtPath = m_moduleConfiguration.getRefmtPath();
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

}
