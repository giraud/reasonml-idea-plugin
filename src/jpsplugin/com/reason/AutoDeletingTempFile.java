package jpsplugin.com.reason;

import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.io.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.nio.charset.*;

public class AutoDeletingTempFile implements AutoCloseable {
    private final File myFile;

    public AutoDeletingTempFile(@NotNull String prefix, @NotNull String extension) throws IOException {
        myFile = FileUtilRt.createTempFile(prefix, extension, true);
    }

    public @NotNull String getPath() {
        return myFile.getPath();
    }

    public void write(@NotNull String text, @NotNull Charset charset) throws IOException {
        FileUtil.writeToFile(myFile, text, charset);
    }

    @Override
    public void close() {
        FileUtilRt.delete(myFile);
    }
}
