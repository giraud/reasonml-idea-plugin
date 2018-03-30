package com.reason.lang.ocaml;

import com.intellij.psi.PsiElement;
import com.reason.lang.BaseModulePathFinder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class OclModulePathFinder extends BaseModulePathFinder {

    // Find the expression paths
    // Need to add implicit elements like open/include/local open/...
    @NotNull
    public List<String> extractPotentialPaths(@NotNull PsiElement element) {
        List<String> qualifiedNames = new ArrayList<>();

        extractQualifiedName(element, qualifiedNames);

        // Find local opens

        // Find opens

        return qualifiedNames;
    }

}
