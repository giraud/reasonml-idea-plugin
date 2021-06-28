package com.reason.ide;

import com.intellij.openapi.*;
import com.intellij.openapi.components.*;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.editor.event.*;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.fileTypes.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.util.*;
import com.intellij.openapi.vfs.*;
import com.intellij.util.messages.*;
import com.reason.comp.*;
import com.reason.hints.*;
import com.reason.ide.files.*;
import com.reason.ide.hints.*;
import org.jetbrains.annotations.*;

import static com.intellij.openapi.fileEditor.FileEditorManagerListener.*;

@Service
public final class OREditorTracker implements Disposable {
    private final MessageBusConnection m_messageBusConnection;

    public OREditorTracker(@NotNull Project project) {
        m_messageBusConnection = project.getMessageBus().connect(this);

        m_messageBusConnection.subscribe(FILE_EDITOR_MANAGER, new FileEditorManagerListener() {
            // When a file is opened inside an editor, we check that rincewind is present
            // we also register document listeners to detect changes
            @Override
            public void fileOpened(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
                InsightManager insightManager = ServiceManager.getService(project, InsightManager.class);
                if (insightManager != null) {
                    insightManager.downloadRincewindIfNeeded(file);
                }

                FileType fileType = file.getFileType();
                if (FileHelper.isCompilable(fileType)) {
                    FileEditor selectedEditor = source.getSelectedEditor(file);
                    Document document = FileDocumentManager.getInstance().getDocument(file);
                    if (selectedEditor instanceof TextEditor && document != null) {
                        document.addDocumentListener(new ORDocumentEventListener((TextEditor) selectedEditor), selectedEditor);

                        CompileOnSave saveListener = new CompileOnSave(project, file);
                        selectedEditor.addPropertyChangeListener(saveListener);
                        Disposer.register(selectedEditor, () -> selectedEditor.removePropertyChangeListener(saveListener));
                    }
                }
            }

            // On tab change, we refresh inferred types
            @Override
            public void selectionChanged(@NotNull FileEditorManagerEvent event) {
                VirtualFile newFile = event.getNewFile();
                FileType fileType = newFile == null ? null : newFile.getFileType();
                if (FileHelper.isCompilable(fileType)) {
                    InferredTypesService.queryForSelectedTextEditor(event.getManager().getProject());
                    EditorFactory.getInstance().refreshAllEditors();
                }
            }
        });
    }

    @Override
    public void dispose() {
        m_messageBusConnection.disconnect();
    }

    static class ORDocumentEventListener implements DocumentListener {
        private final TextEditor m_textEditor;
        private int m_oldLinesCount;

        public ORDocumentEventListener(TextEditor textEditor) {
            m_textEditor = textEditor;
        }

        @Override
        public void beforeDocumentChange(@NotNull DocumentEvent event) {
            Document document = event.getDocument();
            m_oldLinesCount = document.getLineCount();
        }

        // When document lines count change, we need to move the type annotations for line painter
        @Override
        public void documentChanged(@NotNull DocumentEvent event) {
            Document document = event.getDocument();

            int newLineCount = document.getLineCount();
            if (newLineCount != m_oldLinesCount) {
                VirtualFile file = FileDocumentManager.getInstance().getFile(document);
                CodeLens codeLens = file == null ? null : file.getUserData(CodeLens.CODE_LENS);
                if (codeLens != null) {
                    LogicalPosition cursorPosition = m_textEditor.getEditor().offsetToLogicalPosition(event.getOffset());
                    int direction = newLineCount - m_oldLinesCount;
                    codeLens.move(cursorPosition, direction);
                }
            }
        }
    }
}
