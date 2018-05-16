package com.reason.bs.insight;

import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.vfs.VirtualFile;
import com.reason.Streams;
import com.reason.bs.ModuleConfiguration;
import com.reason.hints.InsightManager;
import com.reason.ide.RmlNotification;
import com.reason.ide.hints.InferredTypesImplementation;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class BsQueryTypesService {

    private final Logger m_log;
    private final ModuleConfiguration m_moduleConfiguration;

    public BsQueryTypesService(ModuleConfiguration moduleConfiguration) {
        m_moduleConfiguration = moduleConfiguration;
        m_log = Logger.getInstance("ReasonML.types");
    }

    public void types(@NotNull String cmiPath, @NotNull InsightManager.ProcessTerminated runAfter) {
        InferredTypesImplementation result = null;

        String basePath = m_moduleConfiguration.getBasePath();

        ProcessBuilder m_bscProcessBuilder = new ProcessBuilder(m_moduleConfiguration.getBscPath(), cmiPath);
        m_bscProcessBuilder.directory(new File(basePath));

        Process bsc = null;
        try {
            bsc = m_bscProcessBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(bsc.getInputStream()));
            BufferedReader errReader = new BufferedReader(new InputStreamReader(bsc.getErrorStream()));

            Streams.waitUntilReady(reader, errReader);
            StringBuilder msgBuffer = new StringBuilder();
            if (errReader.ready()) {
                errReader.lines().forEach(line -> msgBuffer.append(line).append(System.lineSeparator()));
                Notifications.Bus.notify(new RmlNotification("Code lens", msgBuffer.toString(), NotificationType.ERROR));
            } else {
                reader.lines().forEach(line -> {
                    //System.out.println("»" + line + "«");
                    if (line.startsWith("type") || line.startsWith("val") || line.startsWith("module") || line.startsWith("external")) {
                        msgBuffer.append('\n');
                    }
                    msgBuffer.append(line);
                });

                String newText = msgBuffer.toString();
                //System.out.println("---\n" + newText + "\n---");

                result = new InferredTypesImplementation();

                String[] types = newText.split("\n");
                for (String type : types) {
                    result.add(type);
                }

                runAfter.run(result);
            }
        } catch (Exception e) {
            m_log.error("An error occurred when reading types", e);
        } finally {
            if (bsc != null) {
                bsc.destroy();
            }
        }
    }

    public void types(@NotNull VirtualFile cmiFile, @NotNull InsightManager.ProcessTerminated runAfter) {
        types(cmiFile.getPath(), runAfter);
    }
}
