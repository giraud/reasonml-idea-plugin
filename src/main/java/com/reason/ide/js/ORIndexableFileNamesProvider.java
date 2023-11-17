package com.reason.ide.js;

import com.intellij.lang.javascript.modules.*;
import com.reason.comp.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class ORIndexableFileNamesProvider extends NodeModulesIndexableFileNamesProvider {
    private static final List<String> EXTENSIONS = new ArrayList<>(6);
    private static final List<String> FILES = new ArrayList<>(2);

    static {
        EXTENSIONS.add("ml");
        EXTENSIONS.add("mli");
        EXTENSIONS.add("re");
        EXTENSIONS.add("rei");
        EXTENSIONS.add("res");
        EXTENSIONS.add("resi");
        FILES.add(ORConstants.BS_CONFIG_FILENAME);
        FILES.add(ORConstants.RESCRIPT_CONFIG_FILENAME);
    }

    @Override
    protected @NotNull List<String> getIndexableFileNames(@NotNull DependencyKind kind) {
        return FILES;
    }

    @Override
    protected @NotNull List<String> getIndexableExtensions(@NotNull DependencyKind kind) {
        return EXTENSIONS;
    }
}
