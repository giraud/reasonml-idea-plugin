package com.reason.ide.structure;

import com.reason.ide.*;
import com.reason.lang.core.psi.*;

@SuppressWarnings("ConstantConditions")
public class ValPresentationOCLTest extends ORBasePlatformTestCase {
    public void test_noSig() {
        PsiVal e = configureCode("A.ml", "val x = 1").getQualifiedExpressions("A.x", PsiVal.class).get(0);

        assertEquals("x", e.getPresentation().getPresentableText());
        assertNull(e.getPresentation().getLocationString());
    }

    public void test_sig() {
        PsiVal e = configureCode("A.mli", "val x : 'a -> 'a t").getQualifiedExpressions("A.x", PsiVal.class).get(0);

        assertEquals("x", e.getPresentation().getPresentableText());
        assertEquals("'a -> 'a t", e.getPresentation().getLocationString());
    }
}
