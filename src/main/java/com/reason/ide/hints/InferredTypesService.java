package com.reason.ide.hints;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.bs.Bucklescript;
import com.reason.bs.BucklescriptProjectComponent;
import com.reason.bs.hints.BsQueryTypesService;
import com.reason.bs.hints.BsQueryTypesServiceComponent;
import com.reason.lang.core.HMSignature;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiModule;
import com.reason.lang.core.psi.impl.PsiFileModuleImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class InferredTypesService {

    public static void queryForSelectedTextEditor(Project project) {
        try {
            Editor selectedTextEditor = FileEditorManager.getInstance(project).getSelectedTextEditor();
            if (selectedTextEditor != null) {
                Document document = selectedTextEditor.getDocument();
                PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
                if (psiFile != null) {
                    VirtualFile sourceFile = psiFile.getVirtualFile();
                    VirtualFile cmiPath = CmiFileManager.fromSource(project, sourceFile);
                    if (cmiPath == null) {
                        Logger.getInstance("ReasonML.types").warn("can't find cmi file " + CmiFileManager.pathFromSource(project, sourceFile));
                    } else {
                        Bucklescript bucklescript = BucklescriptProjectComponent.getInstance(project);
                        BsQueryTypesServiceComponent.InferredTypes types = bucklescript.queryTypes(cmiPath);
                        ApplicationManager.getApplication().runReadAction(() -> annotatePsiExpressions(project, types, sourceFile));
                    }
                }
            }
        } catch (Error e) {
            // might produce an AssertionError when project is disposed but the invokeLater still process that code
        }
    }

    public static void annotateFile(Project project, BsQueryTypesServiceComponent.InferredTypes types, VirtualFile sourceFile) {
        ApplicationManager.getApplication().runWriteAction(() -> {
            annotatePsiExpressions(project, types, sourceFile);
        });
    }

    private static void annotatePsiExpressions(@NotNull Project project, @Nullable BsQueryTypesService.InferredTypes types, @Nullable VirtualFile sourceFile) {
        if (types == null || sourceFile == null) {
            return;
        }

        TextEditor selectedEditor = (TextEditor) FileEditorManager.getInstance(project).getSelectedEditor(sourceFile);

        if (selectedEditor != null) {
            CodeLensView.CodeLensInfo userData = getCodeLensData(project, sourceFile);
            long timestamp = sourceFile.getTimeStamp();
            PsiFile psiFile = PsiManager.getInstance(project).findFile(sourceFile);

            Collection<PsiLet> letExpressions = PsiTreeUtil.findChildrenOfType(psiFile, PsiLet.class);
            for (PsiLet letStatement : letExpressions) {
                PsiElement letParent = letStatement.getParent();
                if (letParent instanceof PsiFileModuleImpl) {
                    HMSignature signature = applyType(types, letStatement);
                    if (signature != null) {
                        int letOffset = letStatement.getTextOffset();
                        LogicalPosition logicalPosition = selectedEditor.getEditor().offsetToLogicalPosition(letOffset);
                        userData.put(sourceFile, logicalPosition, signature.toString(), timestamp);
                    }
                } else {
                    PsiModule letModule = PsiTreeUtil.getParentOfType(letStatement, PsiModule.class);
                    if (letModule != null) {
                        BsQueryTypesServiceComponent.InferredTypes inferredModuleTypes = types.getModuleType(letModule.getName());
                        if (inferredModuleTypes != null) {
                            HMSignature signature = applyType(inferredModuleTypes, letStatement);
                            if (signature != null) {
                                int letOffset = letStatement.getTextOffset();
                                LogicalPosition logicalPosition = selectedEditor.getEditor().offsetToLogicalPosition(letOffset);
                                userData.put(sourceFile, logicalPosition, signature.toString(), timestamp);
                            }
                        }
                    }
                }
            }
        }
    }

    @NotNull
    private static CodeLensView.CodeLensInfo getCodeLensData(@NotNull Project project, @Nullable VirtualFile sourceFile) {
        CodeLensView.CodeLensInfo userData = project.getUserData(CodeLensView.CODE_LENS);
        if (userData == null) {
            userData = new CodeLensView.CodeLensInfo();
            project.putUserData(CodeLensView.CODE_LENS, userData);
        } else {
            userData.clearInternalData(sourceFile);
        }
        return userData;
    }

    private static HMSignature applyType(@Nullable BsQueryTypesService.InferredTypes inferredTypes, PsiLet letStatement) {
        HMSignature signature = null;

        PsiElement letName = letStatement.getNameIdentifier();
        if (letName != null && inferredTypes != null) {
            signature = inferredTypes.getLetType(letName.getText());
            if (signature != null) {
                letStatement.setInferredType(signature);
            }
        }

        return signature;
    }

}
