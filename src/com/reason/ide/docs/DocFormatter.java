package com.reason.ide.docs;

import com.intellij.lang.documentation.DocumentationMarkup;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.reason.ide.files.FileBase;
import com.reason.lang.core.psi.PsiVal;
import com.reason.lang.odoc.ODocConverter;
import com.reason.lang.odoc.ODocLexer;
import org.jetbrains.annotations.NotNull;

class DocFormatter {
    static String format(@NotNull PsiFile file, PsiElement element, @NotNull String text) {
        if (file instanceof FileBase) {
            ODocLexer lexer = new ODocLexer();
            return formatDefinition(file, element) +
                    DocumentationMarkup.CONTENT_START + (new ODocConverter(lexer).convert(text + "\n")) + DocumentationMarkup.CONTENT_END;

        }
        return text;
    }

    private static String formatDefinition(PsiFile file, PsiElement element) {
        StringBuilder sb = new StringBuilder();

        sb.append(DocumentationMarkup.DEFINITION_START).append(((FileBase) file).getQualifiedName());
        if (element instanceof PsiVal) {
            sb.append("<br/>").append(element.getText());
        }
        sb.append(DocumentationMarkup.DEFINITION_END);

        return sb.toString();
    }
}
