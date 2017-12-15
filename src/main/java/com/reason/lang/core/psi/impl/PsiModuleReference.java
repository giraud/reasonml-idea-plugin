package com.reason.lang.core.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.reason.ide.files.RmlFile;
import com.reason.lang.core.RmlPsiUtil;
import com.reason.lang.core.psi.PsiModule;
import com.reason.lang.core.psi.PsiModuleName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PsiModuleReference extends PsiReferenceBase<PsiModuleName> {

    private final String m_referenceName;

    PsiModuleReference(PsiModuleName element) {
        super(element, RmlPsiUtil.getTextRangeForReference(element));
        m_referenceName = element.getName();
    }

    @Override
    public PsiElement handleElementRename(String newName) throws IncorrectOperationException {
        PsiElement newNameIdentifier = RmlElementFactory.createModuleName(myElement.getProject(), newName);
        ASTNode newNameNode = newNameIdentifier.getFirstChild().getNode();

        PsiElement nameIdentifier = myElement.getNameIdentifier();
        if (nameIdentifier == null) {
            myElement.getNode().addChild(newNameNode);
        } else {
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
            module = ((RmlFile) containingFile).getModule(m_referenceName);
            if (module != null) {
                return module.getNameIdentifier();
            }
        }

        return null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return new Object[0];
    }
}
