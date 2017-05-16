package com.reason.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.reason.ide.ReasonMLFileType;
import com.reason.lang.ReasonMLLanguage;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class ReasonMLFile extends PsiFileBase {
    public ReasonMLFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, ReasonMLLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return ReasonMLFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "Reason File";
    }

    @Override
    public Icon getIcon(int flags) {
        return super.getIcon(flags);
    }
}
