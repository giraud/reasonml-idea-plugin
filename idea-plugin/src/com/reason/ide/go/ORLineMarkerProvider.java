package com.reason.ide.go;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.daemon.impl.GutterTooltipHelper;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.psi.PsiElement;
import com.reason.ide.files.FileBase;
import com.reason.ide.search.PsiFinder;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.PsiLowerIdentifier;
import com.reason.lang.core.psi.impl.PsiUpperIdentifier;
import icons.ORIcons;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ORLineMarkerProvider extends RelatedItemLineMarkerProvider {
  @Override
  protected void collectNavigationMarkers(
      @NotNull PsiElement element,
      @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
    PsiElement parent = element.getParent();
    FileBase containingFile = (FileBase) element.getContainingFile();

    if (element instanceof PsiLowerIdentifier) {
      if (parent instanceof PsiLet) {
        extractRelatedExpressions(
            element.getFirstChild(),
            ((PsiLet) parent).getQualifiedName(),
            result,
            containingFile,
            PsiLet.class,
            PsiVal.class);
      } else if (parent instanceof PsiExternal) {
        extractRelatedExpressions(
            element.getFirstChild(),
            ((PsiExternal) parent).getQualifiedName(),
            result,
            containingFile,
            PsiExternal.class);
      } else if (parent instanceof PsiVal) {
        extractRelatedExpressions(
            element.getFirstChild(),
            ((PsiVal) parent).getQualifiedName(),
            result,
            containingFile,
            PsiVal.class,
            PsiLet.class);
      } else if (parent instanceof PsiType) {
        extractRelatedExpressions(
            element.getFirstChild(),
            ((PsiType) parent).getQualifiedName(),
            result,
            containingFile,
            PsiType.class);
      }
    } else if (element instanceof PsiUpperIdentifier) {
      if (parent instanceof PsiInnerModule) {
        extractRelatedExpressions(
            element.getFirstChild(),
            ((PsiInnerModule) parent).getQualifiedName(),
            result,
            containingFile,
            PsiInnerModule.class);
      } else if (parent instanceof PsiException) {
        extractRelatedExpressions(
            element.getFirstChild(),
            ((PsiException) parent).getQualifiedName(),
            result,
            containingFile,
            PsiException.class);
      }
    }
  }

  @SafeVarargs
  private final <T extends PsiQualifiedElement> void extractRelatedExpressions(
      @Nullable PsiElement element,
      @Nullable String qname,
      @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result,
      @NotNull FileBase containingFile,
      @NotNull Class<? extends T>... clazz) {
    if (element == null) {
      return;
    }

    FileBase psiRelatedFile =
        PsiFinder.getInstance(containingFile.getProject()).findRelatedFile(containingFile);
    if (psiRelatedFile != null) {
      List<T> expressions = psiRelatedFile.getQualifiedExpressions(qname, clazz);
      if (expressions.size() == 1) {
        T relatedElement = expressions.iterator().next();
        if (relatedElement != null) {
          boolean isInterface = containingFile.isInterface();
          String tooltip =
              GutterTooltipHelper.getTooltipText(
                  Collections.singletonList(psiRelatedFile),
                  (isInterface ? "Implements" : "Declare") + " method in ",
                  false,
                  null);
          result.add(
              NavigationGutterIconBuilder.create(
                      isInterface ? ORIcons.IMPLEMENTED : ORIcons.IMPLEMENTING)
                  .setTooltipText(tooltip)
                  .setAlignment(GutterIconRenderer.Alignment.RIGHT)
                  .setTargets(Collections.singleton(relatedElement))
                  .createLineMarkerInfo(element));
        }
      }
    }
  }
}
