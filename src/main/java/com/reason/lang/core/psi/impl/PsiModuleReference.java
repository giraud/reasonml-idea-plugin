package com.reason.lang.core.psi.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.reason.RmlFile;
import com.reason.lang.core.RmlPsiUtil;
import com.reason.lang.core.psi.PsiModule;
import com.reason.lang.core.psi.PsiModuleName;

public class PsiModuleReference extends PsiReferenceBase<PsiModuleName> {
    private final String myReferenceName;

    public PsiModuleReference(PsiModuleName element) {
        super(element, RmlPsiUtil.getTextRangeForReference(element));
        myReferenceName = element.getName();
    }

    @Override
    public PsiElement handleElementRename(String newName) throws IncorrectOperationException {
        PsiElement newNameIdentifier = RmlElementFactory.createModuleName(myElement.getProject(), newName);
        ASTNode newNameNode = newNameIdentifier.getNode();

        PsiElement nameIdentifier = myElement.getNameIdentifier();
        if (nameIdentifier == null) {
            myElement.getNode().addChild(newNameNode);
        }
        else {
            ASTNode oldNameNode = nameIdentifier.getNode();
            myElement.getNode().replaceChild(oldNameNode, newNameNode);
        }

        return myElement;
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        // From the definition of a module
        PsiModule module = PsiTreeUtil.getParentOfType(myElement, PsiModule.class);
        if (module != null && module.getNameIdentifier() == myElement) {
            return myElement;
        }

        PsiFile containingFile = myElement.getContainingFile();
        if (containingFile instanceof RmlFile) {
            module = ((RmlFile) containingFile).getModule(myReferenceName);
            if (module != null) {
                return module.getNameIdentifier();
            }
            //return ContainerUtil.getFirstItem(ErlangPsiImplUtil.getErlangMacrosFromIncludes((ErlangFile) containingFile, false, myReferenceName));
        }

        return null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return new Object[0];  // TODO implement method
    }
}
