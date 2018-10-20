package com.reason.ide;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.messages.MessageBusConnection;
import com.reason.build.Compiler;
import com.reason.build.bs.BucklescriptManager;
import com.reason.build.console.CliType;
import com.reason.build.dune.DuneManager;
import com.reason.ide.files.FileHelper;
import com.reason.ide.hints.CodeLensView;
import com.reason.ide.hints.InferredTypesService;
import com.reason.ide.sdk.OCamlSDK;
import org.jetbrains.annotations.NotNull;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Listen to editor events and query merlin for types when editor get the focus.
 */
public class ORFileEditorListener implements FileEditorManagerListener {
//    private static final Key<ListenerInfo<PropertyChangeListener>> PROP_LISTENER = Key.create("reasonml.propinfo");

    private final Project m_project;
    private final List<VirtualFile> m_openedFiles = new ArrayList<>();

    //    static class ListenerInfo<T extends EventListener> {
//        private final T m_listener;
//
//        ListenerInfo(T documentListener) {
//            m_listener = documentListener;
//        }
//
//        T getListener() {
//            return m_listener;
//        }
//    }
    ORFileEditorListener(Project project) {
        m_project = project;
    }

    @Override
    public void fileOpened(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        FileEditor selectedEditor = source.getSelectedEditor(file);
        Document document = FileDocumentManager.getInstance().getDocument(file);
        if (selectedEditor != null && document != null) {
            final PropertyChangeListener propertyChangeListener = new ORPropertyChangeListener(file);
            final DocumentListener documentListener = new ORDocumentEventListener();

            selectedEditor.addPropertyChangeListener(propertyChangeListener);
            document.addDocumentListener(documentListener);

            MessageBusConnection messageBus = m_project.getMessageBus().connect(m_project);
            messageBus.subscribe(FileEditorManagerListener.Before.FILE_EDITOR_MANAGER, new FileEditorManagerListener.Before.Adapter() {
                @Override
                public void beforeFileClosed(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
                    selectedEditor.removePropertyChangeListener(propertyChangeListener);
                    document.removeDocumentListener(documentListener);
                }
            });
        }

        FileType fileType = file.getFileType();
        if (FileHelper.isReason(fileType) || FileHelper.isOCaml(fileType)) {
            m_openedFiles.add(file);
        }
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

        @NotNull
        private Compiler getCompiler(@NotNull Project project) {
            Sdk projectSDK = OCamlSDK.getSDK(project);
            return projectSDK == null ? BucklescriptManager.getInstance(project) : DuneManager.getInstance(project);
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if ("modified".equals(evt.getPropertyName()) && evt.getNewValue() == Boolean.FALSE/* or always, but add debounce */) {
                // Document is saved, run the compiler !!
                getCompiler(m_project).run(m_file, CliType.standard);
            }
        }
    }

    class ORDocumentEventListener implements DocumentListener {
        private int m_oldLinesCount;

        @Override
        public void beforeDocumentChange(DocumentEvent event) {
            Document document = event.getDocument();
            m_oldLinesCount = document.getLineCount();
        }

        @Override
        public void documentChanged(DocumentEvent event) {
            // When document lines count change, we clear the type annotations
            Document document = event.getDocument();
            int newLineCount = document.getLineCount();
            if (newLineCount != m_oldLinesCount) {
                CodeLensView.CodeLensInfo userData = m_project.getUserData(CodeLensView.CODE_LENS);
                if (userData != null) {
                    VirtualFile file = FileDocumentManager.getInstance().getFile(document);
                    if (file != null) {
                        int startLine = document.getLineNumber(event.getOffset());
                        int direction = newLineCount - m_oldLinesCount;
                        userData.move(file, startLine, direction, file.getTimeStamp());
                    }
                }
            }
        }
    }
}
