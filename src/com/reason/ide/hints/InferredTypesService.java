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
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.reason.ide.FileManager;
import com.reason.Platform;
import com.reason.hints.InsightManager;
import com.reason.ide.docs.DocumentationProvider;
import com.reason.ide.files.FileHelper;
import com.reason.ide.sdk.OCamlSDK;
import com.reason.lang.core.LogicalHMSignature;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Map;

public class InferredTypesService {

    private static final Logger LOG = Logger.getInstance("ReasonML.types.inferredService");

    private InferredTypesService() {
    }

    public static void queryForSelectedTextEditor(@NotNull Project project) {
        try {
            Editor selectedTextEditor = FileEditorManager.getInstance(project).getSelectedTextEditor();
            if (selectedTextEditor != null) {
                Document document = selectedTextEditor.getDocument();
                PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
                if (psiFile != null) {
                    VirtualFile sourceFile = psiFile.getVirtualFile();
                    if (!FileHelper.isInterface(sourceFile.getFileType())) {
                        InsightManager insightManager = project.getComponent(InsightManager.class);
                        VirtualFile baseRoot = getBasePath(project, sourceFile);
                        Path relativeBuildPath = getRelativeBuildPath(project);
                        VirtualFile cmtiPath = FileManager.fromSource(project, baseRoot, relativeBuildPath, sourceFile, insightManager.useCmt());
                        if (cmtiPath == null) {
                            LOG.warn("can't find file " + FileManager.pathFromSource(project, baseRoot, relativeBuildPath, sourceFile, insightManager.useCmt()));
                        } else {
                            insightManager.queryTypes(sourceFile, FileSystems.getDefault().getPath(cmtiPath.getPath()), types -> ApplicationManager.getApplication().runReadAction(() -> annotatePsiExpressions(project, types, sourceFile)));
                        }
                    }
                }
            }
        } catch (Error e) {
            // might produce an AssertionError when project is disposed but the invokeLater still process that code
        }
    }

    static void annotateFile(@NotNull Project project, InferredTypes types, VirtualFile sourceFile) {
        ApplicationManager.getApplication().runWriteAction(() -> annotatePsiExpressions(project, types, sourceFile));
    }

    private static void annotatePsiExpressions(@NotNull Project project, @Nullable InferredTypes types, @Nullable VirtualFile sourceFile) {
        if (types == null || sourceFile == null) {
            return;
        }

        TextEditor selectedEditor = (TextEditor) FileEditorManager.getInstance(project).getSelectedEditor(sourceFile);

        if (selectedEditor != null) {
            CodeLensView.CodeLensInfo userData = getCodeLensData(project, sourceFile);
            long timestamp = sourceFile.getTimeStamp();

            for (Map.Entry<LogicalPosition, String> openEntry : types.listOpensByLines().entrySet()) {
                userData.put(sourceFile, openEntry.getKey(), openEntry.getValue(), timestamp);
            }

            for (LogicalHMSignature signatureEntry : types.listTypesByLines()) {
                userData.put(sourceFile, signatureEntry.getLogicalPosition(), signatureEntry.getSignature().toString(), timestamp);
            }

            PsiFile psiFile = PsiManager.getInstance(project).findFile(sourceFile);
            if (psiFile != null) {
                psiFile.putUserData(DocumentationProvider.SIGNATURE_CONTEXT, types.listTypesByIdents());
            }
        }
    }

    @NotNull
    private static CodeLensView.CodeLensInfo getCodeLensData(@NotNull Project project, @Nullable VirtualFile sourceFile) {
        CodeLensView.CodeLensInfo userData = project.getUserData(CodeLensView.CODE_LENS);
        if (userData == null) {
            userData = new CodeLensView.CodeLensInfo();
            project.putUserData(CodeLensView.CODE_LENS, userData);
        } else if (sourceFile != null) {
            userData.clearInternalData(sourceFile);
        }
        return userData;
    }

    @NotNull
    private static Path getRelativeBuildPath(@NotNull Project project) {
        FileSystem fileSystem = FileSystems.getDefault();
        return OCamlSDK.getSDK(project) == null ? fileSystem.getPath("lib", "bs") : fileSystem.getPath("_build", "default");
    }

    @NotNull
    private static VirtualFile getBasePath(@NotNull Project project, @NotNull VirtualFile sourceFile) {
        return OCamlSDK.getSDK(project) == null ? Platform.findBaseRootFromFile(project, sourceFile) : Platform.findBaseRoot(project);
    }

}
