package com.reason.ide.reference;

import com.intellij.psi.PsiElement;
import com.reason.ide.ORBasePlatformTestCase;
import com.reason.lang.core.psi.PsiVariantDeclaration;

public class ResolveVariantElementTest extends ORBasePlatformTestCase {

    public void testWithAlias() {
        myFixture.configureByText("A.re", "type a = | Variant;");
        myFixture.configureByText("B.re", "type b = | Variant;");
        myFixture.configureByText("C.re", "A.Variant<caret>");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.a.Variant", ((PsiVariantDeclaration) e.getParent()).getQualifiedName());
    }
}
