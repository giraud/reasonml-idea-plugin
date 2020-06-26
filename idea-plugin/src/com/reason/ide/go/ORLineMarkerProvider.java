package com.reason.ide.go;

import java.util.*;

import com.reason.lang.core.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.daemon.impl.GutterIconTooltipHelper;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.util.PsiTreeUtil;
import icons.ORIcons;
import com.reason.ide.files.FileBase;
import com.reason.ide.search.PsiFinder;

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
        } else if (element instanceof PsiLowerSymbol && parent instanceof PsiLet && ((PsiNameIdentifierOwner) parent).getNameIdentifier() == element) {
            extractRelatedExpressions(element.getFirstChild(), result, containingFile);
        } else if (element instanceof PsiLowerSymbol && parent instanceof PsiVal && ((PsiNameIdentifierOwner) parent).getNameIdentifier() == element) {
            extractRelatedExpressions(element.getFirstChild(), result, containingFile);
        } else if (element instanceof PsiLowerSymbol && parent instanceof PsiExternal && ((PsiNameIdentifierOwner) parent).getNameIdentifier() == element) {
            extractRelatedExpressions(element.getFirstChild(), result, containingFile);
        } else if (element instanceof PsiUpperSymbol && parent instanceof PsiInnerModule && ((PsiNameIdentifierOwner) parent).getNameIdentifier() == element) {
            extractRelatedExpressions(element.getFirstChild(), result, containingFile);
        } else if (element instanceof PsiUpperSymbol && parent instanceof PsiException && ((PsiNameIdentifierOwner) parent).getNameIdentifier() == element) {
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
            Collection<PsiNameIdentifierOwner> expressions = psiRelatedFile.getExpressions(element.getText());
            if (expressions.size() == 1) {
                PsiNameIdentifierOwner relatedElement = expressions.iterator().next();
                PsiElement nameIdentifier = relatedElement.getNameIdentifier();
                if (nameIdentifier != null) {
                    String tooltip = GutterIconTooltipHelper
                            .composeText(new PsiElement[]{psiRelatedFile}, "", "Implements method <b>" + nameIdentifier.getText() + "</b> in <b>{0}</b>");
                    result.add(NavigationGutterIconBuilder.
                            create(containingFile.isInterface() ? ORIcons.IMPLEMENTED : ORIcons.IMPLEMENTING).
                            setTooltipText(tooltip).
                            setAlignment(GutterIconRenderer.Alignment.RIGHT).
                            setTargets(Collections.singleton(nameIdentifier instanceof PsiLowerSymbol ? nameIdentifier.getFirstChild() : nameIdentifier)).
                            createLineMarkerInfo(element));
                }
            }
        }
    }
}
