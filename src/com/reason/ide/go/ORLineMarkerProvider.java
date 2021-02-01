package com.reason.ide.go;

import com.intellij.codeInsight.daemon.*;
import com.intellij.codeInsight.daemon.impl.*;
import com.intellij.codeInsight.navigation.*;
import com.intellij.openapi.editor.markup.*;
import com.intellij.psi.*;
import com.reason.ide.files.*;
import com.reason.ide.search.*;
import com.reason.lang.core.psi.PsiType;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import icons.*;
import org.jetbrains.annotations.*;

import java.util.*;

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
            "method",
            PsiLet.class,
            PsiVal.class);
      } else if (parent instanceof PsiExternal) {
        extractRelatedExpressions(
            element.getFirstChild(),
            ((PsiExternal) parent).getQualifiedName(),
            result,
            containingFile,
            "method",
            PsiExternal.class);
      } else if (parent instanceof PsiVal) {
        extractRelatedExpressions(
            element.getFirstChild(),
            ((PsiVal) parent).getQualifiedName(),
            result,
            containingFile,
            "method",
            PsiVal.class,
            PsiLet.class);
      } else if (parent instanceof PsiType) {
        extractRelatedExpressions(
            element.getFirstChild(),
            ((PsiType) parent).getQualifiedName(),
            result,
            containingFile,
            "type",
            PsiType.class);
      }
    } else if (element instanceof PsiUpperIdentifier) {
      if (parent instanceof PsiInnerModule) {
        extractRelatedExpressions(
            element.getFirstChild(),
            ((PsiInnerModule) parent).getQualifiedName(),
            result,
            containingFile,
            "module",
            PsiInnerModule.class);
      } else if (parent instanceof PsiException) {
        extractRelatedExpressions(
            element.getFirstChild(),
            ((PsiException) parent).getQualifiedName(),
            result,
            containingFile,
            "exception",
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
      @NotNull String method,
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
                  (isInterface ? "Implements " : "Declare ") + method + " in ",
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
