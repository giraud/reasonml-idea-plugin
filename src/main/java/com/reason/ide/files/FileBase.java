package com.reason.ide.files;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.lang.Language;
import com.intellij.psi.FileViewProvider;
import com.reason.lang.core.RmlPsiUtil;
import com.reason.lang.core.psi.PsiModule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class FileBase extends PsiFileBase {

    @NotNull
    private final String m_moduleName;

    FileBase(@NotNull FileViewProvider viewProvider, @NotNull Language language) {
        super(viewProvider, language);
        m_moduleName = RmlPsiUtil.fileNameToModuleName(getName());
    }

    @NotNull
    public String asModuleName() {
        return m_moduleName;
    }

    @Nullable
    public PsiModule asModule() {
        return findChildByClass(PsiModule.class);
    }
}
