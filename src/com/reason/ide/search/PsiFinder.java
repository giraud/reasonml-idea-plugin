package com.reason.ide.search;

import com.intellij.lang.*;
import com.intellij.openapi.components.*;
import com.intellij.openapi.project.*;
import com.intellij.psi.*;
import com.intellij.psi.search.*;
import com.intellij.psi.stubs.*;
import com.intellij.util.*;
import com.intellij.util.containers.*;
import com.reason.*;
import com.reason.bs.*;
import com.reason.ide.files.*;
import com.reason.ide.search.index.*;
import com.reason.lang.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.PsiParameter;
import com.reason.lang.core.psi.PsiType;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.napkin.*;
import com.reason.lang.ocaml.*;
import com.reason.lang.reason.*;
import gnu.trove.*;
import org.jetbrains.annotations.*;

import java.util.HashSet;
import java.util.*;
import java.util.stream.*;

import static com.intellij.psi.search.GlobalSearchScope.*;
import static com.reason.lang.core.ORFileType.*;
import static java.util.Collections.*;

public final class PsiFinder {

    private static final Log LOG = Log.create("finder");

    @FunctionalInterface
    public interface ModuleFilter<T extends PsiModule> {
        boolean accepts(T module);
    }

    @NotNull
    private final Project m_project;

    public static PsiFinder getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, PsiFinder.class);
    }

    public PsiFinder(@NotNull Project project) {
        m_project = project;
    }

    @NotNull
    public static QNameFinder getQNameFinder(@NotNull Language language) {
        return language == OclLanguage.INSTANCE
                ? OclQNameFinder.INSTANCE
                : language == NsLanguage.INSTANCE ? NsQNameFinder.INSTANCE : RmlQNameFinder.INSTANCE;
    }

    @Nullable
    public FileBase findRelatedFile(@NotNull FileBase file) {
        PsiDirectory directory = file.getParent();
        if (directory != null) {
            String filename = file.getVirtualFile().getNameWithoutExtension();

            String relatedExtension;
            if (FileHelper.isReason(file.getFileType())) {
                relatedExtension =
                        file.isInterface()
                                ? RmlFileType.INSTANCE.getDefaultExtension()
                                : RmlInterfaceFileType.INSTANCE.getDefaultExtension();
            } else {
                relatedExtension =
                        file.isInterface()
                                ? OclFileType.INSTANCE.getDefaultExtension()
                                : OclInterfaceFileType.INSTANCE.getDefaultExtension();
            }

            PsiFile relatedPsiFile = directory.findFile(filename + "." + relatedExtension);
            return relatedPsiFile instanceof FileBase ? (FileBase) relatedPsiFile : null;
        }
        return null;
    }

    static class PartitionedModules {
        private final List<PsiModule> m_interfaces = new ArrayList<>();
        private final List<PsiModule> m_implementations = new ArrayList<>();

        PartitionedModules(@NotNull Project project, @Nullable Collection<PsiModule> modules, @Nullable ModuleFilter<PsiModule> filter) {
            if (modules != null) {
                BsCompiler bucklescript = ServiceManager.getService(project, BsCompiler.class);

                for (PsiModule module : modules) {
                    FileBase file = (FileBase) module.getContainingFile();
                    if (bucklescript.isDependency(file.getVirtualFile())) {
                        if (filter == null || filter.accepts(module)) {
                            if (module.isInterface()) {
                                m_interfaces.add(module);
                            } else {
                                m_implementations.add(module);
                            }
                        }
                    } else {
                        if (LOG.isTraceEnabled()) {
                            LOG.trace(
                                    "  excluded (not in config)    "
                                            + module.getQualifiedName()
                                            + " "
                                            + file.getVirtualFile().getPath());
                        }
                    }
                }
            }
        }

        public boolean hasInterfaces() {
            return !m_interfaces.isEmpty();
        }

        public @NotNull List<PsiModule> getInterfaces() {
            return m_interfaces;
        }

        public @NotNull List<PsiModule> getImplementations() {
            return m_implementations;
        }
    }

    public @NotNull Set<PsiModule> findModulesbyName(@NotNull String name, @NotNull ORFileType fileType, ModuleFilter<PsiModule> filter, @NotNull GlobalSearchScope scope) {
        Set<PsiModule> result = new HashSet<>();
        ModuleIndex moduleIndex = ModuleIndex.getInstance();

        moduleIndex.processAllKeys(
                m_project,
                CommonProcessors.processAll(
                        moduleName -> {
                            if (name.equals(moduleName)) {
                                Collection<PsiModule> modules = moduleIndex.get(moduleName, m_project, scope);
                                PartitionedModules partitionedModules =
                                        new PartitionedModules(m_project, modules, filter);

                                if (fileType == interfaceOrImplementation
                                        || fileType == both
                                        || fileType == interfaceOnly) {
                                    result.addAll(partitionedModules.getInterfaces());
                                }

                                if (fileType != interfaceOnly) {
                                    if (fileType == both
                                            || fileType == implementationOnly
                                            || !partitionedModules.hasInterfaces()) {
                                        result.addAll(partitionedModules.getImplementations());
                                    }
                                }
                            }
                        }));

        if (LOG.isTraceEnabled()) {
            LOG.trace(
                    "  modules "
                            + name
                            + " (found "
                            + result.size()
                            + "): "
                            + Joiner.join(
                            ", ", result.stream().map(PsiModule::getName).collect(Collectors.toList())));
        }

        return result;
    }

    public @Nullable PsiModule findComponentFromQName(@Nullable String fqn, @NotNull GlobalSearchScope scope) {
        Collection<PsiModule> modules = fqn == null ? emptyList() : ModuleComponentFqnIndex.getInstance().get(fqn.hashCode(), m_project, scope);
        if (!modules.isEmpty()) {
            PsiModule module = modules.iterator().next();
            return ServiceManager.getService(m_project, BsCompiler.class)
                    .isDependency(module.getContainingFile().getVirtualFile())
                    ? module
                    : null;
        }

        return null;
    }

    public @NotNull Set<PsiModule> findComponents(@NotNull GlobalSearchScope scope) {
        Project project = scope.getProject();
        if (project == null) {
            return Collections.emptySet();
        }

        Set<PsiModule> result = new HashSet<>();

        BsCompiler bucklescript = ServiceManager.getService(m_project, BsCompiler.class);

        ModuleComponentIndex componentIndex = ModuleComponentIndex.getInstance();
        ModuleIndex moduleIndex = ModuleIndex.getInstance();
        componentIndex.processAllKeys(
                project,
                CommonProcessors.processAll(
                        moduleName -> {
                            for (PsiModule module : moduleIndex.get(moduleName, project, scope)) {
                                FileBase file = (FileBase) module.getContainingFile();
                                if (!module.isInterface() && bucklescript.isDependency(file.getVirtualFile())) {
                                    result.add(module);
                                }
                            }
                        }));

        return result;
    }

    @NotNull
    public Set<PsiParameter> findParameters(@NotNull String name, @NotNull ORFileType fileType) {
        return findLowerSymbols("parameters", name, fileType, IndexKeys.PARAMETERS, PsiParameter.class, allScope(m_project));
    }

    @NotNull
    public Set<PsiLet> findLets(@NotNull String name, @NotNull ORFileType fileType) {
        return findLowerSymbols("lets", name, fileType, IndexKeys.LETS, PsiLet.class, allScope(m_project));
    }

    @NotNull
    public Set<PsiVal> findVals(@NotNull String name, @NotNull ORFileType fileType) {
        return findLowerSymbols("vals", name, fileType, IndexKeys.VALS, PsiVal.class, allScope(m_project));
    }

    @NotNull
    public Set<PsiType> findTypes(@NotNull String name, @NotNull ORFileType fileType) {
        return findLowerSymbols("types", name, fileType, IndexKeys.TYPES, PsiType.class, allScope(m_project));
    }

    @NotNull
    public Set<PsiRecordField> findRecordFields(@NotNull String name, @NotNull ORFileType fileType) {
        return findLowerSymbols(
                "record fields",
                name,
                fileType,
                IndexKeys.RECORD_FIELDS,
                PsiRecordField.class,
                allScope(m_project));
    }

    @NotNull
    public Set<PsiExternal> findExternals(@NotNull String name, @NotNull ORFileType fileType) {
        return findLowerSymbols("externals", name, fileType, IndexKeys.EXTERNALS, PsiExternal.class, allScope(m_project));
    }

    @NotNull
    private <T extends PsiQualifiedElement> Set<T> findLowerSymbols(
            @NotNull String debugName,
            @NotNull String name,
            @NotNull ORFileType fileType,
            @NotNull StubIndexKey<String, T> indexKey,
            @NotNull Class<T> clazz,
            @NotNull GlobalSearchScope scope) {
        Map<String /*qn*/, T> implNames = new THashMap<>();
        Map<String /*qn*/, T> intfNames = new THashMap<>();

        if (LOG.isDebugEnabled()) {
            LOG.debug("Find " + debugName + " name", name);
        }

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
                FileBase itemFile = (FileBase) item.getContainingFile();

                if (fileType == ORFileType.implementationOnly) {
                    if (!FileHelper.isInterface(itemFile.getFileType())) {
                        implNames.put(itemQName, item);
                    }
                } else if (fileType == ORFileType.interfaceOnly) {
                    if (FileHelper.isInterface(itemFile.getFileType())) {
                        intfNames.put(itemQName, item);
                    }
                } else {
                    if (FileHelper.isInterface(itemFile.getFileType())) {
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

        Set<T> result = new ArrayListSet<>();
        result.addAll(intfNames.values());
        for (Map.Entry<String, T> entry : implNames.entrySet()) {
            if (fileType == both || !intfNames.containsKey(entry.getKey())) {
                result.add(entry.getValue());
            }
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("    keep (in the config)");
            for (T item : result) {
                LOG.debug(
                        "      "
                                + item.getQualifiedName()
                                + " "
                                + item.getContainingFile().getVirtualFile().getPath());
            }
        }

        return result;
    }

    @Nullable
    public PsiVariantDeclaration findVariant(@Nullable String qname, @NotNull GlobalSearchScope scope) {
        if (qname == null) {
            return null;
        }

        Collection<PsiVariantDeclaration> variants = VariantFqnIndex.getInstance().get(qname.hashCode(), m_project, scope);
        return variants.isEmpty() ? null : variants.iterator().next();
    }

    @NotNull
    public Collection<PsiVariantDeclaration> findVariantByName(@Nullable String path, @Nullable String name, @NotNull GlobalSearchScope scope) {
        if (name == null) {
            return emptyList();
        }

        Collection<PsiVariantDeclaration> variants = VariantIndex.getInstance().get(name, m_project, scope);
        if (!variants.isEmpty() && path != null) {
            // Keep variants that have correct path
            return variants
                    .stream()
                    .filter(variant -> variant.getQualifiedName().startsWith(path))
                    .collect(Collectors.toList());
        }

        return variants;
    }

    @Nullable
    public PsiException findException(
            @Nullable String qname, ORFileType fileType, @NotNull GlobalSearchScope scope) {
        if (qname == null) {
            return null;
        }

        Collection<PsiException> items =
                ExceptionFqnIndex.getInstance().get(qname.hashCode(), m_project, scope);
        if (items.isEmpty()) {
            return null;
        }

        if (items.size() == 1) {
            return items.iterator().next();
        }

        if (items.size() == 2) {
            boolean useInterface =
                    fileType == ORFileType.interfaceOrImplementation || fileType == ORFileType.interfaceOnly;
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

    @NotNull
    public Set<PsiFakeModule> findTopModules(
            boolean excludeNamespaces, @NotNull GlobalSearchScope scope) {
        Set<PsiFakeModule> result = new HashSet<>();

        ModuleTopLevelIndex index = ModuleTopLevelIndex.getInstance();

        index.processAllKeys(
                m_project,
                name -> {
                    Collection<PsiFakeModule> collection = index.get(name, m_project, scope);
                    for (PsiFakeModule psiFakeModule : collection) {
                        if (!(excludeNamespaces && psiFakeModule.hasNamespace())) {
                            result.add(psiFakeModule);
                        }
                    }
                    result.addAll(collection);
                    return true;
                });

        return result;
    }

    @NotNull
    public Set<PsiModule> findModuleAlias(@Nullable String qname, @NotNull GlobalSearchScope scope) {
        if (qname == null) {
            return Collections.emptySet();
        }

        Set<PsiModule> result = new HashSet<>();

        Collection<PsiModule> psiModules =
                ModuleFqnIndex.getInstance().get(qname.hashCode(), m_project, scope);
        for (PsiModule module : psiModules) {
            String alias = module.getAlias();
            if (alias != null) {
                Collection<PsiModule> aliasModules =
                        ModuleFqnIndex.getInstance().get(alias.hashCode(), m_project, scope);
                if (!aliasModules.isEmpty()) {
                    for (PsiModule aliasModule : aliasModules) {
                        Set<PsiModule> nextModuleAlias = findModuleAlias(aliasModule.getQualifiedName(), scope);
                        if (nextModuleAlias.isEmpty()) {
                            result.add(aliasModule);
                        } else {
                            result.addAll(nextModuleAlias);
                        }
                    }
                }
            }
        }

        return result;
    }

    @NotNull
    public Set<PsiModule> findModulesFromQn(@Nullable String qname, boolean resolveAlias, @NotNull ORFileType fileType, @NotNull GlobalSearchScope scope) {
        if (qname == null) {
            return Collections.emptySet();
        }

        Set<PsiModule> result = new HashSet<>();

        // Try qn directly
        Collection<PsiModule> modules = ModuleFqnIndex.getInstance().get(qname.hashCode(), m_project, scope);

        if (modules.isEmpty()) {
            // Qn not working, maybe because of aliases... try to navigate to each module
            String[] names = qname.split("\\.");

            // extract first token of path
            Set<PsiModule> firstModules =
                    findModulesbyName(names[0], interfaceOrImplementation, null, scope);
            PsiModule firstModule = firstModules.isEmpty() ? null : firstModules.iterator().next();

            Set<PsiModule> firstModuleAliases =
                    findModuleAlias(firstModule == null ? null : firstModule.getQualifiedName(), scope);
            PsiModule currentModule =
                    firstModuleAliases.isEmpty() ? firstModule : firstModuleAliases.iterator().next();
            if (currentModule != null) {
                for (int i = 1; i < names.length; i++) {
                    if (currentModule == null) {
                        break;
                    }
                    currentModule = currentModule.getModuleExpression(names[i]);
                    String alias = currentModule == null ? null : currentModule.getAlias();
                    if (alias != null) {
                        Set<PsiModule> modulesbyName = findModulesbyName(alias, fileType, null, scope);
                        if (!modulesbyName.isEmpty()) {
                            currentModule = modulesbyName.iterator().next();
                        }
                    }
                }
            }

            if (currentModule != null) {
                result.add(currentModule);
            }
        } else {
            // Qn returned something
            for (PsiModule module : modules) {
                String alias = resolveAlias ? module.getAlias() : null;
                if (alias == null) {
                    // It's not an alias, but maybe it's a functor call that we must resolve if asked
                    PsiFunctorCall functorCall = module.getFunctorCall();
                    if (resolveAlias && functorCall != null) {
                        String functorName = functorCall.getFunctorName();
                        Set<PsiModule> modulesFromFunctor = null;

                        QNameFinder qnameFinder = getQNameFinder(functorCall.getLanguage());
                        Set<String> potentialPaths = qnameFinder.extractPotentialPaths(functorCall);
                        for (String path : potentialPaths) {
                            modulesFromFunctor =
                                    findModulesFromQn(path + "." + functorName, true, fileType, scope);
                        }
                        if (modulesFromFunctor == null || modulesFromFunctor.isEmpty()) {
                            modulesFromFunctor = findModulesFromQn(functorName, true, fileType, scope);
                        }

                        if (modulesFromFunctor.isEmpty()) {
                            result.add(module);
                        } else {
                            result.addAll(modulesFromFunctor);
                        }
                    } else {
                        result.add(module);
                    }
                } else {
                    result.addAll(findModulesFromQn(alias, true, fileType, scope));
                }
            }
        }

        return result;
    }

    @Nullable
    public PsiParameter findParameterFromQn(
            @Nullable String qname, @NotNull GlobalSearchScope scope) {
        if (qname == null) {
            return null;
        }

        // Try qn directly
        Collection<PsiParameter> parameters =
                ParameterFqnIndex.getInstance().get(qname.hashCode(), m_project, scope);
        if (!parameters.isEmpty()) {
            return parameters.iterator().next();
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
        Set<PsiModule> modulesbyName =
                findModulesbyName(
                        names[0], interfaceOrImplementation, module -> module instanceof PsiFakeModule, scope);
        PsiModule currentModule = modulesbyName.isEmpty() ? null : modulesbyName.iterator().next();
        if (currentModule != null) {
            if (1 < names.length) {
                for (int i = 1; i < names.length - 1; i++) {
                    String innerModuleName = names[i];
                    currentModule = currentModule.getModuleExpression(innerModuleName);
                    String alias = currentModule == null ? null : currentModule.getAlias();
                    if (alias != null) {
                        modulesbyName = findModulesbyName(alias, interfaceOrImplementation, null, scope);
                        if (!modulesbyName.isEmpty()) {
                            currentModule = modulesbyName.iterator().next();
                        }
                    }
                    if (currentModule == null) {
                        return null;
                    }
                }
                realModule = currentModule;
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

        Set<PsiModule> modulesByName =
                findModulesbyName(
                        names[0], interfaceOrImplementation, module -> module instanceof PsiFakeModule, scope);
        PsiModule currentModule = modulesByName.isEmpty() ? null : modulesByName.iterator().next();
        if (currentModule != null) {
            if (1 < names.length) {
                for (int i = 1; i < names.length - 1; i++) {
                    String innerModuleName = names[i];
                    currentModule = currentModule.getModuleExpression(innerModuleName);
                    String alias = currentModule == null ? null : currentModule.getAlias();
                    if (alias != null) {
                        Set<PsiModule> modulesbyName =
                                findModulesbyName(alias, interfaceOrImplementation, null, scope);
                        if (!modulesbyName.isEmpty()) {
                            currentModule = modulesbyName.iterator().next();
                        }
                    }
                    if (currentModule == null) {
                        return null;
                    }
                }
            }
        }

        if (currentModule != null) {
            return currentModule.getValExpression(names[names.length - 1]);
        }

        return null;
    }

    public @Nullable PsiParameter findParamFromQn(@Nullable String qName) {
        if (qName == null) {
            return null;
        }

        GlobalSearchScope scope = allScope(m_project);

        // Try qn directly
        Collection<PsiParameter> parameters =
                ParameterFqnIndex.getInstance().get(qName.hashCode(), m_project, scope);
        if (!parameters.isEmpty()) {
            if (parameters.size() == 1) {
                return parameters.iterator().next();
            }
        }

        return null;
    }

    public @Nullable PsiType findTypeFromQn(@Nullable String qName, @NotNull GlobalSearchScope scope) {
        if (qName != null) {
            // Try qn directly
            Collection<PsiType> types = TypeFqnIndex.getInstance().get(qName.hashCode(), m_project, scope);
            if (!types.isEmpty()) {
                return types.iterator().next();
            }
        }
        return null;
    }

    public @NotNull Collection<IndexedFileModule> findModulesForNamespace(@NotNull String namespace, @NotNull GlobalSearchScope scope) {
        return FileModuleIndexService.getService().getFilesForNamespace(namespace, scope);
    }
}
