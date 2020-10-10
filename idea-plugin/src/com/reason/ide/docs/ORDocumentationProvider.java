package com.reason.ide.docs;

import com.intellij.lang.Language;
import com.intellij.lang.documentation.DocumentationProvider;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.Platform;
import com.reason.ide.files.FileBase;
import com.reason.ide.hints.SignatureProvider;
import com.reason.ide.search.PsiFinder;
import com.reason.ide.search.PsiTypeElementProvider;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.PsiType;
import com.reason.lang.core.psi.impl.PsiFakeModule;
import com.reason.lang.core.psi.impl.PsiLowerIdentifier;
import com.reason.lang.core.psi.impl.PsiUpperIdentifier;
import com.reason.lang.core.psi.reference.ORFakeResolvedElement;
import com.reason.lang.core.psi.reference.PsiLowerSymbolReference;
import com.reason.lang.core.signature.ORSignature;
import com.reason.lang.ocaml.OclLanguage;
import com.reason.lang.reason.RmlLanguage;
import java.util.Arrays;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ORDocumentationProvider implements DocumentationProvider {

  @Override
  public @Nullable String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
    if (element instanceof PsiFakeModule) {
      PsiElement child = element.getContainingFile().getFirstChild();
      String text = "";

      PsiElement nextSibling = child;
      while (nextSibling instanceof PsiComment) {
        if (isSpecialComment(nextSibling)) {
          text = nextSibling.getText();
          nextSibling = null;
        } else {
          // Not a special comment, try with next child until no more comments found
          nextSibling = PsiTreeUtil.nextVisibleLeaf(nextSibling);
        }
      }

      if (!text.isEmpty()) {
        return DocFormatter.format(element.getContainingFile(), element, text);
      }
    } else if (element instanceof PsiUpperIdentifier || element instanceof PsiLowerIdentifier) {
      element = element.getParent();

      // If it's an alias, resolve to the alias
      if (element instanceof PsiLet) {
        String alias = ((PsiLet) element).getAlias();
        if (alias != null) {
          Project project = element.getProject();
          PsiFinder psiFinder = PsiFinder.getInstance(project);
          PsiVal valFromAlias = psiFinder.findValFromQn(alias);
          if (valFromAlias == null) {
            PsiLet letFromAlias = psiFinder.findLetFromQn(alias);
            if (letFromAlias != null) {
              element = letFromAlias;
            }
          } else {
            element = valFromAlias;
          }
        }
      }

      // Try to find a comment just below (OCaml only)
      if (element.getLanguage() == OclLanguage.INSTANCE) {
        PsiElement belowComment = findBelowComment(element);
        if (belowComment != null) {
          return isSpecialComment(belowComment)
              ? DocFormatter.format(element.getContainingFile(), element, belowComment.getText())
              : belowComment.getText();
        }
      }

      // Else try to find a comment just above
      PsiElement aboveComment = findAboveComment(element);
      if (aboveComment != null) {
        return isSpecialComment(aboveComment)
            ? DocFormatter.format(element.getContainingFile(), element, aboveComment.getText())
            : aboveComment.getText();
      }
    }

    return null;
  }

  @Nullable
  @Override
  public String getQuickNavigateInfo(
      @NotNull PsiElement resolvedIdentifier, @NotNull PsiElement originalElement) {
    String quickDoc = null;

    if (resolvedIdentifier instanceof ORFakeResolvedElement) {
      // A fake element, used to query inferred types
      quickDoc = "Show usages of fake element '" + resolvedIdentifier.getText() + "'";
    } else if (resolvedIdentifier instanceof FileBase) {
      FileBase resolvedFile = (FileBase) resolvedIdentifier;
      String relative_path = Platform.getRelativePathToModule(resolvedFile);
      quickDoc =
          "<div style='white-space:nowrap;font-style:italic'>"
              + relative_path
              + "&nbsp;</div>"
              + "Module "
              + DocFormatter.NAME_START
              + resolvedFile.getModuleName()
              + DocFormatter.NAME_END;
    } else {
      PsiElement resolvedElement =
          (resolvedIdentifier instanceof PsiLowerIdentifier
                  || resolvedIdentifier instanceof PsiUpperIdentifier)
              ? resolvedIdentifier.getParent()
              : resolvedIdentifier;

      if (resolvedElement instanceof PsiType) {
        PsiType type = (PsiType) resolvedElement;
        String path = ORUtil.getQualifiedPath(type);
        String typeBinding =
            type.isAbstract()
                ? "This is an abstract type"
                : DocFormatter.escapeCodeForHtml(type.getBinding());
        return createQuickDocTemplate(path, "type", resolvedIdentifier.getText(), typeBinding);
      }

      if (resolvedElement instanceof PsiSignatureElement) {
        ORSignature signature = ((PsiSignatureElement) resolvedElement).getORSignature();
        if (!signature.isEmpty()) {
          String sig =
              DocFormatter.escapeCodeForHtml(signature.asString(originalElement.getLanguage()));
          if (resolvedElement instanceof PsiQualifiedElement) {
            PsiQualifiedElement qualifiedElement = (PsiQualifiedElement) resolvedElement;
            String elementType = PsiTypeElementProvider.getType(resolvedIdentifier);
            return createQuickDocTemplate(
                qualifiedElement.getPath(), elementType, qualifiedElement.getName(), sig);
          }
          return sig;
        }
      }

      // No signature found, but resolved
      if (resolvedElement instanceof PsiQualifiedElement) {
        String elementType = PsiTypeElementProvider.getType(resolvedIdentifier);
        String desc = ((PsiQualifiedElement) resolvedElement).getName();
        String path = ORUtil.getQualifiedPath((PsiQualifiedElement) resolvedElement);

        PsiFile psiFile = originalElement.getContainingFile();
        String inferredType =
            getInferredSignature(originalElement, psiFile, originalElement.getLanguage());

        if (inferredType == null) {
          // Can't find type in the usage, try to get type from the definition
          inferredType =
              getInferredSignature(
                  resolvedIdentifier,
                  resolvedElement.getContainingFile(),
                  resolvedElement.getLanguage());
        }

        String sig = inferredType == null ? null : DocFormatter.escapeCodeForHtml(inferredType);
        if (resolvedElement instanceof PsiVariantDeclaration) {
          sig = "type " + ((PsiType) resolvedElement.getParent().getParent()).getName();
        }

        return createQuickDocTemplate(
            path, elementType, desc, resolvedElement instanceof PsiModule ? null : sig);
      }
    }

    return quickDoc;
  }

  @Nullable
  private PsiElement findAboveComment(@Nullable PsiElement element) {
    if (element == null) {
      return null;
    }

    PsiElement prevSibling = element.getPrevSibling();
    PsiElement prevPrevSibling = prevSibling == null ? null : prevSibling.getPrevSibling();
    if (prevPrevSibling instanceof PsiComment
        && prevSibling instanceof PsiWhiteSpace
        && prevSibling.getText().replaceAll("[ \t]", "").length() == 1) {
      return prevPrevSibling;
    }

    return null;
  }

  @Nullable
  private PsiElement findBelowComment(@Nullable PsiElement element) {
    if (element != null) {
      PsiElement nextSibling = element.getNextSibling();
      PsiElement nextNextSibling = nextSibling == null ? null : nextSibling.getNextSibling();
      if (nextNextSibling instanceof PsiComment
          && nextSibling instanceof PsiWhiteSpace
          && nextSibling.getText().replaceAll("[ \t]", "").length() == 1) {
        return nextNextSibling;
      }
    }

    return null;
  }

  @Nullable
  @Override
  public PsiElement getCustomDocumentationElement(
      @NotNull Editor editor,
      @NotNull PsiFile file,
      @Nullable PsiElement contextElement,
      int targetOffset) {
    // When quick doc inside empty parenthesis, we want to display the function doc (github #155)
    // functionName(<caret>) ==> functionName<caret>()
    if (contextElement != null
        && contextElement.getParent() instanceof PsiFunctionCallParams
        && contextElement.getLanguage() == RmlLanguage.INSTANCE) {
      PsiElement prevSibling = contextElement.getParent().getPrevSibling();
      if (prevSibling != null) {
        PsiReference reference = prevSibling.getReference();
        if (reference != null) {
          return reference.resolve();
        }
      }
    }

    if (contextElement != null && contextElement.getParent() instanceof PsiLowerSymbol) {
      PsiReference reference = contextElement.getParent().getReference();
      if (reference instanceof PsiPolyVariantReference) {
        PsiLowerSymbolReference lowerReference = (PsiLowerSymbolReference) reference;
        ResolveResult[] resolveResults = lowerReference.multiResolve(false);
        if (0 < resolveResults.length) {
          Arrays.sort(
              resolveResults,
              (rr1, rr2) ->
                  ((PsiLowerSymbolReference.LowerResolveResult) rr1).isInterface()
                      ? -1
                      : (((PsiLowerSymbolReference.LowerResolveResult) rr2).isInterface() ? 1 : 0));
          return resolveResults[0].getElement();
        }
      }
    }

    return null;
  }

  @Nullable
  private String getInferredSignature(
      @NotNull PsiElement element, @NotNull PsiFile psiFile, @NotNull Language language) {
    SignatureProvider.InferredTypesWithLines signaturesContext =
        psiFile.getUserData(SignatureProvider.SIGNATURE_CONTEXT);
    if (signaturesContext != null) {
      ORSignature elementSignature =
          signaturesContext.getSignatureByOffset(element.getTextOffset());
      if (elementSignature != null) {
        return elementSignature.asString(language);
      }
    }
    return null;
  }

  @NotNull
  private String createQuickDocTemplate(
      @NotNull String qPath,
      @Nullable String type,
      @Nullable String name,
      @Nullable String signature) {
    return qPath
        + "<br/>"
        + (type == null ? "" : type)
        + (" <b>" + name + "</b>")
        + (signature == null ? "" : "<hr/>" + signature);
  }

  public static boolean isSpecialComment(@Nullable PsiElement element) {
    if (element == null) {
      return false;
    }

    String nextText = element.getText();
    return (nextText.startsWith("(**") || nextText.startsWith("/**")) && nextText.charAt(3) != '*';
  }
}
