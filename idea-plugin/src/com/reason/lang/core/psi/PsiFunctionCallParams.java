package com.reason.lang.core.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public interface PsiFunctionCallParams extends PsiElement {
    @NotNull List<PsiParameter> getParametersList();
}
