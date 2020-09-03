package com.reason.ide.files;

import javax.swing.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.reason.lang.napkin.NsLanguage;
import icons.ORIcons;

public class NsInterfaceFileType extends LanguageFileType {
    public static final NsInterfaceFileType INSTANCE = new NsInterfaceFileType();

    private NsInterfaceFileType() {
        super(NsLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "Bucklescript interface file";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Reason language interface file for bucklescript (NapkinScript)";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "resi";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return ORIcons.NS_INTERFACE_FILE;
    }

    @NotNull
    @Override
    public String toString() {
        return getName();
    }
}
