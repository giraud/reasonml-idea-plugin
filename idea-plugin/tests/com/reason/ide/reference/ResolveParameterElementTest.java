package com.reason.ide.reference;

import com.intellij.psi.PsiElement;
import com.reason.ide.ORBasePlatformTestCase;
import com.reason.lang.core.psi.PsiParameter;

public class ResolveParameterElementTest extends ORBasePlatformTestCase {

    public void testOcl_parenLess() {
        configureCode("A.ml", "let add10 x = x<caret> + 10");

        PsiElement e = myFixture.getElementAtCaret();
        assertInstanceOf(e.getParent(), PsiParameter.class);
    }

    public void testRml_parenLess() {
        configureCode("A.re", "let add10 = x => x<caret> + 10;");

        PsiElement e = myFixture.getElementAtCaret();
        assertInstanceOf(e.getParent(), PsiParameter.class);
    }

    public void testNs_parenLess() {
        configureCode("A.res", "let add10 = x => x<caret> + 10;");

        PsiElement e = myFixture.getElementAtCaret();
        assertInstanceOf(e.getParent(), PsiParameter.class);
    }
}
