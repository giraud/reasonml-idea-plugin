package com.reason.ide.editors;

import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.reason.ide.files.*;
import org.jetbrains.annotations.*;

public class CmtFileEditorProvider implements com.intellij.openapi.fileEditor.FileEditorProvider {
    @Override
    public boolean accept(@NotNull Project project, @NotNull VirtualFile file) {
        return file.getFileType() == CmtFileType.INSTANCE;
    }

    @Override
    public @NotNull FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile file) {
        return new CmtFileEditor(project, file);
    }

    @Override
    public @NotNull String getEditorTypeId() {
        return "CMT";
    }

    @Override
    public @NotNull FileEditorPolicy getPolicy() {
        return FileEditorPolicy.PLACE_AFTER_DEFAULT_EDITOR;
    }
}
