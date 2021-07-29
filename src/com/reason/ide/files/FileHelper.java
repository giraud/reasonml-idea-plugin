package com.reason.ide.files;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.PsiFile;
import jpsplugin.com.reason.Platform;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FileHelper {
    private FileHelper() {
    }

    public static boolean isCompilable(@Nullable FileType fileType) {
        return isReason(fileType)
                || isRescript(fileType)
                || isOCaml(fileType)
                || isOCamlLexer(fileType)
                || isOCamlParser(fileType);
    }

    public static boolean isReason(@Nullable FileType fileType) {
        return fileType instanceof RmlFileType || fileType instanceof RmlInterfaceFileType;
    }

    public static boolean isRescript(@Nullable FileType fileType) {
        return fileType instanceof ResFileType || fileType instanceof ResInterfaceFileType;
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

    public static boolean isNinja(@Nullable VirtualFile file) {
        return file != null && "build.ninja".equals(file.getName());
    }

    public static boolean isInterface(@Nullable FileType fileType) {
        return fileType instanceof RmlInterfaceFileType
                || fileType instanceof ResInterfaceFileType
                || fileType instanceof OclInterfaceFileType;
    }

    @NotNull
    public static String shortLocation(@NotNull PsiFile file) {
        String newPath = Platform.getRelativePathToModule(file);
        int nodeIndex = newPath.indexOf("node_modules");
        if (0 <= nodeIndex) {
            newPath = newPath.substring(nodeIndex);
        }
        int pos = newPath.lastIndexOf("/");
        return 0 < pos ? newPath.substring(0, pos) : newPath;
    }
}
