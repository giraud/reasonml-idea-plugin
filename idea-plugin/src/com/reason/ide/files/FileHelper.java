package com.reason.ide.files;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.reason.Platform;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class FileHelper {
    private FileHelper() {
    }

    public static boolean isCompilable(@Nullable FileType fileType) {
        return isReason(fileType) || isOCaml(fileType) || isOCamlLexer(fileType) || isOCamlParser(fileType);
    }

    public static boolean isReason(@Nullable FileType fileType) {
        return fileType instanceof RmlFileType || fileType instanceof RmlInterfaceFileType;
    }

    private static boolean isOCamlLexer(@Nullable FileType fileType) {
        return fileType instanceof MllFileType;
    }

    private static boolean isOCamlParser(@Nullable FileType fileType) {
        return fileType instanceof MlyFileType;
    }

    public static boolean isOCaml(@Nullable FileType fileType) {
        return fileType instanceof OclFileType || fileType instanceof OclInterfaceFileType;
    }

    public static boolean isInterface(@Nullable FileType fileType) {
        return fileType instanceof RmlInterfaceFileType || fileType instanceof OclInterfaceFileType;
    }

    @NotNull
    public static String shortLocation(@NotNull Project project, @NotNull String path) {
        String newPath = Platform.removeProjectDir(project, path).replace("node_modules" + File.separator, "");
        int pos = newPath.lastIndexOf("/");
        return 0 < pos ? newPath.substring(0, pos) : newPath;
    }
}
