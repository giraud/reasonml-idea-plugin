package com.reason.ide.files;

import com.intellij.lang.*;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.*;
import org.jetbrains.annotations.*;

public class MllFileViewProviderFactory implements FileViewProviderFactory {
    @Override
    public @NotNull FileViewProvider createFileViewProvider(@NotNull VirtualFile file, Language language, @NotNull PsiManager manager, boolean eventSystemEnabled) {
        return new MllFileViewProvider(manager, file, eventSystemEnabled);
    }
}
