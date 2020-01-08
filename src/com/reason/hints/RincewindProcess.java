package com.reason.hints;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.reason.Log;
import com.reason.Platform;
import com.reason.Streams;
import com.reason.ide.ORNotification;
import com.reason.ide.hints.InferredTypesImplementation;

public class RincewindProcess {

    private final static Log LOG = Log.create("hints.rincewind");

    private final Project m_project;

    public static RincewindProcess getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, RincewindProcess.class);
    }

    RincewindProcess(Project project) {
        m_project = project;
    }

    public void types(@NotNull VirtualFile sourceFile, @NotNull String rincewindBinary, @NotNull String cmiPath,
                      @NotNull InsightManager.ProcessTerminated runAfter) {
        LOG.debug("Looking for types for file", sourceFile);

        VirtualFile contentRoot = Platform.findORPackageJsonContentRoot(m_project, sourceFile);
        if (contentRoot == null) {
            return;
        }

        ProcessBuilder processBuilder = new ProcessBuilder(rincewindBinary, cmiPath);
        processBuilder.directory(new File(contentRoot.getPath()));

        Process rincewind = null;
        try {
            rincewind = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(rincewind.getInputStream()));
            BufferedReader errReader = new BufferedReader(new InputStreamReader(rincewind.getErrorStream()));

            //System.out.println("---");
            Streams.waitUntilReady(reader, errReader);
            StringBuilder msgBuffer = new StringBuilder();
            if (errReader.ready()) {
                errReader.lines().forEach(line -> msgBuffer.append(line).append(System.lineSeparator()));
                Notifications.Bus.notify(new ORNotification("Code lens", msgBuffer.toString(), NotificationType.ERROR));
            } else {
                final InferredTypesImplementation types = new InferredTypesImplementation();

                reader.lines().forEach(line -> {
                    if (!line.isEmpty()) {
                        LOG.trace(line);
                        int entryPos = line.indexOf("|");
                        String entry = line.substring(0, entryPos);
                        if (!"__".equals(entry)) {
                            int locPos = line.indexOf("|", entryPos + 1);
                            String[] loc = line.substring(entryPos + 1, locPos).split(",");
                            types.add(m_project, entry, decodePosition(loc[0]), decodePosition(loc[1]), line.substring(locPos + 1));
                        }
                    }
                });

                runAfter.run(types);
            }
        } catch (Exception e) {
            LOG.error("An error occurred when reading types", e);
        } finally {
            if (rincewind != null) {
                rincewind.destroy();
            }
        }
    }

    @NotNull
    public String dumpTree(@NotNull VirtualFile sourceFile, @NotNull String rincewindBinary, @NotNull String cmtPath) {
        LOG.debug("Dumping file", sourceFile);

        VirtualFile contentRoot = Platform.findORPackageJsonContentRoot(m_project, sourceFile);
        if (contentRoot != null) {
            ProcessBuilder processBuilder = new ProcessBuilder(rincewindBinary, "-d", cmtPath);
            processBuilder.directory(new File(contentRoot.getPath()));

            Process rincewind = null;
            try {
                rincewind = processBuilder.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(rincewind.getInputStream()));
                BufferedReader errReader = new BufferedReader(new InputStreamReader(rincewind.getErrorStream()));

                Streams.waitUntilReady(reader, errReader);
                StringBuilder msgBuffer = new StringBuilder();
                if (errReader.ready()) {
                    errReader.lines().forEach(line -> msgBuffer.append(line).append(System.lineSeparator()));
                    Notifications.Bus.notify(new ORNotification("Rincewind", msgBuffer.toString(), NotificationType.ERROR));
                } else {
                    final StringBuilder dump = new StringBuilder();
                    reader.lines().forEach(s -> dump.append(s).append("\n"));
                    return dump.toString();
                }
            } catch (Exception e) {
                LOG.error("An error occurred when reading types", e);
            } finally {
                if (rincewind != null) {
                    rincewind.destroy();
                }
            }
        }

        return "<unknown/>";
    }

    @NotNull
    public List<String> dumpTypes(@NotNull String rincewindBinary, @NotNull VirtualFile cmtFile) {
        LOG.debug("Dumping types", cmtFile);

        final List<String> dump = new ArrayList<>();


        VirtualFile contentRoot = Platform.findORPackageJsonContentRoot(m_project, cmtFile);
        if (contentRoot != null) {
            Path cmtPath = FileSystems.getDefault().getPath(cmtFile.getPath());

            ProcessBuilder processBuilder = new ProcessBuilder(rincewindBinary, cmtPath.toString());
            processBuilder.directory(new File(contentRoot.getPath()));

            Process rincewind = null;
            try {
                rincewind = processBuilder.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(rincewind.getInputStream()));
                BufferedReader errReader = new BufferedReader(new InputStreamReader(rincewind.getErrorStream()));

                Streams.waitUntilReady(reader, errReader);
                StringBuilder msgBuffer = new StringBuilder();
                if (errReader.ready()) {
                    errReader.lines().forEach(line -> msgBuffer.append(line).append(System.lineSeparator()));
                    Notifications.Bus.notify(new ORNotification("Rincewind", msgBuffer.toString(), NotificationType.ERROR));
                } else {
                    reader.lines().forEach(dump::add);
                }
            } catch (Exception e) {
                LOG.error("An error occurred when reading types", e);
            } finally {
                if (rincewind != null) {
                    rincewind.destroy();
                }
            }
        }

        return dump;
    }

    @NotNull
    public List<String> dumpMeta(@NotNull String rincewindBinary, @NotNull VirtualFile cmtFile) {
        LOG.debug("Dumping types", cmtFile);

        final List<String> dump = new ArrayList<>();

        VirtualFile contentRoot = Platform.findORPackageJsonContentRoot(m_project, cmtFile);
        if (contentRoot != null) {
            Path cmtPath = FileSystems.getDefault().getPath(cmtFile.getPath());

            ProcessBuilder processBuilder = new ProcessBuilder(rincewindBinary, "-m", cmtPath.toString());
            processBuilder.directory(new File(contentRoot.getPath()));

            Process rincewind = null;
            try {
                rincewind = processBuilder.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(rincewind.getInputStream()));
                BufferedReader errReader = new BufferedReader(new InputStreamReader(rincewind.getErrorStream()));

                Streams.waitUntilReady(reader, errReader);
                StringBuilder msgBuffer = new StringBuilder();
                if (errReader.ready()) {
                    errReader.lines().forEach(line -> msgBuffer.append(line).append(System.lineSeparator()));
                    Notifications.Bus.notify(new ORNotification("Rincewind", msgBuffer.toString(), NotificationType.ERROR));
                } else {
                    reader.lines().forEach(dump::add);
                }
            } catch (Exception e) {
                LOG.error("An error occurred when reading types", e);
            } finally {
                if (rincewind != null) {
                    rincewind.destroy();
                }
            }
        }

        return dump;
    }

    @NotNull
    private LogicalPosition decodePosition(@NotNull String location) {
        String[] pos = location.split("\\.");
        int line = Integer.parseInt(pos[0]) - 1;
        int column = Integer.parseInt(pos[1]);
        return new LogicalPosition(Math.max(line, 0), Math.max(column, 0));
    }
}
