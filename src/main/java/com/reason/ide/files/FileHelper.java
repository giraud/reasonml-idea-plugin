package com.reason.ide.files;

import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.fileTypes.FileType;

public class FileHelper {
    public static boolean isCompilable(@Nullable FileType fileType) {
        return isReason(fileType) || isOCaml(fileType);
    }

    public static boolean isReason(@Nullable FileType fileType) {
        return fileType instanceof RmlFileType || fileType instanceof RmlInterfaceFileType;
    }

    public static boolean isOCaml(@Nullable FileType fileType) {
        return fileType instanceof OclFileType || fileType instanceof OclInterfaceFileType;
    }
}
