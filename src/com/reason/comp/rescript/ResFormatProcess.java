package com.reason.comp.rescript;

import com.intellij.openapi.editor.ex.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.reason.*;
import org.jetbrains.annotations.*;

import java.io.*;

import static com.reason.Platform.*;

public class ResFormatProcess {
    private static final Log LOG = Log.create("format.rescript");

    private final Project m_project;

    public ResFormatProcess(@NotNull Project project) {
        m_project = project;
    }

    @NotNull
    public String format(@NotNull VirtualFile sourceFile, boolean isInterface, @NotNull String code) {
        VirtualFile fmtBin = ResPlatform.findBscExecutable(m_project, sourceFile);
        if (fmtBin == null) {
            LOG.debug("No rescript binary found, format cancelled");
            return code;
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Formatting " + sourceFile.getPath() + " for project [" + m_project + "]");
        }

        try (AutoDeletingTempFile tempFile = new AutoDeletingTempFile("fileToFormat", isInterface ? ".resi" : ".res")) {
            tempFile.write(code);

            ProcessBuilder processBuilder = new ProcessBuilder(fmtBin.getPath(), "-format", tempFile.getPath());
            Process fmt = null;
            try {
                fmt = processBuilder.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(fmt.getInputStream(), UTF8));
                BufferedReader errReader = new BufferedReader(new InputStreamReader(fmt.getErrorStream(), UTF8));

                Streams.waitUntilReady(reader, errReader);
                final boolean[] empty = {true};

                StringBuilder msgBuffer = new StringBuilder();
                if (!errReader.ready()) {
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
                if (fmt != null) {
                    fmt.destroyForcibly();
                }
            }
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }

        // Something bad happened, do nothing
        return code;
    }
}
