package com.reason.bs.annotations;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;

import java.util.Collection;

public abstract class BsErrorsManager {

    abstract public void setError(String file, BsbError error);

    abstract public Collection<BsbError> getErrors(String filePath);

    abstract public void clearErrors();

    public abstract void associatePsiElement(VirtualFile virtualFile, PsiElement elementAtOffset);

    public static class BsbError {
        String errorType;
        public int line;
        public int colStart;
        public int colEnd;
        public String message = "";
        PsiElement element;

        @Override
        public String toString() {
            return "BsbError{" +
                    errorType +
                    ": L" + line + " " + colStart + ":" + colEnd +
                    ", " + message + '}';
        }
    }

}
