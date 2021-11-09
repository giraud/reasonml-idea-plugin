package com.reason.lang.core;

import com.intellij.psi.util.*;
import com.reason.ide.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;

@SuppressWarnings("ConstantConditions")
public class PsiScopedExprTest extends ORBasePlatformTestCase {
    public void testEmptyScope() {
        PsiLet e = PsiTreeUtil.findChildOfType(configureCode("A.ml", "let () = x1"), PsiLet.class);

        assertNotNull(ORUtil.findImmediateFirstChildOfClass(e, PsiUnit.class));
        assertNull(e.getName());
    }

    public void testNotEmptyScope() {
        PsiLet e = PsiTreeUtil.findChildOfType(configureCode("A.ml", "let (a, b) = x"), PsiLet.class);

        assertNotNull(ORUtil.findImmediateFirstChildOfClass(e, PsiDeconstruction.class));
    }
}
