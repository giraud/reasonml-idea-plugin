package com.reason.lang.core.psi.impl;

import java.util.*;
import javax.swing.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.reason.icons.Icons;
import com.reason.lang.RmlTypes;
import com.reason.lang.core.psi.Module;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiScopedExpr;

public class ModuleImpl extends ASTWrapperPsiElement implements Module {

    //region Constructors
    public ModuleImpl(ASTNode node) {
        super(node);
    }

    //    public ModuleImpl(ModuleStub stub, IStubElementType nodeType) {
    //        super(stub, nodeType);
    //    }
    //endregion

    //region NamedElement
    @Override
    public String getName() {
        PsiElement nameIdentifier = getNameIdentifier();
        return nameIdentifier == null ? "" : nameIdentifier.getText();
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return findNotNullChildByType(RmlTypes.MODULE_NAME);
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        // TODO: Module setName
        return null;
    }
    //endregion

    @Nullable
    public PsiScopedExpr getModuleBody() {
        return findChildByClass(PsiScopedExpr.class);
    }

    public Collection<PsiLet> getLetExpressions() {
        return PsiTreeUtil.findChildrenOfType(this, PsiLet.class);
    }

    public ItemPresentation getPresentation() {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return getName();
            }

            @Nullable
            @Override
            public String getLocationString() {
                return null;
            }

            @Nullable
            @Override
            public Icon getIcon(boolean unused) {
                return Icons.MODULE;
            }
        };
    }

    @Override
    public String toString() {
        return "Module(" + getName() + ")";
    }
}
