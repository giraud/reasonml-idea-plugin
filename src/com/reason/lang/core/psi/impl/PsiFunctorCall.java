package com.reason.lang.core.psi.impl;

import com.intellij.psi.tree.*;
import com.intellij.psi.util.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

import java.util.*;

import static java.util.Collections.emptyList;

public class PsiFunctorCall extends CompositeTypePsiElement<ORTypes> {
    protected PsiFunctorCall(@NotNull ORTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }

    @NotNull
    public String getFunctorName() {
        String text = getText();

        PsiParameters params = PsiTreeUtil.findChildOfType(this, PsiParameters.class);
        if (params == null) {
            return text;
        }

        return text.substring(0, params.getTextOffset() - getTextOffset());
    }

    public @NotNull Collection<PsiParameter> getParameters() {
      PsiParameters params = PsiTreeUtil.findChildOfType(this, PsiParameters.class);
      return params == null ? emptyList() : params.getParametersList();
    }

    @Override
    public @NotNull String toString() {
        return "Functor call";
    }
}
