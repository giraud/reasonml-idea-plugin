package com.reason.ide.go;

import com.intellij.navigation.*;
import com.intellij.openapi.project.*;
import com.intellij.psi.*;
import com.intellij.psi.search.*;
import com.intellij.psi.stubs.*;
import com.intellij.util.*;
import com.intellij.util.indexing.*;
import com.reason.ide.*;
import com.reason.ide.files.*;
import com.reason.ide.search.*;
import com.reason.ide.search.index.*;
import com.reason.lang.core.psi.*;
import org.jetbrains.annotations.*;

import javax.swing.*;
import java.util.*;

// Implements the goto class
public class ORModuleContributor implements GotoClassContributor, ChooseByNameContributorEx {
    @Override
    public void processNames(@NotNull Processor<? super String> processor, @NotNull GlobalSearchScope scope, @Nullable IdFilter filter) {
        Project project = scope.getProject();
        if (project != null) {
            List<String> keys = new ArrayList<>();

            FileModuleIndex fileModuleIndex = FileModuleIndex.getInstance();
            if (fileModuleIndex != null) {
                keys.addAll(FileBasedIndex.getInstance().getAllKeys(fileModuleIndex.getName(), project));
            }
            keys.addAll(StubIndex.getInstance().getAllKeys(IndexKeys.MODULES, project));

            for (String key : keys) {
                processor.process(key);
            }
        }
    }

    @Override
    public void processElementsWithName(@NotNull String name, @NotNull Processor<? super NavigationItem> processor, @NotNull FindSymbolParameters parameters) {
        Project project = parameters.getProject();
        GlobalSearchScope scope = GlobalSearchScope.allScope(project);

        // Top level modules
        for (FileModuleData moduleDatum : FileModuleIndexService.getService().getTopModuleData(name, scope)) {
            processor.process(new FileModuleDataNavigationItem(moduleDatum, project)
            );
        }

        // Inner modules
        for (RPsiInnerModule module : ModuleIndex.getElements(name, project, scope)) {
            processor.process(module);
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

    private static class FileModuleDataPresentation implements ItemPresentation {
        private final FileModuleData myItem;

        public FileModuleDataPresentation(@NotNull FileModuleData moduleDatum) {
            myItem = moduleDatum;
        }

        @Override
        public String getPresentableText() {
            return myItem.getModuleName();
        }

        @Override
        public @Nullable String getLocationString() {
            return null;
        }

        @Override
        public Icon getIcon(boolean unused) {
            return IconProvider.getDataModuleIcon(myItem);
        }
    }

    public static class FileModuleDataNavigationItem implements NavigationItem {
        private final FileModuleData myData;
        private final Project myProject;

        public FileModuleDataNavigationItem(FileModuleData moduleDatum, Project project) {
            myData = moduleDatum;
            myProject = project;
        }

        @Override
        public String getName() {
            return myData.getModuleName();
        }

        @Override
        public ItemPresentation getPresentation() {
            return new FileModuleDataPresentation(myData);
        }

        @Override
        public void navigate(boolean requestFocus) {
            RPsiModule module = FileHelper.getPsiModule(myData, myProject);
            if (module instanceof FileBase) {
                ((FileBase) module).navigate(requestFocus);
            }
        }

        @Override
        public boolean canNavigate() {
            return true;
        }

        @Override
        public boolean canNavigateToSource() {
            return true;
        }

        public String getLocation() {
            return FileHelper.shortLocation(myData.getPath(), myProject);
        }

        public Icon getLocationIcon() {
            return IconProvider.getDataModuleFileIcon(myData);
        }
    }
}
