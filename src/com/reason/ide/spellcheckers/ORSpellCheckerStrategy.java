package com.reason.ide.spellcheckers;

import com.intellij.lang.injection.*;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.*;
import com.intellij.spellchecker.tokenizer.*;
import com.reason.lang.core.psi.impl.PsiLiteralExpression;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.jetbrains.annotations.*;

/**
 * Handling the Generic SpellcheckingStrategy
 *
 * @see SpellcheckingStrategy
 */
public class ORSpellCheckerStrategy extends SpellcheckingStrategy {
    @Override
    public @NotNull Tokenizer<?> getTokenizer(@Nullable PsiElement element) {
        if (element == null || element instanceof PsiWhiteSpace) { // skip whitespace
            return EMPTY_TOKENIZER;
        }
        // optimization
        if (element.getClass() == LeafPsiElement.class) {
            return EMPTY_TOKENIZER;
        }
        // skip other languages
        if (element instanceof PsiLanguageInjectionHost && InjectedLanguageManager.getInstance(element.getProject()).getInjectedPsiFiles(element) != null) {
            return EMPTY_TOKENIZER;
        }
        // handle comments
        if (element instanceof PsiComment) {
            return myCommentTokenizer;
        }
        // literals
        if (element instanceof PsiLiteralExpression) {
            return TEXT_TOKENIZER;
        }
        // Named elements
        if (element instanceof PsiUpperIdentifier || element instanceof PsiUpperSymbol || element instanceof PsiLowerIdentifier || element instanceof PsiLowerSymbol) {
            return TEXT_TOKENIZER;
        }
        return EMPTY_TOKENIZER; // skip everything else
    }
}
