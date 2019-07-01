package com.reason.ide.search;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiQualifiedNamedElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.stubs.StubIndexKey;
import com.intellij.util.indexing.FileBasedIndex;
import com.reason.Log;
import com.reason.build.bs.Bucklescript;
import com.reason.ide.files.FileBase;
import com.reason.ide.files.FileHelper;
import com.reason.ide.search.index.*;
import com.reason.lang.core.ORFileType;
import com.reason.lang.core.PsiFileHelper;
import com.reason.lang.core.psi.*;
import gnu.trove.THashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

import static com.intellij.psi.search.GlobalSearchScope.allScope;

public final class PsiFinder {

    private static final Log LOG = Log.create("finder");

    @NotNull
    private final Project m_project;

    public static PsiFinder getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, PsiFinder.class);
    }

    public PsiFinder(@NotNull Project project) {
        m_project = project;
    }

    @Nullable
    public PsiInnerModule findComponent(@NotNull String fqn, @NotNull GlobalSearchScope scope) {
        ModuleComponentIndex index = ModuleComponentIndex.getInstance();

        Collection<PsiInnerModule> modules = index.get(fqn, m_project, scope);
        if (modules.isEmpty()) {
            return null;
        }

        PsiInnerModule module = modules.iterator().next();
        return ServiceManager.
                getService(m_project, Bucklescript.class).
                isDependency(module.getContainingFile().getVirtualFile()) ? module : null;
    }

    @NotNull
    public Collection<PsiModule> findComponents(@NotNull GlobalSearchScope scope) {
        Project project = scope.getProject();
        if (project == null) {
            return Collections.emptyList();
        }

        FileModuleIndexService fileModuleIndexService = FileModuleIndexService.getService();
        ModuleComponentIndex index = ModuleComponentIndex.getInstance();
        PsiManager psiManager = PsiManager.getInstance(project);
        Bucklescript bucklescript = ServiceManager.getService(m_project, Bucklescript.class);

        List<PsiModule> result = fileModuleIndexService.getComponents(project, scope).
                stream().
                filter(bucklescript::isDependency).
                map(vFile -> (FileBase) psiManager.findFile(vFile)).
                filter(Objects::nonNull).
                collect(Collectors.toList());

        result.addAll(index.getAllKeys(m_project).
                stream().
                map(key -> index.getUnique(key, m_project, scope)).
                filter(module -> module != null && bucklescript.isDependency(module.getContainingFile().getVirtualFile())).
                collect(Collectors.toList()));

        return result;
    }

    @NotNull
    public Collection<PsiModule> findModules(@NotNull String name, @NotNull ORFileType fileType, @NotNull GlobalSearchScope scope) {
        Map<String/*qn*/, PsiModule> implNames = new THashMap<>();
        Map<String/*qn*/, PsiModule> intfNames = new THashMap<>();

        if (LOG.isDebugEnabled()) {
            LOG.debug("Find modules with name", name);
        }

        VirtualFile file;

        FileModuleIndexService fileModuleIndex = FileModuleIndexService.getService();
        if (fileType == ORFileType.implementationOnly) {
            Collection<VirtualFile> implementations = fileModuleIndex.getImplementationFilesWithName(name, scope);
            file = implementations.isEmpty() ? null : implementations.iterator().next();
        } else if (fileType == ORFileType.interfaceOnly) {
            Collection<VirtualFile> interfaces = fileModuleIndex.getInterfaceFilesWithName(name, scope);
            file = interfaces.isEmpty() ? null : interfaces.iterator().next();
        } else {
            file = fileModuleIndex.getFile(name, scope);
        }

        if (file == null) {
            LOG.debug("  No file module found");
        } else {
            LOG.debug("  File module found", file);
            Map<String, PsiModule> map = FileHelper.isInterface(file.getFileType()) ? intfNames : implNames;
            PsiFile psiFile = PsiManager.getInstance(m_project).findFile(file);
            if (psiFile instanceof FileBase) {
                FileBase psiFileModule = (FileBase) psiFile;
                map.put(psiFileModule.asModuleName(), psiFileModule);
            }
        }

        Collection<PsiInnerModule> items = StubIndex.getElements(IndexKeys.MODULES, name, m_project, scope, PsiInnerModule.class);
        if (items.isEmpty()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("  No inner module found");
            }
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("  Inner modules found", items.size(), items);
            }
            for (PsiInnerModule item : items) {
                String itemQName = item.getQualifiedName();

                String filename = ((FileBase) item.getContainingFile()).asModuleName();

                if (fileType == ORFileType.implementationOnly) {
                    Collection<VirtualFile> implementations = fileModuleIndex.getImplementationFilesWithName(filename, scope);
                    if (!implementations.isEmpty()) {
                        implNames.put(itemQName, item);
                    }
                } else if (fileType == ORFileType.interfaceOnly) {
                    Collection<VirtualFile> interfaces = fileModuleIndex.getInterfaceFilesWithName(filename, scope);
                    if (!interfaces.isEmpty()) {
                        intfNames.put(itemQName, item);
                    }
                } else {
                    file = fileModuleIndex.getFile(filename, scope);
                    if (file != null) {
                        if (FileHelper.isInterface(file.getFileType())) {
                            if (((FileBase) item.getContainingFile()).isInterface()) {
                                intfNames.put(itemQName, item);
                            }
                        } else {
                            if (!((FileBase) item.getContainingFile()).isInterface()) {
                                implNames.put(itemQName, item);
                            }
                        }
                    }
                }
            }
        }

        List<PsiModule> result = new ArrayList<>(intfNames.values());
        for (Map.Entry<String, PsiModule> entry : implNames.entrySet()) {
            if (!intfNames.containsKey(entry.getKey())) {
                result.add(entry.getValue());
            }
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("    keep (in config)");
            for (PsiModule item : result) {
                LOG.debug("      " + item.getQualifiedName() + " " + item.getContainingFile().getVirtualFile().getPath());
            }
        }

        return result;
    }

    @Nullable
    public PsiModule findModule(@NotNull String name, @NotNull ORFileType fileType, GlobalSearchScope scope) {
        Collection<PsiModule> modules = findModules(name, fileType, scope);
        if (!modules.isEmpty()) {
            return modules.iterator().next();
        }

        return null;
    }

    @NotNull
    public Collection<PsiLet> findLets(@NotNull String name, @NotNull ORFileType fileType) {
        return findLowerSymbols("lets", name, fileType, IndexKeys.LETS, PsiLet.class, allScope(m_project));
    }

    @NotNull
    public Collection<PsiVal> findVals(@NotNull String name, @NotNull ORFileType fileType) {
        return findLowerSymbols("vals", name, fileType, IndexKeys.VALS, PsiVal.class, allScope(m_project));
    }

    @NotNull
    public Collection<PsiType> findTypes(@NotNull String name, @NotNull ORFileType fileType) {
        return findLowerSymbols("types", name, fileType, IndexKeys.TYPES, PsiType.class, allScope(m_project));
    }

    @NotNull
    public Collection<PsiRecordField> findRecordFields(@NotNull String name, @NotNull ORFileType fileType) {
        return findLowerSymbols("record fields", name, fileType, IndexKeys.RECORD_FIELDS, PsiRecordField.class, allScope(m_project));
    }

    @NotNull
    public Collection<PsiExternal> findExternals(@NotNull String name, @NotNull ORFileType fileType) {
        return findLowerSymbols("externals", name, fileType, IndexKeys.EXTERNALS, PsiExternal.class, allScope(m_project));
    }

    private <T extends PsiQualifiedNamedElement> Collection<T> findLowerSymbols(@NotNull String debugName, @NotNull String name, @NotNull ORFileType fileType, @NotNull StubIndexKey<String, T> indexKey, @NotNull Class<T> clazz, @NotNull GlobalSearchScope scope) {
        Map<String/*qn*/, T> implNames = new THashMap<>();
        Map<String/*qn*/, T> intfNames = new THashMap<>();

        if (LOG.isDebugEnabled()) {
            LOG.debug("Find " + debugName + " name", name);
        }

        FileModuleIndexService fileModuleIndex = FileModuleIndexService.getService();

        Collection<T> items = StubIndex.getElements(indexKey, name, m_project, scope, clazz);
        if (items.isEmpty()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("  No " + debugName + " found");
            }
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("  " + debugName + " found", items.size(), items);
            }
            for (T item : items) {
                String itemQName = item.getQualifiedName();

                String filename = ((FileBase) item.getContainingFile()).asModuleName();
                VirtualFile file;

                if (fileType == ORFileType.implementationOnly) {
                    Collection<VirtualFile> implementations = fileModuleIndex.getImplementationFilesWithName(filename, scope);
                    if (!implementations.isEmpty()) {
                        implNames.put(itemQName, item);
                    }
                } else if (fileType == ORFileType.interfaceOnly) {
                    Collection<VirtualFile> interfaces = fileModuleIndex.getInterfaceFilesWithName(filename, scope);
                    if (!interfaces.isEmpty()) {
                        intfNames.put(itemQName, item);
                    }
                } else {
                    file = fileModuleIndex.getFile(filename, scope);
                    if (file != null) {
                        if (FileHelper.isInterface(file.getFileType())) {
                            if (((FileBase) item.getContainingFile()).isInterface()) {
                                intfNames.put(itemQName, item);
                            }
                        } else {
                            if (!((FileBase) item.getContainingFile()).isInterface()) {
                                implNames.put(itemQName, item);
                            }
                        }
                    }
                }
            }
        }

        List<T> result = new ArrayList<>(intfNames.values());
        for (Map.Entry<String, T> entry : implNames.entrySet()) {
            if (!intfNames.containsKey(entry.getKey())) {
                result.add(entry.getValue());
            }
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("    keep (in config)");
            for (T item : result) {
                LOG.debug("      " + item.getQualifiedName() + " " + item.getContainingFile().getVirtualFile().getPath());
            }
        }

        return result;
    }

    @Nullable
    public PsiQualifiedNamedElement findVariant(@Nullable String qname, @NotNull GlobalSearchScope scope) {
        if (qname == null) {
            return null;
        }

        Collection<PsiVariantDeclaration> variants = VariantFqnIndex.getInstance().get(qname.hashCode(), m_project, scope);
        return variants.isEmpty() ? null : variants.iterator().next();
    }

    @NotNull
    public Collection<PsiVariantDeclaration> findVariantByName(@Nullable String path, @Nullable String name, @NotNull GlobalSearchScope scope) {
        if (name == null) {
            return Collections.emptyList();
        }

        Collection<PsiVariantDeclaration> variants = VariantIndex.getInstance().get(name, m_project, scope);
        if (!variants.isEmpty() && path != null) {
            // Keep variants that have correct path
            return variants.stream().filter(variant -> {
                String qualifiedName = variant.getQualifiedName();
                return qualifiedName != null && qualifiedName.startsWith(path);
            }).collect(Collectors.toList());
        }

        return variants;
    }

    @Nullable
    public PsiQualifiedNamedElement findException(@Nullable String qname, ORFileType fileType, @NotNull GlobalSearchScope scope) {
        if (qname == null) {
            return null;
        }

        Collection<PsiException> items = ExceptionFqnIndex.getInstance().get(qname.hashCode(), m_project, scope);
        if (items.isEmpty()) {
            return null;
        }

        if (items.size() == 1) {
            return items.iterator().next();
        }

        if (items.size() == 2) {
            boolean useInterface = fileType == ORFileType.interfaceOrImplementation || fileType == ORFileType.interfaceOnly;
            Iterator<PsiException> itException = items.iterator();
            PsiException first = itException.next();
            boolean isInterface = FileHelper.isInterface(first.getContainingFile().getFileType());
            if ((useInterface && isInterface) || (!useInterface && !isInterface)) {
                return first;
            }
            return itException.next();
        }

        LOG.debug("Incorrect size for retrieved exception items", items);
        return null;
    }

    @Nullable
    public PsiQualifiedNamedElement findModuleAlias(@Nullable String moduleQname) {
        if (moduleQname == null) {
            return null;
        }

        GlobalSearchScope scope = allScope(m_project);
        Collection<PsiInnerModule> modules = ModuleFqnIndex.getInstance().get(moduleQname.hashCode(), m_project, scope);

        if (!modules.isEmpty()) {
            PsiInnerModule moduleReference = modules.iterator().next();
            String alias = moduleReference.getAlias();

            if (alias != null) {
                VirtualFile vFile = FileModuleIndexService.getService().getFile(alias, scope);
                if (vFile != null) {
                    PsiFile psiFile = PsiManager.getInstance(m_project).findFile(vFile);
                    return psiFile instanceof FileBase ? (PsiQualifiedNamedElement) psiFile : null;
                }

                modules = ModuleFqnIndex.getInstance().get(alias.hashCode(), m_project, scope);
                if (!modules.isEmpty()) {
                    PsiInnerModule next = modules.iterator().next();
                    if (next != null) {
                        PsiQualifiedNamedElement nextModuleAlias = findModuleAlias(next.getQualifiedName());
                        return nextModuleAlias == null ? next : nextModuleAlias;
                    }
                }
            }
        }

        return null;
    }

    @Nullable
    public PsiModule findModuleFromQn(@Nullable String moduleQName) {
        if (moduleQName == null) {
            return null;
        }

        // extract first token of path
        String[] names = moduleQName.split("\\.");

        VirtualFile vFile = FileModuleIndexService.getService().getFile(names[0], allScope(m_project));
        if (vFile != null) {
            PsiFile file = PsiManager.getInstance(m_project).findFile(vFile);
            if (file instanceof FileBase) {
                FileBase fileModule = (FileBase) file;
                if (1 < names.length) {
                    PsiModule currentModule = fileModule;
                    for (int i = 1; i < names.length; i++) {
                        String innerModuleName = names[i];
                        currentModule = currentModule instanceof FileBase ? PsiFileHelper.getModuleExpression((PsiFile) currentModule, innerModuleName) : ((PsiInnerModule) currentModule).getModule(innerModuleName);
                        if (currentModule == null) {
                            return null;
                        }
                    }
                    return currentModule;
                }

                return fileModule;
            }
        }

        return null;
    }

    public Collection<IndexedFileModule> findModulesForNamespace(String namespace, GlobalSearchScope scope) {
        return FileModuleIndexService.getService().getFilesForNamespace(namespace, true, scope);
    }

    @NotNull
    public String findNamespace(@NotNull PsiModule psiModule, @NotNull GlobalSearchScope scope) {
        FileBase file = psiModule instanceof PsiInnerModule ? (FileBase) psiModule.getContainingFile() : (FileBase) psiModule;
        String path = file.getVirtualFile().getPath();

        List<FileModuleData> values = FileBasedIndex.getInstance().getValues(IndexKeys.FILE_MODULE, file.asModuleName(), scope);
        if (!values.isEmpty()) {
            for (FileModuleData value : values) {
                if (!value.getNamespace().isEmpty()) {
                    if (value.getPath().equals(path)) {
                        return value.getNamespace();
                    }
                }
            }
        }

        return "";
    }
}
