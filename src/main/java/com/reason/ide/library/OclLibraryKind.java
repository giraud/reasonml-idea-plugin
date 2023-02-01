package com.reason.ide.library;

import com.intellij.openapi.roots.libraries.*;
import org.jetbrains.annotations.*;

public class OclLibraryKind extends PersistentLibraryKind<DummyLibraryProperties> {
    public static final OclLibraryKind INSTANCE = new OclLibraryKind();

    public OclLibraryKind() {
        super("OCaml");
    }

    @Override
    public @NotNull DummyLibraryProperties createDefaultProperties() {
        return DummyLibraryProperties.INSTANCE;
    }
}
