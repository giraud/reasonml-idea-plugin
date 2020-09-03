package com.reason.ide.files;

import javax.swing.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.reason.lang.napkin.NsLanguage;
import icons.ORIcons;

public class NsFileType extends LanguageFileType {
    public static final NsFileType INSTANCE = new NsFileType();

    private NsFileType() {
        super(NsLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "Bucklescript file";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "\"Reason language file for bucklescript (NapkinScript)";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "res";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return ORIcons.NS_FILE;
    }

    @NotNull
    @Override
    public String toString() {
        return getName();
    }
}
