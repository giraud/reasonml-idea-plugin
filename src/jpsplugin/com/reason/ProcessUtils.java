package jpsplugin.com.reason;

import com.intellij.execution.*;
import com.intellij.execution.configurations.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.nio.charset.*;

// todo: this was extracted from OcamlFormatProcess
public class ProcessUtils {

    /**
     * Extracting that was in "com.reason.comp.dune.OcamlFormatProcess".
     * Parse the output a command and return it, or Log an error and return null
     */
    public static @Nullable String parseOutputFromCommandLine(@NotNull GeneralCommandLine cli, @NotNull Log logger) {
        Process fmt = null;
        try {
            fmt = cli.createProcess();
            BufferedReader reader = new BufferedReader(new InputStreamReader(fmt.getInputStream(), StandardCharsets.UTF_8));
            BufferedReader errReader = new BufferedReader(new InputStreamReader(fmt.getErrorStream(), StandardCharsets.UTF_8));

            Streams.waitUntilReady(reader, errReader);

            StringBuilder msgBuffer = new StringBuilder();
            if (!errReader.ready()) {
                final boolean[] empty = {true};
                reader
                        .lines()
                        .forEach(line -> {
                            if (empty[0]) {
                                empty[0] = false;
                            } else {
                                msgBuffer.append('\n');
                            }
                            msgBuffer.append(line);
                        });
                String newText = msgBuffer.toString();
                if (!newText.isEmpty()) { // additional protection
                    return newText;
                }
            } else {
                errReader.lines().forEach(line -> msgBuffer.append(line).append('\n'));
                logger.warn(StringUtil.trimLastCR(msgBuffer.toString()));
            }
        } catch (IOException | RuntimeException | ExecutionException e) {
            logger.warn(e);
        } finally {
            if (fmt != null) {
                fmt.destroyForcibly();
            }
        }
        return null;
    }


}
