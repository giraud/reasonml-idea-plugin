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
import org.jetbrains.annotations.NotNull;

public class RmlAnnotator implements Annotator {

    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        EditorColorsScheme globalScheme = EditorColorsManager.getInstance().getGlobalScheme();
        IElementType elementType = element.getNode().getElementType();

        if (elementType == RmlTypes.MODULE_NAME) {
            Annotation annotation = holder.createInfoAnnotation(element, null);
            annotation.setEnforcedTextAttributes(globalScheme.getAttributes(RmlSyntaxHighlighter.MODULE_NAME_));
        } else if (elementType == RmlTypes.TYPE_CONSTR_NAME) {
            TextAttributes scheme = globalScheme.getAttributes(RmlSyntaxHighlighter.TYPE_ARGUMENT_);
            holder.createInfoAnnotation(element, null).setEnforcedTextAttributes(scheme);
        } else if (elementType == RmlTypes.MACRO_NAME) {
            TextAttributes scheme = globalScheme.getAttributes(RmlSyntaxHighlighter.ANNOTATION_);
            holder.createInfoAnnotation(element, null).setEnforcedTextAttributes(TextAttributes.ERASE_MARKER);
            holder.createInfoAnnotation(element, null).setEnforcedTextAttributes(scheme);
        } else if (elementType == RmlTypes.TAG_START || elementType == RmlTypes.TAG_CLOSE) {
            TextAttributes scheme = globalScheme.getAttributes(RmlSyntaxHighlighter.MARKUP_TAG_);
            holder.createInfoAnnotation(element, null).setEnforcedTextAttributes(scheme);
        }
    }
}
