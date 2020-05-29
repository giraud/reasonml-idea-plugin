package com.reason.ide;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.containers.WeakList;
import com.reason.Compiler;
import com.reason.ORCompilerManager;
import com.reason.hints.InsightManager;
import com.reason.hints.InsightUpdateQueue;
import com.reason.ide.files.FileHelper;
import com.reason.ide.hints.CodeLensView;
import com.reason.ide.hints.InferredTypesService;

/**
 * Listen to editor events and query merlin for types when editor gets the focus.
 */
public class ORFileEditorListener implements FileEditorManagerListener {

    private static final Key<InsightUpdateQueue> INSIGHT_QUEUE = new Key<>("reasonml.insight.queue");

    private final Project m_project;
    private final ORCompilerManager m_compilerManager;
    private final List<VirtualFile> m_openedFiles = new ArrayList<>();
    private final WeakList<InsightUpdateQueue> m_queues = new WeakList<>();

    ORFileEditorListener(@NotNull Project project) {
        m_project = project;
        m_compilerManager = ServiceManager.getService(project, ORCompilerManager.class);
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

                // Store the queue in the document, for easy access
                document.putUserData(INSIGHT_QUEUE, insightUpdateQueue);

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
                // On tab change, we redo the background compilation
                Document document = FileDocumentManager.getInstance().getDocument(newFile);
                InsightUpdateQueue insightUpdateQueue = document == null ? null : document.getUserData(INSIGHT_QUEUE);
                if (insightUpdateQueue != null) {
                    insightUpdateQueue.queue(m_project, document);
                }
                // and refresh inferred types
                InferredTypesService.queryForSelectedTextEditor(m_project);
            }
        }
    }

    class ORPropertyChangeListener implements PropertyChangeListener, Disposable {

        private final VirtualFile m_file;

        private final Document m_document;

        private final InsightUpdateQueue m_updateQueue;

        @Nullable
        private final Compiler m_compiler;

        ORPropertyChangeListener(@NotNull VirtualFile file, @NotNull Document document, @NotNull InsightUpdateQueue insightUpdateQueue) {
            m_file = file;
            m_document = document;
            m_updateQueue = insightUpdateQueue;
            m_compiler = m_compilerManager.getCompiler(m_file).orElse(null);
        }

        @Override
        public void dispose() {
        }

        @Override
        public void propertyChange(@NotNull PropertyChangeEvent evt) {
            if ("modified".equals(evt.getPropertyName()) && evt.getNewValue() == Boolean.FALSE && m_compiler != null) {
                // Document is saved, run the compiler !!
                m_compiler.runDefault(m_file, () -> m_updateQueue.queue(m_project, m_document));

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
