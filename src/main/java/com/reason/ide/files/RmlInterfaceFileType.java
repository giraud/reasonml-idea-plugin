package com.reason.ide.files;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.reason.icons.Icons;
import com.reason.lang.RmlLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class RmlInterfaceFileType extends LanguageFileType {
    public static final RmlInterfaceFileType INSTANCE = new RmlInterfaceFileType();

    private RmlInterfaceFileType() {
        super(RmlLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "Reason interface file";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Reason language interface file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "rei";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return Icons.INTERFACE_FILE;
    }}
