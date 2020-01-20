package com.reason.ide.search;

import java.util.*;
import java.util.stream.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiQualifiedNamedElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.stubs.StubIndexKey;
import com.intellij.util.indexing.FileBasedIndex;
import com.reason.Log;
import com.reason.bs.Bucklescript;
import com.reason.ide.files.FileBase;
import com.reason.ide.files.FileHelper;
import com.reason.ide.files.RmlFileType;
import com.reason.ide.files.RmlInterfaceFileType;
import com.reason.ide.search.index.ExceptionFqnIndex;
import com.reason.ide.search.index.IndexKeys;
import com.reason.ide.search.index.LetFqnIndex;
import com.reason.ide.search.index.ModuleComponentIndex;
import com.reason.ide.search.index.ModuleFqnIndex;
import com.reason.ide.search.index.ModuleIndex;
import com.reason.ide.search.index.ParameterFqnIndex;
import com.reason.ide.search.index.ValFqnIndex;
import com.reason.ide.search.index.VariantFqnIndex;
import com.reason.ide.search.index.VariantIndex;
import com.reason.lang.core.ORFileType;
import com.reason.lang.core.psi.PsiException;
import com.reason.lang.core.psi.PsiExternal;
import com.reason.lang.core.psi.PsiInnerModule;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiModule;
import com.reason.lang.core.psi.PsiParameter;
import com.reason.lang.core.psi.PsiRecordField;
import com.reason.lang.core.psi.PsiType;
import com.reason.lang.core.psi.PsiVal;
import com.reason.lang.core.psi.PsiVariantDeclaration;
import gnu.trove.THashMap;

import static com.intellij.psi.search.GlobalSearchScope.allScope;
import static com.reason.lang.core.ORFileType.*;

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
    public FileBase findRelatedFile(@NotNull FileBase file) {
        PsiDirectory directory = file.getParent();
        if (directory != null) {
            String filename = file.getVirtualFile().getNameWithoutExtension();

            String relatedExtension = file.isInterface() ? RmlFileType.INSTANCE.getDefaultExtension() : RmlInterfaceFileType.INSTANCE.getDefaultExtension();
            PsiFile relatedPsiFile = directory.findFile(filename + "." + relatedExtension);
            return relatedPsiFile instanceof FileBase ? (FileBase) relatedPsiFile : null;
        }
        return null;
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

        PsiManager psiManager = PsiManager.getInstance(project);
        Bucklescript bucklescript = ServiceManager.getService(m_project, Bucklescript.class);

        List<PsiModule> result = FileModuleIndexService.getService().getComponents(project, scope).
                stream().
                filter(bucklescript::isDependency).
                map(vFile -> (FileBase) psiManager.findFile(vFile)).
                filter(Objects::nonNull).
                collect(Collectors.toList());

        ModuleComponentIndex index = ModuleComponentIndex.getInstance();
        result.addAll(index.getAllKeys(m_project).
                stream().
                map(key -> index.getUnique(key, m_project, scope)).
                filter(module -> module != null && bucklescript.isDependency(module.getContainingFile().getVirtualFile())).
                collect(Collectors.toList()));

        return result;
    }

    @NotNull
    public Collection<PsiModule> findModules(@NotNull String name, @NotNull ORFileType fileType, @NotNull GlobalSearchScope scope) {
        Map<String/*qn*/, PsiModule> implementations = new THashMap<>();
        Map<String/*qn*/, PsiModule> interfaces = new THashMap<>();

        LOG.debug("Find modules with name", name);

        Collection<PsiModule> modules = ModuleIndex.getInstance().get(name, m_project, scope);
        if (modules.isEmpty()) {
            LOG.debug("  No module found");
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("  Modules found", modules.size(), modules);
            }
            for (PsiModule module : modules) {
                String itemQName = module.getQualifiedName();
                FileBase itemFile = ((FileBase) module.getContainingFile());
                // in config ?

                if (module.isInterface()) {
                    interfaces.put(itemQName, module);
                } else {
                    implementations.put(itemQName, module);
                }
            }
        }

        List<PsiModule> result = new ArrayList<>();

        if (fileType == interfaceOrImplementation || fileType == both || fileType == interfaceOnly) {
            result.addAll(interfaces.values());
        }

        if (fileType != interfaceOnly) {
            for (Map.Entry<String, PsiModule> entry : implementations.entrySet()) {
                if (fileType == both || fileType == implementationOnly || !interfaces.containsKey(entry.getKey())) {
                    result.add(entry.getValue());
                }
            }
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("    keep (in the config)");
            for (PsiModule item : result) {
                LOG.debug("      " + item.getQualifiedName() + " " + item.getContainingFile().getVirtualFile().getPath());
            }
        }

        return result;
    }

    @Nullable
    public PsiModule findModule(@NotNull String name, @NotNull ORFileType fileType, @NotNull GlobalSearchScope scope) {
        Collection<PsiModule> modules = findModules(name, fileType, scope);
        if (!modules.isEmpty()) {
            return modules.iterator().next();
        }

        return null;
    }

    @NotNull
    public Collection<PsiParameter> findParameters(@NotNull String name, @NotNull ORFileType fileType) {
        return findLowerSymbols("parameters", name, fileType, IndexKeys.PARAMETERS, PsiParameter.class, allScope(m_project));
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

    @NotNull
    private <T extends PsiQualifiedNamedElement> Collection<T> findLowerSymbols(@NotNull String debugName, @NotNull String name, @NotNull ORFileType fileType,
                                                                                @NotNull StubIndexKey<String, T> indexKey, @NotNull Class<T> clazz,
                                                                                @NotNull GlobalSearchScope scope) {
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
                    Collection<VirtualFile> files = fileModuleIndex.getFilesWithName(filename, scope);
                    for (VirtualFile file : files) {
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
            LOG.debug("    keep (in the config)");
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
    public PsiModule findModuleAlias(@Nullable String moduleQname) {
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
                    return psiFile instanceof FileBase ? (PsiModule) psiFile : null;
                }

                modules = ModuleFqnIndex.getInstance().get(alias.hashCode(), m_project, scope);
                if (!modules.isEmpty()) {
                    PsiInnerModule next = modules.iterator().next();
                    if (next != null) {
                        PsiModule nextModuleAlias = findModuleAlias(next.getQualifiedName());
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

        GlobalSearchScope scope = allScope(m_project);

        // Try qn directly
        Collection<PsiInnerModule> modules = ModuleFqnIndex.getInstance().get(moduleQName.hashCode(), m_project, scope);
        if (!modules.isEmpty()) {
            if (modules.size() == 1) {
                PsiInnerModule module = modules.iterator().next();
                String alias = module.getAlias();
                return alias == null ? module : findModule(alias, interfaceOrImplementation, scope);
            }

            for (PsiInnerModule module : modules) {
                if (((FileBase) module.getContainingFile()).isInterface()) {
                    String alias = module.getAlias();
                    return alias == null ? module : findModule(alias, interfaceOrImplementation, scope);
                }
            }
        }

        // Qn not working, maybe because of aliases... try to navigate to each module

        // extract first token of path
        String[] names = moduleQName.split("\\.");

        VirtualFile vFile = FileModuleIndexService.getService().getFile(names[0], scope);
        if (vFile != null) {
            PsiFile file = PsiManager.getInstance(m_project).findFile(vFile);
            if (file instanceof FileBase) {
                FileBase fileModule = (FileBase) file;
                if (1 < names.length) {
                    PsiModule currentModule = fileModule;
                    for (int i = 1; i < names.length; i++) {
                        String innerModuleName = names[i];
                        currentModule = currentModule.getModuleExpression(innerModuleName);
                        String alias = currentModule == null ? null : currentModule.getAlias();
                        if (alias != null) {
                            currentModule = findModule(alias, interfaceOrImplementation, scope);
                        }
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

    @Nullable
    public PsiLet findLetFromQn(@Nullable String qname) {
        if (qname == null) {
            return null;
        }

        GlobalSearchScope scope = allScope(m_project);

        // Try qn directly
        Collection<PsiLet> lets = LetFqnIndex.getInstance().get(qname.hashCode(), m_project, scope);
        if (!lets.isEmpty()) {
            return lets.iterator().next();
        }

        // Qn not working, maybe because of aliases... try to navigate to each module

        // extract first token of path
        String[] names = qname.split("\\.");

        PsiModule realModule = null;
        VirtualFile vFile = FileModuleIndexService.getService().getFile(names[0], scope);
        if (vFile != null) {
            PsiFile file = PsiManager.getInstance(m_project).findFile(vFile);
            if (file instanceof FileBase) {
                realModule = (FileBase) file;
                if (1 < names.length) {
                    PsiModule currentModule = realModule;
                    for (int i = 1; i < names.length - 1; i++) {
                        String innerModuleName = names[i];
                        currentModule = currentModule.getModuleExpression(innerModuleName);
                        String alias = currentModule == null ? null : currentModule.getAlias();
                        if (alias != null) {
                            currentModule = findModule(alias, interfaceOrImplementation, scope);
                        }
                        if (currentModule == null) {
                            return null;
                        }
                    }
                    realModule = currentModule;
                }
            }
        }

        if (realModule != null) {
            return realModule.getLetExpression(names[names.length - 1]);
        }

        return null;
    }

    @Nullable
    public PsiVal findValFromQn(@Nullable String qname) {
        if (qname == null) {
            return null;
        }

        GlobalSearchScope scope = allScope(m_project);

        // Try qn directly
        Collection<PsiVal> vals = ValFqnIndex.getInstance().get(qname.hashCode(), m_project, scope);
        if (!vals.isEmpty()) {
            return vals.iterator().next();
        }

        // Qn not working, maybe because of aliases... try to navigate to each module

        // extract first token of path
        String[] names = qname.split("\\.");

        PsiModule realModule = null;
        VirtualFile vFile = FileModuleIndexService.getService().getFile(names[0], scope);
        if (vFile != null) {
            PsiFile file = PsiManager.getInstance(m_project).findFile(vFile);
            if (file instanceof FileBase) {
                realModule = (FileBase) file;
                if (1 < names.length) {
                    PsiModule currentModule = realModule;
                    for (int i = 1; i < names.length - 1; i++) {
                        String innerModuleName = names[i];
                        currentModule = currentModule.getModuleExpression(innerModuleName);
                        String alias = currentModule == null ? null : currentModule.getAlias();
                        if (alias != null) {
                            currentModule = findModule(alias, interfaceOrImplementation, scope);
                        }
                        if (currentModule == null) {
                            return null;
                        }
                    }
                    realModule = currentModule;
                }
            }
        }

        if (realModule != null) {
            return realModule.getValExpression(names[names.length - 1]);
        }

        return null;
    }

    @Nullable
    public PsiParameter findParamFromQn(@Nullable String qName) {
        if (qName == null) {
            return null;
        }

        GlobalSearchScope scope = allScope(m_project);

        // Try qn directly
        Collection<PsiParameter> parameters = ParameterFqnIndex.getInstance().get(qName.hashCode(), m_project, scope);
        if (!parameters.isEmpty()) {
            if (parameters.size() == 1) {
                return parameters.iterator().next();
            }
        }

        return null;
    }

    public Collection<IndexedFileModule> findModulesForNamespace(@NotNull String namespace, @NotNull GlobalSearchScope scope) {
        return FileModuleIndexService.getService().getFilesForNamespace(namespace, scope);
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
