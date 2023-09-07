package com.reason.lang.core.psi.impl;

import com.intellij.lang.*;
import com.intellij.navigation.*;
import com.intellij.psi.*;
import com.intellij.psi.stubs.*;
import com.intellij.util.*;
import com.reason.ide.*;
import com.reason.ide.files.*;
import com.reason.lang.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

import javax.swing.*;
import java.util.*;

public class RPsiInnerModuleImpl extends RPsiTokenStub<ORLangTypes, RPsiModule, PsiModuleStub> implements RPsiInnerModule, PsiNameIdentifierOwner {
    // region Constructors
    public RPsiInnerModuleImpl(@NotNull ORLangTypes types, @NotNull ASTNode node) {
        super(types, node);
    }

    public RPsiInnerModuleImpl(@NotNull ORLangTypes types, @NotNull PsiModuleStub stub, @NotNull IStubElementType nodeType) {
        super(types, stub, nodeType);
    }
    // endregion

    // region NamedElement
    public @Nullable PsiElement getNameIdentifier() {
        return findChildByClass(RPsiUpperSymbol.class);
    }

    @Override
    public int getTextOffset() {
        PsiElement id = getNameIdentifier();
        return id == null ? 0 : id.getTextOffset();
    }

    @Override
    public @Nullable String getName() {
        PsiModuleStub stub = getGreenStub();
        if (stub != null) {
            return stub.getName();
        }

        PsiElement nameIdentifier = getNameIdentifier();
        return nameIdentifier == null ? null : nameIdentifier.getText();
    }

    @Override
    public @NotNull PsiElement setName(@NotNull String newName) throws IncorrectOperationException {
        return this;
    }
    // endregion

    //region PsiQualifiedPath
    @Override
    public @Nullable String[] getPath() {
        PsiModuleStub stub = getGreenStub();
        if (stub != null) {
            return stub.getPath();
        }

        return ORUtil.getQualifiedPath(this);
    }

    @Override
    public @NotNull String getQualifiedName() {
        PsiModuleStub stub = getGreenStub();
        if (stub != null) {
            return stub.getQualifiedName();
        }

        return ORUtil.getQualifiedName(this);
    }
    //endregion

    @Override
    public @NotNull String getModuleName() {
        String name = getName();
        return name == null ? "" : name;
    }

    @Override
    public boolean isInterfaceFile() {
        return ((FileBase) getContainingFile()).isInterface();
    }

    @Override
    public @Nullable RPsiFunctorCall getFunctorCall() {
        return ORUtil.findImmediateFirstChildOfClass(getBody(), RPsiFunctorCall.class);
    }

    @Override
    public @Nullable PsiElement getBody() {
        return ORUtil.findImmediateFirstChildOfAnyClass(this, RPsiModuleBinding.class, RPsiSignature.class);
    }

    @Override
    public @NotNull List<RPsiTypeConstraint> getConstraints() {
        RPsiConstraints constraints = ORUtil.findImmediateFirstChildOfClass(this, RPsiConstraints.class);
        return ORUtil.findImmediateChildrenOfClass(constraints, RPsiTypeConstraint.class);
    }

    @Override
    public @Nullable RPsiModuleSignature getModuleSignature() {
        PsiElement child = ORUtil.findImmediateFirstChildOfAnyClass(this, RPsiModuleSignature.class, RPsiScopedExpr.class);
        if (child instanceof RPsiScopedExpr) {
            child = ORUtil.findImmediateFirstChildOfClass(child, RPsiModuleSignature.class);
        }
        return child instanceof RPsiModuleSignature ? (RPsiModuleSignature) child : null;
    }


    @Override
    public boolean isModuleType() {
        PsiModuleStub stub = getGreenStub();
        if (stub != null) {
            return stub.isModuleType();
        }

        PsiElement psiElement = ORUtil.nextSibling(getFirstChild());
        return psiElement != null && psiElement.getNode().getElementType() == myTypes.TYPE;
    }

    @Override
    public boolean isComponent() {
        PsiModuleStub stub = getGreenStub();
        if (stub != null) {
            return stub.isComponent();
        }

        return ModuleHelper.isComponent(getBody());
    }

    @Override
    public @Nullable PsiElement getMakeFunction() {
        PsiElement make = ORUtil.findImmediateNamedChildOfClass(getBody(), RPsiLet.class, "make");
        if (make == null) {
            make = ORUtil.findImmediateNamedChildOfClass(getBody(), RPsiExternal.class, "make");
        }
        return make;
    }

    @Override
    public boolean isFunctorCall() {
        PsiModuleStub stub = getGreenStub();
        if (stub != null) {
            return stub.isFunctorCall();
        }

        return ORUtil.findImmediateFirstChildOfType(getBody(), myTypes.C_FUNCTOR_CALL) != null;
    }

    public @Nullable RPsiUpperSymbol getAliasSymbol() {
        RPsiModuleBinding binding = ORUtil.findImmediateFirstChildOfClass(this, RPsiModuleBinding.class);
        return binding == null ? null : ORUtil.findImmediateLastChildOfClass(binding, RPsiUpperSymbol.class);
    }

    @Override
    public @Nullable String getAlias() {
        PsiModuleStub stub = getGreenStub();
        if (stub != null) {
            return stub.getAlias();
        }

        RPsiModuleBinding binding = ORUtil.findImmediateFirstChildOfClass(this, RPsiModuleBinding.class);
        if (binding != null) {
            return ORUtil.computeAlias(binding.getFirstChild(), getLanguage(), false);
        }

        return null;
    }

    public ItemPresentation getPresentation() {
        //RPsiModuleSignature moduleSignature = getModuleSignature();

        return new ItemPresentation() {
            @Override
            public @Nullable String getPresentableText() {
                return getName();
            }

            @Override
            public @NotNull String getLocationString() {
                //return moduleSignature != null ? moduleSignature.getQualifiedName() : getQualifiedName();
                return getQualifiedName();
            }

            @Override
            public @NotNull Icon getIcon(boolean unused) {
                return isModuleType()
                        ? ORIcons.MODULE_TYPE
                        : (isInterfaceFile() ? ORIcons.INNER_MODULE_INTF : ORIcons.INNER_MODULE);
            }
        };
    }

    @Override
    public String toString() {
        return super.toString() + ":" + getModuleName();
    }
}
