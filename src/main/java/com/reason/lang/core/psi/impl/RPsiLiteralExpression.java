package com.reason.lang.core.psi.impl;

import com.intellij.codeInsight.*;
import com.intellij.lang.*;
import com.intellij.openapi.util.*;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.*;
import com.intellij.psi.tree.*;
import org.jetbrains.annotations.*;

public class RPsiLiteralExpression extends LeafPsiElement /*implements PsiLanguageInjectionHost*/ {
    public RPsiLiteralExpression(@NotNull IElementType type, CharSequence text) {
        super(type, text);
    }

    //@Override
    //public boolean isValidHost() {
    //    return true;
    //}

    //@Override
    //public @NotNull RPsiLiteralExpression updateText(@NotNull String text) {
    //    ASTNode valueNode = getNode().getFirstChildNode();
    //    assert valueNode instanceof LeafElement;
    //    ((LeafElement) valueNode).replaceWithText(text);
    //    return this;
    //}

    //@Override
    //public @NotNull LiteralTextEscaper<? extends PsiLanguageInjectionHost> createLiteralTextEscaper() {
    //    return new StringLiteralEscaper<>(this);
    //}

    // Copied from com.intellij.psi.impl.source.tree.injected.StringLiteralEscaper
    // See https://github.com/giraud/reasonml-idea-plugin/issues/289
    public static class StringLiteralEscaper<T extends PsiLanguageInjectionHost> extends LiteralTextEscaper<T> {
        private int[] outSourceOffsets;

        public StringLiteralEscaper(@NotNull T host) {
            super(host);
        }

        @Override
        public boolean decode(@NotNull final TextRange rangeInsideHost, @NotNull StringBuilder outChars) {
            String subText = rangeInsideHost.substring(myHost.getText());
            outSourceOffsets = new int[subText.length() + 1];
            return CodeInsightUtilCore.parseStringCharacters(subText, outChars, outSourceOffsets);
        }

        @Override
        public int getOffsetInHost(int offsetInDecoded, @NotNull final TextRange rangeInsideHost) {
            int result = offsetInDecoded < outSourceOffsets.length ? outSourceOffsets[offsetInDecoded] : -1;
            if (result == -1) {
                return -1;
            }
            return Math.min(result, rangeInsideHost.getLength()) + rangeInsideHost.getStartOffset();
        }

        @Override
        public boolean isOneLine() {
            return true;
        }
    }

    @Override
    public @NotNull String toString() {
        return "RPsiLiteralExpression";
    }
}
