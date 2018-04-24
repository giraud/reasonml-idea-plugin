package com.reason.lang.core.psi.impl;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.ide.search.IndexKeys;
import com.reason.lang.core.PsiUtil;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiLowerSymbol;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class PsiVarNameReference extends PsiReferenceBase<PsiLowerSymbol> {

    private final String m_referenceName;
    private final String m_qname;

    PsiVarNameReference(PsiLowerSymbol element, String qname) {
        super(element, PsiUtil.getTextRangeForReference(element));
        m_referenceName = element.getName();
        m_qname = qname;
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        PsiElement parent = PsiTreeUtil.getParentOfType(myElement, PsiLet.class);

        // If name is used in a let definition, it's already the reference
        if (parent != null && ((PsiLet) parent).getNameIdentifier() == myElement) {
            return myElement;
        }

        // Find the name in the index
        Collection<PsiLet> elements = StubIndex.getElements(IndexKeys.LETS, m_referenceName, myElement.getProject(), GlobalSearchScope.allScope(myElement.getProject()), PsiLet.class);
        if (!elements.isEmpty()) {
            for (PsiLet let : elements) {
                if (m_qname.equals(let.getQualifiedName())) {
                    return let.getNameIdentifier();
                }
            }
        }

        return null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return EMPTY_ARRAY;
    }
}
