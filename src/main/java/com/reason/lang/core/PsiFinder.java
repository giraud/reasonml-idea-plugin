package com.reason.lang.core;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.reason.bs.Bucklescript;
import com.reason.bs.BucklescriptProjectComponent;
import com.reason.ide.files.*;
import com.reason.ide.search.IndexKeys;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiModule;
import com.reason.lang.core.psi.impl.PsiFileModuleImpl;
import gnu.trove.THashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.reason.lang.core.MlScope.all;
import static com.reason.lang.core.MlScope.inBsconfig;

public class PsiFinder {

    @NotNull
    public static Collection<PsiModule> findModules(@NotNull Project project, @NotNull String name, @NotNull MlFileType fileType, MlScope scope) {
        ArrayList<PsiModule> inConfig = new ArrayList<>();
        ArrayList<PsiModule> other = new ArrayList<>();

        Bucklescript bucklescript = BucklescriptProjectComponent.getInstance(project);

        Collection<PsiModule> modules = StubIndex.getElements(IndexKeys.MODULES, name, project, GlobalSearchScope.allScope(project), PsiModule.class);
        if (!modules.isEmpty()) {
            for (PsiModule module : modules) {
                boolean keepFile;

                FileBase containingFile = (FileBase) module.getContainingFile();
                VirtualFile virtualFile = containingFile.getVirtualFile();
                FileType moduleFileType = virtualFile.getFileType();

                if (fileType == MlFileType.implementationOnly) {
                    keepFile = moduleFileType instanceof RmlFileType || moduleFileType instanceof OclFileType;
                } else if (fileType == MlFileType.interfaceOnly) {
                    keepFile = moduleFileType instanceof RmlInterfaceFileType || moduleFileType instanceof OclInterfaceFileType;
                } else {
                    // use interface if there is one, implementation otherwise ... always the case ????
                    // we need a better way (cache through VirtualFileListener ?) to find that info
                    if (moduleFileType instanceof RmlInterfaceFileType || moduleFileType instanceof OclInterfaceFileType) {
                        keepFile = true;
                    } else {
                        String nameWithoutExtension = virtualFile.getNameWithoutExtension();
                        String extension = moduleFileType instanceof RmlFileType ? RmlInterfaceFileType.INSTANCE.getDefaultExtension() : OclInterfaceFileType.INSTANCE.getDefaultExtension();
                        Collection<VirtualFile> interfaceFiles = FilenameIndex.getVirtualFilesByName(project, nameWithoutExtension + "." + extension, GlobalSearchScope.allScope(project));
                        keepFile = interfaceFiles.isEmpty();
                    }
                }

                if (keepFile) {
                    if (bucklescript.isDependency(virtualFile.getCanonicalPath())) {
                        inConfig.add(module);
                    } else {
                        other.add(module);
                    }
                }
            }
        }

        if (scope == all) {
            inConfig.addAll(other);
        }

        return inConfig;
    }

    @Nullable
    public static PsiModule findModule(@NotNull Project project, @NotNull String name, @NotNull MlFileType fileType, MlScope scope) {
        Collection<PsiModule> modules = findModules(project, name, fileType, scope);
        if (!modules.isEmpty()) {
            return modules.iterator().next();
        }

        return null;
    }

    @Nullable
    public static PsiModule findFileModule(Project project, String name) {
        PsiModule module = findModule(project, name, MlFileType.interfaceOrImplementation, inBsconfig);
        if (module instanceof PsiFileModuleImpl) {
            return module;
        }

        return null;
    }

    public static Collection<PsiLet> findLets(Project project, String lowerName) {
        ArrayList<PsiLet> result = new ArrayList<>();

        Collection<PsiLet> lets = StubIndex.getElements(IndexKeys.LETS, lowerName, project, GlobalSearchScope.allScope(project), PsiLet.class);
        for (PsiLet let : lets) {
            String canonicalPath = let.getContainingFile().getVirtualFile().getCanonicalPath();
            Bucklescript bucklescript = BucklescriptProjectComponent.getInstance(project);
            if (bucklescript.isDependency(canonicalPath)) {
                result.add(let);
            }
        }

        return result;
    }

    @NotNull
    public static List<PsiModule> findFileModules(@NotNull Project project, @NotNull MlFileType fileType) {
        List<PsiModule> result = new ArrayList<>();
        Bucklescript bucklescript = BucklescriptProjectComponent.getInstance(project);

        Map<String, PsiModule> files = new THashMap<>();
        Collection<VirtualFile> rmiFiles;
        Collection<VirtualFile> rmlFiles;
        Collection<VirtualFile> ociFiles;
        Collection<VirtualFile> oclFiles;

        if (fileType != MlFileType.implementationOnly) {
            rmiFiles = FilenameIndex.getAllFilesByExt(project, RmlInterfaceFileType.INSTANCE.getDefaultExtension());
            ociFiles = FilenameIndex.getAllFilesByExt(project, OclInterfaceFileType.INSTANCE.getDefaultExtension());

            for (VirtualFile virtualFile : rmiFiles) {
                String canonicalPath = virtualFile.getCanonicalPath();
                if (bucklescript.isDependency(canonicalPath)) {
                    PsiFile file = PsiManager.getInstance(project).findFile(virtualFile);
                    if (file != null) {
                        PsiModule module = ((FileBase) file).asModule();
                        if (module != null) {
                            files.put(canonicalPath, module);
                        }
                    }
                }
            }

            for (VirtualFile virtualFile : ociFiles) {
                String canonicalPath = virtualFile.getCanonicalPath();
                if (bucklescript.isDependency(canonicalPath)) {
                    PsiFile file = PsiManager.getInstance(project).findFile(virtualFile);
                    if (file != null) {
                        PsiModule module = ((FileBase) file).asModule();
                        if (module != null) {
                            files.put(canonicalPath, module);
                        }
                    }
                }
            }

            result.addAll(files.values());
        }

        if (fileType != MlFileType.interfaceOnly) {
            rmlFiles = FilenameIndex.getAllFilesByExt(project, RmlFileType.INSTANCE.getDefaultExtension());
            oclFiles = FilenameIndex.getAllFilesByExt(project, OclFileType.INSTANCE.getDefaultExtension());

            for (VirtualFile virtualFile : rmlFiles) {
                String canonicalPath = virtualFile.getCanonicalPath();
                if (canonicalPath != null && bucklescript.isDependency(canonicalPath)) {
                    boolean keep = true;
                    PsiFile file = PsiManager.getInstance(project).findFile(virtualFile);

                    if (fileType != MlFileType.implementationOnly) {
                        String canonicalInterface = canonicalPath.replace("." + RmlFileType.INSTANCE.getDefaultExtension(), "." + RmlInterfaceFileType.INSTANCE.getDefaultExtension());
                        if (files.containsKey(canonicalInterface)) {
                            keep = false;
                        }
                    }

                    if (keep) {
                        if (file instanceof FileBase) {
                            PsiModule module = ((FileBase) file).asModule();
                            if (module != null) {
                                result.add(module);
                            }
                        }
                    }
                }
            }

            for (VirtualFile virtualFile : oclFiles) {
                String canonicalPath = virtualFile.getCanonicalPath();
                if (canonicalPath != null && bucklescript.isDependency(canonicalPath)) {
                    boolean keep = true;
                    PsiFile file = PsiManager.getInstance(project).findFile(virtualFile);

                    if (fileType != MlFileType.implementationOnly) {
                        String canonicalInterface = canonicalPath.replace("." + OclFileType.INSTANCE.getDefaultExtension(), "." + OclInterfaceFileType.INSTANCE.getDefaultExtension());
                        if (files.containsKey(canonicalInterface)) {
                            keep = false;
                        }
                    }

                    if (keep) {
                        if (file instanceof FileBase) {
                            PsiModule module = ((FileBase) file).asModule();
                            if (module != null) {
                                result.add(module);
                            }
                        }
                    }
                }
            }
        }

        return result;
    }
}
