package com.reason.ide.go;

import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.util.ArrayUtil;
import com.reason.ide.files.FileBase;
import com.reason.ide.search.FileModuleIndexService;
import com.reason.ide.search.IndexKeys;
import com.reason.ide.search.PsiFinder;
import com.reason.lang.core.ORFileType;
import com.reason.lang.core.psi.PsiModule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import static com.intellij.psi.search.GlobalSearchScope.allScope;
import static com.intellij.psi.search.GlobalSearchScope.projectScope;

// Implements the goto class
public class ORModuleContributor implements ChooseByNameContributor {
    @NotNull
    @Override
    public NavigationItem[] getItemsByName(@NotNull String name, String pattern, @NotNull Project project, boolean includeNonProjectItems) {
        ArrayList<NavigationItem> items = new ArrayList<>();

        PsiManager psiManager = PsiManager.getInstance(project);
        GlobalSearchScope scope = includeNonProjectItems ? allScope(project) : projectScope(project);

        // Find all files with name
        for (VirtualFile virtualFile : FileModuleIndexService.getService().getFilesWithName(name, scope)) {
            FileBase fileModule = (FileBase) psiManager.findFile(virtualFile);
            if (fileModule != null) {
                items.add(new MlModuleNavigationItem(fileModule, fileModule.asModuleName()));
            }

        }

        Collection<PsiModule> modules = PsiFinder.getInstance().findModules(project, name, scope, ORFileType.interfaceOrImplementation);
        for (PsiModule element : modules) {
            items.add(new MlModuleNavigationItem(element, element.getName()));
        }

        return items.toArray(new NavigationItem[0]);
    }

    @NotNull
    @Override
    public String[] getNames(@NotNull Project project, boolean includeNonProjectItems) {
        ArrayList<String> modules = new ArrayList<>();

        Collection<String> fileModules = FileModuleIndexService.getService().getAllModules(project);
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