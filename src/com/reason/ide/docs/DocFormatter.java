package com.reason.ide.docs;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.reason.ide.files.FileBase;
import com.reason.lang.core.psi.PsiVal;
import com.reason.lang.odoc.ODocConverter;
import com.reason.lang.odoc.ODocLexer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.reason.lang.odoc.ODocMarkup.*;

class DocFormatter {

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

    @NotNull
    static String escapeCodeForHtml(@Nullable PsiElement code) {
        if (code == null) {
            return "";
        }

        return escapeCodeForHtml(code.getText());
    }

    public static String escapeCodeForHtml(@Nullable String code) {
        return code == null ? null : code.replaceAll("<", "&lt;");
    }
}
