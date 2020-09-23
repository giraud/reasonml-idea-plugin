package com.reason.ide.go;

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandlerBase;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveResult;
import com.reason.ide.files.FileBase;
import com.reason.lang.core.psi.PsiLowerSymbol;
import com.reason.lang.core.psi.PsiUpperSymbol;
import org.jetbrains.annotations.Nullable;

public class ORGotoDeclarationHandler extends GotoDeclarationHandlerBase {

  public @Nullable static PsiElement resolveInterface(@Nullable PsiReference reference) {
    if (reference instanceof PsiPolyVariantReference) {
      ResolveResult[] resolveResults = ((PsiPolyVariantReference) reference).multiResolve(false);
      if (resolveResults.length > 0) {
        // return interface if one exist
        for (ResolveResult resolved : resolveResults) {
          PsiElement element = resolved.getElement();
          if (element != null) {
            FileBase file = (FileBase) element.getContainingFile();
            if (file.isInterface()) {
              return element;
            }
          }
        }
        return resolveResults[0].getElement();
      }
    }
    return reference == null ? null : reference.resolve();
  }

  @Override
  public @Nullable PsiElement getGotoDeclarationTarget(
      @Nullable PsiElement sourceElement, Editor editor) {
    PsiElement parent = sourceElement == null ? null : sourceElement.getParent();
    if (parent instanceof PsiUpperSymbol || parent instanceof PsiLowerSymbol) {
      return resolveInterface(parent.getReference());
    }
    return null;
  }
}
