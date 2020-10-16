package com.reason.lang.core.psi.reference;

import com.intellij.openapi.project.*;
import com.intellij.openapi.util.*;
import com.intellij.psi.*;
import com.intellij.psi.search.*;
import com.reason.*;
import com.reason.ide.search.*;
import com.reason.lang.*;
import com.reason.lang.core.psi.PsiParameter;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class PsiPropertyNameReference extends PsiReferenceBase<PsiLeafPropertyName> {
  private final Log LOG = Log.create("ref.params");

  @Nullable
  private final String m_referenceName;

  public PsiPropertyNameReference(@NotNull PsiLeafPropertyName element) {
    super(element, TextRange.from(0, element.getTextLength()));
    m_referenceName = element.getText();
  }

  @Override
  public @Nullable PsiElement resolve() {
    if (m_referenceName == null) {
      return null;
    }

    Project project = myElement.getProject();
    GlobalSearchScope scope = GlobalSearchScope.allScope(project);

    PsiFinder psiFinder = PsiFinder.getInstance(project);

    // Find potential paths of current element
    List<String> potentialPaths = getPotentialPaths(psiFinder);
    LOG.debug("  potential paths", potentialPaths);

    for (String path : potentialPaths) {
      PsiParameter parameter = psiFinder.findParameterFromQn(path, scope);
      if (parameter != null) {
        LOG.debug("  -> Found", parameter);
        return parameter.getNameIdentifier();
      }
    }

    return null; // new ORFakeResolvedElement(myElement);
  }

  private List<String> getPotentialPaths(@NotNull PsiFinder psiFinder) {
    List<String> result = new ArrayList<>();

    QNameFinder qnameFinder = PsiFinder.getQNameFinder(myElement.getLanguage());
    PsiElement parent = myElement.getParent();
    PsiElement grandParent = parent == null ? null : parent.getParent();
    PsiElement nameIdentifier = grandParent == null ? null : ((PsiTagStart) grandParent).getNameIdentifier();
    if (nameIdentifier != null) {
      Set<String> potentialPaths = qnameFinder.extractPotentialPaths(nameIdentifier);

      List<PsiQualifiedElement> resolvedPaths = new ArrayList<>();
      for (String pathName : potentialPaths) {
        PsiLet let =
            psiFinder.findLetFromQn(pathName + (pathName.isEmpty() ? "" : ".") + "make");
        if (let != null) {
          resolvedPaths.add(let);
        }
      }

      for (PsiQualifiedElement resolvedPath : resolvedPaths) {
        result.add(resolvedPath.getQualifiedName() + "[" + m_referenceName + "]");
      }
    }

    return result;
  }
}
