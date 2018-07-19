package com.reason.ide.files;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiQualifiedNamedElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.Platform;
import com.reason.lang.core.PsiFileHelper;
import com.reason.lang.core.PsiUtil;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiNamedElement;
import com.reason.lang.core.psi.PsiType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Collection;
import java.util.List;

public abstract class FileBase extends PsiFileBase implements PsiQualifiedNamedElement {

    @NotNull
    private final String m_moduleName;

    FileBase(@NotNull FileViewProvider viewProvider, @NotNull Language language) {
        super(viewProvider, language);
        m_moduleName = PsiUtil.fileNameToModuleName(getName());
    }

    @NotNull
    public String asModuleName() {
        return m_moduleName;
    }

    public boolean isComponent() {
        FileType fileType = getFileType();
        if (fileType instanceof OclFileType || fileType instanceof OclInterfaceFileType) {
            return false;
        }

        // naive detection

        List<PsiLet> expressions = PsiTreeUtil.getStubChildrenOfTypeAsList(this, PsiLet.class);
        PsiLet componentDef = null;
        PsiLet makeDef = null;
        for (PsiLet let : expressions) {
            if (componentDef == null && "component".equals(let.getName())) {
                componentDef = let;
            } else if (makeDef == null && "make".equals(let.getName())) {
                makeDef = let;
            } else if (componentDef != null && makeDef != null) {
                break;
            }
        }

        return componentDef != null && makeDef != null;
    }

    public String shortLocation(@NotNull Project project) {
        return Platform.removeProjectDir(project, getVirtualFile()).replace("node_modules" + File.separator, "").replace(getName(), "");
    }

    @NotNull
    public Collection<PsiNamedElement> getExpressions() {
        return PsiFileHelper.getExpressions(this);
    }

    @Nullable
    public PsiNamedElement getTypeExpression(String name) {
        List<PsiType> expressions = PsiFileHelper.getTypeExpressions(this);
        if (!expressions.isEmpty()) {
            for (PsiType expression : expressions) {
                if (name.equals(expression.getName())) {
                    return expression;
                }
            }
        }
        return null;
    }

    @Nullable
    @Override
    public String getQualifiedName() {
        return asModuleName();
    }
}
