package com.reason.ide.files;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.lang.Language;
import com.intellij.psi.FileViewProvider;
import com.reason.lang.core.RmlPsiUtil;
import com.reason.lang.core.psi.PsiInclude;
import com.reason.lang.core.psi.PsiModule;
import com.reason.lang.core.psi.PsiNamedElement;
import com.reason.lang.core.psi.PsiOpen;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;

public abstract class FileBase extends PsiFileBase implements PsiModuleFile {

    private final String m_moduleName;

    FileBase(@NotNull FileViewProvider viewProvider, @NotNull Language language) {
        super(viewProvider, language);
        m_moduleName = RmlPsiUtil.fileNameToModuleName(getName());
    }

    @NotNull
    @Override
    public Collection<PsiNamedElement> getExpressions() {
        PsiNamedElement[] localElements = findChildrenByClass(PsiNamedElement.class);

        PsiInclude[] includes = findChildrenByClass(PsiInclude.class);

        return Arrays.asList(localElements);
    }

    public PsiModule[] getModules() {
        return findChildrenByClass(PsiModule.class);
    }

    public PsiModule getModule(String name) {
        PsiModule[] modules = getModules();
        for (PsiModule module : modules) {
            if (name.equals(module.getName())) {
                return module;
            }
        }
        return null;
    }

    @NotNull
    public String asModuleName() {
        return m_moduleName;
    }

    public PsiOpen[] getOpenExpressions() {
        return findChildrenByClass(PsiOpen.class);
    }

    public PsiModule asModule() {
        return (PsiModule) getFirstChild();
    }
}
