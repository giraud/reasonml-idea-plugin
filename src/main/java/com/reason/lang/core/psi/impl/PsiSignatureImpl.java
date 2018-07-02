package com.reason.lang.core.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiFile;
import com.reason.ide.files.OclFile;
import com.reason.ide.files.OclInterfaceFile;
import com.reason.lang.core.HMSignature;
import com.reason.lang.core.psi.PsiSignature;
import org.jetbrains.annotations.NotNull;

public class PsiSignatureImpl extends ASTWrapperPsiElement implements PsiSignature {

    public PsiSignatureImpl(ASTNode node) {
        super(node);
    }

    @NotNull
    @Override
    public HMSignature asHMSignature() {
        PsiFile containingFile = getContainingFile();
        boolean isOcaml = containingFile instanceof OclFile || containingFile instanceof OclInterfaceFile;
        return new HMSignature(isOcaml, getText());
    }

    @Override
    public String toString() {
        return "Signature";
    }
}
