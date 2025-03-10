package com.reason.ide.search;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.*;
import com.intellij.psi.search.*;
import com.reason.ide.files.*;
import com.reason.ide.search.index.*;
import com.reason.lang.core.psi.*;
import org.jetbrains.annotations.*;

import java.util.*;

@Service(Service.Level.APP)
public final class ModuleIndexService {
    public Collection<RPsiModule> getModules(@Nullable String name, @NotNull Project project, @NotNull GlobalSearchScope scope) {
        List<RPsiModule> result = new ArrayList<>();

        if (name != null) {
            if (name.contains(".")) {
                // inner component
                return ModuleFqnIndex.getElements(name, project, scope);
            } else {
                // top level (file) component
                for (VirtualFile file : FileModuleIndex.getContainingFiles(name, scope)) {
                    PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
                    if (psiFile instanceof FileBase) {
                        result.add((FileBase) psiFile);
                    }
                }
            }
        }

        return result;
    }
}
