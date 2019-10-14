package com.reason.ide.reference;

import com.intellij.psi.PsiElement;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

public class ResolveParameterElementTest extends BasePlatformTestCase {

    public void testInnerScopeInFunction() {
        myFixture.configureByText("A.re", "let add10 = x => x<caret> + 10;");

        PsiElement e = myFixture.getElementAtCaret();
//        assertInstanceOf(e.getParent(), PsiParameter.class);
    }

}
