package com.reason.ide.format;

import com.intellij.psi.PsiFile;
import com.reason.ide.files.OclFile;
import com.reason.ide.files.OclInterfaceFile;
import com.reason.ide.files.RmlFile;
import com.reason.ide.files.RmlInterfaceFile;
import org.jetbrains.annotations.Nullable;

class ReformatUtil {

    private ReformatUtil() {
    }

    @Nullable
    static String getFormat(@Nullable PsiFile file) {
        String format = null;

        if (file instanceof OclFile || file instanceof OclInterfaceFile) {
            format = "ml";
        } else if (file instanceof RmlFile || file instanceof RmlInterfaceFile) {
            format = "re";
        }

        return format;
    }

}
