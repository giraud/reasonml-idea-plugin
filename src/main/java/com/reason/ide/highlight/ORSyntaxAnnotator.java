package com.reason.ide.highlight;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.reason.lang.core.psi.PsiInterpolation;
import com.reason.lang.core.psi.PsiType;
import com.reason.lang.core.psi.PsiUpperSymbol;
import com.reason.lang.core.type.ORTypes;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.editor.markup.TextAttributes.ERASE_MARKER;

public abstract class ORSyntaxAnnotator implements Annotator {

    private final ORTypes m_types;

    ORSyntaxAnnotator(ORTypes types) {
        m_types = types;
    }

    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        EditorColorsScheme globalScheme = EditorColorsManager.getInstance().getGlobalScheme();
        IElementType elementType = element.getNode().getElementType();

        if (element instanceof PsiType) {
            TextAttributes scheme = globalScheme.getAttributes(ORSyntaxHighlighter.TYPE_ARGUMENT_);
            String name = ((PsiType) element).getName();
            if (name != null && !name.isEmpty()) {
                // find all occurrences of name
                int nameLength = name.length();
                String text = element.getText();
                int namePos = text.indexOf(name);
                while (namePos >= 0) {
                    TextRange range = TextRange.from(element.getTextOffset() + namePos, nameLength);
                    Annotation infoAnnotation = holder.createInfoAnnotation(range, null);
                    infoAnnotation.setEnforcedTextAttributes(ERASE_MARKER);
                    infoAnnotation.setEnforcedTextAttributes(scheme);
                    namePos = text.indexOf(name, namePos + nameLength);
                }
            }
        } else if (elementType == m_types.UPPER_SYMBOL) {
            PsiUpperSymbol symbol = (PsiUpperSymbol) element;
            TextAttributes colorAttribute = globalScheme.getAttributes(symbol.isVariant() ? ORSyntaxHighlighter.VARIANT_NAME_ : ORSyntaxHighlighter.MODULE_NAME_);
            Annotation annotation = holder.createInfoAnnotation(element, null);
            annotation.setEnforcedTextAttributes(colorAttribute);
        } else if (elementType == m_types.MACRO_NAME) {
            TextAttributes scheme = globalScheme.getAttributes(ORSyntaxHighlighter.ANNOTATION_);
            holder.createInfoAnnotation(element, null).setEnforcedTextAttributes(ERASE_MARKER);
            holder.createInfoAnnotation(element, null).setEnforcedTextAttributes(scheme);
        } else if (elementType == m_types.TAG_NAME || elementType == m_types.TAG_LT || elementType == m_types.TAG_GT) {
            TextAttributes scheme = globalScheme.getAttributes(ORSyntaxHighlighter.MARKUP_TAG_);
            Annotation annotation = holder.createInfoAnnotation(element, null);
            annotation.setEnforcedTextAttributes(ERASE_MARKER);
            annotation.setEnforcedTextAttributes(scheme);
        } else if (elementType == m_types.OPTION) {
            TextAttributes scheme = globalScheme.getAttributes(ORSyntaxHighlighter.OPTION_);
            holder.createInfoAnnotation(element, null).setEnforcedTextAttributes(scheme);
        } else if (elementType == m_types.PROPERTY_NAME) {
            holder.createInfoAnnotation(element, null).setEnforcedTextAttributes(ERASE_MARKER);
            holder.createInfoAnnotation(element, null).setEnforcedTextAttributes(globalScheme.getAttributes(ORSyntaxHighlighter.MARKUP_ATTRIBUTE_));
        } else if (element instanceof PsiInterpolation) {
            TextAttributes scheme = globalScheme.getAttributes(ORSyntaxHighlighter.STRING_);
            holder.createInfoAnnotation(element, null).setEnforcedTextAttributes(scheme);
        }
    }
}
