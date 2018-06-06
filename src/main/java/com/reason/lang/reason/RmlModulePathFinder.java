package com.reason.lang.reason;

import com.intellij.psi.PsiElement;
import com.reason.ide.files.FileBase;
import com.reason.lang.BaseModulePathFinder;
import com.reason.lang.core.psi.PsiLocalOpen;
import com.reason.lang.core.psi.PsiOpen;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RmlModulePathFinder extends BaseModulePathFinder {

    // Find the expression paths
    // Need to add implicit elements like open/include/local open/...
    @NotNull
    public List<String> extractPotentialPaths(@NotNull PsiElement element) {
        List<String> qualifiedNames = new ArrayList<>();

        String path = extractPathName(element, RmlTypes.INSTANCE);
        if (!path.isEmpty()) {
            qualifiedNames.add(path);
            qualifiedNames.add(((FileBase) element.getContainingFile()).asModuleName() + "." + path);
        }

        // Walk backward until top of the file is reached, trying to find local opens and opens/includes
        PsiElement item = element;
        while (item != null) {
            if (item instanceof PsiOpen) {
                // TODO: Add open value to all previous elements
                qualifiedNames.add(0, ((PsiOpen) item).getName());
            }

            PsiElement prevItem = item.getPrevSibling();
            if (prevItem == null) {
                PsiElement parent = item.getParent();
                if (parent instanceof PsiLocalOpen) {
                    qualifiedNames.add(0, ((PsiLocalOpen) parent).getName());
                }
                item = parent;
            } else {
                item = prevItem;
            }
        }

        return qualifiedNames;
    }
}
