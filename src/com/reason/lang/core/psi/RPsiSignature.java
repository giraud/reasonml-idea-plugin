package com.reason.lang.core.psi;

import com.intellij.psi.*;
import org.jetbrains.annotations.*;

import java.util.*;

public interface RPsiSignature extends PsiElement, RPsiLanguageConverter {
    boolean isFunction();

    @NotNull List<RPsiSignatureItem> getItems();
}
