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

import java.util.Collection;
import java.util.Map;

import static com.intellij.psi.search.GlobalSearchScope.projectScope;

public final class PsiFinder {

    private static final PsiFinder INSTANCE = new PsiFinder();

    private final Debug m_debug = new Debug(Logger.getInstance("ReasonML.finder"));

    public static PsiFinder getInstance() {
        return INSTANCE;
    }

    @NotNull
    public Collection<PsiModule> findModules(@NotNull Project project, @NotNull String name, @NotNull MlFileType fileType) {
        m_debug.debug("Find modules, name", name);

        Map<String/*qn*/, PsiModule> inConfig = new THashMap<>();
        Bucklescript bucklescript = BucklescriptManager.getInstance(project);

        Collection<PsiModule> modules = StubIndex.getElements(IndexKeys.MODULES, name, project, projectScope(project), PsiModule.class);
        if (modules.isEmpty()) {
            m_debug.debug("  No modules found");
        } else {
            m_debug.debug("  modules found", modules.size());
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

                if (keepFile && bucklescript.isDependency(virtualFile.getCanonicalPath())) {
                    m_debug.debug("    keep (in config)", module);
                    inConfig.put(module.getQualifiedName(), module);
                }
            }
        }

        return inConfig.values();
    }

    @Nullable
    public PsiModule findModule(@NotNull Project project, @NotNull String name, @NotNull MlFileType fileType) {
        Collection<PsiModule> modules = findModules(project, name, fileType);
        if (!modules.isEmpty()) {
            return modules.iterator().next();
        }

        return null;
    }

    @NotNull
    public Collection<PsiLet> findLets(@NotNull Project project, @NotNull String name, @NotNull MlFileType fileType) {
        return findLowerSymbols("lets", project, name, fileType, IndexKeys.LETS, PsiLet.class);
    }

    @NotNull
    public Collection<PsiVal> findVals(@NotNull Project project, @NotNull String name, @NotNull MlFileType fileType) {
        return findLowerSymbols("vals", project, name, fileType, IndexKeys.VALS, PsiVal.class);
    }

    @NotNull
    public Collection<PsiType> findTypes(@NotNull Project project, @NotNull String name, @NotNull MlFileType fileType) {
        return findLowerSymbols("types", project, name, fileType, IndexKeys.TYPES, PsiType.class);
    }

    @NotNull
    public Collection<PsiExternal> findExternals(@NotNull Project project, @NotNull String name, @NotNull MlFileType fileType) {
        return findLowerSymbols("externals", project, name, fileType, IndexKeys.EXTERNALS, PsiExternal.class);
    }

    private <T extends PsiQualifiedNamedElement> Collection<T> findLowerSymbols(@NotNull String debugName, @NotNull Project project, @NotNull String name, @NotNull MlFileType fileType, StubIndexKey<String, T> indexKey, Class<T> clazz) {
        if (m_debug.isDebugEnabled()) {
            m_debug.debug("Find " + debugName + " name", name);
        }

        Map<String/*qn*/, T> inConfig = new THashMap<>();
        Bucklescript bucklescript = BucklescriptManager.getInstance(project);

        Collection<T> items = StubIndex.getElements(indexKey, name, project, projectScope(project), clazz);
        if (items.isEmpty()) {
            if (m_debug.isDebugEnabled()) {
                m_debug.debug("  No " + debugName + " found");
            }
        } else {
            if (m_debug.isDebugEnabled()) {
                m_debug.debug("  " + debugName + " found", items.size());
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
                        m_debug.debug("    keep (in config)", item);
                        inConfig.put(item.getQualifiedName(), item);
                    }
                }
            }
        }

        return inConfig.values();
    }

    @Nullable
    public FileBase findFileModule(@NotNull Project project, @NotNull String name) {
        GlobalSearchScope scope = GlobalSearchScope.allScope(project);

        FileBase file = findFileModuleByExt(project, name, OclInterfaceFileType.INSTANCE.getDefaultExtension(), scope);
        if (file == null) {
            file = findFileModuleByExt(project, name, RmlInterfaceFileType.INSTANCE.getDefaultExtension(), scope);
            if (file == null) {
                file = findFileModuleByExt(project, name, OclFileType.INSTANCE.getDefaultExtension(), scope);
                if (file == null) {
                    file = findFileModuleByExt(project, name, RmlFileType.INSTANCE.getDefaultExtension(), scope);
                }
            }
        }

        return file;
    }

    @Nullable
    private FileBase findFileModuleByExt(@NotNull Project project, @NotNull String name, @NotNull String ext, GlobalSearchScope scope) {
        m_debug.debug("Find file module", name, ext);

        FileBase result = null;
        Bucklescript bucklescript = BucklescriptManager.getInstance(project);

        PsiFile[] filesByName = FilenameIndex.getFilesByName(project, name + "." + ext, scope);
        if (0 < filesByName.length) {
            m_debug.debug("  found", filesByName);
            for (PsiFile file : filesByName) {
                if (file instanceof FileBase && bucklescript.isDependency(file)) {
                    result = (FileBase) file;
                    m_debug.debug("  resolved to", (FileBase) file);
                    break;
                }
            }
        }

        if (result == null) {
            // retry with lower case name
            filesByName = FilenameIndex.getFilesByName(project, PsiUtil.moduleNameToFileName(name) + "." + ext, scope);
            for (PsiFile file : filesByName) {
                if (file instanceof FileBase && bucklescript.isDependency(file)) {
                    result = (FileBase) file;
                    m_debug.debug("  resolved to", (FileBase) file);
                    break;
                }
            }
        }

        return result;
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
                if (bucklescript.isDependency(file)) {
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

                if (keep && file instanceof FileBase && bucklescript.isDependency(file)) {
                    result.put(virtualFile.getName(), (FileBase) file);
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

                if (keep && file instanceof FileBase && bucklescript.isDependency(file)) {
                    result.put(file.getName(), (FileBase) file);
                }
            }
        }

        return result.values();
    }

    @Nullable
    public PsiQualifiedNamedElement findModuleAlias(@NotNull Project project, @Nullable String moduleQname) {
        if (moduleQname == null) {
            return null;
        }

        GlobalSearchScope scope = projectScope(project);
        Collection<PsiModule> modules = ModuleFqnIndex.getInstance().get(moduleQname.hashCode(), project, scope);

        if (!modules.isEmpty()) {
            PsiModule moduleReference = modules.iterator().next();
            String alias = moduleReference.getAlias();

            if (alias != null) {
                FileBase fileModule = findFileModule(project, alias);
                if (fileModule != null) {
                    return fileModule;
                }

                modules = ModuleFqnIndex.getInstance().get(alias.hashCode(), project, scope);
                if (!modules.isEmpty()) {
                    PsiModule next = modules.iterator().next();
                    if (next != null) {
                        PsiQualifiedNamedElement nextModuleAlias = findModuleAlias(project, next.getQualifiedName());
                        return nextModuleAlias == null ? next : nextModuleAlias;
                    }
                }
            }
        }
        return null;
    }

    @Nullable
    public PsiModule findRealFileModule(Project project, String name) {
        // extract first token of path
        String[] names = name.split("\\.");

        PsiModule module = findModule(project, names[0], MlFileType.interfaceOrImplementation);
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

        return module;
    }

    @Nullable
    public PsiQualifiedNamedElement findModuleFromQn(@NotNull Project project, @NotNull String moduleQName) {
        // extract first token of path
        String[] names = moduleQName.split("\\.");

        FileBase fileModule = findFileModule(project, names[0]);
        if (fileModule != null) {
            if (1 < names.length) {
                //        PsiModule currentModule = module;
                //        for (int i = 1; i < names.length; i++) {
                //            String innerModuleName = names[i];
                //            currentModule = currentModule.getModule(innerModuleName);
                //            if (currentModule == null) {
                //                return null;
                //            }
                //        }
                //        return currentModule;
            }

            return fileModule;
        }

        return null;
    }
}
