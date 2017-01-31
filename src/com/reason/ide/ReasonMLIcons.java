package com.reason.ide;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

import static com.intellij.openapi.util.IconLoader.getIcon;

public class ReasonMLIcons {
    public static final Icon FILE = IconLoader.getIcon("/com/reason/icons/reason-file.png");
    // From jetbrains ide
    public static final Icon MODULE = getIcon("/nodes/method.png");
    public static final Icon LET = getIcon("/nodes/field.png");
    public static final Icon TYPE = getIcon("/nodes/annotationtype.png");
    public static final Icon FUNCTION = getIcon("/nodes/function.png");
}
