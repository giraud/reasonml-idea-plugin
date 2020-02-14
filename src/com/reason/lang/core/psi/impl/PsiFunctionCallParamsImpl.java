package com.reason.lang.core.psi.impl;

import java.util.*;
import org.jetbrains.annotations.NotNull;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.PsiFunctionCallParams;
import com.reason.lang.core.type.ORTypes;

public class PsiFunctionCallParamsImpl extends PsiToken<ORTypes> implements PsiFunctionCallParams {

    public PsiFunctionCallParamsImpl(@NotNull ORTypes types, @NotNull ASTNode node) {
        super(types, node);
    }

    @Override
    @NotNull
    public Collection<PsiElement> getParametersList() {
        return ORUtil.findImmediateChildrenOfType(this, m_types.C_FUN_CALL_PARAM);
    }

    @NotNull
    @Override
    public String toString() {
        return "Function call parameters";
    }
}
