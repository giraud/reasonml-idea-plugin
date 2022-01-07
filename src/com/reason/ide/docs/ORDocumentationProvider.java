package com.reason.ide.docs;

import com.intellij.lang.documentation.*;
import com.intellij.openapi.editor.*;
import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.ide.files.*;
import com.reason.ide.hints.*;
import com.reason.ide.search.*;
import com.reason.lang.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.PsiAnnotation;
import com.reason.lang.core.psi.PsiType;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.psi.reference.*;
import com.reason.lang.ocaml.*;
import com.reason.lang.reason.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class ORDocumentationProvider implements DocumentationProvider {
    private static final Log LOG = Log.create("doc");

    @Override
    public @Nullable String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
        ORLanguageProperties languageProperties = ORLanguageProperties.cast(originalElement == null ? null : originalElement.getLanguage());
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
                return DocFormatter.format(element.getContainingFile(), element, languageProperties, text);
            }
        } else if (element instanceof PsiUpperIdentifier || element instanceof PsiLowerIdentifier) {
            element = element.getParent();

            // If it's an alias, resolve to the alias
            if (element instanceof PsiLet) {
                String alias = ((PsiLet) element).getAlias();
                if (alias != null) {
                    PsiElement resolvedAlias = ((PsiLet) element).resolveAlias();
                    if (resolvedAlias instanceof PsiLowerIdentifier) {
                        element = resolvedAlias.getParent();
                    }
                }
            }

            // Try to find a comment just below (OCaml only)
            if (element.getLanguage() == OclLanguage.INSTANCE) {
                PsiElement belowComment = findBelowComment(element);
                if (belowComment != null) {
                    return isSpecialComment(belowComment)
                            ? DocFormatter.format(element.getContainingFile(), element, languageProperties, belowComment.getText())
                            : belowComment.getText();
                }
            }

            // Else try to find a comment just above
            PsiElement aboveComment = findAboveComment(element);
            if (aboveComment != null) {
                if (aboveComment instanceof PsiAnnotation) {
                    PsiElement value = ((PsiAnnotation) aboveComment).getValue();
                    String text = value == null ? null : value.getText();
                    return text == null ? null : text.substring(1, text.length() - 1);
                }

                return isSpecialComment(aboveComment)
                        ? DocFormatter.format(element.getContainingFile(), element, languageProperties, aboveComment.getText())
                        : aboveComment.getText();
            }
        }

        return null;
    }

    @Override
    public @Nullable String getQuickNavigateInfo(@NotNull PsiElement resolvedIdentifier, @NotNull PsiElement originalElement) {
        String quickDoc = null;
        ORLanguageProperties languageProperties = ORLanguageProperties.cast(originalElement.getLanguage());

        if (resolvedIdentifier instanceof ORFakeResolvedElement) {
            // A fake element, used to query inferred types
            quickDoc = "Show usages of fake element '" + resolvedIdentifier.getText() + "'";
        } else if (resolvedIdentifier instanceof FileBase) {
            LOG.debug("Quickdoc of topModule", resolvedIdentifier);

            FileBase resolvedFile = (FileBase) resolvedIdentifier;
            String relative_path = Platform.getRelativePathToModule(resolvedFile);
            quickDoc =
                    "<div style='white-space:nowrap;font-style:italic'>"
                            + relative_path
                            + "</div>"
                            + "Module "
                            //+ DocFormatter.NAME_START
                            + resolvedFile.getModuleName();
            //+ DocFormatter.NAME_END;
        } else {
            PsiElement resolvedElement = (resolvedIdentifier instanceof PsiLowerIdentifier
                    || resolvedIdentifier instanceof PsiUpperIdentifier)
                    ? resolvedIdentifier.getParent()
                    : resolvedIdentifier;
            LOG.trace("Resolved element", resolvedElement);

            if (resolvedElement instanceof PsiType) {
                PsiType type = (PsiType) resolvedElement;
                String[] path = ORUtil.getQualifiedPath(type);
                String typeBinding = type.isAbstract()
                        ? "This is an abstract type"
                        : DocFormatter.escapeCodeForHtml(type.getBinding());
                return createQuickDocTemplate(path, "type", resolvedIdentifier.getText(), typeBinding);
            }

            if (resolvedElement instanceof PsiSignatureElement) {
                PsiSignature signature = ((PsiSignatureElement) resolvedElement).getSignature();
                if (signature != null) {
                    String sig = DocFormatter.escapeCodeForHtml(signature.asText(languageProperties));
                    if (resolvedElement instanceof PsiQualifiedPathElement) {
                        PsiQualifiedPathElement qualifiedElement = (PsiQualifiedPathElement) resolvedElement;
                        String elementType = PsiTypeElementProvider.getType(resolvedIdentifier);
                        return createQuickDocTemplate(qualifiedElement.getPath(), elementType, qualifiedElement.getName(), sig);
                    }
                    return sig;
                }
            }

            // No signature found, but resolved
            if (resolvedElement instanceof PsiQualifiedNamedElement) {
                LOG.debug("Quickdoc resolved to ", resolvedElement);

                String elementType = PsiTypeElementProvider.getType(resolvedIdentifier);
                String desc = ((PsiQualifiedNamedElement) resolvedElement).getName();
                String[] path = ORUtil.getQualifiedPath(resolvedElement);

                PsiFile psiFile = originalElement.getContainingFile();
                String inferredType = getInferredSignature(originalElement, psiFile, languageProperties);

                if (inferredType == null) {
                    // Can't find type in the usage, try to get type from the definition
                    inferredType = getInferredSignature(resolvedIdentifier, resolvedElement.getContainingFile(), languageProperties);
                }

                String sig = inferredType == null ? null : DocFormatter.escapeCodeForHtml(inferredType);
                if (resolvedElement instanceof PsiVariantDeclaration) {
                    sig = "type " + ((PsiType) resolvedElement.getParent().getParent()).getName();
                }

                return createQuickDocTemplate(path, elementType, desc, resolvedElement instanceof PsiModule ? null : sig);
            }
        }

        return quickDoc;
    }

    private @Nullable PsiElement findAboveComment(@Nullable PsiElement element) {
        if (element == null) {
            return null;
        }

        PsiElement commentElement = null;

        // search for a comment above
        boolean search = true;
        PsiElement prevSibling = element.getPrevSibling();
        while (search) {
            if (prevSibling instanceof PsiComment) {
                search = false;
                commentElement = prevSibling;
            } else if (prevSibling instanceof PsiWhiteSpace) {
                prevSibling = prevSibling.getPrevSibling();
            } else if (prevSibling instanceof PsiAnnotation) {
                PsiAnnotation annotation = (PsiAnnotation) prevSibling;
                if ("@ocaml.doc".equals(annotation.getName())) {
                    search = false;
                    commentElement = annotation;
                } else {
                    prevSibling = prevSibling.getPrevSibling();
                }
            } else {
                search = false;
            }
        }

        return commentElement;
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

    @Override
    public @Nullable PsiElement getCustomDocumentationElement(@NotNull Editor editor, @NotNull PsiFile file, @Nullable PsiElement contextElement, int targetOffset) {
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
    private String getInferredSignature(@NotNull PsiElement element, @NotNull PsiFile psiFile, @NotNull ORLanguageProperties language) {
        SignatureProvider.InferredTypesWithLines signaturesContext = psiFile.getUserData(SignatureProvider.SIGNATURES_CONTEXT);
        if (signaturesContext != null) {
            PsiSignature elementSignature = signaturesContext.getSignatureByOffset(element.getTextOffset());
            if (elementSignature != null) {
                return elementSignature.asText(language);
            }
        }
        return null;
    }

    @NotNull
    private String createQuickDocTemplate(@Nullable String[] path, @Nullable String type, @Nullable String name, @Nullable String signature) {
        return Joiner.join(".", path)
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
