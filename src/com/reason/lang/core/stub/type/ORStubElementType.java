package com.reason.lang.core.stub.type;

import com.intellij.lang.*;
import com.intellij.psi.*;
import com.intellij.psi.stubs.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

public abstract class ORStubElementType<StubT extends StubElement<?>, PsiT extends PsiElement> extends IStubElementType<StubT, PsiT> implements ORCompositeType {
    static final String[] EMPTY_PATH = new String[0];

    ORStubElementType(@NotNull String debugName, @Nullable Language language) {
        super(debugName, language);
    }

    public abstract @NotNull PsiElement createPsi(ASTNode node);
}
