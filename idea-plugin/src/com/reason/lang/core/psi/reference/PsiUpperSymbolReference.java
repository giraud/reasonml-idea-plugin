package com.reason.lang.core.psi.reference;

import static com.reason.lang.core.ORFileType.both;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiPolyVariantReferenceBase;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.containers.ArrayListSet;
import com.reason.Joiner;
import com.reason.Log;
import com.reason.Platform;
import com.reason.ide.files.FileBase;
import com.reason.ide.files.FileHelper;
import com.reason.ide.search.PsiFinder;
import com.reason.lang.QNameFinder;
import com.reason.lang.core.ORCodeFactory;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.PsiFakeModule;
import com.reason.lang.core.psi.PsiModule;
import com.reason.lang.core.psi.PsiQualifiedElement;
import com.reason.lang.core.psi.PsiUpperSymbol;
import com.reason.lang.core.psi.PsiVariantDeclaration;
import com.reason.lang.core.psi.impl.PsiUpperIdentifier;
import com.reason.lang.core.type.ORTypes;
import java.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PsiUpperSymbolReference extends PsiPolyVariantReferenceBase<PsiUpperSymbol> {

  private static final Log LOG = Log.create("ref.upper");

  @Nullable private final String m_referenceName;
  @NotNull private final ORTypes m_types;

  public PsiUpperSymbolReference(@NotNull PsiUpperSymbol element, @NotNull ORTypes types) {
    super(element, TextRange.create(0, element.getTextLength()));
    m_referenceName = element.getText();
    m_types = types;
  }

  @NotNull
  @Override
  public ResolveResult[] multiResolve(boolean incompleteCode) {
    if (m_referenceName == null) {
      return ResolveResult.EMPTY_ARRAY;
    }

    // If name is used in a definition, it's a declaration not a usage: ie, it's not a reference
    // http://www.jetbrains.org/intellij/sdk/docs/basics/architectural_overview/psi_references.html
    PsiUpperIdentifier parent = PsiTreeUtil.getParentOfType(myElement, PsiUpperIdentifier.class);
    if (parent != null && parent.getNameIdentifier() == myElement) {
      return ResolveResult.EMPTY_ARRAY;
    }

    LOG.debug("Find reference for upper symbol", m_referenceName);

    // Find potential paths of current element
    List<PsiQualifiedElement> referencedElements = new ArrayList<>(resolveElementsFromPaths());
    if (referencedElements.isEmpty()) {
      LOG.debug(" -> No resolved elements found from paths");
    } else {
      referencedElements.sort(
          (r1, r2) -> {
            PsiFile f1 = r1.getContainingFile();
            // Hack because bucklescript duplicate files into lib/ocaml
            String p1 = Platform.removeProjectDir(r1.getProject(), f1.getVirtualFile().getPath());
            if (p1.contains("lib")) {
              return -1;
            }

            PsiFile f2 = r2.getContainingFile();
            String p2 = Platform.removeProjectDir(r2.getProject(), f2.getVirtualFile().getPath());
            if (p2.contains("lib")) {
              return 1;
            }

            return FileHelper.isInterface(f1.getFileType())
                ? 1
                : (FileHelper.isInterface(f2.getFileType()) ? -1 : 0);
          });

      if (LOG.isDebugEnabled()) {
        LOG.debug(
            "  => found",
            Joiner.join(
                ", ",
                referencedElements,
                item ->
                    item.getQualifiedName()
                        + " ["
                        + Platform.removeProjectDir(
                            item.getProject(), item.getContainingFile().getVirtualFile().getPath())
                        + "]"));
      }

      ResolveResult[] resolveResults = new ResolveResult[referencedElements.size()];

      int i = 0;
      for (PsiQualifiedElement referencedElement : referencedElements) {
        // A fake module resolve to its file
        resolveResults[i] =
            new UpperResolveResult(
                referencedElement instanceof PsiFakeModule
                    ? (FileBase) referencedElement.getContainingFile()
                    : referencedElement);
        i++;
      }

      return resolveResults;
    }

    return ResolveResult.EMPTY_ARRAY;
  }

  @Nullable
  @Override
  public PsiElement resolve() {
    ResolveResult[] resolveResults = multiResolve(false);
    return 0 < resolveResults.length ? resolveResults[0].getElement() : null;
  }

  @Override
  public PsiElement handleElementRename(@NotNull String newName)
      throws IncorrectOperationException {
    PsiUpperIdentifier newNameIdentifier =
        ORCodeFactory.createModuleName(myElement.getProject(), newName);

    ASTNode newNameNode =
        newNameIdentifier == null ? null : newNameIdentifier.getFirstChild().getNode();
    if (newNameNode != null) {
      PsiElement nameIdentifier = myElement.getFirstChild();
      if (nameIdentifier == null) {
        myElement.getNode().addChild(newNameNode);
      } else {
        ASTNode oldNameNode = nameIdentifier.getNode();
        myElement.getNode().replaceChild(oldNameNode, newNameNode);
      }
    }

    return myElement;
  }

  private @NotNull Set<PsiQualifiedElement> resolveElementsFromPaths() {
    Project project = myElement.getProject();
    GlobalSearchScope scope = GlobalSearchScope.allScope(project);
    PsiFinder psiFinder = PsiFinder.getInstance(project);

    QNameFinder qnameFinder = PsiFinder.getQNameFinder(myElement.getLanguage());
    Set<String> paths = qnameFinder.extractPotentialPaths(myElement);
    if (LOG.isTraceEnabled()) {
      LOG.trace(" -> Paths before resolution: " + Joiner.join(", ", paths));
    }

    Set<PsiQualifiedElement> resolvedElements = new ArrayListSet<>();
    for (String path : paths) {
      String qn = path + "." + m_referenceName;

      PsiQualifiedElement variant = psiFinder.findVariant(qn, scope);
      if (variant != null) {
        resolvedElements.add(variant);
      } else {
        // Trying to resolve variant from the name,
        // Variant might be locally open with module name only - and not including type name... qn
        // can't be used
        Collection<PsiVariantDeclaration> variants =
            psiFinder.findVariantByName(path, m_referenceName, scope);
        if (!variants.isEmpty()) {
          resolvedElements.addAll(variants);
        } else {
          PsiQualifiedElement exception = psiFinder.findException(qn, both, scope);
          if (exception != null) {
            resolvedElements.add(exception);
          } else {
            // Don't resolve local module aliases to their real reference: this is needed for
            // refactoring
            Set<PsiModule> modulesFromQn = psiFinder.findModulesFromQn(qn, false, both, scope);
            if (!modulesFromQn.isEmpty()) {
              resolvedElements.addAll(modulesFromQn);
            }
          }
        }
      }
    }

    PsiElement prevSibling = myElement.getPrevSibling();
    if (prevSibling == null || prevSibling.getNode().getElementType() != m_types.DOT) {
      Set<PsiModule> modulesReference =
          psiFinder.findModulesFromQn(m_referenceName, true, both, scope);
      if (modulesReference.isEmpty()) {
        if (LOG.isTraceEnabled()) {
          LOG.trace(" -> No module found for qn " + m_referenceName);
        }
      } else {
        resolvedElements.addAll(modulesReference);
      }
    }

    return resolvedElements;
  }

  private static class UpperResolveResult implements ResolveResult {
    private final PsiElement m_referencedIdentifier;

    public UpperResolveResult(PsiQualifiedElement referencedElement) {
      PsiUpperIdentifier identifier =
          ORUtil.findImmediateFirstChildOfClass(referencedElement, PsiUpperIdentifier.class);
      m_referencedIdentifier = identifier == null ? referencedElement : identifier;
    }

    @Nullable
    @Override
    public PsiElement getElement() {
      return m_referencedIdentifier;
    }

    @Override
    public boolean isValidResult() {
      return true;
    }
  }
}
