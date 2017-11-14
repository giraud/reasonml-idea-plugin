package com.reason.ide.files;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.reason.icons.Icons;
import com.reason.lang.RmlLanguage;
import org.jetbrains.annotations.*;

import javax.swing.*;

public class RmlFileType extends LanguageFileType {
    public static final RmlFileType INSTANCE = new RmlFileType();

    private RmlFileType() {
        super(RmlLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "Reason file";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Reason language file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "re";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return Icons.RML_FILE;
    }
}
