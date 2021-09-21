package com.reason.ide.spellcheckers;

import com.intellij.psi.*;
import com.intellij.spellchecker.tokenizer.*;
import org.jetbrains.annotations.*;

public class OCamlSpellCheckerStrategy extends CustomSpellCheckerStrategy {

    @Override public @NotNull Tokenizer<?> getTokenizer(PsiElement element) {
        return super.getTokenizer(element);
    }
}