package com.reason.ide.files;

import com.intellij.openapi.fileTypes.*;
import com.reason.ide.*;
import com.reason.ide.highlight.*;
import com.reason.lang.ocamllex.*;
import org.jetbrains.annotations.*;

import javax.swing.*;

public class MllFileType extends LanguageFileType {
    public static final MllFileType INSTANCE = new MllFileType();

    private MllFileType() {
        super(OclLexLanguage.INSTANCE);
        FileTypeEditorHighlighterProviders.getInstance().addExplicitExtension(this, (project, fileType, virtualFile, colors) -> new OclLexEditorHighlighter(project, virtualFile, colors));
    }

    @Override
    public @NotNull String getName() {
        return "MLL";
    }

    @Override
    public @NotNull String getDescription() {
        return "OCaml lexer";
    }

    @Override
    public @NotNull String getDefaultExtension() {
        return "mll";
    }

    @Override
    public @Nullable Icon getIcon() {
        return ORIcons.OCL_GREEN_FILE;
    }
}
