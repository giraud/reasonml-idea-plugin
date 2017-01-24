package com.reason;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.*;

import javax.swing.*;

public class ReasonMLFileType extends LanguageFileType {
    public static final ReasonMLFileType INSTANCE = new ReasonMLFileType();

    private ReasonMLFileType() {
        super(ReasonMLLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "ReasonML file";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "ReasonML language file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "re";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return ReasonMLIcons.FILE;
    }
}
