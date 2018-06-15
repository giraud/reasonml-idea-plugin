package com.reason.lang.ocaml;

import com.intellij.psi.PsiElement;
import com.reason.ide.files.FileBase;
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

        String path = extractPathName(element, OclTypes.INSTANCE);
        if (!path.isEmpty()) {
            qualifiedNames.add(path);
            qualifiedNames.add(((FileBase) element.getContainingFile()).asModuleName() + "." + path);
        }

        // Find local opens

        // Find opens

        qualifiedNames.add("Pervasives");
        return qualifiedNames;
    }

}
