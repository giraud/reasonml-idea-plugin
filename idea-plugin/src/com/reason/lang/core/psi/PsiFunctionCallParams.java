package com.reason.lang.core.psi;

import com.intellij.psi.PsiElement;
import java.util.*;
import org.jetbrains.annotations.NotNull;

public interface PsiFunctionCallParams extends PsiElement {
  @NotNull
  List<PsiParameter> getParametersList();
}
