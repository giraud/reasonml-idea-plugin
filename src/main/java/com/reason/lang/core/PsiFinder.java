package com.reason.lang.core;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.reason.bs.Bucklescript;
import com.reason.bs.BucklescriptManager;
import com.reason.ide.Debug;
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

public final class PsiFinder {

    private static final PsiFinder INSTANCE = new PsiFinder();

    private final Debug m_log = new Debug(Logger.getInstance("ReasonML.finder"));

    public static PsiFinder getInstance() {
        return INSTANCE;
    }

    @NotNull
    public Collection<PsiModule> findModules(@NotNull Project project, @NotNull String name, @NotNull MlFileType fileType, MlScope scope) {
        m_log.debug("Find modules, name", name, scope.name());

        Map<String/*qn*/, PsiModule> inConfig = new THashMap<>();
        Map<String/*qn*/, PsiModule> other = new THashMap<>();

        Bucklescript bucklescript = BucklescriptManager.getInstance(project);

        Collection<PsiModule> modules = StubIndex.getElements(IndexKeys.MODULES, name, project, GlobalSearchScope.allScope(project), PsiModule.class);
        if (modules.isEmpty()) {
            m_log.debug("  No modules found");
        } else {
            m_log.debug("  modules found", modules.size());
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
                        m_log.debug("    keep (in config)", module);
                        inConfig.put(module.getQualifiedName(), module);
                    } else {
                        m_log.debug("    keep (not in config)", module);
                        other.put(module.getQualifiedName(), module);
                    }
                }
            }
        }

        if (scope == all) {
            other.putAll(inConfig);
            inConfig = other;
        }

        return inConfig.values();
    }

    @Nullable
    public PsiModule findModule(@NotNull Project project, @NotNull String name, @NotNull MlFileType fileType, MlScope scope) {
        Collection<PsiModule> modules = findModules(project, name, fileType, scope);
        if (!modules.isEmpty()) {
            return modules.iterator().next();
        }

        return null;
    }

    @Nullable
    public PsiModule findFileModule(Project project, String name) {
        PsiModule module = findModule(project, name, MlFileType.interfaceOrImplementation, inBsconfig);
        if (module instanceof PsiFileModuleImpl) {
            return module;
        }

        return null;
    }

    @NotNull
    public Collection<PsiLet> findLets(@NotNull Project project, @NotNull String lowerName, @NotNull MlFileType fileType, MlScope scope) {
        List<PsiLet> result = new ArrayList<>();

        Collection<PsiLet> lets = StubIndex.getElements(IndexKeys.LETS, lowerName, project, GlobalSearchScope.allScope(project), PsiLet.class);
        for (PsiLet let : lets) {
            String canonicalPath = let.getContainingFile().getVirtualFile().getCanonicalPath();
            Bucklescript bucklescript = BucklescriptManager.getInstance(project);
            if (bucklescript.isDependency(canonicalPath)) {
                result.add(let);
            }
        }

        return result;
    }

    @NotNull
    public Collection<PsiModule> findFileModules(@NotNull Project project, @NotNull MlFileType fileType) {
        // All file names are unique in a project, we use the file name in the key
        // Need a better algo to priorise the paths and not overwrite the correct resolved files
        Map<String, PsiModule> result = new THashMap<>();
        Bucklescript bucklescript = BucklescriptManager.getInstance(project);

        Map<String, PsiModule> files = new THashMap<>();
        Collection<VirtualFile> rmiFiles;
        Collection<VirtualFile> rmlFiles;
        Collection<VirtualFile> ociFiles;
        Collection<VirtualFile> oclFiles;

        // List all interface files
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
                            files.put(virtualFile.getName(), module);
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
                            files.put(virtualFile.getName(), module);
                        }
                    }
                }
            }

            result.putAll(files);
        }

        // List all implementation files
        if (fileType != MlFileType.interfaceOnly) {
            rmlFiles = FilenameIndex.getAllFilesByExt(project, RmlFileType.INSTANCE.getDefaultExtension());
            oclFiles = FilenameIndex.getAllFilesByExt(project, OclFileType.INSTANCE.getDefaultExtension());

            for (VirtualFile virtualFile : rmlFiles) {
                String canonicalPath = virtualFile.getCanonicalPath();
                if (canonicalPath != null && bucklescript.isDependency(canonicalPath)) {
                    boolean keep = true;
                    PsiFile file = PsiManager.getInstance(project).findFile(virtualFile);

                    if (fileType != MlFileType.implementationOnly) {
                        String interfaceName = virtualFile.getNameWithoutExtension() + "." + RmlInterfaceFileType.INSTANCE.getDefaultExtension();
                        if (files.containsKey(interfaceName)) {
                            keep = false;
                        }
                    }

                    if (keep) {
                        if (file instanceof FileBase) {
                            PsiModule module = ((FileBase) file).asModule();
                            if (module != null) {
                                result.put(virtualFile.getName(), module);
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
                        String interfaceName = virtualFile.getNameWithoutExtension() + "." + OclInterfaceFileType.INSTANCE.getDefaultExtension();
                        if (files.containsKey(interfaceName)) {
                            keep = false;
                        }
                    }

                    if (keep) {
                        if (file instanceof FileBase) {
                            PsiModule module = ((FileBase) file).asModule();
                            if (module != null) {
                                result.put(file.getName(), module);
                            }
                        }
                    }
                }
            }
        }

        return result.values();
    }
}
