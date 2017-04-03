package com.reason.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.reason.psi.ReasonMLInferredType;
import org.jetbrains.annotations.NotNull;

public class ReasonMLInferredTypeMixin extends ASTWrapperPsiElement implements ReasonMLInferredType {
    private String inferredType = "";

    public ReasonMLInferredTypeMixin(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public void setInferredType(String inferredType) {
        this.inferredType = inferredType;
    }

    @Override
    public String getInferredType() {
        return inferredType;
    }

    @Override
    public boolean hasInferredType() {
        return inferredType != null && !inferredType.isEmpty();
    }
}
