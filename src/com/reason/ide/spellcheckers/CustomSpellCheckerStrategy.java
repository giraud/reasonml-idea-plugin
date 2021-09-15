package com.reason.ide.spellcheckers;

import com.intellij.lang.injection.*;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.*;
import com.intellij.spellchecker.tokenizer.*;
import com.reason.lang.core.psi.PsiLiteralExpression;
import com.reason.lang.core.psi.impl.*;
import org.jetbrains.annotations.*;

/**
 * Handling the Generic SpellcheckingStrategy
 *
 * @see SpellcheckingStrategy
 * @see OCamlSpellCheckerStrategy
 */
public class CustomSpellCheckerStrategy extends SpellcheckingStrategy {

    @Override public @NotNull Tokenizer<?> getTokenizer(PsiElement element) {
        if (element instanceof PsiWhiteSpace) { // skip whitespace
            return EMPTY_TOKENIZER;
        }
        // optimization
        if (element.getClass() == LeafPsiElement.class) {
            return EMPTY_TOKENIZER;
        }
        // skip other languages
        if (element instanceof PsiLanguageInjectionHost &&
                InjectedLanguageManager.getInstance(element.getProject()).getInjectedPsiFiles(element) != null) {
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
        // named elements
        if (element instanceof PsiNameIdentifierOwner) {
            // not allowed
            if (element instanceof PsiAnnotationImpl || element instanceof PsiMacroName) {
                return EMPTY_TOKENIZER;
            }
            return PsiIdentifierOwnerTokenizer.INSTANCE;
        }
        // ... = PsiLowerSymbolImpl
        if (element instanceof PsiLowerSymbolImpl) {
            return TEXT_TOKENIZER;
        }
        return EMPTY_TOKENIZER; // skip everything else
    }
}