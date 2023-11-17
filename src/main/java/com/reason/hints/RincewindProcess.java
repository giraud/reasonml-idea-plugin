package com.reason.hints;

import com.intellij.notification.*;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.progress.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.reason.comp.bs.*;
import com.reason.comp.dune.*;
import com.reason.ide.hints.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class RincewindProcess {
    private static final Log LOG = Log.create("hints.rincewind");
    private final Project myProject;

    RincewindProcess(@NotNull Project project) {
        myProject = project;
    }

    public void types(@NotNull VirtualFile sourceFile, @NotNull String rincewindBinary, @NotNull String cmtPath, @NotNull ORProcessTerminated<InferredTypes> runAfter) {
        if (!new File(rincewindBinary).exists()) {
            return;
        }

        LOG.debug("Looking for types for file", sourceFile);

        VirtualFile processDir = DunePlatform.findContentRoot(myProject, sourceFile);
        if (processDir == null) {
            VirtualFile configFile = BsPlatform.findConfigFile(myProject, sourceFile);
            processDir = configFile != null ? configFile.getParent() : null;
        }

        if (processDir == null) {
            LOG.trace("no content root found");
            return;
        }

        ProcessBuilder processBuilder = new ProcessBuilder(rincewindBinary, cmtPath);
        processBuilder.directory(new File(processDir.getPath()));

        Process rincewind = null;
        try {
            rincewind = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(rincewind.getInputStream()));
            BufferedReader errReader = new BufferedReader(new InputStreamReader(rincewind.getErrorStream()));

            // System.out.println("---");
            Streams.waitUntilReady(reader, errReader);
            StringBuilder msgBuffer = new StringBuilder();
            if (errReader.ready()) {
                errReader.lines().forEach(line -> msgBuffer.append(line).append(System.lineSeparator()));
                LOG.warn("Error when processing types");
                LOG.warn(msgBuffer.toString());
            } else {
                final InferredTypesImplementation types = new InferredTypesImplementation();

                reader.lines().forEach(line -> {
                    if (!line.isEmpty()) {
                        LOG.trace(line);
                        int entryPos = line.indexOf("|");
                        String entry = entryPos > 0 ? line.substring(0, entryPos) : line;
                        if (!"__".equals(entry)) {
                            int locPos = line.indexOf("|", entryPos + 1);
                            if (locPos > entryPos) {
                                String[] loc = line.substring(entryPos + 1, locPos).split(",");
                                types.add(
                                        myProject,
                                        entry,
                                        decodePosition(loc[0]),
                                        decodePosition(loc[1]),
                                        line.substring(locPos + 1));
                            }
                        }
                    }
                });

                runAfter.run(types);
            }
        } catch (ProcessCanceledException e) {
            //  Control-flow exceptions (like ProcessCanceledException) should never be logged:
            //  ignore for explicitly started processes or rethrow to handle on the outer process level
            throw e;
        } catch (Exception e) {
            LOG.error("An error occurred when reading types", e);
        } finally {
            if (rincewind != null) {
                rincewind.destroy();
            }
        }
    }

    public @NotNull String dumpTree(@NotNull VirtualFile cmtFile, @NotNull String rincewindBinary) {
        LOG.debug("Dumping tree", cmtFile);

        final StringBuilder dump = new StringBuilder();
        dumper(rincewindBinary, cmtFile, "-d", dump::append);

        return dump.toString();
    }


    public @NotNull List<String> dumpMeta(@NotNull String rincewindBinary, @NotNull VirtualFile cmtFile) {
        LOG.debug("Dumping meta", cmtFile);

        List<String> dump = new ArrayList<>();
        dumper(rincewindBinary, cmtFile, "-m", dump::add);

        return dump;
    }

    public @NotNull List<String> dumpTypes(@NotNull String rincewindBinary, @NotNull VirtualFile cmtFile) {
        LOG.debug("Dumping types", cmtFile);

        final List<String> dump = new ArrayList<>();
        dumper(rincewindBinary, cmtFile, null, dump::add);

        return dump;
    }


    interface DumpVisitor {
        void visitLine(String line);
    }

    public void dumper(@NotNull String rincewindBinary, @NotNull VirtualFile cmtFile, @Nullable String arg, @NotNull DumpVisitor visitor) {
        if (!new File(rincewindBinary).exists()) {
            LOG.debug("File doesn't exists", rincewindBinary);
            return;
        }

        VirtualFile contentRoot = DunePlatform.findContentRoot(myProject, cmtFile);
        if (contentRoot == null) {
            VirtualFile configFile = BsPlatform.findConfigFile(myProject, cmtFile);
            contentRoot = configFile != null ? configFile.getParent() : null;
        }

        if (contentRoot != null) {
            Path cmtPath = FileSystems.getDefault().getPath(cmtFile.getPath());

            ProcessBuilder processBuilder = arg == null
                    ? new ProcessBuilder(rincewindBinary, cmtPath.toString())
                    : new ProcessBuilder(rincewindBinary, arg, cmtPath.toString());
            processBuilder.directory(new File(contentRoot.getPath()));
            if (LOG.isTraceEnabled()) {
                LOG.trace(Joiner.join(" ", processBuilder.command()));
            }

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

    private @NotNull LogicalPosition decodePosition(@NotNull String location) {
        String[] pos = location.split("\\.");
        int line = Integer.parseInt(pos[0]) - 1;
        int column = Integer.parseInt(pos[1]);
        return new LogicalPosition(Math.max(line, 0), Math.max(column, 0));
    }
}
