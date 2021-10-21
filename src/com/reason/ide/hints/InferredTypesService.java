package com.reason.ide.hints;

import com.intellij.lang.*;
import com.intellij.openapi.application.*;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.fileTypes.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.*;
import com.reason.hints.*;
import com.reason.ide.files.*;
import com.reason.lang.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.nio.file.*;

import static com.reason.ide.ORFileManager.*;
import static com.reason.ide.hints.CodeLens.*;

public class InferredTypesService {

    private static final Log LOG = Log.create("hints.inferredTypes");

    private InferredTypesService() {
    }

    public static void queryForSelectedTextEditor(@NotNull Project project) {
        try {
            Editor selectedTextEditor = FileEditorManager.getInstance(project).getSelectedTextEditor();
            if (selectedTextEditor != null) {
                Document document = selectedTextEditor.getDocument();
                PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
                if (psiFile instanceof FileBase && !FileHelper.isInterface(psiFile.getFileType())) {
                    // Try to get the inferred types cached at the psi file user data
                    VirtualFile sourceFile = psiFile.getVirtualFile();
                    Application application = ApplicationManager.getApplication();

                    SignatureProvider.InferredTypesWithLines sigContext = psiFile.getUserData(SignatureProvider.SIGNATURES_CONTEXT);
                    InferredTypes signatures = sigContext == null ? null : sigContext.getTypes();
                    ORLanguageProperties languageProperties = ORLanguageProperties.cast(psiFile.getLanguage());
                    if (signatures == null) {
                        FileType fileType = sourceFile.getFileType();
                        if (FileHelper.isCompilable(fileType)) {
                            InsightManager insightManager = project.getService(InsightManager.class);

                            if (!DumbService.isDumb(project)) {
                                LOG.debug("Reading types from file", psiFile);
                                PsiFile cmtFile = findCmtFileFromSource(project, sourceFile.getNameWithoutExtension());
                                if (cmtFile != null) {
                                    Path cmtPath = FileSystems.getDefault().getPath(cmtFile.getVirtualFile().getPath());
                                    insightManager.queryTypes(sourceFile, cmtPath,
                                            types -> application.runReadAction(() -> annotatePsiFile(project, languageProperties, sourceFile, types)));
                                }
                            }
                        }
                    } else {
                        LOG.debug("Signatures found in user data cache");
                        application.runReadAction(() -> annotatePsiFile(project, languageProperties, sourceFile, signatures));
                    }
                }
            }
        } catch (Error e) {
            // might produce an AssertionError when project is being disposed, but the invokeLater still
            // process that code
        }
    }

    public static void annotatePsiFile(@NotNull Project project, @Nullable ORLanguageProperties lang, @Nullable VirtualFile sourceFile, @Nullable InferredTypes types) {
        if (types == null || sourceFile == null) {
            return;
        }

        if (FileHelper.isInterface(sourceFile.getFileType())) {
            return;
        }

        LOG.debug("Updating signatures in user data cache for file", sourceFile);
        getSignatures(sourceFile).putAll(types.signaturesByLines(lang));

        PsiFile psiFile = PsiManager.getInstance(project).findFile(sourceFile);
        if (psiFile != null && !FileHelper.isInterface(psiFile.getFileType())) {
            String[] lines = psiFile.getText().split("\n");
            psiFile.putUserData(SignatureProvider.SIGNATURES_CONTEXT, new SignatureProvider.InferredTypesWithLines(types, lines));
        }
    }

    public static @NotNull CodeLens getSignatures(@NotNull VirtualFile file) {
        CodeLens userData = file.getUserData(CODE_LENS);
        if (userData == null) {
            userData = new CodeLens();
            file.putUserData(CODE_LENS, userData);
        }
        return userData;
    }
}
