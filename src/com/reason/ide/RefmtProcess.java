package com.reason.ide;


import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.diagnostic.Logger;
import com.reason.Platform;

import java.io.*;

class RefmtProcess {

    private final String refmtBin;
    private final Logger log;

    RefmtProcess() {
        this.refmtBin = Platform.getBinary("REASON_REFMT_BIN", "reasonRefmt", "refmt");
        log = Logger.getInstance("ReasonML.refmt");
    }

    // refmt API is not stable
    boolean useDoubleDash() {
        Process process = null;
        BufferedReader reader = null;

        try {
            process = Runtime.getRuntime().exec(this.refmtBin + " --help");
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.indexOf("--use-stdin") > 0) {
                    return true;
                }
            }
        } catch (Exception err) {
            log.error("refmt: " + err.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // do nothing
                }
            }
            if (process != null) {
                try {
                    process.waitFor();
                } catch (InterruptedException e) {
                    // do nothing
                }
            }
        }
        return false;
    }

    String run(boolean useDoubleDash, String code) {
        ProcessBuilder processBuilder = useDoubleDash ? new ProcessBuilder(this.refmtBin) : new ProcessBuilder(this.refmtBin, "-use-stdin", "true", "-is-interface-pp", "false", "-print", "re", "-parse", "re");

        Process refmt = null;
        try {
            refmt = processBuilder.start();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(refmt.getOutputStream()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(refmt.getInputStream()));
            BufferedReader errReader = new BufferedReader(new InputStreamReader(refmt.getErrorStream()));

            writer.write(code);
            writer.close();

            // Wait a little for refmt to do its stuff and can write to the error stream, might not work all the time...
            Thread.sleep(50);
            if (errReader.ready()) {
                StringBuilder er = new StringBuilder();
                errReader.lines().forEach(line -> er.append(line).append(/*System.lineSeparator() ??*/"\n"));
                // todo: transform into an annotation
                Notifications.Bus.notify(new ReasonMLNotification("Reformat", er.toString(), NotificationType.ERROR));
            } else {
                StringBuilder refmtBuffer = new StringBuilder();
                reader.lines().forEach(line -> refmtBuffer.append(line).append(/*System.lineSeparator() ??*/"\n"));
                String newText = refmtBuffer.toString();
                if (!code.isEmpty() && !newText.isEmpty()) { // additional protection
                    return newText;
                }
            }
        } catch (InterruptedException | IOException | RuntimeException e) {
            log.error(e.getMessage());
        } finally {
            if (refmt != null && refmt.isAlive()) {
                refmt.destroy();
            }
        }

        // Something bad happened, do nothing
        return code;
    }
}
