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
import com.reason.lang.core.HMSignature;
import com.reason.lang.core.psi.PsiHMSignature;
import com.reason.lang.core.psi.PsiLowerSymbol;
import com.reason.lang.core.psi.PsiUpperSymbol;
import com.reason.lang.core.psi.PsiVal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class DocumentationProvider extends AbstractDocumentationProvider {

    public static final Key<Map<Integer/*Line*/, Map<String/*ident*/, Map<LogicalPosition, HMSignature>>>> SIGNATURE_CONTEXT = Key.create("REASONML_SIGNATURE_CONTEXT");

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
    public String getQuickNavigateInfo(PsiElement element, PsiElement originalElement) {
        PsiFile psiFile = originalElement.getContainingFile();
        TextEditor editor = (TextEditor) FileEditorManager.getInstance(originalElement.getProject()).getSelectedEditor(psiFile.getVirtualFile());

        if (editor != null) {
            LogicalPosition elementPosition = editor.getEditor().offsetToLogicalPosition(originalElement.getTextOffset());
            Map<Integer, Map<String, Map<LogicalPosition, HMSignature>>> signaturesContext = psiFile.getUserData(SIGNATURE_CONTEXT);
            if (signaturesContext != null) {
                Map<String, Map<LogicalPosition, HMSignature>> lineSignatures = signaturesContext.get(elementPosition.line);
                if (lineSignatures != null) {
                    if (originalElement instanceof PsiLowerSymbol) {
                        PsiLowerSymbol lowerSymbol = (PsiLowerSymbol) originalElement;
                        Map<LogicalPosition, HMSignature> signatures = lineSignatures.get(lowerSymbol.getText());
                        if (signatures != null && signatures.size() == 1) {
                            return limitSignature(signatures.values().iterator().next());
                        }
                    }
                }
            }
        }

        // No inferred type, look at the reference and use its signature if present
        PsiReference reference = originalElement.getReference();
        if (reference != null) {
            PsiElement resolvedElement = reference.resolve();
            if (resolvedElement instanceof PsiHMSignature) {
                return limitSignature(((PsiHMSignature) resolvedElement).getSignature());
            }
        }

        return super.getQuickNavigateInfo(element, originalElement);
    }

    @NotNull
    private String limitSignature(HMSignature signature) {
        String signatureString = signature.toString();
        if (signatureString.length() > 1000) {
            return signatureString.substring(0, 1000) + "...";
        }
        return signatureString;
    }
}
