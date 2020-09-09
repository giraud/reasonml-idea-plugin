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
import com.reason.ide.files.FileBase;
import com.reason.ide.search.PsiFinder;
import com.reason.lang.core.psi.PsiException;
import com.reason.lang.core.psi.PsiExternal;
import com.reason.lang.core.psi.PsiInnerModule;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiQualifiedElement;
import com.reason.lang.core.psi.PsiType;
import com.reason.lang.core.psi.PsiVal;
import com.reason.lang.core.psi.impl.PsiLowerIdentifier;
import com.reason.lang.core.psi.impl.PsiUpperIdentifier;
import icons.ORIcons;

public class ORLineMarkerProvider extends RelatedItemLineMarkerProvider {
    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
        PsiElement parent = element.getParent();
        FileBase containingFile = (FileBase) element.getContainingFile();

        if (element instanceof PsiLowerIdentifier) {
            if (parent instanceof PsiLet) {
                extractRelatedExpressions(element.getFirstChild(), ((PsiLet) parent).getQualifiedName(), result, PsiLet.class, containingFile);
            } else if (parent instanceof PsiExternal) {
                extractRelatedExpressions(element.getFirstChild(), ((PsiExternal) parent).getQualifiedName(), result, PsiExternal.class, containingFile);
            } else if (parent instanceof PsiVal) {
                extractRelatedExpressions(element.getFirstChild(), ((PsiVal) parent).getQualifiedName(), result, PsiVal.class, containingFile);
            } else if (parent instanceof PsiType) {
                extractRelatedExpressions(element.getFirstChild(), ((PsiType) parent).getQualifiedName(), result, PsiType.class, containingFile);
            }
        } else if (element instanceof PsiUpperIdentifier) {
            if (parent instanceof PsiInnerModule) {
                extractRelatedExpressions(element.getFirstChild(), ((PsiInnerModule) parent).getQualifiedName(), result, PsiInnerModule.class, containingFile);
            } else if (parent instanceof PsiException) {
                extractRelatedExpressions(element.getFirstChild(), ((PsiException) parent).getQualifiedName(), result, PsiException.class, containingFile);
            }
        }
    }

    private <T extends PsiQualifiedElement> void extractRelatedExpressions(@Nullable PsiElement element, @Nullable String qname,
                                                                           @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result,
                                                                           @NotNull Class<T> clazz, @NotNull FileBase containingFile) {
        if (element == null) {
            return;
        }

        FileBase psiRelatedFile = PsiFinder.getInstance(containingFile.getProject()).findRelatedFile(containingFile);
        if (psiRelatedFile != null) {
            Collection<T> expressions = psiRelatedFile.getExpressions(qname, clazz);
            if (expressions.size() == 1) {
                T relatedElement = expressions.iterator().next();
                if (relatedElement != null) {
                    String tooltip = GutterIconTooltipHelper
                            .composeText(new PsiElement[]{psiRelatedFile}, "", "Implements method <b>" + relatedElement.getName() + "</b> in <b>{0}</b>");
                    result.add(NavigationGutterIconBuilder.
                            create(containingFile.isInterface() ? ORIcons.IMPLEMENTED : ORIcons.IMPLEMENTING).
                            setTooltipText(tooltip).
                            setAlignment(GutterIconRenderer.Alignment.RIGHT).
                            setTargets(Collections.singleton(relatedElement)).
                            createLineMarkerInfo(element));
                }
            }
        }
    }
}
