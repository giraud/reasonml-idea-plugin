package com.reason.ide.files;

import com.intellij.json.JsonFileType;
import com.reason.Icons;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class BsConfigJsonFileType extends JsonFileType {

    public static final BsConfigJsonFileType INSTANCE = new BsConfigJsonFileType();

    public static String getDefaultFilename() {
        return "bsconfig.json";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return Icons.BS_FILE;
    }
}
