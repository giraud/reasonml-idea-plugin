package com.reason.ide.files;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.lang.Language;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.lang.core.PsiUtil;
import com.reason.lang.core.psi.PsiLet;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class FileBase extends PsiFileBase {

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
        //PsiModuleStub stub = getGreenStub();
        //if (stub != null) {
        //    return stub.isComponent();
        //}

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
}
