package com.reason.ide.go;

import java.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.daemon.impl.GutterIconTooltipHelper;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.ide.files.FileBase;
import com.reason.ide.search.PsiFinder;
import com.reason.lang.core.psi.PsiException;
import com.reason.lang.core.psi.PsiExternal;
import com.reason.lang.core.psi.PsiInnerModule;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiLowerSymbol;
import com.reason.lang.core.psi.PsiType;
import com.reason.lang.core.psi.PsiTypeConstrName;
import com.reason.lang.core.psi.PsiVal;
import com.reason.lang.core.psi.impl.PsiLowerIdentifier;
import com.reason.lang.core.psi.impl.PsiUpperIdentifier;
import icons.ORIcons;

public class ORLineMarkerProvider extends RelatedItemLineMarkerProvider {
    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo> result) {
        PsiElement parent = element.getParent();
        FileBase containingFile = (FileBase) element.getContainingFile();

        if (element instanceof PsiTypeConstrName) {
            FileBase psiRelatedFile = PsiFinder.getInstance(containingFile.getProject()).findRelatedFile(containingFile);
            if (psiRelatedFile != null) {
                Collection<PsiType> expressions = psiRelatedFile.getExpressions(element.getText(), PsiType.class);
                if (expressions.size() == 1) {
                    PsiType relatedType = expressions.iterator().next();
                    PsiElement symbol = PsiTreeUtil.findChildOfType(element, PsiLowerSymbol.class);
                    PsiElement relatedSymbol = PsiTreeUtil.findChildOfType(relatedType, PsiLowerSymbol.class);
                    if (symbol != null && relatedSymbol != null) {
                        result.add(NavigationGutterIconBuilder.
                                create(containingFile.isInterface() ? ORIcons.IMPLEMENTED : ORIcons.IMPLEMENTING).
                                setAlignment(GutterIconRenderer.Alignment.RIGHT).
                                setTargets(Collections.singleton(relatedSymbol.getFirstChild())).
                                createLineMarkerInfo(symbol.getFirstChild()));
                    }
                }
            }
        } else if (element instanceof PsiLowerIdentifier && parent instanceof PsiLet) {
            extractRelatedExpressions(element.getFirstChild(), result, containingFile);
        } else if (element instanceof PsiLowerIdentifier && parent instanceof PsiVal) {
            extractRelatedExpressions(element.getFirstChild(), result, containingFile);
        } else if (element instanceof PsiLowerIdentifier && parent instanceof PsiExternal) {
            extractRelatedExpressions(element.getFirstChild(), result, containingFile);
        } else if (element instanceof PsiUpperIdentifier && parent instanceof PsiInnerModule) {
            extractRelatedExpressions(element.getFirstChild(), result, containingFile);
        } else if (element instanceof PsiUpperIdentifier && parent instanceof PsiException) {
            extractRelatedExpressions(element.getFirstChild(), result, containingFile);
        }
    }

    private void extractRelatedExpressions(@Nullable PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result,
                                           @NotNull FileBase containingFile) {
        if (element == null) {
            return;
        }

        FileBase psiRelatedFile = PsiFinder.getInstance(containingFile.getProject()).findRelatedFile(containingFile);
        if (psiRelatedFile != null) {
            Collection<PsiNamedElement> expressions = psiRelatedFile.getExpressions(element.getText());
            if (expressions.size() == 1) {
                PsiNamedElement relatedElement = expressions.iterator().next();
                //PsiElement nameIdentifier = ORUtil.findImmediateFirstChildOfClass(relatedElement, PsiUpperIdentifier.class);
                if (relatedElement != null) {
                    String tooltip = GutterIconTooltipHelper
                            .composeText(new PsiElement[]{psiRelatedFile}, "", "Implements method <b>" + relatedElement.getName() + "</b> in <b>{0}</b>");
                    result.add(NavigationGutterIconBuilder.
                            create(containingFile.isInterface() ? ORIcons.IMPLEMENTED : ORIcons.IMPLEMENTING).
                            setTooltipText(tooltip).
                            setAlignment(GutterIconRenderer.Alignment.RIGHT).
                            setTargets(Collections.singleton(/*nameIdentifier instanceof PsiLowerSymbol ? nameIdentifier.getFirstChild() : nameIdentifier*/
                                    relatedElement)).
                            createLineMarkerInfo(element));
                }
            }
        }
    }
}
