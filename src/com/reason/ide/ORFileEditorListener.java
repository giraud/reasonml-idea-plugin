package com.reason.ide;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.containers.WeakList;
import com.reason.Compiler;
import com.reason.hints.InsightManager;
import com.reason.hints.InsightUpdateQueue;
import com.reason.ide.console.CliType;
import com.reason.ide.files.FileHelper;
import com.reason.ide.hints.CodeLensView;
import com.reason.ide.hints.InferredTypesService;
import org.jetbrains.annotations.NotNull;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Listen to editor events and query merlin for types when editor gets the focus.
 */
public class ORFileEditorListener implements FileEditorManagerListener {

    private final Project m_project;
    private final List<VirtualFile> m_openedFiles = new ArrayList<>();
    private final WeakList<InsightUpdateQueue> m_queues = new WeakList<>();

    ORFileEditorListener(@NotNull Project project) {
        m_project = project;
    }

    public void updateQueues() {
        for (InsightUpdateQueue queue : m_queues) {
            queue.initConfig(m_project);
        }
    }

    @Override
    public void fileOpened(@NotNull FileEditorManager source, @NotNull VirtualFile sourceFile) {
        ServiceManager.getService(m_project, InsightManager.class).downloadRincewindIfNeeded(sourceFile);

        FileType fileType = sourceFile.getFileType();
        if (FileHelper.isCompilable(fileType)) {
            FileEditor selectedEditor = source.getSelectedEditor(sourceFile);
            Document document = FileDocumentManager.getInstance().getDocument(sourceFile);
            if (selectedEditor instanceof TextEditor && document != null) {
                InsightUpdateQueue insightUpdateQueue = new InsightUpdateQueue(m_project, sourceFile);
                Disposer.register(selectedEditor, insightUpdateQueue);
                document.addDocumentListener(new ORDocumentEventListener(insightUpdateQueue), selectedEditor);

                ORPropertyChangeListener propertyChangeListener = new ORPropertyChangeListener(sourceFile, document, insightUpdateQueue);
                selectedEditor.addPropertyChangeListener(propertyChangeListener);
                Disposer.register(selectedEditor, () -> {
                    selectedEditor.removePropertyChangeListener(propertyChangeListener);
                });

                // Initial query when opening the editor
                insightUpdateQueue.queue(m_project, document);

                m_queues.add(insightUpdateQueue);
            }
        }

        m_openedFiles.add(sourceFile);
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

    class ORPropertyChangeListener implements PropertyChangeListener, Disposable {
        private final VirtualFile m_file;
        private final Document m_document;
        private final InsightUpdateQueue m_updateQueue;

        ORPropertyChangeListener(@NotNull VirtualFile file, @NotNull Document document, @NotNull InsightUpdateQueue insightUpdateQueue) {
            m_file = file;
            m_document = document;
            m_updateQueue = insightUpdateQueue;
        }

        @Override
        public void dispose() {
        }

        @Override
        public void propertyChange(@NotNull PropertyChangeEvent evt) {
            if ("modified".equals(evt.getPropertyName()) && evt.getNewValue() == Boolean.FALSE) {
                // Document is saved, run the compiler !!
                Compiler compiler = ORCompilerManager.getInstance().getCompiler(m_project);
                switch (compiler.getType()) {
                    case BS:
                        compiler.run(m_file, CliType.Bs.MAKE, () -> m_updateQueue.queue(m_project, m_document));
                        break;
                    case DUNE:
                        compiler.run(m_file, CliType.Dune.BUILD, () -> m_updateQueue.queue(m_project, m_document));
                        break;
                    case ESY:
                        compiler.run(m_file, CliType.Esy.BUILD, () -> m_updateQueue.queue(m_project, m_document));
                        break;
                }


                //() -> ApplicationManager.getApplication().runReadAction(() -> {
                //InferredTypesService.clearTypes(m_project, m_file);
                //PsiFile psiFile = PsiManager.getInstance(m_project).findFile(m_file);
                //if (psiFile instanceof FileBase) {
                //    ApplicationManager.getApplication().invokeLater(() -> {
                //         Query types and update psi cache
                //PsiFile cmtFile = FileManager.findCmtFileFromSource(m_project, m_file.getNameWithoutExtension());
                //if (cmtFile != null) {
                //    Path cmtPath = FileSystems.getDefault().getPath(cmtFile.getVirtualFile().getPath());
                //    queryTypes(m_file, cmtPath, psiFile.getLanguage());
                //    EditorFactory.getInstance().refreshAllEditors();
                //}
                //});
                //}

            }
        }
    }

    class ORDocumentEventListener implements DocumentListener {
        private final InsightUpdateQueue m_queue;
        private int m_oldLinesCount;

        public ORDocumentEventListener(InsightUpdateQueue insightUpdateQueue) {
            m_queue = insightUpdateQueue;
        }

        @Override
        public void beforeDocumentChange(@NotNull DocumentEvent event) {
            Document document = event.getDocument();
            m_oldLinesCount = document.getLineCount();
        }

        @Override
        public void documentChanged(@NotNull DocumentEvent event) {
            Document document = event.getDocument();

            // When document lines count change, we move the type annotations
            int newLineCount = document.getLineCount();
            if (newLineCount != m_oldLinesCount) {
                CodeLensView.CodeLensInfo userData = m_project.getUserData(CodeLensView.CODE_LENS);
                if (userData != null) {
                    VirtualFile file = FileDocumentManager.getInstance().getFile(document);
                    if (file != null) {
                        FileEditor selectedEditor = FileEditorManager.getInstance(m_project).getSelectedEditor(file);
                        if (selectedEditor instanceof TextEditor) {
                            TextEditor editor = (TextEditor) selectedEditor;
                            LogicalPosition cursorPosition = editor.getEditor().offsetToLogicalPosition(event.getOffset());
                            int direction = newLineCount - m_oldLinesCount;
                            userData.move(file, cursorPosition, direction);
                        }
                    }
                }
            }

            m_queue.queue(m_project, document);
        }
    }
}
