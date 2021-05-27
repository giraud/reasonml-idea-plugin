package com.reason.lang.core.stub;

import com.intellij.psi.stubs.*;
import com.reason.lang.core.psi.*;

public class PsiOpenStub extends StubBase<PsiOpen> {
    private final String myOpenPath;

    public PsiOpenStub(StubElement parent, IStubElementType elementType, String openPath) {
        super(parent, elementType);
        myOpenPath = openPath;
    }

    public String getOpenPath() {
        return myOpenPath;
    }
}
