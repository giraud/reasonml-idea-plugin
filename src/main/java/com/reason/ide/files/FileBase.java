package com.reason.ide.files;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.lang.Language;
import com.intellij.psi.FileViewProvider;
import com.reason.lang.core.PsiUtil;
import org.jetbrains.annotations.NotNull;

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
        return false;
    }
}
