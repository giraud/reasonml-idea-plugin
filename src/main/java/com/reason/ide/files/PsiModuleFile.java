package com.reason.ide.files;

import com.reason.lang.core.psi.PsiNamedElement;

import java.util.Collection;

public interface PsiModuleFile {
    Collection<PsiNamedElement> getExpressions();
}
