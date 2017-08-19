package com.reason.ide;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.reason.icons.ReasonMLIcons;
import com.reason.lang.RmlLanguage;
import org.jetbrains.annotations.*;

import javax.swing.*;

public class ReasonMLFileType extends LanguageFileType {
    public static final ReasonMLFileType INSTANCE = new ReasonMLFileType();

    private ReasonMLFileType() {
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
        return ReasonMLIcons.FILE;
    }
}
