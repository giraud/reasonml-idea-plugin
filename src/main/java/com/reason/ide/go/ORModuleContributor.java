package com.reason.ide.go;

import com.intellij.navigation.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.util.*;
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
            processor.process(
                    new NavigationItem() {
                        @Override
                        public String getName() {
                            return moduleDatum.getModuleName();
                        }

                        @Override
                        public ItemPresentation getPresentation() {
                            return new FilePresentation(moduleDatum, project);
                        }

                        @Override
                        public void navigate(boolean requestFocus) {
                            RPsiModule module = FileHelper.getPsiModule(moduleDatum, project);
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
                    }
            );
        }

        // Inner modules
        for (RPsiInnerModule module : ModuleIndex.getElements(name, project, scope)) {
            processor.process(new NavigationItem() {
                                  @Override
                                  public String getName() {
                                      return module.getName();
                                  }

                                  @Override
                                  public ItemPresentation getPresentation() {
                                      return new ModulePresentation(module);
                                  }

                                  @Override
                                  public void navigate(boolean requestFocus) {
                                      module.navigate(requestFocus);
                                  }

                                  @Override public boolean canNavigate() {
                                      return true;
                                  }

                                  @Override public boolean canNavigateToSource() {
                                      return true;
                                  }
                              }
            );
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

    private static class FilePresentation implements ItemPresentation {
        private final FileModuleData myItem;
        private final Project myProject;

        public FilePresentation(@NotNull FileModuleData moduleDatum, @NotNull Project project) {
            myItem = moduleDatum;
            myProject = project;
        }

        @Override
        public String getPresentableText() {
            return myItem.getModuleName();
        }

        @Override
        public String getLocationString() {
            return FileHelper.shortLocation(myItem.getPath(), myProject);
        }

        @Override
        public Icon getIcon(boolean unused) {
            return IconProvider.getDataModuleIcon(myItem);
        }
    }

    private static class ModulePresentation implements ItemPresentation {
        private final RPsiInnerModule myItem;

        public ModulePresentation(@NotNull RPsiInnerModule module) {
            myItem = module;
        }

        @Override
        public String getPresentableText() {
            return myItem.getModuleName();
        }

        @Override
        public String getLocationString() {
            return myItem.getQualifiedName();
        }

        @Override
        public Icon getIcon(boolean unused) {
            return PsiIconUtil.getProvidersIcon(myItem, Iconable.ICON_FLAG_VISIBILITY);
        }
    }
}
