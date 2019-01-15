package com.reason.ide.files;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.reason.Icons;
import com.reason.lang.reason.RmlLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    @NotNull
    @Override
    public String toString() {
        return getName();
    }
}
