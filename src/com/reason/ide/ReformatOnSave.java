package com.reason.ide;

import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManagerAdapter;
import com.reason.Platform;
import org.jetbrains.annotations.NotNull;

import java.io.*;

class ReformatOnSave extends FileDocumentManagerAdapter {

    String refmtBin;

    public ReformatOnSave() {
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
        ProcessBuilder processBuilder = new ProcessBuilder(this.refmtBin).redirectErrorStream(true);

        Process refmt = null;
        try {
            refmt = processBuilder.start();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(refmt.getOutputStream()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(refmt.getInputStream()));
            BufferedReader errReader = new BufferedReader(new InputStreamReader(refmt.getErrorStream()));

            String text = document.getText();

            writer.write(text);
            writer.flush();
            writer.close();

            StringBuilder errorBuffer = new StringBuilder();
            errReader.lines().forEach(errorBuffer::append);
            if (0 < errorBuffer.length()) {
                throw new RuntimeException(errorBuffer.toString());
            } else {
                StringBuilder refmtBuffer = new StringBuilder(text.length());
                reader.lines().forEach(line -> refmtBuffer.append(line).append(/*System.lineSeparator() ??*/"\n"));

                // hack
                String reformattedText = refmtBuffer.toString();
                if (reformattedText.startsWith("File") && 0 < refmtBuffer.toString().indexOf("Error")) {
                    // it seems that refmt returned an error !?
                    System.err.println("REFMT ERROR\n" + reformattedText);
                    Notifications.Bus.notify(new ReasonMLNotification("Reformat", reformattedText, NotificationType.ERROR));
                    return;
                }

                document.setText(refmtBuffer);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } finally {
            if (refmt != null && refmt.isAlive()) {
                refmt.destroyForcibly();
            }
        }
    }
}
