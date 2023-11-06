package com.reason.ide.highlight;

import com.intellij.lang.*;
import com.intellij.lang.annotation.*;
import com.intellij.openapi.editor.colors.*;
import com.intellij.openapi.editor.markup.*;
import com.intellij.openapi.util.*;
import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

import static com.intellij.lang.annotation.HighlightSeverity.*;
import static com.reason.ide.highlight.ORSyntaxHighlighter.*;

public abstract class ORSyntaxAnnotator implements Annotator {
    private final ORLangTypes myTypes;

    ORSyntaxAnnotator(@NotNull ORLangTypes types) {
        myTypes = types;
    }

    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        ASTNode elementNode = element.getNode();
        IElementType elementType = elementNode.getElementType();

        if (elementType == myTypes.C_TAG_START) {
            PsiElement nameIdentifier = ((RPsiTagStart) element).getNameIdentifier();
            if (nameIdentifier != null) {
                TextRange range = TextRange.create(element.getTextRange().getStartOffset(), nameIdentifier.getTextRange().getEndOffset());
                enforceColor(holder, range, MARKUP_TAG_);
                PsiElement lastChild = element.getLastChild();
                IElementType lastElementType = lastChild == null ? null : lastChild.getNode().getElementType();
                if (lastElementType == myTypes.TAG_AUTO_CLOSE || lastElementType == myTypes.GT) {
                    enforceColor(holder, lastChild, MARKUP_TAG_);
                }
            } else {
                enforceColor(holder, element, MARKUP_TAG_);
            }
        } else if (elementType == myTypes.C_TAG_CLOSE) {
            enforceColor(holder, element, MARKUP_TAG_);
        } else if (elementType == myTypes.PROPERTY_NAME) {
            enforceColor(holder, element, MARKUP_ATTRIBUTE_);
        } else if (elementType == myTypes.C_MACRO_NAME) {
            enforceColor(holder, element, ANNOTATION_);
        } else if (elementType == myTypes.C_INTERPOLATION_PART) {
            enforceColor(holder, element, STRING_);
        } else if (element instanceof RPsiInterpolationReference) {
            enforceColor(holder, element, INTERPOLATED_REF_);
        }
        // remapped tokens are not seen by syntaxAnnotator
        else if (elementType == myTypes.A_VARIANT_NAME) {
            enforceColor(holder, element, VARIANT_NAME_);
        } else if (elementType == myTypes.A_MODULE_NAME) {
            enforceColor(holder, element, MODULE_NAME_);
        } else if (elementType == myTypes.LIDENT) {
            IElementType parentElementType = elementNode.getTreeParent().getElementType();
            if (parentElementType == myTypes.C_TYPE_DECLARATION || parentElementType == myTypes.C_EXTERNAL_DECLARATION || parentElementType == myTypes.C_RECORD_FIELD) {
                eraseColor(element, holder);
            }
        }
    }

    private static void eraseColor(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        holder.newSilentAnnotation(INFORMATION).range(element).enforcedTextAttributes(TextAttributes.ERASE_MARKER).create();
    }

    @SuppressWarnings("SameParameterValue")
    private void enforceColor(@NotNull AnnotationHolder holder, @NotNull TextRange range, @NotNull TextAttributesKey key) {
        holder.newSilentAnnotation(INFORMATION).range(range).enforcedTextAttributes(TextAttributes.ERASE_MARKER).create();
        holder.newSilentAnnotation(INFORMATION).range(range).textAttributes(key).create();
    }

    private void enforceColor(@NotNull AnnotationHolder holder, @Nullable PsiElement element, @NotNull TextAttributesKey key) {
        if (element != null) {
            holder.newSilentAnnotation(INFORMATION).range(element).enforcedTextAttributes(TextAttributes.ERASE_MARKER).create();
            holder.newSilentAnnotation(INFORMATION).range(element).textAttributes(key).create();
        }
    }
}
