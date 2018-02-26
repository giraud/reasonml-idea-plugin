package com.reason.lang.core.psi;

import com.intellij.psi.PsiElement;

import java.util.Map;

public interface PsiTagStart extends PsiElement {
    Map<String, String> getAttributes(); // Map is not a good API
}
