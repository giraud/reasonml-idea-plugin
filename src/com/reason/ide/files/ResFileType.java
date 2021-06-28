package com.reason.ide.files;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.reason.lang.rescript.ResLanguage;
import icons.ORIcons;

import javax.swing.*;

import org.jetbrains.annotations.NotNull;

public class ResFileType extends LanguageFileType {
    public static final ResFileType INSTANCE = new ResFileType();

    private ResFileType() {
        super(ResLanguage.INSTANCE);
    }

    @Override
    public @NotNull String getName() {
        return "RESCRIPT";
    }

    @Override
    public @NotNull String getDescription() {
        return "Rescript language file";
    }

    @Override
    public @NotNull String getDefaultExtension() {
        return "res";
    }

    @Override
    public @NotNull Icon getIcon() {
        return ORIcons.NS_FILE;
    }

    @Override
    public @NotNull String toString() {
        return getName();
    }
}
