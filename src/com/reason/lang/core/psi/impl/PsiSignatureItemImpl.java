package com.reason.lang.core.psi.impl;

import org.jetbrains.annotations.NotNull;
import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.psi.PsiElement;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.PsiLanguageConverter;
import com.reason.lang.core.psi.PsiSignatureItem;
import com.reason.lang.core.type.ORTypes;

public class PsiSignatureItemImpl extends PsiToken<ORTypes> implements PsiSignatureItem {
    public PsiSignatureItemImpl(@NotNull ORTypes types, @NotNull ASTNode node) {
        super(types, node);
    }

    @Override
    public boolean isNamedItem() {
        PsiElement firstChild = getFirstChild();
        return firstChild != null && ORUtil.nextSiblingWithTokenType(firstChild, m_types.COLON) != null;
    }

    @NotNull
    @Override
    public String asText(@NotNull Language language) {
        PsiElement firstChild = getFirstChild();
        if (firstChild instanceof PsiLanguageConverter) {
            return ((PsiLanguageConverter) firstChild).asText(language);
        }
        return getText();
    }

    @NotNull
    @Override
    public String toString() {
        return "Signature item";
    }
}
