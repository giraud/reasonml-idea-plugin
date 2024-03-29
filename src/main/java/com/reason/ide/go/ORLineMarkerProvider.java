package com.reason.ide.go;

import com.intellij.codeInsight.daemon.*;
import com.intellij.codeInsight.navigation.*;
import com.intellij.openapi.editor.markup.*;
import com.intellij.openapi.project.*;
import com.intellij.psi.*;
import com.intellij.psi.search.*;
import com.intellij.psi.util.*;
import com.reason.ide.*;
import com.reason.ide.search.index.*;
import com.reason.ide.search.reference.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.ocaml.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.stream.*;

import static java.util.Collections.*;

public class ORLineMarkerProvider extends RelatedItemLineMarkerProvider {
    private static final Log LOG = Log.create("goto");

    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
        Project project = element.getProject();
        GlobalSearchScope scope = GlobalSearchScope.allScope(project);
        LOG.trace("collectNavigationMarkers", element, project, scope);

        if (element instanceof RPsiUpperSymbol uSymbolElement) {
            PsiElement parentElement = uSymbolElement.getParent();

            if (parentElement instanceof RPsiInnerModule moduleElement) {
                collectInnerModuleNavigationMarkers(moduleElement, project, scope, result);
            } else if (parentElement instanceof RPsiException exceptionElement) {
                collectExceptionNavigationMarkers(exceptionElement, project, scope, result);
            }
        }
        //
        else if (element instanceof RPsiLowerSymbol lSymbolElement) {
            PsiElement parentElement = lSymbolElement.getParent();

            if (parentElement instanceof RPsiVal valElement) {
                collectValNavigationMarkers(valElement, project, scope, result);
            } else if (parentElement instanceof RPsiLet letElement) {
                collectLetNavigationMarkers(letElement, project, scope, result);
            } else if (parentElement instanceof RPsiType typeElement) {
                collectTypeNavigationMarkers(typeElement, project, scope, result);
            } else if (parentElement instanceof RPsiExternal externalElement) {
                collectExternalNavigationMarkers(externalElement, project, scope, result);
            } else if (parentElement instanceof RPsiClass classElement) {
                collectClassNavigationMarkers(classElement, project, scope, result);
            } else if (parentElement instanceof RPsiClassMethodImpl methodElement) {
                collectClassMethodNavigationMarkers(methodElement, project, scope, result);
            }
        }
    }

    private void collectLetNavigationMarkers(@NotNull RPsiLet element, @NotNull Project project, @NotNull GlobalSearchScope scope, @NotNull Collection<? super RelatedItemLineMarkerInfo<PsiElement>> result) {
        LOG.debug("collect_LET_NavigationMarkers", element);

        List<? extends RPsiVar> targets = null;
        boolean isOcaml = element.getLanguage() == OclLanguage.INSTANCE;
        RPsiModule module = PsiTreeUtil.getStubOrPsiParentOfType(element, RPsiModule.class);
        boolean inInterface = ORUtil.inInterface(element);

        if (module instanceof RPsiInnerModule innerModule) {
            LOG.trace("  from inner module", innerModule);
            String letName = element.getName();
            if (letName != null) {
                targets = inInterface
                        ? findTargetFromInterfaceModule(innerModule, letName, RPsiVar.class, scope)
                        : findTargetFromImplementationModule(innerModule, letName, RPsiVar.class);
            }
        } else if (module != null) {
            LOG.trace("  from file module", module);
            // Top module navigation
            String qName = element.getQualifiedName();
            Collection<? extends RPsiVar> resolvedElements;
            if (isOcaml && !inInterface) {
                resolvedElements = qName == null ? null : ValFqnIndex.getElements(qName, project, scope);
            } else {
                resolvedElements = qName == null ? null : LetFqnIndex.getElements(qName, project, scope);
            }
            targets = resolveTargetFromIndex(inInterface, resolvedElements);
        }

        RelatedItemLineMarkerInfo<PsiElement> marker = createMarkerInfo(element, inInterface, "let/val", targets);
        if (marker != null) {
            result.add(marker);
        }
    }

    private void collectValNavigationMarkers(@NotNull RPsiVal element, @NotNull Project project, @NotNull GlobalSearchScope scope, @NotNull Collection<? super RelatedItemLineMarkerInfo<PsiElement>> result) {
        List<? extends RPsiVar> targets = null;

        RPsiModule module = PsiTreeUtil.getStubOrPsiParentOfType(element, RPsiModule.class);
        boolean inInterface = ORUtil.inInterface(element);

        if (module instanceof RPsiInnerModule innerModule) {
            inInterface = innerModule.isModuleType() || inInterface;
            String valName = element.getName();
            if (valName != null) {
                targets = inInterface
                        ? findTargetFromInterfaceModule(innerModule, valName, RPsiVar.class, scope)
                        : findTargetFromImplementationModule(innerModule, valName, RPsiVar.class);
            }
        } else if (module != null) {
            // Top module navigation
            String qName = element.getQualifiedName();
            Collection<? extends RPsiVar> resolvedElements = qName == null ? null : LetFqnIndex.getElements(qName, project, scope);
            targets = resolveTargetFromIndex(inInterface, resolvedElements);
        }

        RelatedItemLineMarkerInfo<PsiElement> marker = createMarkerInfo(element, inInterface, "let/val", targets);
        if (marker != null) {
            result.add(marker);
        }
    }

    private void collectTypeNavigationMarkers(@NotNull RPsiType element, @NotNull Project project, @NotNull GlobalSearchScope scope, @NotNull Collection<? super RelatedItemLineMarkerInfo<PsiElement>> result) {
        List<RPsiType> targets = null;

        RPsiModule module = PsiTreeUtil.getStubOrPsiParentOfType(element, RPsiModule.class);
        boolean inInterface = ORUtil.inInterface(element);

        if (module instanceof RPsiInnerModule innerModule) {
            inInterface = innerModule.isModuleType() || inInterface;
            String typeName = element.getName();
            if (typeName != null) {
                targets = inInterface ? findTargetFromInterfaceModule(innerModule, typeName, RPsiType.class, scope)
                        : findTargetFromImplementationModule(innerModule, typeName, RPsiType.class);
            }
        } else if (module != null) {
            // Top module navigation
            String qName = element.getQualifiedName();
            Collection<RPsiType> resolvedElements = qName == null ? null : TypeFqnIndex.getElements(qName, project, scope);
            targets = resolveTargetFromIndex(inInterface, resolvedElements);
        }

        RelatedItemLineMarkerInfo<PsiElement> marker = createMarkerInfo(element, inInterface, "type", targets);
        if (marker != null) {
            result.add(marker);
        }
    }

    private void collectExternalNavigationMarkers(@NotNull RPsiExternal element, @NotNull Project project, @NotNull GlobalSearchScope scope, @NotNull Collection<? super RelatedItemLineMarkerInfo<PsiElement>> result) {
        RPsiModule module = PsiTreeUtil.getStubOrPsiParentOfType(element, RPsiModule.class);
        boolean inInterface = ORUtil.inInterface(element);
        List<RPsiExternal> targets = null;

        if (module instanceof RPsiInnerModule innerModule) {
            inInterface = innerModule.isModuleType() || inInterface;
            String elementName = element.getName();
            if (elementName != null) {
                targets = inInterface ? findTargetFromInterfaceModule(innerModule, elementName, RPsiExternal.class, scope)
                        : findTargetFromImplementationModule(innerModule, elementName, RPsiExternal.class);
            }
        } else {
            // Top module navigation
            String qName = element.getQualifiedName();
            Collection<RPsiExternal> resolvedElements = qName == null ? null : ExternalFqnIndex.getElements(qName, project, scope);
            targets = resolveTargetFromIndex(inInterface, resolvedElements);
        }

        RelatedItemLineMarkerInfo<PsiElement> marker = createMarkerInfo(element, inInterface, "external", targets);
        if (marker != null) {
            result.add(marker);
        }
    }

    private void collectClassNavigationMarkers(@NotNull RPsiClass element, @NotNull Project project, @NotNull GlobalSearchScope scope, @NotNull Collection<? super RelatedItemLineMarkerInfo<PsiElement>> result) {
        RPsiModule module = PsiTreeUtil.getStubOrPsiParentOfType(element, RPsiModule.class);
        boolean inInterface = ORUtil.inInterface(element);
        List<RPsiClass> targets = null;

        if (module instanceof RPsiInnerModule innerModule) {
            inInterface = innerModule.isModuleType() || inInterface;
            String elementName = element.getName();
            if (elementName != null) {
                targets = innerModule.isModuleType() ? findTargetFromInterfaceModule(innerModule, elementName, RPsiClass.class, scope)
                        : findTargetFromImplementationModule(innerModule, elementName, RPsiClass.class);
            }
        } else {
            // Top module navigation
            String qName = element.getQualifiedName();
            Collection<RPsiClass> resolvedElements = qName == null ? null : ClassFqnIndex.getElements(qName, project, scope);
            targets = resolveTargetFromIndex(inInterface, resolvedElements);
        }

        RelatedItemLineMarkerInfo<PsiElement> marker = createMarkerInfo(element, inInterface, "class", targets);
        if (marker != null) {
            result.add(marker);
        }
    }

    private void collectClassMethodNavigationMarkers(@NotNull RPsiClassMethod element, @NotNull Project project, @NotNull GlobalSearchScope scope, @NotNull Collection<? super RelatedItemLineMarkerInfo<PsiElement>> result) {
        RPsiModule module = PsiTreeUtil.getStubOrPsiParentOfType(element, RPsiModule.class);
        boolean inInterface = ORUtil.inInterface(element);
        List<RPsiClassMethod> targets = null;

        if (module instanceof RPsiInnerModule innerModule) {
            String elementName = element.getName();
            if (elementName != null) {
                targets = inInterface ? findTargetFromInterfaceModule(innerModule, elementName, RPsiClassMethod.class, scope)
                        : findTargetFromImplementationModule(innerModule, elementName, RPsiClassMethod.class);
            }
        } else {
            // Top module navigation
            String qName = element.getQualifiedName();
            Collection<RPsiClassMethod> resolvedElements = qName == null ? null : ClassMethodFqnIndex.getElements(qName, project, scope);
            targets = resolveTargetFromIndex(inInterface, resolvedElements);
        }

        RelatedItemLineMarkerInfo<PsiElement> marker = createMarkerInfo(element, inInterface, "method", targets);
        if (marker != null) {
            result.add(marker);
        }
    }

    private void collectExceptionNavigationMarkers(@NotNull RPsiException element, @NotNull Project project, @NotNull GlobalSearchScope scope, @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
        RPsiModule module = PsiTreeUtil.getStubOrPsiParentOfType(element, RPsiModule.class);
        boolean inInterface = ORUtil.inInterface(element);
        List<RPsiException> targets = null;

        if (module instanceof RPsiInnerModule innerModule) {
            inInterface = innerModule.isModuleType();
            String elementName = element.getName();
            if (elementName != null) {
                targets = inInterface ? findTargetFromInterfaceModule(innerModule, elementName, RPsiException.class, scope)
                        : findTargetFromImplementationModule(innerModule, elementName, RPsiException.class);
            }
        } else {
            // Top module navigation
            String qName = element.getQualifiedName();
            Collection<RPsiException> resolvedElements = qName == null ? null : ExceptionFqnIndex.getElements(qName, project, scope);
            targets = resolveTargetFromIndex(inInterface, resolvedElements);
        }

        RelatedItemLineMarkerInfo<PsiElement> marker = createMarkerInfo(element, inInterface, "exception", targets);
        if (marker != null) {
            result.add(marker);
        }
    }

    private void collectInnerModuleNavigationMarkers(@NotNull RPsiInnerModule element, @NotNull Project project, @NotNull GlobalSearchScope scope, @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
        List<RPsiModule> implementsModules = new ArrayList<>();
        List<RPsiModule> declareModules = new ArrayList<>();

        String qName = element.getQualifiedName();

        // A module type define a signature
        if (element.isModuleType()) {
            String signatureName = element.getModuleName();
            if (signatureName != null) {
                // Find module(s) that use the interface as a result
                Collection<RPsiModule> elements = ModuleSignatureIndex.getElements(signatureName, project, scope);

                List<RPsiModule> signatureModules = elements
                        .stream()
                        .map(m -> {
                            if (m instanceof RPsiInnerModule innerModule) {
                                RPsiModuleSignature moduleSignature = innerModule.getModuleSignature();
                                RPsiUpperSymbol moduleSignatureIdentifier = moduleSignature != null ? moduleSignature.getNameIdentifier() : null;
                                ORPsiUpperSymbolReference reference = moduleSignatureIdentifier != null ? moduleSignatureIdentifier.getReference() : null;
                                PsiElement resolvedSignature = reference != null ? reference.resolve() : null;
                                if (resolvedSignature instanceof RPsiModule resolvedSignatureModule) {
                                    String sigQName = resolvedSignatureModule.getQualifiedName();
                                    return sigQName != null && sigQName.equals(qName) ? m : null;
                                }
                                return null;
                            }
                            return null;
                        })
                        .filter(Objects::nonNull)
                        .toList();

                implementsModules.addAll(signatureModules);
            }
        } else {
            RPsiModuleSignature moduleSignature = element.getModuleSignature();
            RPsiUpperSymbol signatureIdentifier = moduleSignature != null ? moduleSignature.getNameIdentifier() : null;

            // Module is implementing a named signature (module type), we need to find its definition
            if (signatureIdentifier != null) {
                PsiElement resolvedElement = signatureIdentifier.getReference().resolveInterface();
                if (resolvedElement instanceof RPsiInnerModule) {
                    declareModules.add((RPsiInnerModule) resolvedElement);
                }
            }
        }

        // Find module(s) in the related file
        Collection<RPsiModule> modules = qName != null ? ModuleFqnIndex.getElements(qName, project, scope) : null;
        if (modules != null) {
            boolean fromInterfaceFile = ORUtil.isInterfaceFile(element);
            List<RPsiInnerModule> relatedModules = modules.stream()
                    .filter(m -> ORUtil.isInterfaceFile(m) != fromInterfaceFile)
                    .map(m -> m instanceof RPsiInnerModule ? (RPsiInnerModule) m : null)
                    .filter(Objects::nonNull)
                    .toList();

            if (fromInterfaceFile) {
                implementsModules.addAll(relatedModules);
            } else {
                declareModules.addAll(relatedModules);
            }
        }

        if (!implementsModules.isEmpty()) {
            result.add(createMarkerInfo(element, true, "module", implementsModules));
        }
        if (!declareModules.isEmpty()) {
            result.add(createMarkerInfo(element, false, "module", declareModules));
        }
    }

    private static @NotNull <T extends RPsiQualifiedPathElement> List<T> resolveTargetFromIndex(boolean inInterfaceFile, @Nullable Collection<T> resolvedElements) {
        if (resolvedElements != null) {
            for (T resolvedElement : resolvedElements) {
                RPsiModule targetModule = PsiTreeUtil.getStubOrPsiParentOfType(resolvedElement, RPsiModule.class);
                boolean targetInterface = ORUtil.isInterfaceFile(targetModule);
                if (inInterfaceFile && !targetInterface) {
                    return singletonList(resolvedElement);
                } else if (!inInterfaceFile && targetInterface) {
                    return singletonList(resolvedElement);
                }
            }
        }
        return emptyList();
    }

    private @NotNull <T extends RPsiQualifiedPathElement> List<T> findTargetFromImplementationModule(@NotNull RPsiInnerModule sourceModule, @NotNull String elementName, @NotNull Class<T> expectedClass) {
        RPsiModuleSignature sourceModuleSignature = sourceModule.getModuleSignature();
        RPsiUpperSymbol sourceSignatureIdentifier = sourceModuleSignature != null ? sourceModuleSignature.getNameIdentifier() : null;
        if (sourceSignatureIdentifier != null) {
            PsiElement resolvedElement = sourceSignatureIdentifier.getReference().resolveInterface();
            if (resolvedElement instanceof RPsiInnerModule resolvedModule) {
                for (T rPsiType : PsiTreeUtil.getChildrenOfTypeAsList(resolvedModule.getBody(), expectedClass)) {
                    if (elementName.equals(rPsiType.getName())) {
                        return singletonList(rPsiType);
                    }
                }
            }
        }
        return emptyList();
    }

    private @NotNull <T extends RPsiQualifiedPathElement> List<T> findTargetFromInterfaceModule(@NotNull RPsiInnerModule sourceModule, @NotNull String elementName, @NotNull Class<T> expectedClass, @Nullable GlobalSearchScope scope) {
        // Find all modules that return that type name
        String interfaceQName = sourceModule.getQualifiedName();
        String interfaceName = sourceModule.getModuleName();
        List<RPsiModule> refModules = interfaceName != null ? ModuleSignatureIndex.getElements(interfaceName, sourceModule.getProject(), scope).stream().toList() : emptyList();

        List<RPsiModule> targetModules = refModules.stream()
                .filter(m -> m instanceof RPsiInnerModule)
                .map(module -> {
                    RPsiModuleSignature moduleSignature = ((RPsiInnerModule) module).getModuleSignature();
                    RPsiUpperSymbol moduleSignatureIdentifier = moduleSignature != null ? moduleSignature.getNameIdentifier() : null;
                    ORPsiUpperSymbolReference reference = moduleSignatureIdentifier != null ? moduleSignatureIdentifier.getReference() : null;
                    PsiElement resolvedSignature = reference != null ? reference.resolve() : null;
                    if (resolvedSignature instanceof RPsiModule resolvedSignatureModule) {
                        String sigQName = resolvedSignatureModule.getQualifiedName();
                        return sigQName != null && sigQName.equals(interfaceQName) ? module : null;
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .toList();

        if (!targetModules.isEmpty()) {
            // Iterate over potential modules to find the correct ones
            return targetModules.stream().map(module -> {
                        for (T targetElement : PsiTreeUtil.getChildrenOfTypeAsList(module.getBody(), expectedClass)) {
                            if (elementName.equals(targetElement.getName())) {
                                return targetElement;
                            }
                        }
                        return null;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

        return emptyList();
    }

    private @Nullable <T extends PsiQualifiedNamedElement> RelatedItemLineMarkerInfo<PsiElement> createMarkerInfo(@NotNull PsiNameIdentifierOwner psiSource, boolean isInterface, @NotNull String method, @Nullable Collection<T> relatedElements) {
        PsiElement nameIdentifier = psiSource.getNameIdentifier();
        if (nameIdentifier != null && relatedElements != null && !relatedElements.isEmpty()) {
            return NavigationGutterIconBuilder.create(isInterface ? ORIcons.IMPLEMENTED : ORIcons.IMPLEMENTING)
                    .setTooltipText((isInterface ? "Implements " : "Declare ") + method)
                    .setAlignment(GutterIconRenderer.Alignment.RIGHT)
                    .setTargets(relatedElements)
                    .createLineMarkerInfo(nameIdentifier);
        }
        return null;
    }
}
