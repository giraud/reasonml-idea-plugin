package com.reason.ide.docs;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.reason.ide.files.FileBase;
import com.reason.lang.core.psi.PsiVal;
import com.reason.lang.odoc.ODocConverter;
import com.reason.lang.odoc.ODocLexer;
import org.jetbrains.annotations.NotNull;

class DocFormatter {

    public static final String HEADER_START = "<div style=\"padding-bottom: 5px; border-bottom: 1px solid #AAAAAAEE\">";
    public static final String HEADER_END = "</div>";
    public static final String CONTENT_START = "<div style=\"\">";
    public static final String CONTENT_END = "</div>";

    static String format(@NotNull PsiFile file, PsiElement element, @NotNull String text) {
        if (file instanceof FileBase) {
            ODocLexer lexer = new ODocLexer();
            return formatDefinition(file, element) +
                    CONTENT_START + (new ODocConverter(lexer).convert(text + "\n")) + CONTENT_END;

        }
        return text;
    }

    private static String formatDefinition(PsiFile file, PsiElement element) {
        StringBuilder sb = new StringBuilder();

        sb.append(HEADER_START).append(((FileBase) file).getQualifiedName());
        if (element instanceof PsiVal) {
            sb.append("<br/>").append(element.getText());
        }
        sb.append(HEADER_END);

        return sb.toString();
    }
}
