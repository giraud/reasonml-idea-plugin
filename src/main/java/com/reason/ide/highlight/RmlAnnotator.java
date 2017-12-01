package com.reason.ide.highlight;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.reason.lang.RmlTypes;
import com.reason.lang.core.psi.PsiTypeConstrName;
import com.reason.lang.core.psi.TagClose;
import com.reason.lang.core.psi.TagStart;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.editor.markup.TextAttributes.ERASE_MARKER;

public class RmlAnnotator implements Annotator {

    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        EditorColorsScheme globalScheme = EditorColorsManager.getInstance().getGlobalScheme();
        IElementType elementType = element.getNode().getElementType();

        if (elementType == RmlTypes.MODULE_NAME) {
            Annotation annotation = holder.createInfoAnnotation(element, null);
            annotation.setEnforcedTextAttributes(globalScheme.getAttributes(RmlSyntaxHighlighter.MODULE_NAME_));
        }
        else if (element instanceof PsiTypeConstrName) {
            PsiTypeConstrName constrName = (PsiTypeConstrName) element;

            holder.createInfoAnnotation(constrName, null).setEnforcedTextAttributes(ERASE_MARKER);
            holder.createInfoAnnotation(constrName, null).setEnforcedTextAttributes(globalScheme.getAttributes(RmlSyntaxHighlighter.TYPE_ARGUMENT_));
        } else if (elementType == RmlTypes.TAG_START || elementType == RmlTypes.TAG_CLOSE) {
            TextAttributes tagScheme = globalScheme.getAttributes(RmlSyntaxHighlighter.TAG_);
            holder.createInfoAnnotation(element, null).setEnforcedTextAttributes(tagScheme);
        }
    }
}
