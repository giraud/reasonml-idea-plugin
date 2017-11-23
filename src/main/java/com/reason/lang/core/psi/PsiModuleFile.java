package com.reason.lang.core.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import com.reason.Platform;
import com.reason.icons.Icons;
import com.reason.lang.core.RmlPsiUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class PsiModuleFile extends ASTWrapperPsiElement implements PsiRmlNamedElement {

    private String m_name;

    public PsiModuleFile(ASTNode node) {
        super(node);
        ASTNode treeParent = node.getTreeParent();
        if (treeParent != null) {
            PsiElement psi = treeParent.getPsi();
            if (psi instanceof PsiFileBase) {
                PsiFileBase file = (PsiFileBase) psi;
                VirtualFile virtualFile = file.getVirtualFile();
                m_name = virtualFile == null ? file.getName() : RmlPsiUtil.fileNameToModuleName(file);
            }
        }
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return this;
    }

    public String getName() {
        return m_name;
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        m_name = name;
        return this;
    }

    public ItemPresentation getPresentation() {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return m_name;
            }

            @Override
            public String getLocationString() {
                PsiFile containingFile = getContainingFile();
                if (containingFile != null) {
                    return Platform.removeProjectDir(containingFile.getProject(), containingFile.getVirtualFile().getCanonicalPath());
                }
                return "Unknown file";
            }

            @Nullable
            @Override
            public Icon getIcon(boolean unused) {
                return Icons.RML_FILE;
            }
        };
    }

    @Override
    public String toString() {
        return "Module(File) " + m_name;
    }
}
