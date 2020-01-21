package com.reason.ide.go;

import java.util.*;
import javax.swing.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.ArrayUtil;
import com.reason.Icons;
import com.reason.Platform;
import com.reason.ide.IconProvider;
import com.reason.ide.files.FileBase;
import com.reason.ide.search.PsiFinder;
import com.reason.ide.search.index.ModuleIndex;
import com.reason.lang.core.ORFileType;
import com.reason.lang.core.psi.PsiInnerModule;
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
            items.add(new MlModuleNavigationItem(module));
        }

        return items.toArray(new NavigationItem[0]);
    }

    @NotNull
    @Override
    public String[] getNames(@NotNull Project project, boolean includeNonProjectItems) {
        Collection<String> allModuleNames = ModuleIndex.getInstance().getAllKeys(project);
        return ArrayUtil.toStringArray(new HashSet<>(allModuleNames));
    }

    private static class MlModuleNavigationItem implements NavigationItem {
        private final PsiModule m_element;

        MlModuleNavigationItem(PsiModule element) {
            m_element = element;
        }

        @Nullable
        @Override
        public String getName() {
            return m_element.getName();
        }

        @Nullable
        @Override
        public ItemPresentation getPresentation() {
            if (m_element instanceof PsiInnerModule) {
                return new ItemPresentation() {
                    @Nullable
                    @Override
                    public String getPresentableText() {
                        return m_element.getName();
                    }

                    @NotNull
                    @Override
                    public String getLocationString() {
                        return m_element.getQualifiedName() + ", " + Platform
                                .removeProjectDir(m_element.getProject(), m_element.getContainingFile().getVirtualFile().getPath());
                    }

                    @NotNull
                    @Override
                    public Icon getIcon(boolean unused) {
                        PsiFile containingFile = m_element.getContainingFile();
                        Icon icon = IconProvider.getFileModuleIcon((FileBase) containingFile);
                        return icon == null ? Icons.MODULE : icon;
                    }
                };
            }

            return m_element.getPresentation();
        }

        @Override
        public void navigate(boolean requestFocus) {
            m_element.navigate(requestFocus);
        }

        @Override
        public boolean canNavigate() {
            return m_element.canNavigate();
        }

        @Override
        public boolean canNavigateToSource() {
            return m_element.canNavigateToSource();
        }
    }
}