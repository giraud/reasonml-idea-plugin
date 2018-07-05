package com.reason.lang.core;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiQualifiedNamedElement;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.stubs.StubIndexKey;
import com.reason.build.bs.Bucklescript;
import com.reason.build.bs.BucklescriptManager;
import com.reason.ide.Debug;
import com.reason.ide.files.*;
import com.reason.ide.search.IndexKeys;
import com.reason.ide.search.ModuleFqnIndex;
import com.reason.lang.core.psi.*;
import gnu.trove.THashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.intellij.psi.search.GlobalSearchScope.projectScope;
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

        Collection<PsiModule> modules = StubIndex.getElements(IndexKeys.MODULES, name, project, projectScope(project), PsiModule.class);
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
                        String extension = moduleFileType instanceof RmlFileType ? RmlInterfaceFileType.INSTANCE.getDefaultExtension() :
                                OclInterfaceFileType.INSTANCE.getDefaultExtension();
                        Collection<VirtualFile> interfaceFiles = FilenameIndex
                                .getVirtualFilesByName(project, nameWithoutExtension + "." + extension, projectScope(project));
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

        Collection<PsiModule> result = new ArrayList<>(inConfig.values());
        if (scope == all) {
            result.addAll(other.values());
        }

        return result;
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
    public PsiModule findRealFileModule(Project project, String name) {
        // extract first token of path
        String[] names = name.split("\\.");

        PsiModule module = findModule(project, names[0], MlFileType.interfaceOrImplementation, inBsconfig);
        // zzz
        //if (module instanceof Psi File Module Impl) {
        //    if (1 < names.length) {
        //        PsiModule currentModule = module;
        //        for (int i = 1; i < names.length; i++) {
        //            String innerModuleName = names[i];
        //            currentModule = currentModule.getModule(innerModuleName);
        //            if (currentModule == null) {
        //                return null;
        //            }
        //        }
        //        return currentModule;
        //    }
        //
        //    return module;
        //}

        return null;
    }

    @NotNull
    public Collection<PsiLet> findLets(@NotNull Project project, @NotNull String name, @NotNull MlFileType fileType, MlScope scope) {
        Map<String/*qn*/, PsiLet> inConfig = new THashMap<>();
        Map<String/*qn*/, PsiLet> other = new THashMap<>();

        findLowerSymbols("lets", inConfig, other, project, name, fileType, scope, IndexKeys.LETS, PsiLet.class);

        List<PsiLet> result = new ArrayList<>(inConfig.values());
        if (scope == all) {
            result.addAll(other.values());
        }

        return result;
    }

    @NotNull
    public Collection<PsiVal> findVals(@NotNull Project project, @NotNull String name, @NotNull MlFileType fileType, MlScope scope) {
        Map<String/*qn*/, PsiVal> inConfig = new THashMap<>();
        Map<String/*qn*/, PsiVal> other = new THashMap<>();

        findLowerSymbols("vals", inConfig, other, project, name, fileType, scope, IndexKeys.VALS, PsiVal.class);

        List<PsiVal> result = new ArrayList<>(inConfig.values());
        if (scope == all) {
            result.addAll(other.values());
        }

        return result;
    }

    @NotNull
    public Collection<PsiType> findTypes(@NotNull Project project, @NotNull String name, @NotNull MlFileType fileType, MlScope scope) {
        Map<String/*qn*/, PsiType> inConfig = new THashMap<>();
        Map<String/*qn*/, PsiType> other = new THashMap<>();

        findLowerSymbols("types", inConfig, other, project, name, fileType, scope, IndexKeys.TYPES, PsiType.class);

        List<PsiType> result = new ArrayList<>(inConfig.values());
        if (scope == all) {
            result.addAll(other.values());
        }

        return result;
    }

    @NotNull
    public Collection<PsiExternal> findExternals(@NotNull Project project, @NotNull String name, @NotNull MlFileType fileType, MlScope scope) {
        Map<String/*qn*/, PsiExternal> inConfig = new THashMap<>();
        Map<String/*qn*/, PsiExternal> other = new THashMap<>();

        findLowerSymbols("externals", inConfig, other, project, name, fileType, scope, IndexKeys.EXTERNALS, PsiExternal.class);

        List<PsiExternal> result = new ArrayList<>(inConfig.values());
        if (scope == all) {
            result.addAll(other.values());
        }

        return result;
    }

    private <T extends PsiQualifiedNamedElement> void findLowerSymbols(@NotNull String debugName, @NotNull Map<String/*qn*/, T> inConfig, @NotNull Map<String/*qn*/, T> other, @NotNull Project project, @NotNull String name, @NotNull MlFileType fileType, MlScope scope, StubIndexKey<String, T> indexKey, Class<T> clazz) {
        if (m_log.isDebugEnabled()) {
            m_log.debug("Find " + debugName + " name", name, scope.name());
        }

        Bucklescript bucklescript = BucklescriptManager.getInstance(project);

        Collection<T> items = StubIndex.getElements(indexKey, name, project, projectScope(project), clazz);
        if (items.isEmpty()) {
            if (m_log.isDebugEnabled()) {
                m_log.debug("  No " + debugName + " found");
            }
        } else {
            if (m_log.isDebugEnabled()) {
                m_log.debug("  " + debugName + " found", items.size());
            }
            for (T item : items) {
                boolean keepFile;

                FileBase containingFile = (FileBase) item.getContainingFile();
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
                        String extension = moduleFileType instanceof RmlFileType ? RmlInterfaceFileType.INSTANCE.getDefaultExtension() :
                                OclInterfaceFileType.INSTANCE.getDefaultExtension();
                        Collection<VirtualFile> interfaceFiles = FilenameIndex
                                .getVirtualFilesByName(project, nameWithoutExtension + "." + extension, projectScope(project));
                        keepFile = interfaceFiles.isEmpty();
                    }
                }

                if (keepFile) {
                    if (bucklescript.isDependency(virtualFile.getCanonicalPath())) {
                        m_log.debug("    keep (in config)", item);
                        inConfig.put(item.getQualifiedName(), item);
                    } else {
                        m_log.debug("    keep (not in config)", item);
                        other.put(item.getQualifiedName(), item);
                    }
                }
            }
        }
    }

    @NotNull
    public Collection<FileBase> findFileModules(@NotNull Project project, @NotNull MlFileType fileType) {
        // All file names are unique in a project, we use the file name in the key
        // Need a better algo to prioritise the paths and not overwrite the correct resolved files
        Map<String, FileBase> result = new THashMap<>();
        Bucklescript bucklescript = BucklescriptManager.getInstance(project);

        Map<String, FileBase> files = new THashMap<>();
        Collection<VirtualFile> rmiFiles;
        Collection<VirtualFile> rmlFiles;
        Collection<VirtualFile> ociFiles;
        Collection<VirtualFile> oclFiles;

        PsiManager psiManager = PsiManager.getInstance(project);
        GlobalSearchScope searchScope = GlobalSearchScope.projectScope(project);

        // List all interface files
        if (fileType != MlFileType.implementationOnly) {
            rmiFiles = FilenameIndex.getAllFilesByExt(project, RmlInterfaceFileType.INSTANCE.getDefaultExtension(), searchScope);
            ociFiles = FilenameIndex.getAllFilesByExt(project, OclInterfaceFileType.INSTANCE.getDefaultExtension(), searchScope);

            for (VirtualFile virtualFile : rmiFiles) {
                PsiFile file = psiManager.findFile(virtualFile);
                if (bucklescript.isDependency(file)) {
                    files.put(virtualFile.getName(), (FileBase) file);
                }
            }

            for (VirtualFile virtualFile : ociFiles) {
                PsiFile file = psiManager.findFile(virtualFile);
                if (file != null) {
                    files.put(virtualFile.getName(), (FileBase) file);
                }
            }

            result.putAll(files);
        }

        // List all implementation files
        if (fileType != MlFileType.interfaceOnly) {
            rmlFiles = FilenameIndex.getAllFilesByExt(project, RmlFileType.INSTANCE.getDefaultExtension());
            oclFiles = FilenameIndex.getAllFilesByExt(project, OclFileType.INSTANCE.getDefaultExtension());

            for (VirtualFile virtualFile : rmlFiles) {
                boolean keep = true;
                PsiFile file = psiManager.findFile(virtualFile);

                if (fileType != MlFileType.implementationOnly) {
                    String interfaceName = virtualFile.getNameWithoutExtension() + "." + RmlInterfaceFileType.INSTANCE.getDefaultExtension();
                    if (files.containsKey(interfaceName)) {
                        keep = false;
                    }
                }

                if (keep) {
                    if (file instanceof FileBase) {
                        result.put(virtualFile.getName(), (FileBase) file);
                    }
                }
            }

            for (VirtualFile virtualFile : oclFiles) {
                boolean keep = true;
                PsiFile file = psiManager.findFile(virtualFile);

                if (fileType != MlFileType.implementationOnly) {
                    String interfaceName = virtualFile.getNameWithoutExtension() + "." + OclInterfaceFileType.INSTANCE.getDefaultExtension();
                    if (files.containsKey(interfaceName)) {
                        keep = false;
                    }
                }

                if (keep) {
                    if (file instanceof FileBase) {
                        result.put(file.getName(), (FileBase) file);
                    }
                }
            }
        }

        return result.values();
    }

    @Nullable
    public PsiModule findModuleAlias(@NotNull Project project, @Nullable String moduleQname) {
        if (moduleQname == null) {
            return null;
        }

        Collection<PsiModule> modules = ModuleFqnIndex.getInstance().get(moduleQname.hashCode(), project, projectScope(project));
        if (!modules.isEmpty()) {
            PsiModule moduleReference = modules.iterator().next();
            String alias = moduleReference.getAlias();
            if (alias != null) {
                modules = ModuleFqnIndex.getInstance().get(alias.hashCode(), project, projectScope(project));
                if (!modules.isEmpty()) {
                    PsiModule next = modules.iterator().next();
                    if (next != null) {
                        PsiModule nextModuleAlias = findModuleAlias(project, next.getQualifiedName());
                        return nextModuleAlias == null ? next : nextModuleAlias;
                    }
                }
            }
        }
        return null;
    }
}
