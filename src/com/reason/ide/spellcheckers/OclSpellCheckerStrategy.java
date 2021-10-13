package com.reason.ide.spellcheckers;

import com.intellij.psi.*;
import com.intellij.spellchecker.tokenizer.*;
import org.jetbrains.annotations.*;

public class OclSpellCheckerStrategy extends ORSpellCheckerStrategy {
    @Override
    public @NotNull Tokenizer<?> getTokenizer(@Nullable PsiElement element) {
        return super.getTokenizer(element);
    }
}
