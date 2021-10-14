package com.reason.ide.highlight;

import com.intellij.lang.annotation.*;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.editor.colors.*;
import com.intellij.openapi.editor.markup.*;
import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

import static com.intellij.lang.annotation.HighlightSeverity.*;
import static com.reason.ide.highlight.ORSyntaxHighlighter.*;

public abstract class ORSyntaxAnnotator implements Annotator {
    private final ORTypes m_types;

    ORSyntaxAnnotator(ORTypes types) {
        m_types = types;
    }

    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        IElementType elementType = element.getNode().getElementType();

        if (elementType == m_types.C_UPPER_IDENTIFIER) {
            PsiElement parent = element.getParent();
            if (parent instanceof PsiModule) {
                color(holder, element.getNavigationElement(), MODULE_NAME_);
            }
        } else if (elementType == m_types.C_UPPER_SYMBOL) {
            PsiElement parent = element.getParent();
            PsiElement nextElement = element.getNextSibling();
            IElementType nextElementType =
                    nextElement == null ? null : nextElement.getNode().getElementType();
            boolean mightBeVariant =
                    (nextElementType != m_types.DOT)
                            && !(parent instanceof PsiOpen)
                            && !(parent instanceof PsiInclude)
                            && !(parent instanceof PsiModule && ((PsiModule) parent).getAlias() != null);
            color(holder, element, mightBeVariant ? VARIANT_NAME_ : MODULE_NAME_);
        } else if (elementType == m_types.C_VARIANT_DECLARATION) {
            PsiElement identifier = element.getNavigationElement();
            color(holder, identifier, VARIANT_NAME_);
        } else if (elementType == m_types.VARIANT_NAME) {
            color(holder, element, VARIANT_NAME_);
        } else if (elementType == m_types.C_MACRO_NAME) {
            color(holder, element, ANNOTATION_);
        } else if (elementType == m_types.TAG_NAME
                || elementType == m_types.TAG_LT
                || elementType == m_types.TAG_LT_SLASH
                || elementType == m_types.TAG_GT
                || elementType == m_types.TAG_AUTO_CLOSE) {
            color(holder, element, MARKUP_TAG_);
        } else if (elementType == m_types.OPTION) {
            color(holder, element, OPTION_);
        } else if (elementType == m_types.PROPERTY_NAME) {
            color(holder, element, MARKUP_ATTRIBUTE_);
        } else if (elementType == m_types.C_INTERPOLATION_PART) {
            color(holder, element, STRING_);
        } else if (element instanceof PsiInterpolationReference) {
            color(holder, element, INTERPOLATED_REF_);
        }
    }

    private void color(@NotNull AnnotationHolder holder, @NotNull PsiElement element, @NotNull TextAttributesKey key) {
        holder.newSilentAnnotation(INFORMATION)
                .enforcedTextAttributes(TextAttributes.ERASE_MARKER)
                .create();
        holder.newSilentAnnotation(INFORMATION)
                .range(element)
                .textAttributes(key)
                .create();
    }
}
