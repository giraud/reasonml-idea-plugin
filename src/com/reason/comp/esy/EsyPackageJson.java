package com.reason.comp.esy;

import com.google.gson.JsonParser;
import com.google.gson.*;
import com.intellij.framework.detection.*;
import com.intellij.json.*;
import com.intellij.openapi.vfs.*;
import com.intellij.patterns.*;
import com.intellij.util.*;
import com.intellij.util.indexing.*;
import com.reason.ide.files.*;
import org.jetbrains.annotations.*;

import java.io.*;

import static java.nio.charset.StandardCharsets.*;

public class EsyPackageJson {
    private EsyPackageJson() {
    }

    /* detects any "package.json" with a top-level "esy" property */
    public static boolean isEsyPackageJson(@NotNull VirtualFile file) {
        if (file.getFileType() instanceof JsonFileType) {
            try {
                return FileContentPattern.fileContent()
                        .withName(EsyPackageJsonFileType.getDefaultFilename())
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
