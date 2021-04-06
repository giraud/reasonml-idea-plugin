package com.reason.lang.core.psi;

import com.intellij.psi.*;
import org.jetbrains.annotations.*;

import java.util.*;

public interface PsiTagStart extends PsiNameIdentifierOwner {
    @NotNull List<PsiTagProperty> getProperties();

    @NotNull List<ComponentPropertyAdapter> getUnifiedPropertyList();
}
