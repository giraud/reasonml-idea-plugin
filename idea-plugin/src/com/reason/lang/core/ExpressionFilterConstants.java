package com.reason.lang.core;

import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiType;

public class ExpressionFilterConstants {

    public static ExpressionFilter FILTER_LET = element -> element instanceof PsiLet;
    public static ExpressionFilter FILTER_TYPE = element -> element instanceof PsiType;

    private ExpressionFilterConstants() {
    }
}
