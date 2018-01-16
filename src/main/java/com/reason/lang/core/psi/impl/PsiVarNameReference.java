package com.reason.lang.core.psi.impl;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.lang.core.RmlPsiUtil;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiVarName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PsiVarNameReference extends PsiReferenceBase<PsiVarName> {

    private final String m_referenceName;

    public PsiVarNameReference(PsiVarName element) {
        super(element, RmlPsiUtil.getTextRangeForReference(element));
        m_referenceName = element.getName();
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        PsiElement parent = PsiTreeUtil.getParentOfType(myElement, PsiLet.class);

        // If name is used in a let definition, it's already the reference
        if (parent instanceof PsiLet && ((PsiLet) parent).getNameIdentifier() == myElement) {
            return null;
        }

        // Find the name in the index


        return null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return EMPTY_ARRAY;
    }
}
