package com.reason.ide.go;

import com.intellij.codeInsight.daemon.*;
import com.intellij.codeInsight.navigation.*;
import com.intellij.codeInsight.navigation.impl.*;
import com.intellij.openapi.editor.markup.*;
import com.intellij.openapi.project.*;
import com.intellij.platform.backend.presentation.*;
import com.intellij.psi.*;
import com.intellij.psi.search.*;
import com.intellij.psi.util.*;
import com.intellij.util.*;
import com.reason.ide.*;
import com.reason.ide.files.*;
import com.reason.ide.search.index.*;
import com.reason.ide.search.reference.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.type.*;
import com.reason.lang.ocaml.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import javax.swing.*;
import java.util.*;
import java.util.stream.*;

import static java.util.Collections.*;

public class ORLineMarkerProvider extends RelatedItemLineMarkerProvider {
    private static final Log LOG = Log.create("goto");

    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
        Project project = element.getProject();
        GlobalSearchScope scope = GlobalSearchScope.allScope(project);
        ORLangTypes types = ORTypesUtil.getInstance(element.getLanguage());
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
        List<RPsiVar> implementedTargets = new ArrayList<>();
        List<RPsiVar> implementingTargets = new ArrayList<>();

        RPsiModule parentModule = PsiTreeUtil.getStubOrPsiParentOfType(element, RPsiModule.class);
        boolean isInterfaceFile = ORUtil.isInterfaceFile(element);
        boolean isOcaml = element.getLanguage() == OclLanguage.INSTANCE;

        if (parentModule instanceof RPsiInnerModule innerModule) {
            LOG.trace("  from inner module", innerModule);
            String letName = element.getName();
            if (letName != null) {
                if (element.getParent() instanceof RPsiModuleSignature) {
                    // The let is inside an inline signature of an inner module,
                    implementedTargets.addAll(PsiTreeUtil.getChildrenOfTypeAsList(parentModule.getBody(), RPsiVar.class)
                            .stream().filter(var -> letName.equals(var.getName()))
                            .toList());

                    // Check if there is a module signature with same name than the module in an interface
                    String qName = parentModule.getQualifiedName();
                    List<RPsiInnerModule> modules = qName != null ? ModuleFqnIndex.getElements(qName, project, scope)
                            .stream().map(m -> m instanceof RPsiInnerModule ? (RPsiInnerModule) m : null)
                            .filter(Objects::nonNull)
                            .filter(m -> m != parentModule && !m.isModuleType()).toList() : List.of();

                    List<RPsiVar> targets = isInterfaceFile ? implementedTargets : implementingTargets;
                    for (RPsiInnerModule module : modules) {
                        List<RPsiVar> vars = new ArrayList<>();
                        // Vars in signature
                        vars.addAll(PsiTreeUtil.getChildrenOfTypeAsList(module.getModuleSignature(), RPsiVar.class)
                                .stream().filter(var -> letName.equals(var.getName())).toList());
                        // Vars in body
                        vars.addAll(PsiTreeUtil.getChildrenOfTypeAsList(module.getBody(), RPsiVar.class)
                                .stream().filter(var -> letName.equals(var.getName())).toList());
                        // Need special marker
                        targets.addAll(vars);
                    }
                } else if (innerModule.isModuleType()) {
                    // Check if there is a module type in interface/implementation file
                    String parentQName = parentModule.getQualifiedName();
                    List<RPsiInnerModule> modules = parentQName != null ? ModuleFqnIndex.getElements(parentQName, project, scope)
                            .stream().map(m -> m instanceof RPsiInnerModule ? (RPsiInnerModule) m : null)
                            .filter(Objects::nonNull)
                            .filter(m -> m != parentModule && m.isModuleType())
                            .toList() : List.of();

                    // Vars in body
                    List<RPsiVar> targets = isInterfaceFile ? implementedTargets : implementingTargets;
                    for (RPsiInnerModule module : modules) {
                        List<RPsiVar> vars = new ArrayList<>(PsiTreeUtil.getChildrenOfTypeAsList(module.getBody(), RPsiVar.class)
                                .stream().filter(var -> letName.equals(var.getName())).toList());
                        targets.addAll(vars);
                    }

                    // Look for the corresponding implementation
                    String qName = innerModule.getQualifiedName();
                    List<RPsiInnerModule> moduleImplementations = findModulesUsingSignatureName(innerModule.getModuleName(), qName, project, scope);
                    for (RPsiInnerModule moduleImplementation : moduleImplementations) {
                        RPsiModuleSignature moduleSignature = moduleImplementation.getModuleSignature();
                        PsiElement moduleBlock = moduleSignature != null && moduleSignature.getNameIdentifier() == null ? moduleSignature : moduleImplementation.getBody();
                        List<RPsiVar> vars = PsiTreeUtil.getChildrenOfTypeAsList(moduleBlock, RPsiVar.class).stream().filter(var ->
                                letName.equals(var.getName())).toList();
                        implementedTargets.addAll(vars);
                    }


                } else {
                    // `let` is inside an inner module body
                    RPsiModuleSignature moduleSignature = ((RPsiInnerModule) parentModule).getModuleSignature();
                    if (moduleSignature != null && moduleSignature.getNameIdentifier() == null) {
                        // Inline signature
                        implementingTargets.addAll(PsiTreeUtil.getChildrenOfTypeAsList(moduleSignature, RPsiVar.class).stream().filter(var -> letName.equals(var.getName())).toList());

                        // Check if there is a module signature with same name than the module in an interface
                        String qName = parentModule.getQualifiedName();
                        List<RPsiInnerModule> modules = qName != null ? ModuleFqnIndex.getElements(qName, project, scope)
                                .stream().map(m -> m instanceof RPsiInnerModule ? (RPsiInnerModule) m : null)
                                .filter(Objects::nonNull)
                                .filter(m -> m != parentModule && !m.isModuleType())
                                .toList() : List.of();
                        List<RPsiVar> targets = isInterfaceFile ? implementedTargets : implementingTargets;
                        for (RPsiInnerModule module : modules) {
                            // Vars in signature
                            targets.addAll(PsiTreeUtil.getChildrenOfTypeAsList(module.getModuleSignature(), RPsiVar.class)
                                    .stream().filter(var -> letName.equals(var.getName())).toList());
                            // Vars in body
                            targets.addAll(PsiTreeUtil.getChildrenOfTypeAsList(module.getBody(), RPsiVar.class)
                                    .stream().filter(var -> letName.equals(var.getName())).toList());
                        }
                    } else {
                        boolean inInterface = ORUtil.inInterface(element);
                        List<RPsiVar> targets = inInterface ? implementedTargets : implementingTargets;
                        targets.addAll(inInterface
                                ? findTargetFromInterfaceModule(innerModule, letName, RPsiVar.class, scope)
                                : findTargetFromImplementationModule(innerModule, letName, RPsiVar.class, project, scope));
                    }
                }
            }
        } else if (parentModule != null) {
            LOG.trace("  from file module", parentModule);
            // Top module navigation
            String qName = element.getQualifiedName();
            Collection<? extends RPsiVar> resolvedElements;
            boolean inInterface = ORUtil.inInterface(element);
            List<RPsiVar> targets = inInterface ? implementedTargets : implementingTargets;

            if (isOcaml && !inInterface) {
                resolvedElements = qName == null ? null : ValFqnIndex.getElements(qName, project, scope);
            } else {
                resolvedElements = qName == null ? null : LetFqnIndex.getElements(qName, project, scope);
            }
            targets.addAll(resolveTargetFromIndex(inInterface, resolvedElements));
        }

        createMarkerInfo(result, element, "let/val", implementedTargets, implementingTargets);
    }

    private void collectValNavigationMarkers(@NotNull RPsiVal element, @NotNull Project project, @NotNull GlobalSearchScope scope, @NotNull Collection<? super RelatedItemLineMarkerInfo<PsiElement>> result) {
        List<RPsiVar> implementedTargets = new ArrayList<>();
        List<RPsiVar> implementingTargets = new ArrayList<>();

        RPsiModule parentModule = PsiTreeUtil.getStubOrPsiParentOfType(element, RPsiModule.class);
        boolean isInterfaceFile = ORUtil.isInterfaceFile(element);

        if (parentModule instanceof RPsiInnerModule innerModule) {
            String valName = element.getName();
            if (valName != null) {
                if (element.getParent() instanceof RPsiSignature) {
                    // The val is inside an inline signature of an inner module,
                    implementedTargets.addAll(PsiTreeUtil.getChildrenOfTypeAsList(parentModule.getBody(), RPsiVar.class)
                            .stream().filter(var -> valName.equals(var.getName()))
                            .toList());

                    // Check if there is a module signature with same name than the module in an interface
                    String qName = parentModule.getQualifiedName();
                    List<RPsiInnerModule> modules = qName != null ? ModuleFqnIndex.getElements(qName, project, scope)
                            .stream().map(m -> m instanceof RPsiInnerModule ? (RPsiInnerModule) m : null)
                            .filter(Objects::nonNull)
                            .filter(m -> m != parentModule && !m.isModuleType()).toList() : List.of();

                    List<RPsiVar> targets = isInterfaceFile ? implementedTargets : implementingTargets;
                    for (RPsiInnerModule module : modules) {
                        List<RPsiVar> vars = new ArrayList<>();
                        // Vars in signature
                        vars.addAll(PsiTreeUtil.getChildrenOfTypeAsList(module.getModuleSignature(), RPsiVar.class)
                                .stream().filter(var -> valName.equals(var.getName())).toList());
                        // Vars in body
                        vars.addAll(PsiTreeUtil.getChildrenOfTypeAsList(module.getBody(), RPsiVar.class)
                                .stream().filter(var -> valName.equals(var.getName())).toList());
                        // Need special marker
                        targets.addAll(vars);
                    }
                } else if (innerModule.isModuleType()) {
                    // Val is inside a module type

                    // Check if there is a module type in interface/implementation file
                    String parentQName = parentModule.getQualifiedName();
                    List<RPsiInnerModule> modules = parentQName != null ? ModuleFqnIndex.getElements(parentQName, project, scope)
                            .stream().map(m -> m instanceof RPsiInnerModule ? (RPsiInnerModule) m : null)
                            .filter(Objects::nonNull)
                            .filter(m -> m != parentModule && m.isModuleType())
                            .toList() : List.of();

                    // Vars in body
                    List<RPsiVar> targets = isInterfaceFile ? implementedTargets : implementingTargets;
                    for (RPsiInnerModule module : modules) {
                        List<RPsiVar> vars = new ArrayList<>(PsiTreeUtil.getChildrenOfTypeAsList(module.getBody(), RPsiVar.class)
                                .stream().filter(var -> valName.equals(var.getName())).toList());
                        targets.addAll(vars);
                    }

                    // Look for the corresponding implementation
                    String qName = innerModule.getQualifiedName();
                    List<RPsiInnerModule> moduleImplementations = findModulesUsingSignatureName(innerModule.getModuleName(), qName, project, scope);
                    for (RPsiInnerModule moduleImplementation : moduleImplementations) {
                        RPsiModuleSignature moduleSignature = moduleImplementation.getModuleSignature();
                        PsiElement moduleBlock = moduleSignature != null && moduleSignature.getNameIdentifier() == null ? moduleSignature : moduleImplementation.getBody();
                        List<RPsiVar> vars = PsiTreeUtil.getChildrenOfTypeAsList(moduleBlock, RPsiVar.class).stream().filter(var ->
                                valName.equals(var.getName())).toList();
                        implementedTargets.addAll(vars);
                    }
                } else {
                    boolean isImplements = innerModule.isModuleType() || ORUtil.inInterface(element);
                    List<RPsiVar> targets = isImplements ? implementedTargets : implementingTargets;
                    targets.addAll(isImplements
                            ? findTargetFromInterfaceModule(innerModule, valName, RPsiVar.class, scope)
                            : findTargetFromImplementationModule(innerModule, valName, RPsiVar.class, project, scope));
                }
            }
        } else if (parentModule != null) {
            // Top module navigation
            String qName = element.getQualifiedName();
            Collection<? extends RPsiVar> resolvedElements = qName == null ? null : LetFqnIndex.getElements(qName, project, scope);
            List<RPsiVar> targets = isInterfaceFile ? implementedTargets : implementingTargets;
            targets.addAll(resolveTargetFromIndex(isInterfaceFile, resolvedElements));
        }

        createMarkerInfo(result, element, "let/val", implementedTargets, implementingTargets);
    }

    private void collectTypeNavigationMarkers(@NotNull RPsiType element, @NotNull Project project, @NotNull GlobalSearchScope scope, @NotNull Collection<? super RelatedItemLineMarkerInfo<PsiElement>> result) {
        List<RPsiQualifiedPathElement> implementedTargets = new ArrayList<>();
        List<RPsiQualifiedPathElement> implementingTargets = new ArrayList<>();

        RPsiModule parentModule = PsiTreeUtil.getStubOrPsiParentOfType(element, RPsiModule.class);
        boolean inInterface = ORUtil.inInterface(element);

        if (parentModule instanceof RPsiInnerModule innerModule) {
            inInterface = innerModule.isModuleType() || inInterface;
            String typeName = element.getName();
            if (typeName != null) {
                List<RPsiQualifiedPathElement> targets = inInterface ? implementedTargets : implementingTargets;
                targets.addAll(inInterface
                        ? findTargetFromInterfaceModule(innerModule, typeName, RPsiType.class, scope)
                        : findTargetFromImplementationModule(innerModule, typeName, RPsiType.class, project, scope));
            }
        } else if (parentModule != null) {
            // Top module navigation
            String qName = element.getQualifiedName();
            Collection<RPsiType> resolvedElements = qName == null ? null : TypeFqnIndex.getElements(qName, project, scope);
            List<RPsiQualifiedPathElement> targets = inInterface ? implementedTargets : implementingTargets;
            targets.addAll(resolveTargetFromIndex(inInterface, resolvedElements));
        }

        createMarkerInfo(result, element, "type", implementedTargets, implementingTargets);
    }

    private void collectExternalNavigationMarkers(@NotNull RPsiExternal element, @NotNull Project project, @NotNull GlobalSearchScope scope, @NotNull Collection<? super RelatedItemLineMarkerInfo<PsiElement>> result) {
        List<RPsiQualifiedPathElement> implementedTargets = new ArrayList<>();
        List<RPsiQualifiedPathElement> implementingTargets = new ArrayList<>();

        RPsiModule parentModule = PsiTreeUtil.getStubOrPsiParentOfType(element, RPsiModule.class);
        boolean inInterface = ORUtil.inInterface(element);

        if (parentModule instanceof RPsiInnerModule innerModule) {
            inInterface = innerModule.isModuleType() || inInterface;
            String elementName = element.getName();
            if (elementName != null) {
                List<RPsiQualifiedPathElement> targets = inInterface ? implementedTargets : implementingTargets;
                targets.addAll(inInterface
                        ? findTargetFromInterfaceModule(innerModule, elementName, RPsiExternal.class, scope)
                        : findTargetFromImplementationModule(innerModule, elementName, RPsiExternal.class, project, scope));
            }
        } else {
            // Top module navigation
            String qName = element.getQualifiedName();
            Collection<RPsiExternal> resolvedElements = qName == null ? null : ExternalFqnIndex.getElements(qName, project, scope);
            List<RPsiQualifiedPathElement> targets = inInterface ? implementedTargets : implementingTargets;
            targets.addAll(resolveTargetFromIndex(inInterface, resolvedElements));
        }

        createMarkerInfo(result, element, "external", implementedTargets, implementingTargets);
    }

    private void collectClassNavigationMarkers(@NotNull RPsiClass element, @NotNull Project project, @NotNull GlobalSearchScope scope, @NotNull Collection<? super RelatedItemLineMarkerInfo<PsiElement>> result) {
        List<RPsiQualifiedPathElement> implementedTargets = new ArrayList<>();
        List<RPsiQualifiedPathElement> implementingTargets = new ArrayList<>();

        RPsiModule parentModule = PsiTreeUtil.getStubOrPsiParentOfType(element, RPsiModule.class);

        if (parentModule instanceof RPsiInnerModule innerModule) {
            String elementName = element.getName();
            if (elementName != null) {
                List<RPsiQualifiedPathElement> targets = innerModule.isModuleType() ? implementedTargets : implementingTargets;
                targets.addAll(innerModule.isModuleType()
                        ? findTargetFromInterfaceModule(innerModule, elementName, RPsiClass.class, scope)
                        : findTargetFromImplementationModule(innerModule, elementName, RPsiClass.class, project, scope));
            }
        } else {
            // Top module navigation
            String qName = element.getQualifiedName();
            Collection<RPsiClass> resolvedElements = qName == null ? null : ClassFqnIndex.getElements(qName, project, scope);
            boolean isInterface = ORUtil.isInterfaceFile(element);
            List<RPsiQualifiedPathElement> targets = isInterface ? implementedTargets : implementingTargets;
            targets.addAll(resolveTargetFromIndex(isInterface, resolvedElements));
        }

        createMarkerInfo(result, element, "class", implementedTargets, implementingTargets);
    }

    private void collectClassMethodNavigationMarkers(@NotNull RPsiClassMethod element, @NotNull Project project, @NotNull GlobalSearchScope scope, @NotNull Collection<? super RelatedItemLineMarkerInfo<PsiElement>> result) {
        List<RPsiQualifiedPathElement> implementedTargets = new ArrayList<>();
        List<RPsiQualifiedPathElement> implementingTargets = new ArrayList<>();

        RPsiModule parentModule = PsiTreeUtil.getStubOrPsiParentOfType(element, RPsiModule.class);
        boolean inInterface = ORUtil.inInterface(element);
        List<RPsiQualifiedPathElement> targets = inInterface ? implementedTargets : implementingTargets;

        if (parentModule instanceof RPsiInnerModule innerModule) {
            String elementName = element.getName();
            if (elementName != null) {
                targets.addAll(inInterface
                        ? findTargetFromInterfaceModule(innerModule, elementName, RPsiClassMethod.class, scope)
                        : findTargetFromImplementationModule(innerModule, elementName, RPsiClassMethod.class, project, scope));
            }
        } else {
            // Top module navigation
            String qName = element.getQualifiedName();
            Collection<RPsiClassMethod> resolvedElements = qName != null ? ClassMethodFqnIndex.getElements(qName, project, scope).stream().filter(m -> ORUtil.inInterface(m) != inInterface).toList() : null;
            if (resolvedElements != null) {
                targets.addAll(resolvedElements);
            }
        }

        createMarkerInfo(result, element, "method", implementedTargets, implementingTargets);
    }

    private void collectExceptionNavigationMarkers(@NotNull RPsiException element, @NotNull Project project, @NotNull GlobalSearchScope scope, @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
        List<RPsiQualifiedPathElement> implementedTargets = new ArrayList<>();
        List<RPsiQualifiedPathElement> implementingTargets = new ArrayList<>();

        RPsiModule parentModule = PsiTreeUtil.getStubOrPsiParentOfType(element, RPsiModule.class);
        boolean inInterface = ORUtil.inInterface(element);

        if (parentModule instanceof RPsiInnerModule innerModule) {
            inInterface = innerModule.isModuleType();
            String elementName = element.getName();
            if (elementName != null) {
                List<RPsiQualifiedPathElement> targets = inInterface ? implementedTargets : implementingTargets;
                targets.addAll(inInterface
                        ? findTargetFromInterfaceModule(innerModule, elementName, RPsiException.class, scope)
                        : findTargetFromImplementationModule(innerModule, elementName, RPsiException.class, project, scope));
            }
        } else {
            // Top module navigation
            String qName = element.getQualifiedName();
            Collection<RPsiException> resolvedElements = qName == null ? null : ExceptionFqnIndex.getElements(qName, project, scope);
            List<RPsiQualifiedPathElement> targets = inInterface ? implementedTargets : implementingTargets;
            targets.addAll(resolveTargetFromIndex(inInterface, resolvedElements));
        }

        createMarkerInfo(result, element, "exception", implementedTargets, implementingTargets);
    }

    private void collectInnerModuleNavigationMarkers(@NotNull RPsiInnerModule element, @NotNull Project project, @NotNull GlobalSearchScope scope, @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
        List<RPsiQualifiedPathElement> implementedTargets = new ArrayList<>();
        List<RPsiQualifiedPathElement> implementingTargets = new ArrayList<>();

        String qName = element.getQualifiedName();
        boolean isModuleType = element.isModuleType();

        // A module type define a signature
        if (isModuleType) {
            String signatureName = element.getModuleName();
            if (signatureName != null) {
                // Find module(s) that use the interface as a result
                List<RPsiInnerModule> signatureModules = findModulesUsingSignatureName(signatureName, qName, project, scope);
                implementedTargets.addAll(signatureModules);
            }
        } else {
            RPsiModuleSignature moduleSignature = element.getModuleSignature();
            RPsiUpperSymbol signatureIdentifier = moduleSignature != null ? moduleSignature.getNameIdentifier() : null;
            if (signatureIdentifier != null) {
                // Module is implementing a named signature (module type), we need to find its definition
                PsiElement resolvedElement = signatureIdentifier.getReference().resolveInterface();
                if (resolvedElement instanceof RPsiInnerModule resolvedModule) {
                    implementingTargets.add(resolvedModule);
                }
            }
        }

        // Find module(s) in the related file
        Collection<RPsiModule> modules = qName != null ? ModuleFqnIndex.getElements(qName, project, scope) : null;
        if (modules != null) {
            boolean isInterfaceFile = ORUtil.isInterfaceFile(element);
            List<RPsiInnerModule> relatedModules = modules.stream()
                    .map(m -> m instanceof RPsiInnerModule ? (RPsiInnerModule) m : null)
                    .filter(Objects::nonNull)
                    .filter(m -> ORUtil.isInterfaceFile(m) != isInterfaceFile && m.isModuleType() == isModuleType)
                    .toList();

            if (isInterfaceFile) {
                implementedTargets.addAll(relatedModules);
            } else {
                implementingTargets.addAll(relatedModules);
            }
        }

        createMarkerInfo(result, element, "module", implementedTargets, implementingTargets);
    }

    private static @NotNull List<RPsiInnerModule> findModulesUsingSignatureName(@Nullable String name, @Nullable String qName, @NotNull Project project, @NotNull GlobalSearchScope scope) {
        if (name == null || qName == null) {
            return List.of();
        }

        return ModuleSignatureIndex.getElements(name, project, scope)
                .stream()
                .map(m -> {
                    RPsiInnerModule result = null;
                    if (m instanceof RPsiInnerModule innerModule) {
                        RPsiModuleSignature moduleSignature = innerModule.getModuleSignature();
                        RPsiUpperSymbol moduleSignatureIdentifier = moduleSignature != null ? moduleSignature.getNameIdentifier() : null;
                        ORPsiUpperSymbolReference reference = moduleSignatureIdentifier != null ? moduleSignatureIdentifier.getReference() : null;
                        PsiElement resolvedSignature = reference != null ? reference.resolve() : null;
                        if (resolvedSignature instanceof RPsiModule resolvedSignatureModule) {
                            String sigQName = resolvedSignatureModule.getQualifiedName();
                            result = sigQName != null && sigQName.equals(qName) ? innerModule : null;
                        }
                    }
                    return result;
                })
                .filter(Objects::nonNull)
                .toList();
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

    private @NotNull <T extends RPsiQualifiedPathElement> List<T> findTargetFromImplementationModule(@NotNull RPsiInnerModule sourceModule, @NotNull String elementName, @NotNull Class<T> expectedClass, @NotNull Project project, @NotNull GlobalSearchScope scope) {
        RPsiModuleSignature sourceModuleSignature = sourceModule.getModuleSignature();
        RPsiUpperSymbol sourceSignatureIdentifier = sourceModuleSignature != null ? sourceModuleSignature.getNameIdentifier() : null;
        if (sourceSignatureIdentifier != null) {
            PsiElement resolvedElement = sourceSignatureIdentifier.getReference().resolveInterface();
            if (resolvedElement instanceof RPsiInnerModule resolvedModule) {
                List<T> result = new ArrayList<>();


                // if the resolved module is inside an implementation file, we need to look for same definition in the interface file
                String resolvedQName = resolvedModule.getQualifiedName();
                List<RPsiInnerModule> modules = resolvedQName != null ? ModuleFqnIndex.getElements(resolvedQName, project, scope)
                        .stream().map(m -> m instanceof RPsiInnerModule ? (RPsiInnerModule) m : null)
                        .filter(m -> m != resolvedModule && m != null).toList() : List.of();

                for (RPsiInnerModule module : modules) {
                    result.addAll(PsiTreeUtil.getChildrenOfTypeAsList(module.getBody(), expectedClass).stream().filter(i -> elementName.equals(i.getName())).toList());
                }
                result.addAll(PsiTreeUtil.getChildrenOfTypeAsList(resolvedModule.getBody(), expectedClass).stream().filter(i -> elementName.equals(i.getName())).toList());

                return result;
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

    private <T extends PsiElement> void createMarkerInfo(@NotNull Collection<? super RelatedItemLineMarkerInfo<PsiElement>> result, @NotNull PsiNameIdentifierOwner psiSource, @NotNull String method, @NotNull Collection<T> implementedTargets, @NotNull Collection<T> implementingTargets) {
        PsiElement nameIdentifier = psiSource.getNameIdentifier();

        if (nameIdentifier != null && !(implementedTargets.isEmpty() && implementingTargets.isEmpty())) {
            PresentationRenderer renderer = new PresentationRenderer(psiSource);

            if (!implementedTargets.isEmpty()) {
                // Prefer source items unless there are none
                List<T> sourceImplementedTargets = implementedTargets.stream().filter(Platform::isElementInSourceContent).toList();
                Collection<T> targets = sourceImplementedTargets.isEmpty() ? implementedTargets : sourceImplementedTargets;
                result.add(NavigationGutterIconBuilder.create(ORIcons.IMPLEMENTED)
                        .setAlignment(GutterIconRenderer.Alignment.RIGHT)
                        .setTargetRenderer(() -> renderer)
                        .setTooltipText("Implements " + method)
                        .setTargets(targets)
                        .createLineMarkerInfo(nameIdentifier));
            }

            if (!implementingTargets.isEmpty()) {
                // Prefer source items unless there are none
                List<T> sourceImplementingTargets = implementingTargets.stream().filter(Platform::isElementInSourceContent).toList();
                Collection<T> targets = sourceImplementingTargets.isEmpty() ? implementingTargets : sourceImplementingTargets;
                result.add(NavigationGutterIconBuilder.create(ORIcons.IMPLEMENTING)
                        .setAlignment(GutterIconRenderer.Alignment.RIGHT)
                        .setTargetRenderer(() -> renderer)
                        .setTooltipText("Declare " + method)
                        .setTargets(targets)
                        .createLineMarkerInfo(nameIdentifier));
            }
        }
    }

    static class PresentationRenderer extends PsiTargetPresentationRenderer<PsiElement> {
        private final PsiNameIdentifierOwner mySource;

        public PresentationRenderer(PsiNameIdentifierOwner psiSource) {
            mySource = psiSource;
        }

        @Override
        @SuppressWarnings("UnstableApiUsage")
        public @NotNull TargetPresentation getPresentation(@NotNull PsiElement element) {
            if (element instanceof PsiQualifiedNamedElement namedElement) {
                String name = namedElement.getName();
                PsiFile elementFile = name != null ? namedElement.getContainingFile() : null;
                if (elementFile instanceof FileBase elementBaseFile) {
                    boolean sameFile = mySource.getContainingFile() == elementFile;
                    Icon locationIcon = elementBaseFile.isInterface() ? ORIcons.INNER_MODULE_INTF : ORIcons.INNER_MODULE;
                    Icon icon = PsiIconUtil.getProvidersIcon(element, 0);
                    boolean inSignature = PsiTreeUtil.getStubOrPsiParentOfType(element, RPsiSignature.class) != null;
                    return new TargetPresentationBuilderImpl(null, icon, name, null, inSignature ? "sig" : null, null, sameFile ? null : elementBaseFile.getName(), sameFile ? null : locationIcon).presentation();
                }
            }

            return super.getPresentation(element);
        }
    }
}
