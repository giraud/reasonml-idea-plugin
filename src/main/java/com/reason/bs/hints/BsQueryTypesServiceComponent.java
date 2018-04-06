package com.reason.bs.hints;

import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.reason.Platform;
import com.reason.Streams;
import com.reason.bs.BucklescriptProjectComponent;
import com.reason.ide.RmlNotification;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

// WARNING... THIS IS A BIG WIP...
public class BsQueryTypesServiceComponent implements BsQueryTypesService {

    private final Logger m_log;
    private final Project m_project;
    private final VirtualFile m_baseDir;
    private final String m_bsbPath;

    public BsQueryTypesServiceComponent(Project project, VirtualFile baseDir, String bsbPath) {
        m_log = Logger.getInstance("ReasonML.types");
        m_project = project;
        m_baseDir = baseDir;
        m_bsbPath = bsbPath;
    }

    @Nullable
    @Override
    public InferredTypes types(VirtualFile file) {
        InferredTypesImplementation result = null;

        // Find corresponding cmi file... wip
        String filePath = file.getCanonicalPath();
        if (filePath != null) {
            String namespace = BucklescriptProjectComponent.getInstance(m_project).getNamespace();
            String cmiFilename = Platform.removeProjectDir(m_project, filePath).replace(file.getPresentableName(), file.getNameWithoutExtension() + (namespace.isEmpty() ? "" : "-" + namespace) + ".cmi");
            VirtualFile cmiFile = m_baseDir.findFileByRelativePath("lib/bs" + cmiFilename);
            if (cmiFile == null) {
                m_log.warn("can't read types for " + filePath + ", cmi not found: " + cmiFilename);
            } else {
                ProcessBuilder m_bscProcessBuilder = new ProcessBuilder(m_bsbPath, cmiFile.getCanonicalPath());
                String basePath = m_baseDir.getCanonicalPath();
                if (basePath != null) {
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
                        }
                    } catch (Exception e) {
                        m_log.error("An error occurred when reading types", e);
                    } finally {
                        if (bsc != null) {
                            bsc.destroy();
                        }
                    }
                }
            }
        }

        return result;
    }
}
