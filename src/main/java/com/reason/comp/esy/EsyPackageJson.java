package com.reason.comp.esy;

import com.google.gson.JsonParser;
import com.google.gson.*;
import com.intellij.framework.detection.*;
import com.intellij.json.*;
import com.intellij.openapi.vfs.*;
import com.intellij.patterns.*;
import com.intellij.util.*;
import com.intellij.util.indexing.*;
import org.jetbrains.annotations.*;

import java.io.*;

import static com.reason.comp.esy.EsyConstants.ESY_CONFIG_FILENAME;
import static java.nio.charset.StandardCharsets.*;

public class EsyPackageJson {
    private EsyPackageJson() {
    }

    /* detects any "package.json" with a top-level "esy" property */
    public static boolean isEsyPackageJson(@Nullable VirtualFile file) {
        if (file != null && file.getFileType() instanceof JsonFileType) {
            try {
                return FileContentPattern.fileContent()
                        .withName(ESY_CONFIG_FILENAME)
                        .with(new FileContentPatternCondition())
                        .accepts(FileContentImpl.createByFile(file));
            } catch (IOException e) {
                return false;
            }
        }
        return false;
    }

    private static class FileContentPatternCondition extends PatternCondition<FileContent> {
        public FileContentPatternCondition() {
            super("esyPackageJsonPattern");
        }

        // No PSI here, not allowed
        @Override
        public boolean accepts(@NotNull FileContent fileContent, ProcessingContext context) {
            try {
                JsonElement jsonContent = JsonParser.parseString(new String(fileContent.getContent(), UTF_8));

                if (jsonContent.isJsonObject()) {
                    JsonObject top = jsonContent.getAsJsonObject();
                    return top.has("esy");
                }
            }
            catch (JsonSyntaxException e) {
                return false;
            }

            return false;
        }
    }
}
