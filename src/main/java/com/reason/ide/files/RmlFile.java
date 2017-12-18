package com.reason.ide.files;

import org.jetbrains.annotations.NotNull;
import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.reason.lang.RmlLanguage;
import com.reason.lang.core.psi.PsiModule;

public class RmlFile extends PsiFileBase {
    public RmlFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, RmlLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return RmlFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "Reason File";
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

    public String asModuleName() {
        return getName();
    }
}
