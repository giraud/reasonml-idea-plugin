package com.reason.ide.hints;

import com.intellij.lang.parameterInfo.*;
import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.reason.*;
import org.jetbrains.annotations.*;

public class RmlParameterInfoHandler extends ORParameterInfoHandler {
    @Override
    int computeParameterIndex(@NotNull RPsiParameters paramsOwner, @NotNull UpdateParameterInfoContext context) {
        return ParameterInfoUtils.getCurrentParameterIndex(paramsOwner.getNode(), context.getOffset(), RmlTypes.INSTANCE.COMMA);
    }

    @Override
    @Nullable RPsiParameters findFunctionParams(@NotNull PsiFile file, int offset) {
        PsiElement elementAt = file.findElementAt(offset);
        return elementAt == null ? null : PsiTreeUtil.getParentOfType(elementAt, RPsiParameters.class);
    }
}
