package com.reason.ide.files.viewer;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.SingleRootFileViewProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CmtFileViewProvider extends SingleRootFileViewProvider {
    public CmtFileViewProvider(@NotNull PsiManager manager, @NotNull VirtualFile file) {
        super(manager, file);
    }

    @Nullable
    @Override
    protected PsiFile createFile(@NotNull Project project, @NotNull VirtualFile file, @NotNull FileType fileType) {
        return new CmtTextFileImpl(this);
    }
}
