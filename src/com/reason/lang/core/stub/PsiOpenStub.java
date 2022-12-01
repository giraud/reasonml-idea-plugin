package com.reason.lang.core.stub;

import com.intellij.psi.stubs.*;
import com.reason.lang.core.psi.*;
import org.jetbrains.annotations.*;

public class PsiOpenStub extends StubBase<RPsiOpen> {
    private final String myOpenPath;

    public PsiOpenStub(@Nullable StubElement parent, @NotNull IStubElementType elementType, @Nullable String openPath) {
        super(parent, elementType);
        myOpenPath = openPath;
    }

    public @Nullable String getOpenPath() {
        return myOpenPath;
    }
}
