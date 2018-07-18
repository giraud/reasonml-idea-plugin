package com.reason.hints;

import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.vfs.VirtualFile;
import com.reason.Streams;
import com.reason.build.bs.ModuleConfiguration;
import com.reason.ide.RmlNotification;
import com.reason.ide.hints.InferredTypesImplementation;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class RincewindProcess {

    private final Logger m_log;
    private final ModuleConfiguration m_moduleConfiguration;


    RincewindProcess(@NotNull ModuleConfiguration moduleConfiguration) {
        m_moduleConfiguration = moduleConfiguration;
        m_log = Logger.getInstance("ReasonML.types.rincewind");
    }

    public void types(@NotNull VirtualFile sourceFile, @NotNull String rincewindBinary, @NotNull String cmiPath, @NotNull InsightManager.ProcessTerminated runAfter) {
        ProcessBuilder processBuilder = new ProcessBuilder(rincewindBinary, cmiPath);
        processBuilder.directory(new File(m_moduleConfiguration.getBasePath(sourceFile)));

        Process rincewind = null;
        try {
            rincewind = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(rincewind.getInputStream()));
            BufferedReader errReader = new BufferedReader(new InputStreamReader(rincewind.getErrorStream()));

            Streams.waitUntilReady(reader, errReader);
            StringBuilder msgBuffer = new StringBuilder();
            if (errReader.ready()) {
                errReader.lines().forEach(line -> msgBuffer.append(line).append(System.lineSeparator()));
                Notifications.Bus.notify(new RmlNotification("Code lens", msgBuffer.toString(), NotificationType.ERROR));
            } else {
                final InferredTypesImplementation types = new InferredTypesImplementation();

                reader.lines().forEach(line -> {
                    if (!line.isEmpty()) {
                        //System.out.println(line);
                        types.add(line.split("\\|"));
                    }
                });

                runAfter.run(types);
            }
        } catch (Exception e) {
            m_log.error("An error occurred when reading types", e);
        } finally {
            if (rincewind != null) {
                rincewind.destroy();
            }
        }
    }
}
