package com.reason.lang.core.psi;

import com.intellij.psi.*;
import org.jetbrains.annotations.*;

import java.util.*;

public interface PsiSignature extends PsiElement, PsiLanguageConverter {
    boolean isFunction();

    @NotNull List<PsiSignatureItem> getItems();
}
