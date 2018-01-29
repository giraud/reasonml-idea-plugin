package com.reason.lang.core.psi.impl;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.reason.icons.Icons;
import com.reason.lang.core.ModulePath;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.ModuleStub;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class PsiModuleImpl extends StubBasedPsiElementBase<ModuleStub> implements PsiModule {

    private ModulePath m_modulePath;

    //region Constructors
    public PsiModuleImpl(ASTNode node) {
        super(node);
    }

    public PsiModuleImpl(ModuleStub stub, IStubElementType nodeType) {
        super(stub, nodeType);
    }
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
        return findChildByClass(PsiModuleName.class);
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return this; // Use PsiModuleReference.handleElementRename()
    }
    //endregion

    @Nullable
    public PsiScopedExpr getBody() {
        return findChildByClass(PsiScopedExpr.class);
    }

    @Nullable
    public PsiSignature getSignature() {
        return findChildByClass(PsiSignature.class);
    }

    @Override
    public Collection<PsiLet> getLetExpressions() {
        PsiScopedExpr body = getBody();
        return body == null ? Collections.emptyList() : PsiTreeUtil.findChildrenOfType(body, PsiLet.class);
    }

    @Override
    public Collection<PsiType> getTypeExpressions() {
        PsiScopedExpr body = getBody();
        return body == null ? Collections.emptyList() : PsiTreeUtil.findChildrenOfType(body, PsiType.class);
    }

    @NotNull
    @Override
    public Collection<PsiModule> getModules() {
        PsiScopedExpr body = getBody();
        return body == null ? Collections.emptyList() : PsiTreeUtil.findChildrenOfType(body, PsiModule.class);
    }

    @Override
    public Collection<PsiNamedElement> getExpressions() {
        PsiScopedExpr body = getBody();
        return body == null ? Collections.emptyList() : PsiTreeUtil.findChildrenOfAnyType(body, PsiType.class, PsiModule.class, PsiLet.class);
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

    @NotNull
    @Override
    public ModulePath getPath() {
        if (m_modulePath == null) {
            List<PsiElement> parents = new ArrayList<>();

            PsiModule parent = PsiTreeUtil.getParentOfType(this, PsiModule.class);
            while (parent != null) {
                parents.add(parent);
                parent = PsiTreeUtil.getParentOfType(parent, PsiModule.class);
            }

            Collections.reverse(parents);
            m_modulePath = new ModulePath(parents);
        }

        return m_modulePath;
    }

    @Nullable
    @Override
    public String getQualifiedName() {
        return getPath() + "." + getName();
    }

    @Override
    public String toString() {
        return "Module(" + getName() + ")";
    }
}
