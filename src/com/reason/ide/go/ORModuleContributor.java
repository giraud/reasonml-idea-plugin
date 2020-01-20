package com.reason.ide.go;

import java.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.util.ArrayUtil;
import com.reason.ide.search.FileModuleIndexService;
import com.reason.ide.search.PsiFinder;
import com.reason.ide.search.index.IndexKeys;
import com.reason.lang.core.ORFileType;
import com.reason.lang.core.psi.PsiFakeModule;
import com.reason.lang.core.psi.PsiModule;

import static com.intellij.psi.search.GlobalSearchScope.*;

// Implements the goto class
public class ORModuleContributor implements ChooseByNameContributor {
    @NotNull
    @Override
    public NavigationItem[] getItemsByName(@NotNull String name, String pattern, @NotNull Project project, boolean includeNonProjectItems) {
        ArrayList<NavigationItem> items = new ArrayList<>();

        GlobalSearchScope scope = includeNonProjectItems ? allScope(project) : projectScope(project);

        Collection<PsiModule> modules = PsiFinder.getInstance(project).findModules(name, ORFileType.both, scope);
        for (PsiModule module : modules) {
            String moduleName = module instanceof PsiFakeModule ? module.getContainingFile().getName() : module.getName();
            items.add(new MlModuleNavigationItem(module, moduleName));
        }

        return items.toArray(new NavigationItem[0]);
    }

    @NotNull
    @Override
    public String[] getNames(@NotNull Project project, boolean includeNonProjectItems) {
        Set<String> modules = new HashSet<>();

        Collection<String> fileModules = FileModuleIndexService.getService().getAllFileModules(project);
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