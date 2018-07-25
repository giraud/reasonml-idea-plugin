package com.reason.ide.go;

import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.util.ArrayUtil;
import com.reason.ide.files.FileBase;
import com.reason.ide.search.IndexKeys;
import com.reason.lang.core.MlFileType;
import com.reason.lang.core.PsiFinder;
import com.reason.lang.core.psi.PsiModule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public class MlModuleContributor implements ChooseByNameContributor {
    @NotNull
    @Override
    public NavigationItem[] getItemsByName(String name, String pattern, Project project, boolean includeNonProjectItems) {
        ArrayList<NavigationItem> items = new ArrayList<>();

        GlobalSearchScope scope = includeNonProjectItems ? GlobalSearchScope.allScope(project) : GlobalSearchScope.projectScope(project);
        FileBase fileModule = PsiFinder.getInstance().findFileModule(project, name, scope);
        if (fileModule != null) {
            items.add(new MlModuleNavigationItem(fileModule, fileModule.asModuleName()));
        }

        Collection<PsiModule> modules = PsiFinder.getInstance().findModules(project, name, scope, MlFileType.interfaceOrImplementation);
        for (PsiModule element : modules) {
            items.add(new MlModuleNavigationItem(element, element.getName()));
        }

        return items.toArray(new NavigationItem[0]);
    }

    @NotNull
    @Override
    public String[] getNames(Project project, boolean includeNonProjectItems) {
        ArrayList<String> modules = new ArrayList<>();

        Collection<String> fileModules = PsiFinder.getInstance().findFileModules(project, MlFileType.implementationOnly).stream().map(FileBase::asModuleName).collect(Collectors.toList());
        Collection<String> allModuleNames = StubIndex.getInstance().getAllKeys(IndexKeys.MODULES, project);

        modules.addAll(fileModules);
        modules.addAll(allModuleNames);

        return ArrayUtil.toStringArray(modules);
    }

    private static class MlModuleNavigationItem implements NavigationItem {
        private final PsiElement m_element;
        private final String m_name;

        MlModuleNavigationItem(PsiElement element, String name) {
            m_element = element;
            m_name = name;
        }

        @Nullable
        @Override
        public String getName() {
            return m_name;
        }

        @Nullable
        @Override
        public ItemPresentation getPresentation() {
            return ((NavigationItem) m_element).getPresentation();
        }

        @Override
        public void navigate(boolean requestFocus) {
            ((Navigatable) m_element).navigate(requestFocus);
        }

        @Override
        public boolean canNavigate() {
            return true;
        }

        @Override
        public boolean canNavigateToSource() {
            return true;
        }
    }
}