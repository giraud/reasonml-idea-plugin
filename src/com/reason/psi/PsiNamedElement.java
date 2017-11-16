package com.reason.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiNameIdentifierOwner;
import org.jetbrains.annotations.NotNull;

/**
 * Every element which can be renamed or referenced needs to implement this interface.
 */
abstract class PsiNamedElement extends ASTWrapperPsiElement implements PsiNameIdentifierOwner {
    PsiNamedElement(@NotNull ASTNode node) {
        super(node);
    }
}
