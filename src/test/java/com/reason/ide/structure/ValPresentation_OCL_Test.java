package com.reason.ide.structure;

import com.reason.ide.*;
import com.reason.lang.core.psi.*;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

@SuppressWarnings("ConstantConditions")
@RunWith(JUnit4.class)
public class ValPresentation_OCL_Test extends ORBasePlatformTestCase {
    @Test
    public void test_noSig() {
        RPsiVal e = configureCode("A.ml", "val x = 1").getQualifiedExpressions("A.x", RPsiVal.class).get(0);

        assertEquals("x", e.getPresentation().getPresentableText());
        assertNull(e.getPresentation().getLocationString());
    }

    @Test
    public void test_sig() {
        RPsiVal e = configureCode("A.mli", "val x : 'a -> 'a t").getQualifiedExpressions("A.x", RPsiVal.class).get(0);

        assertEquals("x", e.getPresentation().getPresentableText());
        assertEquals("'a -> 'a t", e.getPresentation().getLocationString());
    }
}
