package com.reason.bs;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.reason.bs.annotations.BsErrorsManager;
import com.reason.bs.hints.BsQueryTypesService;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface Bucklescript {
    boolean isDependency(String path);

    void refresh();

    void run(FileType fileType);

    @Nullable
    BsCompiler getCompiler();

    @Nullable
    BsQueryTypesService.InferredTypes queryTypes(VirtualFile file);

    @Nullable
    Collection<BsErrorsManager.BsbError> getErrors(String path);

    void clearErrors();

    void setError(String path, BsErrorsManager.BsbError error);

    void associatePsiElement(VirtualFile file, PsiElement element);
}
