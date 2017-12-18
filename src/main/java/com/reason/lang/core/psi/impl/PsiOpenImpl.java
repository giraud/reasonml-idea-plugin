package com.reason.lang.core.psi.impl;

import java.util.*;
import javax.swing.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.search.FilenameIndex;
import com.reason.icons.Icons;
import com.reason.ide.files.RmlFileType;
import com.reason.lang.core.RmlPsiUtil;
import com.reason.lang.core.psi.PsiModuleName;
import com.reason.lang.core.psi.PsiOpen;
import com.reason.lang.core.psi.PsiStructuredElement;

import static com.reason.lang.RmlTypes.MODULE_PATH;

public class PsiOpenImpl extends ASTWrapperPsiElement implements PsiOpen, PsiStructuredElement {

    //region Constructors
    public PsiOpenImpl(@NotNull ASTNode node) {
        super(node);
    }
    //endregion

    @Nullable
    public PsiElement getNameIdentifier() {
        return findChildByType(MODULE_PATH);
    }

    @Override
    public String getName() {
        PsiElement nameIdentifier = getNameIdentifier();
        return nameIdentifier == null ? "" : nameIdentifier.getText();
    }

    @Override
    public PsiReference getReference() {
        PsiElement nameIdentifier = this.getNameIdentifier();
        if (nameIdentifier != null) {
            PsiElement firstChild = nameIdentifier.getFirstChild();
            if (firstChild instanceof PsiModuleName) {
                return new OpenReference((PsiModuleName) firstChild);
            }
        }
        return null;
    }

    @Override
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
                return Icons.OPEN;
            }
        };
    }

    @Override
    public String toString() {
        return "Open(" + getName() + ")";
    }

    private class OpenReference extends PsiReferenceBase<PsiModuleName> {
        public OpenReference(PsiModuleName element) {
            super(element, RmlPsiUtil.getTextRangeForReference(element), false);
        }

        @Nullable
        @Override
        public PsiElement resolve() {
            // Find the file corresponding to the module name
            Collection<VirtualFile> rmlFiles = FilenameIndex.getAllFilesByExt(getProject(), RmlFileType.INSTANCE.getDefaultExtension());
            for (VirtualFile file : rmlFiles) {
                String name = RmlPsiUtil.fileNameToModuleName(file.getName());
                if (name.equals(getName())) {
                    return PsiManager.getInstance(getProject()).findFile(file);
                }
            }

            return null;
        }

        @Override
        public boolean isReferenceTo(PsiElement element) {
            return super.isReferenceTo(element);  // TODO implement method override
        }

        @NotNull
        @Override
        public Object[] getVariants() {
            return new Object[0];
        }
    }
}
