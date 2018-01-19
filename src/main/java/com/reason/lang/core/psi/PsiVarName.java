package com.reason.lang.core.psi;

import com.reason.lang.core.ModulePath;

public interface PsiVarName extends PsiNamedElement {
    ModulePath getPath();
}
