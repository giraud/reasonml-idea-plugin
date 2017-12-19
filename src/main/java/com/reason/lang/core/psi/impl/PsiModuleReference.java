package com.reason.lang.core.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.reason.ide.files.FileBase;
import com.reason.lang.core.RmlPsiUtil;
import com.reason.lang.core.psi.PsiModule;
import com.reason.lang.core.psi.PsiModuleName;
import com.reason.lang.core.psi.PsiOpen;
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
        PsiElement parent = PsiTreeUtil.getParentOfType(myElement, PsiModule.class, PsiOpen.class);

        // If name is used in a module definition, it's already the reference
        if (parent instanceof PsiModule && ((PsiModule) parent).getNameIdentifier() == myElement) {
            return null;
        }

        // In open expression, name resolve to a file
        if (parent instanceof PsiOpen) {
            PsiFile file = RmlPsiUtil.findFileModule(myElement.getProject(), m_referenceName);
            if (file != null) {
                return file;
            }
        }

        // Try to find the expression in current file (this is WIP)
        PsiFile containingFile = myElement.getContainingFile();
        PsiModule module = ((FileBase) containingFile).getModule(m_referenceName);
        if (module != null) {
            return module.getNameIdentifier();
        }

        return null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return new Object[0];
    }
}
