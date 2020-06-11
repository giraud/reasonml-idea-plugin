package com.reason.ide.reference;

import com.intellij.psi.PsiElement;
import com.reason.ide.ORBasePlatformTestCase;
import com.reason.lang.core.psi.PsiQualifiedElement;

public class ResolveInFunctorTest extends ORBasePlatformTestCase {

    public void testRml_Body() {
        configureCode("A.re", "module Make = (M:I) => { let a = 3; };");
        configureCode("B.re", "module Instance = A.Make({}); let b = Instance.a<caret>;");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.Make.a", ((PsiQualifiedElement) e.getParent()).getQualifiedName());
    }

    public void testOcl_LocalOpenWithParens() {
        configureCode("A.ml", "module Make (M:I) = struct let a = 3 end");
        configureCode("B.ml", "module Instance = A.Make(struct end)\n let b = Instance.a<caret>");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.Make.a", ((PsiQualifiedElement) e.getParent()).getQualifiedName());
    }

    public void testRml_FileInclude() {
        configureCode("A.re", "module Make = (M:I) => { let a = 3; }; include Make({})");
        configureCode("B.re", "let b = A.a<caret>;");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.Make.a", ((PsiQualifiedElement) e.getParent()).getQualifiedName());
    }
}
