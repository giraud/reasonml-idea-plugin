package com.reason.ide.highlight;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.psi.PsiElement;
import com.reason.psi.ReasonMLModuleName;
import com.reason.psi.ReasonMLTypeConstrName;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.editor.markup.TextAttributes.ERASE_MARKER;

public class RmlAnnotator implements Annotator {

    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        EditorColorsScheme globalScheme = EditorColorsManager.getInstance().getGlobalScheme();

        if (element instanceof ReasonMLModuleName) {
            ReasonMLModuleName moduleName = (ReasonMLModuleName) element;

            holder.createInfoAnnotation(moduleName, null).setEnforcedTextAttributes(ERASE_MARKER);
            holder.createInfoAnnotation(moduleName, null).setEnforcedTextAttributes(globalScheme.getAttributes(RmlSyntaxHighlighter.MODULE_NAME_));
        } else if (element instanceof ReasonMLTypeConstrName) {
            ReasonMLTypeConstrName constrName = (ReasonMLTypeConstrName) element;

            holder.createInfoAnnotation(constrName, null).setEnforcedTextAttributes(ERASE_MARKER);
            holder.createInfoAnnotation(constrName, null).setEnforcedTextAttributes(globalScheme.getAttributes(RmlSyntaxHighlighter.TYPE_ARGUMENT_));
        }
    }
}
