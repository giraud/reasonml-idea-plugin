package com.reason;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.*;
import java.util.stream.*;

import org.jetbrains.annotations.NotNull;

public class Streams {
    public static final String LINE_SEPARATOR = System.lineSeparator();

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

    public static String inputToString(@NotNull InputStream is) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, UTF_8))) {
            return br.lines().collect(Collectors.joining(LINE_SEPARATOR));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
