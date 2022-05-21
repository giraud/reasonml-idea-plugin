package com.reason.ide.highlight;

import com.intellij.lang.annotation.*;
import com.intellij.openapi.editor.colors.*;
import com.intellij.openapi.editor.markup.*;
import com.intellij.openapi.util.*;
import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

import static com.intellij.lang.annotation.HighlightSeverity.*;
import static com.reason.ide.highlight.ORSyntaxHighlighter.*;

public abstract class ORSyntaxAnnotator implements Annotator {
    private final ORTypes myTypes;

    ORSyntaxAnnotator(@NotNull ORTypes types) {
        myTypes = types;
    }

    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        IElementType elementType = element.getNode().getElementType();

        if (elementType == myTypes.C_TAG_START) {
            PsiElement nameIdentifier = ((PsiTagStart) element).getNameIdentifier();
            if (nameIdentifier != null) {
                TextRange range = TextRange.create(element.getTextRange().getStartOffset(), nameIdentifier.getTextRange().getEndOffset());
                color(holder, range, MARKUP_TAG_);
                color(holder, element.getLastChild(), MARKUP_TAG_);
            }
        } else if (elementType == myTypes.C_TAG_CLOSE) {
            color(holder, element, MARKUP_TAG_);
        } else if (elementType == myTypes.PROPERTY_NAME) {
            color(holder, element, MARKUP_ATTRIBUTE_);
        }
        // zzz
        else if (elementType == myTypes.UIDENT) {
            PsiElement parent = element.getParent();
            //if (parent instanceof PsiModule) {
            color(holder, element.getNavigationElement(), MODULE_NAME_);

            //}
            //    if (!(parent instanceof PsiTagStart) && !(parent instanceof PsiTagClose)) {
            //        PsiElement nextElement = element.getNextSibling();
            //        IElementType nextElementType =
            //                nextElement == null ? null : nextElement.getNode().getElementType();
            //        boolean mightBeVariant =
            //                (nextElementType != m_types.DOT)
            //                        && !(parent instanceof PsiOpen)
            //                        && !(parent instanceof PsiInclude)
            //                        && !(parent instanceof PsiModule && ((PsiModule) parent).getAlias() != null);
            //        color(holder, element, mightBeVariant ? VARIANT_NAME_ : MODULE_NAME_);
            //    }
        }
        // zzz
        //else if (elementType == m_types.VARIANT_NAME) {
        //    color(holder, element, VARIANT_NAME_);
        //}
        else if (elementType == myTypes.C_VARIANT_DECLARATION) {
            PsiElement identifier = ((PsiVariantDeclaration) element).getNameIdentifier();
            color(holder, identifier, VARIANT_NAME_);
        } else if (elementType == myTypes.C_MACRO_NAME) {
            color(holder, element, ANNOTATION_);
        } else if (elementType == myTypes.OPTION) {
            color(holder, element, OPTION_);
        } else if (elementType == myTypes.C_INTERPOLATION_PART) {
            color(holder, element, STRING_);
        } else if (element instanceof PsiInterpolationReference) {
            color(holder, element, INTERPOLATED_REF_);
        }
    }

    private void color(@NotNull AnnotationHolder holder, @NotNull TextRange range, @NotNull TextAttributesKey key) {
        holder.newSilentAnnotation(INFORMATION).range(range).enforcedTextAttributes(TextAttributes.ERASE_MARKER).create();
        holder.newSilentAnnotation(INFORMATION).range(range).textAttributes(key).create();
    }

    private void color(@NotNull AnnotationHolder holder, @Nullable PsiElement element, @NotNull TextAttributesKey key) {
        if (element != null) {
            holder.newSilentAnnotation(INFORMATION).range(element).enforcedTextAttributes(TextAttributes.ERASE_MARKER).create();
            holder.newSilentAnnotation(INFORMATION).range(element).textAttributes(key).create();
        }
    }
}
