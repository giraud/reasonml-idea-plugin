package com.reason.ide.go;

import com.intellij.navigation.*;
import com.intellij.openapi.project.*;
import com.intellij.psi.*;
import com.intellij.psi.search.*;
import com.intellij.psi.stubs.*;
import com.intellij.util.*;
import com.intellij.util.indexing.*;
import com.reason.ide.files.*;
import com.reason.ide.search.*;
import com.reason.ide.search.index.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import icons.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import javax.swing.*;

// Implements the goto class
public class ORModuleContributor implements GotoClassContributor, ChooseByNameContributorEx {
    @Override
    public void processNames(@NotNull Processor<? super String> processor, @NotNull GlobalSearchScope scope, @Nullable IdFilter filter) {
        Project project = scope.getProject();
        if (project != null) {
            StubIndex.getInstance().processAllKeys(IndexKeys.MODULES, project, processor);
        }
    }

    @Override
    public void processElementsWithName(@NotNull String name, @NotNull Processor<? super NavigationItem> processor, @NotNull FindSymbolParameters parameters) {
        Project project = parameters.getProject();
        GlobalSearchScope scope = parameters.getSearchScope();

        for (PsiModule psiModule : project.getService(PsiFinder.class).findModulesbyName(name, ORFileType.both, null)) {
            NavigationItem element = psiModule;
            if (psiModule instanceof PsiInnerModule) {
                Icon icon = psiModule.isInterface() ? ORIcons.INNER_MODULE_INTF : ORIcons.INNER_MODULE;

                element = new ModuleDelegatePresentation(
                        psiModule,
                        new ItemPresentation() {
                            @Override
                            public @Nullable String getPresentableText() {
                                return psiModule.getName();
                            }

                            @Override
                            public String getLocationString() {
                                return Joiner.join(".", psiModule.getPath());
                            }

                            @Override
                            public Icon getIcon(boolean unused) {
                                return icon;
                            }
                        });
            }

            processor.process(element);
        }
    }

    @Override
    public @Nullable String getQualifiedName(NavigationItem item) {
        if (item instanceof FileBase) {
            return ((FileBase) item).getModuleName();
        } else if (item instanceof PsiQualifiedNamedElement) {
            return ((PsiQualifiedNamedElement) item).getQualifiedName();
        }
        return null;
    }

    @Override
    public @Nullable String getQualifiedNameSeparator() {
        return null;
    }
}
