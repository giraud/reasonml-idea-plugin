package com.reason.comp.bs;

import com.intellij.openapi.components.*;
import com.intellij.openapi.editor.ex.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.reason.comp.rescript.*;
import com.reason.ide.settings.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.io.*;

import static jpsplugin.com.reason.Platform.*;

@Service(Service.Level.PROJECT)
public final class BsFormatProcess {
    private static final Log LOG = Log.create("format.refmt");

    private final Project m_project;

    public BsFormatProcess(@NotNull Project project) {
        m_project = project;
    }

    @NotNull
    public String convert(@NotNull VirtualFile sourceFile, boolean isInterface, @NotNull String fromFormat, @NotNull String toFormat, @NotNull String code) {
        VirtualFile refmtDir = BsPlatform.findRefmtExecutable(m_project, sourceFile);
        if (refmtDir == null) {
            refmtDir = ResPlatform.findRefmtExecutable(m_project, sourceFile);
            if (refmtDir == null) {
                LOG.debug("No refmt binary found, reformat cancelled");
                return code;
            }
        }

        String columnsWidth = m_project.getService(ORSettings.class).getFormatColumnWidth();
        ProcessBuilder processBuilder =
                new ProcessBuilder(refmtDir.getPath(), "-i", Boolean.toString(isInterface), "--parse=" + fromFormat, "-p", toFormat, "-w", columnsWidth);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Reformatting " + sourceFile.getPath() + " (" + fromFormat + " -> " + toFormat + ") using " + columnsWidth + " cols for project [" + m_project + "]");
        }

        Process refmt = null;
        try {
            refmt = processBuilder.start();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(refmt.getOutputStream(), UTF8));
            BufferedReader reader = new BufferedReader(new InputStreamReader(refmt.getInputStream(), UTF8));
            BufferedReader errReader = new BufferedReader(new InputStreamReader(refmt.getErrorStream(), UTF8));

            writer.write(code);
            writer.flush();
            writer.close();

            Streams.waitUntilReady(reader, errReader);

            StringBuilder msgBuffer = new StringBuilder();
            if (!errReader.ready()) {
                final boolean[] empty = {true};
                reader
                        .lines()
                        .forEach(
                                line -> {
                                    if (empty[0]) {
                                        empty[0] = false;
                                    } else {
                                        msgBuffer.append('\n');
                                    }
                                    msgBuffer.append(line);
                                });
                String newText = msgBuffer.toString();
                if (!code.isEmpty() && !newText.isEmpty()) { // additional protection
                    boolean ensureNewLineAtEOF = EditorSettingsExternalizable.getInstance().isEnsureNewLineAtEOF();
                    boolean addNewLine = ensureNewLineAtEOF && newText.charAt(newText.length() - 1) != '\n';
                    return addNewLine ? newText + '\n' : newText;
                }
            }
        } catch (IOException | RuntimeException e) {
            LOG.warn(e);
        } finally {
            if (refmt != null) {
                refmt.destroyForcibly();
            }
        }

        // Something bad happened, do nothing
        return code;
    }
}
