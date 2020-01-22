package com.reason.ide.go;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.navigation.ChooseByNameContributorEx;
import com.intellij.navigation.GotoClassContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiQualifiedNamedElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.Processor;
import com.intellij.util.indexing.FindSymbolParameters;
import com.intellij.util.indexing.IdFilter;
import com.reason.ide.files.FileBase;
import com.reason.ide.search.PsiFinder;
import com.reason.ide.search.index.ModuleIndex;
import com.reason.lang.core.ORFileType;
import com.reason.lang.core.psi.PsiModule;

// Implements the goto class
public class ORModuleContributor implements GotoClassContributor, ChooseByNameContributorEx {

    @Override
    public void processNames(@NotNull Processor<String> processor, @NotNull GlobalSearchScope scope, @Nullable IdFilter filter) {
        Project project = scope.getProject();
        if (project != null) {
            ModuleIndex.getInstance().processAllKeys(project, processor);
        }
    }

    @Override
    public void processElementsWithName(@NotNull String name, @NotNull Processor<NavigationItem> processor, @NotNull FindSymbolParameters parameters) {
        Project project = parameters.getProject();
        GlobalSearchScope scope = parameters.getSearchScope();

        for (PsiModule psiModule : PsiFinder.getInstance(project).findModulesbyName(name, ORFileType.both, null, scope)) {
            processor.process(psiModule);
        }
    }

    @Nullable
    @Override
    public String getQualifiedName(NavigationItem item) {
        if (item instanceof FileBase) {
            return ((FileBase) item).getModuleName();
        } else if (item instanceof PsiQualifiedNamedElement) {
            return ((PsiQualifiedNamedElement) item).getQualifiedName();
        }
        return null;
    }

    @Nullable
    @Override
    public String getQualifiedNameSeparator() {
        return null;
    }
}