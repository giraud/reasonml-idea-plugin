package com.reason;

import org.jetbrains.annotations.NotNull;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessOutputTypes;

public class ProcessFinishedListener extends ProcessAdapter {

    private long m_start;
    
    public ProcessFinishedListener() {
        this(System.currentTimeMillis());
    }

    public ProcessFinishedListener(long start) {
        m_start = start;
    }

    @Override
    public void processTerminated(@NotNull ProcessEvent event) {
        long end = System.currentTimeMillis();
        Object source = event.getSource();
        if (source instanceof ProcessHandler) {
            ((ProcessHandler) source).notifyTextAvailable("Process finished in " + formatBuildTime(end - m_start) + "\n\n", ProcessOutputTypes.SYSTEM);
        }
    }

    private static String formatBuildTime(long milliSeconds) {
        if (milliSeconds < 1000) {
            return milliSeconds + "ms";
        }

        long seconds = milliSeconds / 1000;

        final StringBuilder sb = new StringBuilder();
        if (seconds >= 3600) {
            sb.append(seconds / 3600).append("h ");
            seconds %= 3600;
        }
        if (seconds >= 60 || sb.length() > 0) {
            sb.append(seconds / 60).append("m ");
            seconds %= 60;
        }
        if (seconds > 0 || sb.length() > 0) {
            sb.append(seconds).append("s");
        }

        return sb.toString();
    }

}
