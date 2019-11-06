package com.reason;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;

public class Streams {
    private Streams() {
    }

    public static void waitUntilReady(@NotNull BufferedReader reader, @NotNull BufferedReader errorReader) throws IOException {
        long start = System.currentTimeMillis();
        boolean isReady = reader.ready() || errorReader.ready();
        while (!isReady) {
            if (200 < (System.currentTimeMillis() - start)) {
                // max 1s
                isReady = true;
            } else {
                Interrupted.sleep(20);
                isReady = reader.ready() || errorReader.ready();
            }
        }
    }
}
