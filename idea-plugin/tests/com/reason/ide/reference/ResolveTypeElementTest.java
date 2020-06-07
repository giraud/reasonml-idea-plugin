package com.reason.ide.reference;

import com.intellij.psi.PsiElement;
import com.reason.ide.ORBasePlatformTestCase;
import com.reason.lang.core.psi.PsiQualifiedElement;

public class ResolveTypeElementTest extends ORBasePlatformTestCase {

    public void testRml_SameNameWithPath() {
        configureCode("A.re", "type t;");
        configureCode("B.re", "type t = A.t<caret>;");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.t", ((PsiQualifiedElement) e.getParent().getParent()).getQualifiedName());
    }

    public void testOcl_SameNameWithPath() {
        configureCode("A.ml", "type t");
        configureCode("B.ml", "type t = A.t<caret>");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.t", ((PsiQualifiedElement) e.getParent().getParent()).getQualifiedName());
    }
}
