package com.reason.ide.hints;

import com.intellij.lang.parameterInfo.*;
import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.rescript.*;
import org.jetbrains.annotations.*;

public class ResParameterInfoHandler extends ORParameterInfoHandler {
    @Override
    int computeParameterIndex(@NotNull PsiParameters paramsOwner, @NotNull UpdateParameterInfoContext context) {
        return ParameterInfoUtils.getCurrentParameterIndex(paramsOwner.getNode(), context.getOffset(), ResTypes.INSTANCE.COMMA);
    }

    @Override
    @Nullable PsiParameters findFunctionParams(@NotNull PsiFile file, int offset) {
        PsiElement elementAt = file.findElementAt(offset);
        return elementAt == null ? null : PsiTreeUtil.getParentOfType(elementAt, PsiParameters.class);
    }
}
