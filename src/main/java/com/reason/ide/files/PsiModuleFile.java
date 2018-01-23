package com.reason.ide.files;

import com.reason.lang.core.psi.PsiNamedElement;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface PsiModuleFile {
    @NotNull
    Collection<PsiNamedElement> getExpressions();
}
