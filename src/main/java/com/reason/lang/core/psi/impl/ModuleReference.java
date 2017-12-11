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
import com.reason.lang.core.psi.Module;
import com.reason.lang.core.psi.ModuleName;

public class ModuleReference extends PsiReferenceBase<ModuleName> {
    private final String myReferenceName;

    public ModuleReference(ModuleName element) {
        super(element, RmlPsiUtil.getTextRangeForReference(element));
        myReferenceName = element.getName();
    }

    @Override
    public PsiElement handleElementRename(String newName) throws IncorrectOperationException {
        PsiElement newNameIdentifier = RmlElementFactory.createModuleName(myElement.getProject(), newName);

        ASTNode oldNameNode = myElement.getNameElement().getNode();
        ASTNode newNameNode = newNameIdentifier.getNode();

        myElement.getNode().replaceChild(oldNameNode, newNameNode);

        return myElement;
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        // From the definition of a module
        Module module = PsiTreeUtil.getParentOfType(myElement, Module.class);
        if (module != null && module.getModuleName() == myElement) {
            return myElement;
        }

        PsiFile containingFile = myElement.getContainingFile();
        if (containingFile instanceof RmlFile) {
            module = ((RmlFile) containingFile).getModule(myReferenceName);
            if (module != null) {
                return module.getModuleName();
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
