package com.reason.ide.files;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.reason.lang.rescript.ResLanguage;
import com.reason.ide.ORIcons;

import javax.swing.*;

import org.jetbrains.annotations.*;

public class ResInterfaceFileType extends LanguageFileType {
    public static final ResInterfaceFileType INSTANCE = new ResInterfaceFileType();

    private ResInterfaceFileType() {
        super(ResLanguage.INSTANCE);
    }

    @Override
    public @NotNull String getName() {
        return "RESCRIPT_INTF";
    }

    @Override
    public @NotNull @Nls String getDisplayName() {
        return "Rescript interface";
    }

    @Override
    public @NotNull String getDescription() {
        return "Rescript language interface file";
    }

    @Override
    public @NotNull String getDefaultExtension() {
        return "resi";
    }

    @Override
    public @NotNull Icon getIcon() {
        return ORIcons.RES_INTERFACE_FILE;
    }

    @Override
    public @NotNull String toString() {
        return getName();
    }
}
