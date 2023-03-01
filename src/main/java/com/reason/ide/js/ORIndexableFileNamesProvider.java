package com.reason.ide.js;

import com.intellij.lang.javascript.modules.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class ORIndexableFileNamesProvider extends NodeModulesIndexableFileNamesProvider {
    private static final List<String> EXTENSIONS = new ArrayList<>(4);
    private static final List<String> FILES = new ArrayList<>(1);

    static {
        EXTENSIONS.add("ml");
        EXTENSIONS.add("mli");
        EXTENSIONS.add("re");
        EXTENSIONS.add("rei");
        EXTENSIONS.add("res");
        EXTENSIONS.add("resi");
        FILES.add("bsconfig.json");
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
