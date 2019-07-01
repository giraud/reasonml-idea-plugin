package com.reason.ide.hints;

import com.intellij.lang.Language;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.reason.Log;
import com.reason.hints.InsightManager;
import com.reason.ide.files.FileBase;
import com.reason.ide.files.FileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.FileSystems;
import java.nio.file.Path;

import static com.reason.ide.FileManager.findCmtFileFromSource;

public class InferredTypesService {

    private static final Log LOG = Log.create("hints.inferredTypes");

    private InferredTypesService() {
    }

    public static void clearTypes(@NotNull Project project, @NotNull VirtualFile sourceFile) {
        CodeLensView.CodeLensInfo userData = getCodeLensData(project, sourceFile);
        userData.clearInternalData(sourceFile);
    }

    public static void queryForSelectedTextEditor(@NotNull Project project) {
        try {
            Editor selectedTextEditor = FileEditorManager.getInstance(project).getSelectedTextEditor();
            if (selectedTextEditor != null) {
                Document document = selectedTextEditor.getDocument();
                PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
                if (psiFile instanceof FileBase) {
                    // Try to get the inferred types cached at the psi file user data
                    VirtualFile sourceFile = psiFile.getVirtualFile();
                    Application application = ApplicationManager.getApplication();

                    InferredTypes signatures = psiFile.getUserData(SignatureProvider.SIGNATURE_CONTEXT);
                    if (signatures == null) {
                        FileType fileType = sourceFile.getFileType();
                        if (FileHelper.isCompilable(fileType) && !FileHelper.isInterface(fileType)) {
                            InsightManager insightManager = ServiceManager.getService(project, InsightManager.class);

                            if (!DumbService.isDumb(project)) {
                                LOG.debug("Reading files from file");
                                PsiFile cmtFile = findCmtFileFromSource(project, (FileBase) psiFile);
                                if (cmtFile != null) {
                                    Path cmtPath = FileSystems.getDefault().getPath(cmtFile.getVirtualFile().getPath());
                                    insightManager.queryTypes(sourceFile, cmtPath,
                                            types -> application.runReadAction(() -> annotatePsiFile(project, psiFile.getLanguage(), types, sourceFile)));
                                }
                            }
                        }
                    } else {
                        LOG.debug("Signatures found in user data cache");
                        application.runReadAction(() -> annotatePsiFile(project, psiFile.getLanguage(), signatures, sourceFile));
                    }
                }
            }
        } catch (Error e) {
            // might produce an AssertionError when project is disposed but the invokeLater still process that code
        }
    }

    public static void annotatePsiFile(@NotNull Project project, @NotNull Language lang, @Nullable InferredTypes types, @Nullable VirtualFile sourceFile) {
        if (types == null || sourceFile == null) {
            return;
        }

        LOG.debug("Updating signatures in user data cache for file", sourceFile);

        TextEditor selectedEditor = (TextEditor) FileEditorManager.getInstance(project).getSelectedEditor(sourceFile);
        if (selectedEditor != null) {
            CodeLensView.CodeLensInfo userData = getCodeLensData(project, sourceFile);
            userData.clearInternalData(sourceFile);
            userData.putAll(sourceFile, types.signaturesByLines(lang));
        }

        PsiFile psiFile = PsiManager.getInstance(project).findFile(sourceFile);
        if (psiFile != null) {
            psiFile.putUserData(SignatureProvider.SIGNATURE_CONTEXT, types);
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
}
