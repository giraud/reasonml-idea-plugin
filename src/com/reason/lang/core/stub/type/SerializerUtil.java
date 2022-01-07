package com.reason.lang.core.stub.type;

import com.intellij.psi.stubs.*;
import org.jetbrains.annotations.*;

import java.io.*;

public class SerializerUtil {
    private SerializerUtil() {
    }

    static void writePath(@NotNull StubOutputStream dataStream, String @Nullable [] path) throws IOException {
        if (path == null) {
            dataStream.writeByte(0);
        } else {
            dataStream.writeByte(path.length);
            for (String name : path) {
                dataStream.writeUTFFast(name == null ? "" : name);
            }
        }
    }

    static String @Nullable [] readPath(@NotNull StubInputStream dataStream) throws IOException {
        String[] path = null;
        byte namesCount = dataStream.readByte();
        if (namesCount > 0) {
            path = new String[namesCount];
            for (int i = 0; i < namesCount; i++) {
                path[i] = dataStream.readUTFFast();
            }
        }
        return path;
    }
}
