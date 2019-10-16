package com.reason.lang;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.Set;

public interface QNameFinder {

    enum Includes {
        containingFile,
        includedModules
    }

    EnumSet<Includes> includeAll = EnumSet.of(Includes.containingFile, Includes.includedModules);

    @NotNull
    Set<String> extractPotentialPaths(@NotNull PsiElement element, @NotNull EnumSet<Includes> include, boolean addTypes);
}
