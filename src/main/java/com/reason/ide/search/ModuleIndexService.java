package com.reason.ide.search;

import com.intellij.openapi.application.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.*;
import com.intellij.psi.search.*;
import com.intellij.util.indexing.*;
import com.reason.ide.files.*;
import com.reason.ide.search.index.*;
import com.reason.lang.core.psi.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class ModuleIndexService {
    private static final Log LOG = Log.create("index.toplevelservice");

    private final FileModuleIndex m_index;

    public ModuleIndexService() {
        m_index = FileModuleIndex.getInstance();
    }

    public static ModuleIndexService getService() {
        return ApplicationManager.getApplication().getService(ModuleIndexService.class);
    }

    public Collection<RPsiModule> getModules(@Nullable String name, @NotNull Project project, @NotNull GlobalSearchScope scope) {
        List<RPsiModule> result = new ArrayList<>();

        if (name != null) {
            if (name.contains(".")) {
                // inner component
                return ModuleFqnIndex.getElements(name, project, scope);
            } else {
                // top level (file) component
                for (VirtualFile file : FileBasedIndex.getInstance().getContainingFiles(m_index.getName(), name, scope)) {
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
