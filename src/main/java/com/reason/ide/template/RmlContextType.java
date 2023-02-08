package com.reason.ide.template;

import com.intellij.codeInsight.template.*;
import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.lang.reason.*;
import org.jetbrains.annotations.*;

public abstract class RmlContextType extends TemplateContextType {
    RmlContextType(@NotNull String id, @NotNull String presentableName, @Nullable Class<? extends TemplateContextType> baseContextType) {
        super(id, presentableName, baseContextType);
    }

    @Override
    public boolean isInContext(@NotNull TemplateActionContext context) {
        PsiFile file = context.getFile();
        int offset = context.getStartOffset();
        if (!PsiUtilCore.getLanguageAtOffset(file, offset).isKindOf(RmlLanguage.INSTANCE)) {
            return false;
        }

        PsiElement element = file.findElementAt(offset);
        if (element instanceof PsiWhiteSpace) {
            return false;
        }

        return element != null && isInContext(element);
    }

    protected abstract boolean isInContext(PsiElement element);

    public static class Generic extends RmlContextType {
        protected Generic() {
            super("REASON_CODE", "Reason", EverywhereContextType.class);
        }

        @Override
        protected boolean isInContext(PsiElement element) {
            return true;
        }
    }
}
