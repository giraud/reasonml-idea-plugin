package com.reason.ide.files;

import com.intellij.openapi.fileTypes.*;
import com.reason.ide.*;
import com.reason.lang.ocamlyacc.*;
import org.jetbrains.annotations.*;

import javax.swing.*;

public class MlyFileType extends LanguageFileType {
    public static final MlyFileType INSTANCE = new MlyFileType();

    private MlyFileType() {
        super(OclYaccLanguage.INSTANCE);
    }

    @Override
    public @NotNull String getName() {
        return "MlY";
    }

    @Override
    public @NotNull String getDescription() {
        return "OCaml yacc parser";
    }

    @Override
    public @NotNull String getDefaultExtension() {
        return "mly";
    }

    @Override
    public @Nullable Icon getIcon() {
        return ORIcons.OCL_GREEN_FILE;
    }
}
