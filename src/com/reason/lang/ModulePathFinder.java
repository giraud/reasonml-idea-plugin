package com.reason.lang;

import java.util.*;

import org.jetbrains.annotations.NotNull;
import com.intellij.psi.PsiElement;

public interface ModulePathFinder {

    enum Includes {
        containingFile,
        includedModules
    }

    EnumSet<Includes> includeAll = EnumSet.of(Includes.containingFile, Includes.includedModules);

    @NotNull
    List<String> extractPotentialPaths(@NotNull PsiElement element, @NotNull EnumSet<Includes> include, boolean addTypes);
}
