package com.reason.lang.core.psi;

import com.intellij.psi.*;
import org.jetbrains.annotations.*;

import java.util.*;

public interface PsiParameters extends PsiElement {
  @NotNull
  List<PsiParameter> getParametersList();
}
