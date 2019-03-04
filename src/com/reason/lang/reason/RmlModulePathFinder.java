package com.reason.lang.reason;

import com.intellij.psi.PsiElement;
import com.reason.ide.files.FileBase;
import com.reason.lang.BaseModulePathFinder;
import com.reason.lang.core.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RmlModulePathFinder extends BaseModulePathFinder {

    // Find the expression paths
    @NotNull
    public List<String> extractPotentialPaths(@NotNull PsiElement element) {
        List<String> qualifiedNames = new ArrayList<>();

        String path = extractPathName(element, RmlTypes.INSTANCE);
//        if (!path.isEmpty()) {
//            qualifiedNames.add(path);
//        }

        // Walk backward until top of the file is reached, trying to find local opens and opens/includes
        PsiElement item = element;
        while (item != null) {
            if (100 < qualifiedNames.size()) {
                break; // There must be a problem with the parser
            }

            if (item instanceof FileBase) {
                qualifiedNames.add(((FileBase) item).asModuleName());
                break;
            } else if (item instanceof PsiOpen || item instanceof PsiInclude) {
                String openName = ((PsiNamedElement) item).getName();
                qualifiedNames.add(openName + (path.isEmpty() ? "" : "." + path));
            } else if (item instanceof PsiModule) {
                if (path.equals(((PsiModule) item).getName())) {
                    qualifiedNames.add(((FileBase) element.getContainingFile()).asModuleName() + "." + path);
                }
            }

            PsiElement prevItem = item.getPrevSibling();
            if (prevItem == null) {
                PsiElement parent = item.getParent();
                if (parent instanceof PsiLocalOpen) {
                    qualifiedNames.add(((PsiLocalOpen) parent).getName() + (path.isEmpty() ? "" : "." + path));
                }
                item = parent;
            } else {
                item = prevItem;
            }
        }

        qualifiedNames.add(path);
        qualifiedNames.add("Pervasives");
        return qualifiedNames;
    }

}
