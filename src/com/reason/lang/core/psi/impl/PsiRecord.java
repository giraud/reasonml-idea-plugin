package com.reason.lang.core.psi.impl;

import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.intellij.util.containers.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class PsiRecord extends ORCompositePsiElement {
    protected PsiRecord(IElementType type) {
        super(type);
    }

    public @NotNull List<PsiRecordField> getFields() {
        PsiElement[] children = getChildren();
        if (children.length == 0) {
            return ContainerUtil.emptyList();
        }

        List<PsiRecordField> result = new ArrayList<>(children.length);
        for (PsiElement child : children) {
            if (child instanceof PsiRecordField) {
                result.add((PsiRecordField) child);
            }
        }

        return result;
    }
}
