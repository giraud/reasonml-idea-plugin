package com.reason.ide.docs;

import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.util.Key;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.ide.files.FileBase;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.signature.ORSignature;
import com.reason.lang.ocaml.OclLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class DocumentationProvider extends AbstractDocumentationProvider {

    public static final Key<Map<LogicalPosition, ORSignature>> SIGNATURE_CONTEXT = Key.create("REASONML_SIGNATURE_CONTEXT");

    public static boolean isSpecialComment(@Nullable PsiElement element) {
        if (element == null) {
            return false;
        }

        String nextText = element.getText();
        return (nextText.startsWith("(**") || nextText.startsWith("/**")) && nextText.charAt(3) != '*';
    }

    @Override
    public String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
        if (element instanceof FileBase) {
            PsiElement child = element.getFirstChild();
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
    public String getQuickNavigateInfo(PsiElement element, @NotNull PsiElement originalElement) {
        PsiFile psiFile = originalElement.getContainingFile();
        TextEditor editor = (TextEditor) FileEditorManager.getInstance(originalElement.getProject()).getSelectedEditor(psiFile.getVirtualFile());

        if (editor != null) {
            LogicalPosition elementPosition = editor.getEditor().offsetToLogicalPosition(originalElement.getTextOffset());
            Map<LogicalPosition, ORSignature> signaturesContext = psiFile.getUserData(SIGNATURE_CONTEXT);
            if (signaturesContext != null) {
                ORSignature elementSignature = signaturesContext.get(elementPosition);
                if (elementSignature != null) {
                    return elementSignature.asString(element.getLanguage());
                }
            }
        }

        // No inferred type, look at the reference and use its signature if present
        PsiReference reference = originalElement.getReference();
        if (reference != null) {
            PsiElement resolvedElement = reference.resolve();

            if (!(resolvedElement instanceof PsiSignatureElement) && resolvedElement != null) {
                resolvedElement = resolvedElement.getParent();
            }

            if (resolvedElement instanceof PsiSignatureElement) {
                ORSignature signature = ((PsiSignatureElement) resolvedElement).getORSignature();
                if (!signature.isEmpty()) {
                    return signature.asString(element.getLanguage());
                }
            }
        }

        return super.getQuickNavigateInfo(element, originalElement);
    }
}
