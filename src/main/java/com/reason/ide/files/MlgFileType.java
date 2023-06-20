package com.reason.ide.files;

import com.intellij.openapi.fileTypes.*;
import com.reason.ide.highlight.*;
import com.reason.ide.ORIcons;

import javax.swing.*;

import com.reason.lang.ocamlgrammar.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MlgFileType extends LanguageFileType {
    public static final MlgFileType INSTANCE = new MlgFileType();

    private MlgFileType() {
        super(OclGrammarLanguage.INSTANCE);
        FileTypeEditorHighlighterProviders.INSTANCE.addExplicitExtension(this, (project, fileType, virtualFile, colors) -> new OclGrammarEditorHighlighter(project, virtualFile, colors));
    }

    @Override
    public @NotNull String getName() {
        return "MLG";
    }

    @Override
    public @NotNull String getDescription() {
        return "OCaml grammar file";
    }

    @Override
    public @NotNull String getDefaultExtension() {
        return "mlg";
    }

    @Override
    public @Nullable Icon getIcon() {
        return ORIcons.OCL_GREEN_FILE;
    }
}
