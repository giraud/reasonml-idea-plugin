package com.reason.ide.format;

import com.intellij.psi.PsiFile;
import com.reason.ide.files.FileHelper;
import org.jetbrains.annotations.Nullable;

public class ReformatUtil {

    private ReformatUtil() {
    }

    @Nullable
    public static String getFormat(@Nullable PsiFile file) {
        String format = null;

        if (file != null) {
            if (FileHelper.isOCaml(file.getFileType())) {
                format = "ml";
            } else if (FileHelper.isReason(file.getFileType())) {
                format = "re";
            }
        }

        return format;
    }

}
