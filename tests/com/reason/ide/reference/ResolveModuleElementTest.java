package com.reason.ide.reference;

import com.intellij.psi.PsiElement;
import com.reason.ide.ORBasePlatformTestCase;
import com.reason.lang.core.psi.PsiModule;

public class ResolveModuleElementTest extends ORBasePlatformTestCase {

    public void testBasicNull() {
        configureCode("Dimensions.re", "let space = 5;");
        configureCode("Comp.re", "Dimensions<caret>");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("Dimensions.re", ((PsiModule) e).getName());
    }

    public void testBasic() {
        configureCode("Dimensions.re", "let space = 5;");
        configureCode("Comp.re", "let D = Dimensions<caret>");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("Dimensions.re", ((PsiModule) e).getName());
    }

    public void testWithAlias() {
        configureCode("A1.re", "module A11 = {};");
        configureCode("A.re", "module A1 = {};");
        configureCode("B.re", "module X = A; X.A1<caret>);");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.A1", ((PsiModule) e.getParent()).getQualifiedName());
    }

    public void testWithAliasAndInterface() {
        configureCode("C.rei", "module A1 = {};");
        configureCode("D.re", "module X = C; X.A1<caret>);");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("C.A1", ((PsiModule) e.getParent()).getQualifiedName());
    }
}
