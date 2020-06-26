package com.reason.ide.docs;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.lang.Language;
import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.Platform;
import com.reason.ide.files.FileBase;
import com.reason.ide.hints.SignatureProvider;
import com.reason.ide.search.PsiFinder;
import com.reason.ide.search.PsiTypeElementProvider;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.PsiFakeModule;
import com.reason.lang.core.psi.PsiFunctionCallParams;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiLowerSymbol;
import com.reason.lang.core.psi.PsiModule;
import com.reason.lang.core.psi.PsiQualifiedElement;
import com.reason.lang.core.psi.PsiSignatureElement;
import com.reason.lang.core.psi.PsiType;
import com.reason.lang.core.psi.PsiTypeConstrName;
import com.reason.lang.core.psi.PsiUpperSymbol;
import com.reason.lang.core.psi.PsiVal;
import com.reason.lang.core.psi.PsiVariantDeclaration;
import com.reason.lang.core.psi.reference.ORFakeResolvedElement;
import com.reason.lang.core.signature.ORSignature;
import com.reason.lang.ocaml.OclLanguage;
import com.reason.lang.reason.RmlLanguage;

import static com.reason.lang.odoc.ODocMarkup.*;

public class DocumentationProvider extends AbstractDocumentationProvider {

    //private static final Log LOG = Log.create("doc");

    public static boolean isSpecialComment(@Nullable PsiElement element) {
        if (element == null) {
            return false;
        }

        String nextText = element.getText();
        return (nextText.startsWith("(**") || nextText.startsWith("/**")) && nextText.charAt(3) != '*';
    }

    @Override
    public String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
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
        } else if (element instanceof PsiUpperSymbol || element instanceof PsiLowerSymbol) {
            element = element.getParent();
            if (element instanceof PsiTypeConstrName) {
                element = element.getParent();
            }

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
                    return isSpecialComment(belowComment) ? DocFormatter.format(element.getContainingFile(), element, belowComment.getText()) :
                            belowComment.getText();
                }
            }

            // Else try to find a comment just above
            PsiElement aboveComment = findAboveComment(element);
            if (aboveComment != null) {
                return isSpecialComment(aboveComment) ? DocFormatter.format(element.getContainingFile(), element, aboveComment.getText()) :
                        aboveComment.getText();
            }
        }

        return super.generateDoc(element, originalElement);
    }

    @Nullable
    private PsiElement findAboveComment(@Nullable PsiElement element) {
        if (element == null) {
            return null;
        }

        PsiElement prevSibling = element.getPrevSibling();
        PsiElement prevPrevSibling = prevSibling == null ? null : prevSibling.getPrevSibling();
        if (prevPrevSibling instanceof PsiComment && prevSibling instanceof PsiWhiteSpace && prevSibling.getText().replaceAll("[ \t]", "").length() == 1) {
            return prevPrevSibling;
        }

        return null;
    }

    @Nullable
    private PsiElement findBelowComment(@Nullable PsiElement element) {
        if (element == null) {
            return null;
        }

        PsiElement nextSibling = element.getNextSibling();
        PsiElement nextNextSibling = nextSibling == null ? null : nextSibling.getNextSibling();
        if (nextNextSibling instanceof PsiComment && nextSibling instanceof PsiWhiteSpace && nextSibling.getText().replaceAll("[ \t]", "").length() == 1) {
            return nextNextSibling;
        }

        return null;
    }

    @Nullable
    @Override
    public String getQuickNavigateInfo(@NotNull PsiElement element, @NotNull PsiElement originalElement) {
        PsiFile psiFile = originalElement.getContainingFile();

        String inferredType = getInferredSignature(originalElement, psiFile, element.getLanguage());

        PsiReference reference = originalElement.getReference();
        if (reference != null) {
            PsiElement resolvedElement = reference.resolve();

            if (resolvedElement instanceof ORFakeResolvedElement) {
                // A fake element, used to query inferred types
                return "Show usages of fake element '" + resolvedElement.getText() + "'";
            }

            if (resolvedElement instanceof FileBase) {
                FileBase resolvedFile = (FileBase) resolvedElement;
                String relative_path = Platform.removeProjectDir(resolvedFile.getProject(), resolvedFile.getVirtualFile().getParent().getPath());
                return relative_path + "<br/>" + resolvedElement.getContainingFile();
            }

            if (!(resolvedElement instanceof PsiSignatureElement) && resolvedElement != null) {
                resolvedElement = resolvedElement.getParent();
            }

            if (resolvedElement instanceof PsiTypeConstrName) {
                PsiType type = (PsiType) resolvedElement.getParent();
                String path = ORUtil.getQualifiedPath(type);
                String typeBinding = type.isAbstract() ? "This is an abstract type" : DocFormatter.escapeCodeForHtml(type.getBinding());
                return createQuickDocTemplate(path, "type", resolvedElement.getText(), typeBinding);
            }

            if (resolvedElement instanceof PsiSignatureElement) {
                ORSignature signature = ((PsiSignatureElement) resolvedElement).getORSignature();
                if (!signature.isEmpty()) {
                    String sig = DocFormatter.escapeCodeForHtml(signature.asString(originalElement.getLanguage()));
                    if (resolvedElement instanceof PsiQualifiedElement) {
                        PsiQualifiedElement qualifiedElement = (PsiQualifiedElement) resolvedElement;
                        String elementType = PsiTypeElementProvider.getType(resolvedElement);
                        return createQuickDocTemplate(qualifiedElement.getPath(), elementType, qualifiedElement.getName(), sig);
                    }
                    return sig;
                }
            }

            // No signature found, but resolved
            if (resolvedElement instanceof PsiNameIdentifierOwner) {
                String elementType = PsiTypeElementProvider.getType(resolvedElement);
                String desc = ((PsiNameIdentifierOwner) resolvedElement).getName();
                String path = ORUtil.getQualifiedPath((PsiNameIdentifierOwner) resolvedElement);

                if (inferredType == null) {
                    // Can't find type in the usage, try to get type from the definition
                    PsiElement nameIdentifier = ((PsiNameIdentifierOwner) resolvedElement).getNameIdentifier();
                    inferredType = getInferredSignature(nameIdentifier == null ? resolvedElement : nameIdentifier, resolvedElement.getContainingFile(),
                                                        resolvedElement.getLanguage());
                }

                String sig = inferredType == null ? null : DocFormatter.escapeCodeForHtml(inferredType);
                if (resolvedElement instanceof PsiVariantDeclaration) {
                    sig = "type " + ((PsiType) resolvedElement.getParent().getParent()).getName();
                }

                return createQuickDocTemplate(path, elementType, desc, resolvedElement instanceof PsiModule ? null : sig);
            }
        }

        return super.getQuickNavigateInfo(element, originalElement);
    }

    @Nullable
    @Override
    public PsiElement getCustomDocumentationElement(@NotNull Editor editor, @NotNull PsiFile file, @Nullable PsiElement contextElement) {
        // When quick doc inside empty parenthesis, we want to display the function doc (github #155)
        // functionName(<caret>) ==> functionName<caret>()
        if (contextElement != null && contextElement.getParent() instanceof PsiFunctionCallParams && contextElement.getLanguage() == RmlLanguage.INSTANCE) {
            PsiElement prevSibling = contextElement.getParent().getPrevSibling();
            if (prevSibling != null) {
                PsiReference reference = prevSibling.getReference();
                if (reference != null) {
                    return reference.resolve();
                }
            }
        }
        return null;
    }

    @Nullable
    private String getInferredSignature(@NotNull PsiElement element, @NotNull PsiFile psiFile, @NotNull Language language) {
        SignatureProvider.InferredTypesWithLines signaturesContext = psiFile.getUserData(SignatureProvider.SIGNATURE_CONTEXT);
        if (signaturesContext != null) {
            ORSignature elementSignature = signaturesContext.getSignatureByOffset(element.getTextOffset());
            if (elementSignature != null) {
                return elementSignature.asString(language);
            }
        }
        return null;
    }

    @NotNull
    private String createQuickDocTemplate(@NotNull String qPath, @Nullable String type, @Nullable String name, @Nullable String signature) {
        return "<html><body>" + "<div>" + qPath + "</div>" + CONTENT_START + (type == null ? "" : type) + " <b>" + name + "</b>" + CONTENT_END + (
                signature == null ? "" : "<hr/><i>" + CODE_START + signature + CODE_END + "</i>") + "</body></html>";
    }
}
