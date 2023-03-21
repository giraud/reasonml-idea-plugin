package com.reason.ide.search.reference;

import com.intellij.openapi.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.util.*;
import com.intellij.openapi.util.text.*;
import com.intellij.psi.search.*;
import com.intellij.psi.stubs.*;
import com.intellij.psi.util.*;
import com.intellij.util.*;
import com.reason.ide.*;
import com.reason.ide.search.index.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.util.*;

import static java.util.Collections.*;

public class ORElementResolver implements Disposable {
    private static final Log LOG = Log.create("ref");

    private final Project myProject;
    private final CachedValue<Map<String, Set<String[]>>> myCachedIncludeDependencies;
    private final CachedValue<Map<String, Set<String[]>>> myCachedFunctorsUsages;

    ORElementResolver(@NotNull Project project) {
        myProject = project;
        CachedValuesManager cachedValuesManager = CachedValuesManager.getManager(project);

        myCachedIncludeDependencies = cachedValuesManager.createCachedValue(() -> {
            Map<String, Set<Pair<String, String[]>>> topIncludedModules = new HashMap<>();
            Map<String, Set<String[]>> dependencies = new HashMap<>();

            StubIndex stubIndex = StubIndex.getInstance();
            stubIndex.processAllKeys(IndexKeys.INCLUDES, myProject, includeIndex -> {
                stubIndex.processElements(IndexKeys.INCLUDES, includeIndex, myProject, null, RPsiInclude.class, psiInclude -> {
                    String[] includeQPath = psiInclude.getQualifiedPath();
                    String includeQName = Joiner.join(".", psiInclude.getQualifiedPath()) + "." + psiInclude.getIncludePath();
                    Map<String, String[]> gistData = ORIncludePsiGist.getData(psiInclude.getContainingFile());
                    String[] resolvedPath = gistData == null ? null : gistData.get(includeQName);
                    if (Arrays.equals(resolvedPath, includeQPath)) {
                        // !? coq ?!
                        LOG.info("Equality with recursion found: " + psiInclude + " [" + Joiner.join(".", includeQPath) + "] in " + ORFileUtils.getVirtualFile(psiInclude.getContainingFile()));
                        return true;
                    }

                    String includePath = Joiner.join(".", resolvedPath);
                    Set<String[]> depPaths = dependencies.computeIfAbsent(includePath, k -> new TreeSet<>(ArrayUtil::lexicographicCompare));
                    depPaths.add(includeQPath);

                    String includeModuleName = includeQPath[0];

                    String firstResolvedPath = resolvedPath == null ? null : resolvedPath[0];
                    Set<Pair<String, String[]>> alternatePaths = topIncludedModules.get(firstResolvedPath);
                    if (alternatePaths != null) {
                        for (Pair<String, String[]> alternatePath : alternatePaths) {
                            String alternateKey = alternatePath.first;
                            Set<String[]> alternateDependencies = dependencies.get(alternateKey);
                            String[] newPath = new String[alternatePath.second.length];
                            System.arraycopy(alternatePath.second, 0, newPath, 0, alternatePath.second.length);
                            newPath[0] = includeModuleName;
                            alternateDependencies.add(newPath);
                        }
                    }

                    Set<Pair<String, String[]>> topIncludes = topIncludedModules.computeIfAbsent(includeModuleName, k -> new TreeSet<>((o1, o2) -> {
                        int compare = NaturalComparator.INSTANCE.compare(o1.first, o2.first);
                        if (compare != 0) {
                            return compare;
                        }

                        return ArrayUtil.lexicographicCompare(o1.second, o2.second);
                    }));
                    topIncludes.add(Pair.create(includePath, includeQPath));

                    return true;
                });
                return true;
            });

            return CachedValueProvider.Result.create(dependencies, PsiModificationTracker.MODIFICATION_COUNT);
        });

        myCachedFunctorsUsages = cachedValuesManager.createCachedValue(() -> {
            Map<String, Set<String[]>> dependencies = new HashMap<>();

            StubIndex stubIndex = StubIndex.getInstance();
            stubIndex.processAllKeys(IndexKeys.FUNCTORS_CALL_FQN, myProject, includeIndex -> {
                stubIndex.processElements(IndexKeys.FUNCTORS_CALL_FQN, includeIndex, myProject, null, RPsiInnerModule.class, psiFunctorCall -> {
                    Map<String, String[]> gistData = ORFunctorPsiGist.getData(psiFunctorCall.getContainingFile());
                    String[] resolvedPath = gistData == null ? null : gistData.get(psiFunctorCall.getQualifiedName());
                    if (resolvedPath != null) {
                        String usageQName = psiFunctorCall.getQualifiedName();
                        if (usageQName != null) {
                            String[] usagePath = usageQName.split("\\.");
                            String functorQName = Joiner.join(".", resolvedPath);
                            Set<String[]> usages = dependencies.computeIfAbsent(functorQName, k -> new TreeSet<>(ArrayUtil::lexicographicCompare));
                            usages.add(usagePath);
                        }
                    }
                    return true;
                });
                return true;
            });

            return CachedValueProvider.Result.create(dependencies, PsiModificationTracker.MODIFICATION_COUNT);
        });
    }

    @NotNull Resolutions getComputation() {
        return new ResolutionsImpl();
    }

    @Override
    public void dispose() {
    }

    interface Resolutions {
        void add(@NotNull Collection<? extends RPsiQualifiedPathElement> elements, boolean includeSource);

        void addIncludesEquivalence();

        void addFunctorsEquivalence();

        void updateWeight(@Nullable String value, Set<String> alternateNames);

        void udpateTerminalWeight(@NotNull String value);

        void removeUpper();

        void removeIfNotFound(String value, @Nullable Set<String> alternateNames);

        void removeIncomplete();

        @NotNull Collection<RPsiQualifiedPathElement> resolvedElements();
    }

    /*
     Note: performance is extremely important
     */
    private class ResolutionsImpl implements Resolutions {
        private final Map<Integer, Integer> myWeightPerLevel = new HashMap<>();
        private final Map<String, Map<String, Resolution>> myResolutionsPerTopModule = new HashMap<>();

        @Override
        public void add(@NotNull Collection<? extends RPsiQualifiedPathElement> elements, boolean includeSource) {
            if (elements.isEmpty()) {
                return;
            }

            for (RPsiQualifiedPathElement source : elements) {
                String sourceName = source.getName();
                if (sourceName == null) {
                    continue;
                }

                String sourceQName = source.getQualifiedName();
                String[] sourcePath = source.getPath();
                if (sourcePath != null) {
                    // Add source name to the path in case of modules
                    if (includeSource) {
                        String[] newPath = new String[sourcePath.length + 1];
                        System.arraycopy(sourcePath, 0, newPath, 0, sourcePath.length);
                        newPath[sourcePath.length] = sourceName;
                        sourcePath = newPath;
                    }
                    // Remove type name in case of variants
                    if (source instanceof RPsiVariantDeclaration) {
                        String[] newPath = new String[sourcePath.length - 1];
                        System.arraycopy(sourcePath, 0, newPath, 0, newPath.length);
                        sourcePath = newPath;
                    }

                    String first = sourcePath.length > 0 ? sourcePath[0] : null;
                    Map<String, Resolution> resolutionsPerQName = first == null ? null : myResolutionsPerTopModule.get(first);
                    if (resolutionsPerQName == null) {
                        resolutionsPerQName = new HashMap<>();
                        myResolutionsPerTopModule.put(first, resolutionsPerQName);
                    }

                    // try to find duplicates
                    Resolution resolution = resolutionsPerQName.get(sourceQName);
                    if (resolution == null) {
                        resolutionsPerQName.put(sourceQName, new Resolution(sourcePath, source));
                    } else {
                        resolution.myElements.add(source);
                    }
                } else if (includeSource) {
                    Map<String, Resolution> resolutionsPerQName = myResolutionsPerTopModule.get(sourceName);
                    //noinspection Java8MapApi
                    if (resolutionsPerQName == null) {
                        resolutionsPerQName = new HashMap<>();
                        myResolutionsPerTopModule.put(sourceName, resolutionsPerQName);
                    }

                    // try to find duplicates
                    Resolution resolution = resolutionsPerQName.get(sourceQName);
                    if (resolution == null) {
                        resolutionsPerQName.put(sourceQName, new Resolution(new String[]{sourceName}, source));
                    } else {
                        resolution.myElements.add(source);
                    }
                }
            }

            Project project = elements.iterator().next().getProject();
            List<Resolution> aliasResolutions = new ArrayList<>();
            for (Map.Entry<String, Map<String, Resolution>> entry : myResolutionsPerTopModule.entrySet()) {
                String first = entry.getKey();
                Collection<Resolution> resolutions = entry.getValue().values();
                if (first != null) {
                    Collection<RPsiModule> aliases = ModuleAliasedIndex.getElements(first, project, GlobalSearchScope.allScope(project));
                    for (RPsiModule alias : aliases) {
                        String[] aliasPath = alias.getQualifiedNameAsPath();
                        for (Resolution resolution : resolutions) {
                            if (aliasPath != null) {
                                Resolution aliasResolution = Resolution.createAlternate(resolution, aliasPath);
                                aliasResolutions.add(aliasResolution);
                            }
                        }
                    }
                }
            }

            for (Resolution aliasResolution : aliasResolutions) {
                String topModuleName = aliasResolution.getTopModuleName();
                Map<String, Resolution> resolutionsPerQName = myResolutionsPerTopModule.get(topModuleName);
                //noinspection Java8MapApi
                if (resolutionsPerQName == null) {
                    resolutionsPerQName = new HashMap<>();
                    myResolutionsPerTopModule.put(topModuleName, resolutionsPerQName);
                }

                RPsiQualifiedPathElement aliasElement = aliasResolution.myElements.get(0);
                String aliasQName = aliasResolution.joinPath() + (aliasElement instanceof RPsiModule ? "" : "." + aliasElement.getName());

                resolutionsPerQName.put(aliasQName, aliasResolution);
            }
        }

        @Override
        public void addIncludesEquivalence() {
            Map<String, Set<String[]>> cachedIncludes = myCachedIncludeDependencies.getValue();

            List<Resolution> includeResolutions = new ArrayList<>();

            for (Map.Entry<String, Map<String, Resolution>> resolutionPerNameEntry : myResolutionsPerTopModule.entrySet()) {
                Map<String, Resolution> resolutions = resolutionPerNameEntry.getValue();
                for (Map.Entry<String, Resolution> resolutionEntry : resolutions.entrySet()) {
                    String key = resolutionEntry.getKey();
                    int pos = key.lastIndexOf(".");
                    String keyPath = pos < 0 ? key : key.substring(0, pos);
                    findResolutionEquivalence(keyPath, resolutionEntry.getValue(), cachedIncludes, includeResolutions, 0);
                }
            }

            for (Resolution includeResolution : includeResolutions) {
                String topModuleName = includeResolution.getTopModuleName();
                Map<String, Resolution> resolutionsPerQName = myResolutionsPerTopModule.get(topModuleName);
                //noinspection Java8MapApi
                if (resolutionsPerQName == null) {
                    resolutionsPerQName = new HashMap<>();
                    myResolutionsPerTopModule.put(topModuleName, resolutionsPerQName);
                }

                RPsiQualifiedPathElement includeElement = includeResolution.myElements.get(0);
                String includeQName = includeResolution.joinPath() + (includeElement instanceof RPsiModule ? "" : "." + includeElement.getName());

                // try to find duplicates
                Resolution resolution = resolutionsPerQName.get(includeQName);
                if (resolution == null) {
                    resolutionsPerQName.put(includeQName, includeResolution);
                } else {
                    resolution.myElements.add(includeElement);
                }
            }
        }

        @Override
        public void addFunctorsEquivalence() {
            List<Resolution> functors = new ArrayList<>();

            // Compute a list of alternate resolutions that are the usages of found functors
            for (Map<String, Resolution> resolutionsPerTopModule : myResolutionsPerTopModule.values()) {
                for (Map.Entry<String, Resolution> resolutionEntry : resolutionsPerTopModule.entrySet()) {
                    String[] tokens = resolutionEntry.getKey().split("\\.");
                    String moduleQName = tokens[0];
                    // iterate on every element of the path and detect if it’s a used functor
                    for (int i = 1; i < tokens.length; i++) {
                        moduleQName = moduleQName + "." + tokens[i];
                        Set<String[]> usages = myCachedFunctorsUsages.getValue().get(moduleQName);
                        if (usages != null && !usages.isEmpty()) {
                            // Found an instantiation of the functor, we store these usages as an alternate resolution
                            for (String[] usage : usages) {
                                Resolution value = resolutionEntry.getValue();
                                String[] newPath = value.substitutePath(usage);
                                Resolution newResolution = new Resolution(newPath, value.myElements);
                                functors.add(newResolution);
                            }
                        }
                    }
                }
            }

            for (Resolution functor : functors) {
                String topModuleName = functor.getTopModuleName();
                Map<String, Resolution> resolutionsPerQName = myResolutionsPerTopModule.get(topModuleName);
                //noinspection Java8MapApi
                if (resolutionsPerQName == null) {
                    resolutionsPerQName = new HashMap<>();
                    myResolutionsPerTopModule.put(topModuleName, resolutionsPerQName);
                }

                // Try to find duplicates
                String instantiationQName = functor.joinPath();
                Resolution resolution = resolutionsPerQName.get(instantiationQName);
                if (resolution == null) {
                    resolutionsPerQName.put(instantiationQName, functor);
                } else {
                    resolution.myElements.add(functor.myElements.get(0));
                }
            }
        }

        private void findResolutionEquivalence(String path, @NotNull Resolution resolution, @NotNull Map<String, Set<String[]>> cachedIncludes, @NotNull List<Resolution> result, int guard) {
            Set<String[]> includeDeps = cachedIncludes.get(path);
            if (includeDeps != null) {
                //    A.B.C.t
                //       A.B.C ==> A   ::   A.B.C.t => A.t
                for (String[] includeDepPath : includeDeps) {
                    Resolution newResolution = new Resolution(includeDepPath, resolution.myElements);
                    result.add(newResolution);
                    String newPath = Joiner.join(".", includeDepPath);
                    if (20 < guard) {
                        LOG.warn("Too much recursion for " + path);
                        return;
                    }

                    if (newPath.equals(path)) {
                        LOG.info("equivalent path found (#" + guard + "): " + path + ", deps: [" + Joiner.join(",", includeDeps, p -> Joiner.join(".", p)) + "]");
                    } else {
                        findResolutionEquivalence(newPath, newResolution, cachedIncludes, result, guard + 1);
                    }
                }
            } else {
                includeDeps = cachedIncludes.get(resolution.getTopModuleName());
                if (includeDeps != null) {
                    //    Core.Types.Visibility
                    //       Core ==> Css  ::   Core.Types.Visibility.t => Css.Types.Visibility.t
                    for (String[] includeDepPath : includeDeps) {
                        String[] newPath = resolution.augmentPath(includeDepPath);
                        Resolution newResolution = new Resolution(newPath, resolution.myElements);
                        result.add(newResolution);
                    }
                }
            }
        }

        public void updateWeight(@Nullable String value, @Nullable Set<String> alternateNames) {
            Map<Integer, Integer> newWeights = new HashMap<>();

            for (Map<String, Resolution> topModuleEntry : myResolutionsPerTopModule.values()) {
                for (Resolution resolution : topModuleEntry.values()) {
                    String name = resolution.getCurrentName();
                    if (name != null) {
                        if (value == null || value.equals(name)) {
                            int level = resolution.myLevel;
                            Integer weight = myWeightPerLevel.get(level);
                            int newWeight = weight == null ? 1 : weight + 1;
                            resolution.updateCurrentWeight(newWeight);
                            newWeights.put(level, newWeight);
                        } else if (alternateNames != null) {
                            for (String alternateName : alternateNames) {
                                if (alternateName.equals(name)) {
                                    int level = resolution.myLevel;
                                    Integer weight = myWeightPerLevel.get(level);
                                    int newWeight = weight == null ? 1 : weight + 1;
                                    resolution.updateCurrentWeight(newWeight);
                                    newWeights.put(level, newWeight);
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            myWeightPerLevel.putAll(newWeights);
        }

        public void udpateTerminalWeight(@NotNull String value) {
            for (Map<String, Resolution> topModuleEntry : myResolutionsPerTopModule.values()) {
                for (Resolution resolution : topModuleEntry.values()) {
                    String name = resolution.getCurrentName();
                    if (value.equals(name) && resolution.isLastLevel()) {
                        // terminal
                        int level = resolution.myLevel;
                        Integer weight = myWeightPerLevel.get(level);
                        int newWeight = weight == null ? 1 : weight + 1;
                        resolution.updateCurrentWeight(newWeight);
                        myWeightPerLevel.put(level, newWeight);
                    }
                }
            }
        }

        public void removeUpper() {
            for (Map<String, Resolution> topModuleEntry : myResolutionsPerTopModule.values()) {
                topModuleEntry.values().removeIf(resolution -> {
                    String name = resolution.getCurrentName();
                    return name != null && !name.isEmpty() && Character.isUpperCase(name.charAt(0));
                });
            }
        }

        // all resolutions must be complete
        public void removeIfNotFound(@NotNull String value, @Nullable Set<String> alternateNames) {
            for (Map<String, Resolution> topModuleEntry : myResolutionsPerTopModule.values()) {
                topModuleEntry.values().removeIf(resolution -> {
                    String currentName = resolution.getCurrentName();
                    return !value.equals(currentName) && (alternateNames == null || !alternateNames.contains(currentName));
                });
            }
        }

        public void removeIncomplete() {
            for (Map<String, Resolution> topModuleEntry : myResolutionsPerTopModule.values()) {
                topModuleEntry.values().removeIf(resolution -> !resolution.myIsComplete);
            }
        }

        public @NotNull Collection<RPsiQualifiedPathElement> resolvedElements() {
            List<Resolution> allResolutions = new ArrayList<>();

            // flatten elements
            for (Map<String, Resolution> topModuleEntry : myResolutionsPerTopModule.values()) {
                allResolutions.addAll(topModuleEntry.values());
            }

            allResolutions.sort(Resolution::compareTo);

            return allResolutions.isEmpty() ? emptyList() : allResolutions.get(0).myElements;
        }
    }
}
