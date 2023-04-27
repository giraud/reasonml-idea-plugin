package com.reason.lang.core.psi.impl;

import com.intellij.openapi.util.*;
import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.intellij.util.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

public class RPsiModuleType extends ORCompositePsiElement<ORLangTypes> implements RPsiQualifiedPathElement {
    protected RPsiModuleType(@NotNull ORLangTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }

    @Override
    public String @Nullable [] getPath() {
        // TODO: stub
        //PsiModuleStub stub = getGreenStub();
        //if (stub != null) {
        //    return stub.getPath();
        //}

        return ORUtil.getQualifiedPath(this);

    }

    @Override
    public @Nullable String getQualifiedName() {
        // TODO: stub
        //PsiModuleStub stub = getGreenStub();
        //if (stub != null) {
        //    return stub.getQualifiedName();
        //}

        return ORUtil.getQualifiedName(this);
    }

    @Override
    public PsiElement setName(@NlsSafe @NotNull String name) throws IncorrectOperationException {
        return this;
    }
}
