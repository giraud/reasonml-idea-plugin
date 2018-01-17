package com.reason.lang.core.psi.impl;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.ide.search.IndexKeys;
import com.reason.lang.core.RmlPsiUtil;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiVarName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

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
            return myElement;
        }

        // Find the name in the index
        Collection<PsiLet> elements = StubIndex.getElements(IndexKeys.LETS, m_referenceName, myElement.getProject(), GlobalSearchScope.allScope(myElement.getProject()), PsiLet.class);
        if (!elements.isEmpty()) {
            // TODO: only let with correct QN
            PsiLet let = elements.iterator().next();
            return let.getNameIdentifier();
        }

        return null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return EMPTY_ARRAY;
    }
}
