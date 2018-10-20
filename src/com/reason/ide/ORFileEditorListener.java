package com.reason.ide;

import java.util.*;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.reason.ide.files.FileHelper;
import com.reason.ide.hints.InferredTypesService;

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
}
