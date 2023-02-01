package com.reason.ide.template;

import com.intellij.codeInsight.template.*;
import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.lang.rescript.*;
import org.jetbrains.annotations.*;

public abstract class ResContextType extends TemplateContextType {
    ResContextType(@NotNull String id, @NotNull String presentableName, @Nullable Class<? extends TemplateContextType> baseContextType) {
        super(id, presentableName, baseContextType);
    }

    @Override
    public boolean isInContext(@NotNull TemplateActionContext context) {
        PsiFile file = context.getFile();
        int offset = context.getStartOffset();
        if (!PsiUtilCore.getLanguageAtOffset(file, offset).isKindOf(ResLanguage.INSTANCE)) {
            return false;
        }

        PsiElement element = file.findElementAt(offset);
        if (element instanceof PsiWhiteSpace) {
            return false;
        }

        return element != null && isInContext(element);
    }

    protected abstract boolean isInContext(PsiElement element);

    public static class Generic extends ResContextType {
        protected Generic() {
            super("RESCRIPT_CODE", "Rescript", EverywhereContextType.class);
        }

        @Override
        protected boolean isInContext(PsiElement element) {
            return true;
        }
    }
}
