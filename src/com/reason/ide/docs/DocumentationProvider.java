package com.reason.ide.docs;

import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.lang.core.psi.PsiLowerSymbol;
import com.reason.lang.core.psi.PsiSignatureElement;
import com.reason.lang.core.psi.PsiUpperSymbol;
import com.reason.lang.core.psi.PsiVal;
import com.reason.lang.core.signature.ORSignature;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class DocumentationProvider extends AbstractDocumentationProvider {

    public static final Key<Map<Integer/*Line*/, Map<String/*ident*/, Map<LogicalPosition, ORSignature>>>> SIGNATURE_CONTEXT = Key.create("REASONML_SIGNATURE_CONTEXT");

    @Override
    public String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
        if (element instanceof PsiUpperSymbol) {
            element = element.getParent();
            PsiElement previousElement = element == null ? null : PsiTreeUtil.prevVisibleLeaf(element);
            if (previousElement instanceof PsiComment) {
                return previousElement.getText();
            }
        } else if (element instanceof PsiVal) {
            PsiElement previousElement = PsiTreeUtil.prevVisibleLeaf(element);
            if (previousElement instanceof PsiComment) {
                String commentText = previousElement.getText();
                return commentText.substring(3/* (** */, commentText.length() - 2 /* *) */).trim();
            }
        }

        return super.generateDoc(element, originalElement);
    }

    @Nullable
    @Override
    public String getQuickNavigateInfo(PsiElement element, @NotNull PsiElement originalElement) {
        PsiFile psiFile = originalElement.getContainingFile();
        TextEditor editor = (TextEditor) FileEditorManager.getInstance(originalElement.getProject()).getSelectedEditor(psiFile.getVirtualFile());

        if (editor != null) {
            LogicalPosition elementPosition = editor.getEditor().offsetToLogicalPosition(originalElement.getTextOffset());
            Map<Integer, Map<String, Map<LogicalPosition, ORSignature>>> signaturesContext = psiFile.getUserData(SIGNATURE_CONTEXT);
            if (signaturesContext != null) {
                Map<String, Map<LogicalPosition, ORSignature>> lineSignatures = signaturesContext.get(elementPosition.line);
                if (lineSignatures != null) {
                    if (originalElement instanceof PsiLowerSymbol) {
                        PsiLowerSymbol lowerSymbol = (PsiLowerSymbol) originalElement;
                        Map<LogicalPosition, ORSignature> signatures = lineSignatures.get(lowerSymbol.getText());
                        if (signatures != null && signatures.size() == 1) {
                            return signatures.values().iterator().next().asString(element.getLanguage());
                        }
                    }
                }
            }
        }

        // No inferred type, look at the reference and use its signature if present
        PsiReference reference = originalElement.getReference();
        if (reference != null) {
            PsiElement resolvedElement = reference.resolve();
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
