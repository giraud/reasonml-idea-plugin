package com.reason.ide;

import com.intellij.lang.Language;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.reason.ide.console.CliType;
import com.reason.hints.InsightManager;
import com.reason.ide.files.FileBase;
import com.reason.ide.files.FileHelper;
import com.reason.ide.hints.CodeLensView;
import com.reason.ide.hints.InferredTypesService;
import org.jetbrains.annotations.NotNull;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Listen to editor events and query merlin for types when editor get the focus.
 */
public class ORFileEditorListener implements FileEditorManagerListener {

    private final Project m_project;
    private final List<VirtualFile> m_openedFiles = new ArrayList<>();

    ORFileEditorListener(Project project) {
        m_project = project;
    }

    @Override
    public void fileOpened(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        ServiceManager.getService(m_project, InsightManager.class).downloadRincewindIfNeeded(file);

        FileType fileType = file.getFileType();
        if (FileHelper.isCompilable(fileType) && !DumbService.isDumb(m_project)) {
            PsiFile psiFile = PsiManager.getInstance(m_project).findFile(file);
            if (psiFile != null) {
                ApplicationManager.getApplication().invokeLater(() -> {
                    // Query types and update psi cache
                    PsiFile cmtFile = FileManager.findCmtFileFromSource(m_project, (FileBase) psiFile);
                    if (cmtFile != null) {
                        Path cmtPath = FileSystems.getDefault().getPath(cmtFile.getVirtualFile().getPath());

                        queryTypes(file, cmtPath, psiFile.getLanguage());
                    }
                });
            }
        }

        FileEditor selectedEditor = source.getSelectedEditor(file);
        Document document = FileDocumentManager.getInstance().getDocument(file);
        if (selectedEditor != null && document != null) {
            PropertyChangeListener propertyChangeListener = new ORPropertyChangeListener(file);
            selectedEditor.addPropertyChangeListener(propertyChangeListener);
            Disposer.register(selectedEditor, () -> selectedEditor.removePropertyChangeListener(propertyChangeListener));

            document.addDocumentListener(new ORDocumentEventListener(), selectedEditor);
        }

        m_openedFiles.add(file);
    }

    private void queryTypes(@NotNull VirtualFile file, @NotNull Path cmtPath, @NotNull Language language) {
        ServiceManager.
                getService(m_project, InsightManager.class).
                queryTypes(file, cmtPath, inferredTypes -> InferredTypesService.annotatePsiFile(m_project, language, file, inferredTypes));
    }

    @Override
    public void fileClosed(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        FileType fileType = file.getFileType();
        if (FileHelper.isReason(fileType) || FileHelper.isOCaml(fileType)) {
            m_openedFiles.remove(file);
        }
    }

    boolean isOpen(@NotNull VirtualFile file) {
        return m_openedFiles.contains(file);
    }

    @Override
    public void selectionChanged(@NotNull FileEditorManagerEvent event) {
        VirtualFile newFile = event.getNewFile();
        if (newFile != null) {
            FileType fileType = newFile.getFileType();
            if (FileHelper.isReason(fileType) || FileHelper.isOCaml(fileType)) {
                InferredTypesService.queryForSelectedTextEditor(m_project);
            }
        }
    }

    class ORPropertyChangeListener implements PropertyChangeListener {
        private final VirtualFile m_file;

        ORPropertyChangeListener(VirtualFile file) {
            m_file = file;
        }

        @Override
        public void propertyChange(@NotNull PropertyChangeEvent evt) {
            if ("modified".equals(evt.getPropertyName()) && evt.getNewValue() == Boolean.FALSE/* or always, but add debounce */) {
                // Document is saved, run the compiler !!
                CompilerManager.getInstance().
                        getCompiler(m_project).
                        run(m_file, CliType.standard, () -> ApplicationManager.getApplication().runReadAction(() -> {
                            InferredTypesService.clearTypes(m_project, m_file);
                            PsiFile psiFile = PsiManager.getInstance(m_project).findFile(m_file);
                            if (psiFile instanceof FileBase) {
                                ApplicationManager.getApplication().invokeLater(() -> {
                                    // Query types and update psi cache
                                    PsiFile cmtFile = FileManager.findCmtFileFromSource(m_project, (FileBase) psiFile);
                                    if (cmtFile != null) {
                                        Path cmtPath = FileSystems.getDefault().getPath(cmtFile.getVirtualFile().getPath());
                                        queryTypes(m_file, cmtPath, psiFile.getLanguage());
                                        //EditorFactory.getInstance().refreshAllEditors();
                                    }
                                });
                            }
                        }));
            }
        }
    }

    class ORDocumentEventListener implements DocumentListener {
        private int m_oldLinesCount;

        @Override
        public void beforeDocumentChange(@NotNull DocumentEvent event) {
            Document document = event.getDocument();
            m_oldLinesCount = document.getLineCount();
        }

        @Override
        public void documentChanged(@NotNull DocumentEvent event) {
            // When document lines count change, we clear the type annotations
            Document document = event.getDocument();
            int newLineCount = document.getLineCount();
            if (newLineCount != m_oldLinesCount) {
                CodeLensView.CodeLensInfo userData = m_project.getUserData(CodeLensView.CODE_LENS);
                if (userData != null) {
                    VirtualFile file = FileDocumentManager.getInstance().getFile(document);
                    if (file != null) {
                        TextEditor editor = (TextEditor) FileEditorManager.getInstance(m_project).getSelectedEditor(file);
                        if (editor != null) {
                            LogicalPosition cursorPosition = editor.getEditor().offsetToLogicalPosition(event.getOffset());
                            int direction = newLineCount - m_oldLinesCount;
                            userData.move(file, cursorPosition, direction);
                        }
                    }
                }
            }
        }
    }
}
