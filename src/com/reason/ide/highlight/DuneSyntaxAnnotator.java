package com.reason.ide.highlight;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.reason.lang.dune.DuneTypes;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.editor.markup.TextAttributes.ERASE_MARKER;

public class DuneSyntaxAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        EditorColorsScheme globalScheme = EditorColorsManager.getInstance().getGlobalScheme();
        IElementType elementType = element.getNode().getElementType();

        if (elementType == DuneTypes.INSTANCE.C_VAR) {
            TextAttributes scheme = globalScheme.getAttributes(DuneSyntaxHighlighter.VAR_);
            holder.createInfoAnnotation(element, null).setEnforcedTextAttributes(ERASE_MARKER);
            holder.createInfoAnnotation(element, null).setEnforcedTextAttributes(scheme);
        }
    }
}
