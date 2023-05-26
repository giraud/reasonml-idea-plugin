package com.reason.ide.go;

import com.intellij.codeInsight.daemon.*;
import com.intellij.codeInsight.navigation.*;
import com.intellij.openapi.editor.markup.*;
import com.intellij.openapi.project.*;
import com.intellij.psi.*;
import com.intellij.psi.search.*;
import com.intellij.psi.search.searches.*;
import com.intellij.psi.util.*;
import com.reason.ide.*;
import com.reason.ide.search.index.*;
import com.reason.lang.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.type.*;
import com.reason.lang.ocaml.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.stream.*;

import static java.util.Collections.*;

public class ORLineMarkerProvider extends RelatedItemLineMarkerProvider {
    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
        Project project = element.getProject();
        GlobalSearchScope scope = GlobalSearchScope.allScope(project);

        RelatedItemLineMarkerInfo<PsiElement> marker = null;

        // LET
        if (element instanceof RPsiLet) {
            RPsiModule module = PsiTreeUtil.getStubOrPsiParentOfType(element, RPsiModule.class);
            boolean inInterface = module != null && module.isInterface();
            List<? extends RPsiQualifiedPathElement> targets = null;
            boolean isOcaml = element.getLanguage() == OclLanguage.INSTANCE;

            if (module instanceof RPsiInnerModule) {
                String elementName = ((RPsiQualifiedPathElement) element).getName();
                if (elementName != null) {
                    targets = inInterface ? findTargetFromInterfaceModule((RPsiInnerModule) module, elementName, RPsiVar.class)
                            : findTargetFromImplementationModule((RPsiInnerModule) module, elementName, RPsiVar.class);
                }
            } else {
                // Top module navigation
                String qName = ((RPsiQualifiedPathElement) element).getQualifiedName();
                Collection<? extends RPsiVar> resolvedElements;
                if (isOcaml && !inInterface) {
                    resolvedElements = qName == null ? null : ValFqnIndex.getElements(qName, project, scope);
                } else {
                    resolvedElements = qName == null ? null : LetFqnIndex.getElements(qName, project, scope);
                }
                targets = resolveTargetFromIndex(inInterface, resolvedElements);
            }

            marker = createMarkerInfo((RPsiLet) element, inInterface, "let/val", targets);
        }
        // VAL
        else if (element instanceof RPsiVal) {
            RPsiModule module = PsiTreeUtil.getStubOrPsiParentOfType(element, RPsiModule.class);
            boolean inInterface = module != null && module.isInterface();
            List<? extends RPsiQualifiedPathElement> targets = null;

            if (module instanceof RPsiInnerModule) {
                String elementName = ((RPsiQualifiedPathElement) element).getName();
                if (elementName != null) {
                    targets = inInterface ? findTargetFromInterfaceModule((RPsiInnerModule) module, elementName, RPsiVar.class)
                            : findTargetFromImplementationModule((RPsiInnerModule) module, elementName, RPsiVar.class);
                }
            } else {
                // Top module navigation
                String qName = ((RPsiQualifiedPathElement) element).getQualifiedName();
                Collection<? extends RPsiVar> resolvedElements = qName == null ? null : LetFqnIndex.getElements(qName, project, scope);
                targets = resolveTargetFromIndex(inInterface, resolvedElements);
            }

            marker = createMarkerInfo((PsiNameIdentifierOwner) element, inInterface, "let/val", targets);
        }
        // TYPE
        else if (element instanceof RPsiType) {
            RPsiModule module = PsiTreeUtil.getStubOrPsiParentOfType(element, RPsiModule.class);
            boolean inInterface = module != null && module.isInterface();
            List<RPsiType> targetTypes = emptyList();

            if (module instanceof RPsiInnerModule) {
                String elementName = ((RPsiQualifiedPathElement) element).getName();
                if (elementName != null) {
                    targetTypes = inInterface ? findTargetFromInterfaceModule((RPsiInnerModule) module, elementName, RPsiType.class)
                            : findTargetFromImplementationModule((RPsiInnerModule) module, elementName, RPsiType.class);
                }
            } else {
                // Top module navigation
                String qName = ((RPsiQualifiedPathElement) element).getQualifiedName();
                Collection<RPsiType> resolvedElements = qName == null ? null : TypeFqnIndex.getElements(qName, project, scope);
                targetTypes = resolveTargetFromIndex(inInterface, resolvedElements);
            }

            marker = createMarkerInfo((RPsiType) element, inInterface, "type", targetTypes);
        }
        // EXTERNAL
        else if (element instanceof RPsiExternal) {
            RPsiModule module = PsiTreeUtil.getStubOrPsiParentOfType(element, RPsiModule.class);
            boolean inInterface = module != null && module.isInterface();
            List<RPsiExternal> targets = null;

            if (module instanceof RPsiInnerModule) {
                String elementName = ((RPsiQualifiedPathElement) element).getName();
                if (elementName != null) {
                    targets = inInterface ? findTargetFromInterfaceModule((RPsiInnerModule) module, elementName, RPsiExternal.class)
                            : findTargetFromImplementationModule((RPsiInnerModule) module, elementName, RPsiExternal.class);
                }
            } else {
                // Top module navigation
                String qName = ((RPsiQualifiedPathElement) element).getQualifiedName();
                Collection<RPsiExternal> resolvedElements = qName == null ? null : ExternalFqnIndex.getElements(qName, project, scope);
                targets = resolveTargetFromIndex(inInterface, resolvedElements);
            }

            marker = createMarkerInfo((RPsiExternal) element, inInterface, "external", targets);
        }
        // CLASS
        else if (element instanceof RPsiClass) {
            RPsiModule module = PsiTreeUtil.getStubOrPsiParentOfType(element, RPsiModule.class);
            boolean inInterface = module != null && module.isInterface();
            List<RPsiClass> targets = null;

            if (module instanceof RPsiInnerModule) {
                String elementName = ((RPsiQualifiedPathElement) element).getName();
                if (elementName != null) {
                    targets = inInterface ? findTargetFromInterfaceModule((RPsiInnerModule) module, elementName, RPsiClass.class)
                            : findTargetFromImplementationModule((RPsiInnerModule) module, elementName, RPsiClass.class);
                }
            } else {
                // Top module navigation
                String qName = ((RPsiQualifiedPathElement) element).getQualifiedName();
                Collection<RPsiClass> resolvedElements = qName == null ? null : ClassFqnIndex.getElements(qName, project, scope);
                targets = resolveTargetFromIndex(inInterface, resolvedElements);
            }

            marker = createMarkerInfo((RPsiClass) element, inInterface, "class", targets);
        }
        // CLASS METHOD
        else if (element instanceof RPsiClassMethodImpl) {
            RPsiModule module = PsiTreeUtil.getStubOrPsiParentOfType(element, RPsiModule.class);
            boolean inInterface = module != null && module.isInterface();
            List<RPsiClassMethod> targets = null;

            if (module instanceof RPsiInnerModule) {
                String elementName = ((RPsiQualifiedPathElement) element).getName();
                if (elementName != null) {
                    targets = inInterface ? findTargetFromInterfaceModule((RPsiInnerModule) module, elementName, RPsiClassMethod.class)
                            : findTargetFromImplementationModule((RPsiInnerModule) module, elementName, RPsiClassMethod.class);
                }
            } else {
                // Top module navigation
                String qName = ((RPsiQualifiedPathElement) element).getQualifiedName();
                Collection<RPsiClassMethod> resolvedElements = qName == null ? null : ClassMethodFqnIndex.getElements(qName, project, scope);
                targets = resolveTargetFromIndex(inInterface, resolvedElements);
            }

            marker = createMarkerInfo((RPsiClassMethod) element, inInterface, "method", targets);
        }
        // EXCEPTION
        else if (element instanceof RPsiException) {
            RPsiModule module = PsiTreeUtil.getStubOrPsiParentOfType(element, RPsiModule.class);
            boolean inInterface = module != null && module.isInterface();
            List<RPsiException> targets = null;

            if (module instanceof RPsiInnerModule) {
                String elementName = ((RPsiQualifiedPathElement) element).getName();
                if (elementName != null) {
                    targets = inInterface ? findTargetFromInterfaceModule((RPsiInnerModule) module, elementName, RPsiException.class)
                            : findTargetFromImplementationModule((RPsiInnerModule) module, elementName, RPsiException.class);
                }
            } else {
                // Top module navigation
                String qName = ((RPsiQualifiedPathElement) element).getQualifiedName();
                Collection<RPsiException> resolvedElements = qName == null ? null : ExceptionFqnIndex.getElements(qName, project, scope);
                targets = resolveTargetFromIndex(inInterface, resolvedElements);
            }

            marker = createMarkerInfo((RPsiException) element, inInterface, "exception", targets);
        }
        // MODULE
        else if (element instanceof RPsiInnerModule module) {
            List<RPsiInnerModule> targets = null;

            if (module.isInterface()) {
                // Find module(s) from interface
                targets = ReferencesSearch.search(module).findAll().stream()
                        .map(searchReference -> {
                            PsiElement referenceElement = searchReference.getElement().getParent();
                            PsiElement targetElement = (referenceElement instanceof RPsiModuleType) ? referenceElement.getParent() : null;
                            return (targetElement instanceof RPsiInnerModule) ? (RPsiInnerModule) targetElement : null;
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
            } else {
                // Find interface from module
                RPsiModuleType moduleType = module.getModuleType();
                PsiElement lastChild = moduleType == null ? null : PsiTreeUtil.lastChild(moduleType);
                if (lastChild instanceof RPsiUpperSymbol) {
                    PsiElement resolvedElement = ((RPsiUpperSymbol) lastChild).getReference().resolveInterface();
                    if (resolvedElement instanceof RPsiInnerModule) {
                        targets = singletonList((RPsiInnerModule) resolvedElement);
                    }
                }
            }

            marker = createMarkerInfo(module, module.isInterface(), "module", targets);
        }

        if (marker != null) {
            result.add(marker);
        }
    }

    private static @NotNull <T extends RPsiQualifiedPathElement> List<T> resolveTargetFromIndex(boolean inInterface, @Nullable Collection<T> resolvedElements) {
        if (resolvedElements != null) {
            for (T resolvedElement : resolvedElements) {
                RPsiModule targetModule = PsiTreeUtil.getStubOrPsiParentOfType(resolvedElement, RPsiModule.class);
                boolean targetInterface = targetModule != null && targetModule.isInterface();
                if (inInterface && !targetInterface) {
                    return singletonList(resolvedElement);
                } else if (!inInterface && targetInterface) {
                    return singletonList(resolvedElement);
                }
            }
        }
        return emptyList();
    }

    private @NotNull <T extends RPsiQualifiedPathElement> List<T> findTargetFromImplementationModule(@NotNull RPsiInnerModule sourceModule, @NotNull String elementName, @NotNull Class<T> expectedClass) {
        RPsiModuleType sourceModuleType = sourceModule.getModuleType();
        PsiElement lastChild = sourceModuleType == null ? null : PsiTreeUtil.lastChild(sourceModuleType);
        if (lastChild instanceof RPsiUpperSymbol) {
            PsiElement resolvedElement = ((RPsiUpperSymbol) lastChild).getReference().resolveInterface();
            if (resolvedElement instanceof RPsiInnerModule) {
                for (T rPsiType : PsiTreeUtil.getChildrenOfTypeAsList(((RPsiInnerModule) resolvedElement).getBody(), expectedClass)) {
                    if (elementName.equals(rPsiType.getName())) {
                        return singletonList(rPsiType);
                    }
                }
            }
        }
        return emptyList();
    }

    private @NotNull <T extends RPsiQualifiedPathElement> List<T> findTargetFromInterfaceModule(@NotNull RPsiInnerModule sourceModule, @NotNull String elementName, @NotNull Class<T> expectedClass) {
        List<RPsiInnerModule> targetModules = ReferencesSearch.search(sourceModule).findAll().stream()
                .map(searchReference -> {
                    PsiElement referenceElement = searchReference.getElement().getParent();
                    PsiElement targetElement = (referenceElement instanceof RPsiModuleType) ? referenceElement.getParent() : null;
                    return (targetElement instanceof RPsiInnerModule) ? (RPsiInnerModule) targetElement : null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (!targetModules.isEmpty()) {
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
