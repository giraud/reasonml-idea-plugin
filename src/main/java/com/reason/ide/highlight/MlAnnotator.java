package com.reason.ide.highlight;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.reason.lang.MlTypes;
import org.jetbrains.annotations.NotNull;

public abstract class MlAnnotator implements Annotator {

    private MlTypes m_types;

    MlAnnotator(MlTypes types) {
        m_types = types;
    }

    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        EditorColorsScheme globalScheme = EditorColorsManager.getInstance().getGlobalScheme();
        IElementType elementType = element.getNode().getElementType();

        if (elementType == m_types.MODULE_NAME) {
            Annotation annotation = holder.createInfoAnnotation(element, null);
            annotation.setEnforcedTextAttributes(globalScheme.getAttributes(MlSyntaxHighlighter.MODULE_NAME_));
        } else if (elementType == m_types.TYPE_CONSTR_NAME) {
            TextAttributes scheme = globalScheme.getAttributes(MlSyntaxHighlighter.TYPE_ARGUMENT_);
            holder.createInfoAnnotation(element, null).setEnforcedTextAttributes(scheme);
        } else if (elementType == m_types.MACRO_NAME) {
            TextAttributes scheme = globalScheme.getAttributes(MlSyntaxHighlighter.ANNOTATION_);
            holder.createInfoAnnotation(element, null).setEnforcedTextAttributes(TextAttributes.ERASE_MARKER);
            holder.createInfoAnnotation(element, null).setEnforcedTextAttributes(scheme);
        } else if (elementType == m_types.TAG_START || elementType == m_types.TAG_CLOSE) {
            TextAttributes scheme = globalScheme.getAttributes(MlSyntaxHighlighter.MARKUP_TAG_);
            holder.createInfoAnnotation(element, null).setEnforcedTextAttributes(scheme);
        }
    }
}
