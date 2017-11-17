package reason;

import java.io.BufferedReader;
import java.io.IOException;

public class Streams {
    public static void waitUntilReady(BufferedReader reader, BufferedReader errorReader) throws IOException {
        long start = System.currentTimeMillis();
        boolean isReady = reader.ready() || errorReader.ready();
        while (!isReady) {
            if (60000 < (System.currentTimeMillis() - start)) {
                // max 1s
                isReady = true;
            } else {
                Interrupted.sleep(20);
                isReady = reader.ready() || errorReader.ready();
            }
        }
    }
}
