package com.reason.ide.highlight;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.psi.PsiElement;
import com.reason.psi.ReasonMLModuleName;
import org.jetbrains.annotations.NotNull;

public class RmlAnnotator implements Annotator {

    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof ReasonMLModuleName)) {
            return;
        }

        final ReasonMLModuleName moduleName = (ReasonMLModuleName) element;

        holder.createInfoAnnotation(moduleName, null).setEnforcedTextAttributes(TextAttributes.ERASE_MARKER);
        holder.createInfoAnnotation(moduleName, null).setEnforcedTextAttributes(EditorColorsManager.getInstance().getGlobalScheme().getAttributes(RmlSyntaxHighlighter.MODULE_NAME_));
    }
}
