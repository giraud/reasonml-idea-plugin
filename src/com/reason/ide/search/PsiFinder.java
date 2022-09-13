package com.reason.ide.search;

import com.intellij.openapi.project.*;
import com.intellij.psi.*;
import com.intellij.psi.search.*;
import com.intellij.psi.stubs.*;
import com.intellij.util.*;
import com.reason.comp.bs.*;
import com.reason.ide.files.*;
import com.reason.ide.search.index.*;
import com.reason.lang.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.regex.*;
import java.util.stream.*;

import static com.reason.lang.core.ORFileType.*;

public final class PsiFinder {
    private static final Log LOG = Log.create("finder");
    private final Project myProject;

    public PsiFinder(@NotNull Project project) {
        myProject = project;
    }

    public @Nullable PsiQualifiedPathElement findModuleBack(@Nullable PsiElement root, @Nullable String path) {
        if (root != null && path != null) {
            PsiElement prev = ORUtil.prevSibling(root);
            PsiElement item = prev == null ? root.getParent() : prev;
            while (item != null) {
                if (item instanceof PsiInnerModule) {
                    PsiInnerModule module = (PsiInnerModule) item;
                    String name = module.getModuleName();
                    String alias = module.getAlias();
                    if (alias != null) {
                        // This is a local module alias, we'll need to replace it in final paths
                        Pattern compile = Pattern.compile("(\\.?)(" + name + ")(\\.?)");
                        String replace = "$1" + alias + "$3";
                        path = compile.matcher(path).replaceFirst(replace);
                    } else if (path.equals(name)) {
                        return module;
                    } else if (name != null && path.startsWith(name)) {
                        // Follow module from top to bottom to find real module
                        path = path.substring(name.length() + 1);
                        return findModuleForward(module.getBody(), path);
                    }
                }
                prev = ORUtil.prevSibling(item);
                item = prev == null ? item.getParent() : prev;
            }
        }

        return null;
    }

    private @Nullable PsiQualifiedPathElement findModuleForward(@Nullable PsiElement root, @Nullable String path) {
        if (root != null && path != null) {
            PsiElement next = ORUtil.nextSibling(root);
            PsiElement item = next == null ? root.getFirstChild() : next;
            while (item != null) {
                if (item instanceof PsiInnerModule) {
                    PsiInnerModule module = (PsiInnerModule) item;
                    String name = module.getModuleName();
                    if (path.equals(name)) {
                        return module;
                    } else if (name != null && path.startsWith(name)) {
                        // Go deeper
                        path = path.substring(name.length() + 1);
                        return findModuleForward(module.getBody(), path);
                    }
                }
                next = ORUtil.nextSibling(item);
                item = next == null ? item.getFirstChild() : next;
            }
        }

        return null;
    }

    @FunctionalInterface
    public interface ModuleFilter<T extends PsiModule> {
        boolean accepts(T module);
    }

    static class PartitionedModules {
        private final List<PsiModule> m_interfaces = new ArrayList<>();
        private final List<PsiModule> m_implementations = new ArrayList<>();

        PartitionedModules(@NotNull Project project, @Nullable Collection<PsiModule> modules, @Nullable ModuleFilter<PsiModule> filter) {
            if (modules != null) {
                BsCompiler bucklescript = project.getService(BsCompiler.class);

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

    public @NotNull Set<PsiModule> findModulesbyName(@NotNull String name, @NotNull ORFileType fileType, ModuleFilter<PsiModule> filter) {
        Set<PsiModule> result = new HashSet<>();
        GlobalSearchScope scope = GlobalSearchScope.allScope(myProject);

        StubIndex.getInstance().processAllKeys(IndexKeys.MODULES,
                myProject,
                CommonProcessors.processAll(
                        moduleName -> {
                            if (name.equals(moduleName)) {
                                Collection<PsiModule> modules = ModuleIndex.getElements(moduleName, myProject, scope);
                                PartitionedModules partitionedModules =
                                        new PartitionedModules(myProject, modules, filter);

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

    @NotNull
    public Set<PsiModule> findModuleAlias(@Nullable String qname) {
        if (qname == null) {
            return Collections.emptySet();
        }

        Set<PsiModule> result = new HashSet<>();

        GlobalSearchScope scope = GlobalSearchScope.allScope(myProject);
        Collection<PsiModule> psiModules = ModuleFqnIndex.getElements(qname, myProject, scope);
        for (PsiModule module : psiModules) {
            String alias = module.getAlias();
            if (alias != null) {
                Collection<PsiModule> aliasModules = ModuleFqnIndex.getElements(alias, myProject, scope);
                if (!aliasModules.isEmpty()) {
                    for (PsiModule aliasModule : aliasModules) {
                        Set<PsiModule> nextModuleAlias = findModuleAlias(aliasModule.getQualifiedName());
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
    public Set<PsiModule> findModulesFromQn(@Nullable String qname, boolean resolveAlias, @NotNull ORFileType fileType) {
        if (qname == null) {
            return Collections.emptySet();
        }

        Set<PsiModule> result = new HashSet<>();
        GlobalSearchScope scope = GlobalSearchScope.allScope(myProject);

        // Try qn directly
        Collection<PsiModule> modules = ModuleFqnIndex.getElements(qname, myProject, scope);

        if (modules.isEmpty()) {
            // Qn not working, maybe because of aliases... try to navigate to each module
            String[] names = qname.split("\\.");

            // extract first token of path
            Set<PsiModule> firstModules =
                    findModulesbyName(names[0], interfaceOrImplementation, null);
            PsiModule firstModule = firstModules.isEmpty() ? null : firstModules.iterator().next();

            Set<PsiModule> firstModuleAliases =
                    findModuleAlias(firstModule == null ? null : firstModule.getQualifiedName());
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
                        Set<PsiModule> modulesByName = findModulesbyName(alias, fileType, null);
                        if (!modulesByName.isEmpty()) {
                            currentModule = modulesByName.iterator().next();
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
                    PsiFunctorCall functorCall = module instanceof PsiInnerModule ? ((PsiInnerModule) module).getFunctorCall() : null;
                    if (resolveAlias && functorCall != null) {
                        String functorName = functorCall.getName();
                        Set<PsiModule> modulesFromFunctor = null;

                        QNameFinder qnameFinder = QNameFinderFactory.getQNameFinder(functorCall.getLanguage());
                        Set<String> potentialPaths = qnameFinder.extractPotentialPaths(functorCall);
                        for (String path : potentialPaths) {
                            modulesFromFunctor =
                                    findModulesFromQn(path + "." + functorName, true, fileType);
                        }
                        if (modulesFromFunctor == null || modulesFromFunctor.isEmpty()) {
                            modulesFromFunctor = findModulesFromQn(functorName, true, fileType);
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
                    result.addAll(findModulesFromQn(alias, true, fileType));
                }
            }
        }

        return result;
    }
}
