package com.reason.ide.docs;

import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

class DocFormatter {
    static String format(@NotNull PsiFile file, @NotNull String text) {
        // Parse odoc special comment with a grammar ?
        String substring = text.substring(3); // remove (**_
        return substring.substring(0, substring.length() - 3).trim(); // remove *)
    }
}
