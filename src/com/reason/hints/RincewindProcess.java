package com.reason.hints;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.*;
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

        VirtualFile contentRoot = Platform.findAncestorContentRoot(m_project, sourceFile);
        if (contentRoot == null) {
            return;
        }

        if (!new File(rincewindBinary).exists()) { // to a
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
    public String dumpTree(@NotNull VirtualFile cmtFile, @NotNull String rincewindBinary) {
        LOG.debug("Dumping tree", cmtFile);

        final StringBuilder dump = new StringBuilder();
        dumper(rincewindBinary, cmtFile, "-d", dump::append);

        return dump.toString();
    }

    @NotNull
    public List<String> dumpTypes(@NotNull String rincewindBinary, @NotNull VirtualFile cmtFile) {
        LOG.debug("Dumping types", cmtFile);

        final List<String> dump = new ArrayList<>();
        dumper(rincewindBinary, cmtFile, "", dump::add);

        return dump;
    }

    @NotNull
    public List<String> dumpMeta(@NotNull String rincewindBinary, @NotNull VirtualFile cmtFile) {
        LOG.debug("Dumping meta", cmtFile);

        final List<String> dump = new ArrayList<>();
        dumper(rincewindBinary, cmtFile, "-m", dump::add);

        return dump;
    }

    interface DumpVisitor {
        void visitLine(String line);
    }

    public void dumper(@NotNull String rincewindBinary, @NotNull VirtualFile cmtFile, String arg, DumpVisitor visitor) {
        VirtualFile contentRoot = Platform.findAncestorContentRoot(m_project, cmtFile);
        if (contentRoot != null) {
            Path cmtPath = FileSystems.getDefault().getPath(cmtFile.getPath());

            ProcessBuilder processBuilder = new ProcessBuilder(rincewindBinary, arg, cmtPath.toString());
            processBuilder.directory(new File(contentRoot.getPath()));

            Process rincewind = null;
            try {
                rincewind = processBuilder.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(rincewind.getInputStream()));
                BufferedReader errReader = new BufferedReader(new InputStreamReader(rincewind.getErrorStream()));

                Streams.waitUntilReady(reader, errReader);

                if (errReader.ready()) {
                    StringBuilder msgBuffer = new StringBuilder();
                    errReader.lines().forEach(line -> msgBuffer.append(line).append("\n"));
                    Notifications.Bus.notify(new ORNotification("Rincewind", msgBuffer.toString(), NotificationType.ERROR));
                } else {
                    reader.lines().forEach(line -> visitor.visitLine(line + "\n"));
                }
            } catch (Exception e) {
                LOG.error("An error occurred when dumping cmt file", e);
            } finally {
                if (rincewind != null) {
                    rincewind.destroy();
                }
            }
        }
    }

    @NotNull
    private LogicalPosition decodePosition(@NotNull String location) {
        String[] pos = location.split("\\.");
        int line = Integer.parseInt(pos[0]) - 1;
        int column = Integer.parseInt(pos[1]);
        return new LogicalPosition(Math.max(line, 0), Math.max(column, 0));
    }
}
