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
import com.reason.ide.files.FileBase;
import com.reason.lang.core.psi.PsiLowerSymbol;
import com.reason.lang.core.psi.PsiSignatureElement;
import com.reason.lang.core.psi.PsiUpperSymbol;
import com.reason.lang.core.psi.PsiVal;
import com.reason.lang.core.signature.ORSignature;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class DocumentationProvider extends AbstractDocumentationProvider {

    public static final Key<Map<LogicalPosition, ORSignature>> SIGNATURE_CONTEXT = Key.create("REASONML_SIGNATURE_CONTEXT");

    @Override
    public String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
        if (element instanceof FileBase) {
            PsiElement child = element.getFirstChild();
            String text = "";

            PsiElement nextSibling = child;
            while (nextSibling instanceof PsiComment) {
                String nextText = nextSibling.getText();
                if (nextText.startsWith("(** ") || nextText.startsWith("/** ")) {
                    text = nextText;
                    nextSibling = null;
                } else {
                    // Not a special comment, try with next child until no more comments found
                    nextSibling = PsiTreeUtil.nextVisibleLeaf(nextSibling);
                }
            }

            if (!text.isEmpty()) {
                return DocFormatter.format(element.getContainingFile(), text);
            }
        } else if (element instanceof PsiUpperSymbol) {
            element = element.getParent();
            PsiElement previousElement = element == null ? null : PsiTreeUtil.prevVisibleLeaf(element);
            if (previousElement instanceof PsiComment) {
                return previousElement.getText();
            }
        } else if (element instanceof PsiLowerSymbol) {
            PsiElement parent = element.getParent();
            if (parent instanceof PsiVal) {
                PsiElement nextElement = PsiTreeUtil.nextVisibleLeaf(parent);
                if (nextElement instanceof PsiComment && nextElement.getText().startsWith("(**")) {
                    return DocFormatter.format(parent.getContainingFile(), nextElement.getText());
                }
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
