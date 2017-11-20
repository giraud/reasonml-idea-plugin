package com.reason;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.reason.lang.OclLanguage;
import com.reason.ide.files.OclFileType;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class OclFile extends PsiFileBase {
    public OclFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, OclLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return OclFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "Ocaml File";
    }

    @Override
    public Icon getIcon(int flags) {
        return super.getIcon(flags);
    }
}
