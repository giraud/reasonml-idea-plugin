package com.reason.lang.core.psi.impl;

import com.intellij.psi.tree.*;
import com.intellij.psi.util.*;
import com.reason.lang.core.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

import static java.util.Collections.*;

public class RPsiFunctorCall extends ORCompositePsiElement<ORLangTypes> {
    protected RPsiFunctorCall(@NotNull ORLangTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }

    public @Nullable RPsiUpperSymbol getReferenceIdentifier() {
        return ORUtil.findImmediateLastChildOfClass(this, RPsiUpperSymbol.class);
    }

    @Override
    public int getTextOffset() {
        RPsiUpperSymbol id = getReferenceIdentifier();
        return id == null ? 0 : id.getTextOffset();
    }

    @Override
    public @NotNull String getName() {
        RPsiUpperSymbol name = getReferenceIdentifier();
        return name == null ? "" : name.getText();
    }

    public @NotNull List<RPsiParameterReference> getParameters() {
        RPsiParameters params = PsiTreeUtil.findChildOfType(this, RPsiParameters.class);
        return params == null ? emptyList() : ORUtil.findImmediateChildrenOfClass(params, RPsiParameterReference.class);
    }
}
