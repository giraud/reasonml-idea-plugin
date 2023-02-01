package com.reason.ide.library;

import com.intellij.openapi.project.*;
import com.intellij.openapi.roots.libraries.*;
import com.intellij.openapi.roots.libraries.ui.*;
import com.intellij.openapi.vfs.*;
import com.reason.ide.*;
import org.jetbrains.annotations.*;

import javax.swing.*;

// DownloadableLibraryType ?? and use code from SDK download
public class OclLibraryType extends LibraryType<DummyLibraryProperties> {
    public OclLibraryType() {
        super(OclLibraryKind.INSTANCE);
    }

    @Override
    public @NotNull String getCreateActionName() {
        return "OCaml";
    }

    @Override
    public @NotNull Icon getIcon(@Nullable DummyLibraryProperties properties) {
        return ORIcons.OCL_SDK;
    }

    @Override
    public @Nullable NewLibraryConfiguration createNewLibrary(@NotNull JComponent parentComponent, @Nullable VirtualFile contextDirectory, @NotNull Project project) {
        LibraryTypeService libTypeService = LibraryTypeService.getInstance();
        return libTypeService.createLibraryFromFiles(createLibraryRootsComponentDescriptor(), parentComponent, contextDirectory, this, project);
    }

    @Override
    public @NotNull LibraryRootsComponentDescriptor createLibraryRootsComponentDescriptor() {
        return new OclLibraryRootsComponentDescriptor();
    }

    @Override
    public @Nullable LibraryPropertiesEditor createPropertiesEditor(@NotNull LibraryEditorComponent<DummyLibraryProperties> editorComponent) {
        return null;
    }
}
