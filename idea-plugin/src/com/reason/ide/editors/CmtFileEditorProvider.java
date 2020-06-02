package com.reason.ide.editors;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorPolicy;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.reason.ide.editors.CmtFileEditor;
import com.reason.ide.files.CmtFileType;
import org.jetbrains.annotations.NotNull;

public class CmtFileEditorProvider implements com.intellij.openapi.fileEditor.FileEditorProvider {
    @Override
    public boolean accept(@NotNull Project project, @NotNull VirtualFile file) {
        return file.getFileType() == CmtFileType.INSTANCE;
    }

    @NotNull
    @Override
    public FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile file) {
        return new CmtFileEditor(project, file);
    }

    @NotNull
    @Override
    public String getEditorTypeId() {
        return "CMT";
    }

    @NotNull
    @Override
    public FileEditorPolicy getPolicy() {
        return FileEditorPolicy.PLACE_AFTER_DEFAULT_EDITOR;
    }
}
