package com.reason.ide.search;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import com.reason.lang.core.psi.Type;
import com.reason.lang.core.stub.index.RmlStubIndexKeys;
import org.jetbrains.annotations.NotNull;

public class RmlAllNameIndex extends StringStubIndexExtension<Type> {
    private static final int VERSION = 0;

    @Override
    public int getVersion() {
        return super.getVersion() + VERSION;
    }

    @NotNull
    public StubIndexKey<String, Type> getKey() {
        return RmlStubIndexKeys.ALL_NAMES;
    }
}
