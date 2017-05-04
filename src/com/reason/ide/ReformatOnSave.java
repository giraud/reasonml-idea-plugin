package com.reason.ide;

import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileDocumentManagerAdapter;
import com.intellij.openapi.vfs.VirtualFile;
import com.reason.Platform;
import org.jetbrains.annotations.NotNull;

import java.io.*;

class ReformatOnSave extends FileDocumentManagerAdapter {

    private String refmtBin;

    ReformatOnSave() {
        refmtBin = Platform.getBinary("REASON_REFMT_BIN", "reasonRefmt", "refmt");
    }

    /**
     * On save, reformat code using remft tool.
     * This method is only working on linux for now, refmt doesn't seem to work on my windows 64bits.
     * Might need more optimisation in the future, and could be a service like merlin (don't create processes
     * each time ?).
     *
     * @param document Document that is being saved
     */
    @Override
    public void beforeDocumentSaving(@NotNull Document document) {
        if (isReasonFile(document)) {
            ProcessBuilder processBuilder = new ProcessBuilder(this.refmtBin);

            Process refmt = null;
            try {
                refmt = processBuilder.start();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(refmt.getOutputStream()));
                BufferedReader reader = new BufferedReader(new InputStreamReader(refmt.getInputStream()));
                BufferedReader errReader = new BufferedReader(new InputStreamReader(refmt.getErrorStream()));

                String text = document.getText();
                writer.write(text);
                writer.flush();

                String errorText = errReader.readLine();
                if (null != errorText) {
                    // todo: transform into an annotation
                    Notifications.Bus.notify(new ReasonMLNotification("Reformat", errorText, NotificationType.ERROR));
                } else {
                    StringBuilder refmtBuffer = new StringBuilder(text.length());
                    reader.lines().forEach(line -> refmtBuffer.append(line).append(/*System.lineSeparator() ??*/"\n"));
                    document.replaceString(0, Integer.MAX_VALUE, refmtBuffer);
                }
            } catch (IOException | RuntimeException e) {
                e.printStackTrace();
            } finally {
                if (refmt != null && refmt.isAlive()) {
                    refmt.destroy();
                }
            }
        }
    }

    private boolean isReasonFile(Document document) {
        VirtualFile file = FileDocumentManager.getInstance().getFile(document);
        String extension = file == null ? null : file.getExtension();
        return "re".equals(extension) || "rei".equals(extension);
    }
}
