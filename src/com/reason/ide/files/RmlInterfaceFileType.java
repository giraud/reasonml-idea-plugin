package com.reason.ide.files;

import com.intellij.openapi.fileTypes.*;
import com.reason.lang.reason.*;
import icons.*;
import org.jetbrains.annotations.*;

import javax.swing.*;

public class RmlInterfaceFileType extends LanguageFileType {
    public static final RmlInterfaceFileType INSTANCE = new RmlInterfaceFileType();

    private RmlInterfaceFileType() {
        super(RmlLanguage.INSTANCE);
    }

    @Override
    public @NotNull String getName() {
        return "REASON_INTF";
    }

    @Override
    public @NotNull @Nls String getDisplayName() {
        return "Reason interface";
    }

    @Override
    public @NotNull String getDescription() {
        return "Reason language interface file";
    }

    @Override
    public @NotNull String getDefaultExtension() {
        return "rei";
    }

    @Override
    public @Nullable Icon getIcon() {
        return ORIcons.RML_INTERFACE_FILE;
    }

    @Override
    public @NotNull String toString() {
        return getName();
    }
}
