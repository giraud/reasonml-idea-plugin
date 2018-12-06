package com.reason.lang.core.psi.impl;

import com.intellij.lang.ASTNode;
import com.reason.lang.core.psi.PsiTagPropertyValue;
import com.reason.lang.core.type.ORTypes;
import org.jetbrains.annotations.NotNull;

public class PsiTagPropertyValueImpl extends PsiToken<ORTypes> implements PsiTagPropertyValue {

    //region Constructors
    public PsiTagPropertyValueImpl(@NotNull ORTypes types, @NotNull ASTNode node) {
        super(types, node);
    }
    //endregion


    @NotNull
    @Override
    public String toString() {
        return "TagPropertyValue";
    }
}
