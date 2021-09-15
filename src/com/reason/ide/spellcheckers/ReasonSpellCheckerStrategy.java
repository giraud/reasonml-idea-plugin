package com.reason.ide.spellcheckers;

import com.intellij.psi.*;
import com.intellij.spellchecker.tokenizer.*;
import org.jetbrains.annotations.*;

public class ReasonSpellCheckerStrategy extends CustomSpellCheckerStrategy {

    @Override public @NotNull Tokenizer<?> getTokenizer(PsiElement element) {
        return super.getTokenizer(element);
    }
}