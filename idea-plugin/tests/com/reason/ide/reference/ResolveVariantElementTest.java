package com.reason.ide.reference;

import com.intellij.psi.PsiElement;
import com.reason.ide.ORBasePlatformTestCase;
import com.reason.lang.core.psi.PsiVariantDeclaration;

public class ResolveVariantElementTest extends ORBasePlatformTestCase {

    public void testRml_withPath() {
        configureCode("A.re", "type a = | Variant;");
        configureCode("B.re", "type b = | Variant;");
        configureCode("C.re", "A.Variant<caret>");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.a.Variant", ((PsiVariantDeclaration) e.getParent()).getQualifiedName());
    }

    public void testOcl_withPath() {
        configureCode("A.ml", "type a = | Variant");
        configureCode("B.ml", "type b = | Variant");
        configureCode("C.ml", "A.Variant<caret>");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.a.Variant", ((PsiVariantDeclaration) e.getParent()).getQualifiedName());
    }

    public void testRml_withModuleAlias() {
        configureCode("Aaa.re", "type t = | Test;");
        configureCode("Bbb.re", "module A = Aaa; A.Test<caret>");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("Aaa.t.Test", ((PsiVariantDeclaration) e.getParent()).getQualifiedName());
    }

    public void testOcl_withModuleAlias() {
        configureCode("Aaa.ml", "type t = | Test");
        configureCode("Bbb.ml", "module A = Aaa \nlet _ = A.Test<caret>");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("Aaa.t.Test", ((PsiVariantDeclaration) e.getParent()).getQualifiedName());
    }

    public void testRml_withModuleAliasInner() {
        configureCode("Aaa.re", "module Option = { type t = | Test; }");
        configureCode("Bbb.re", "module A = Aaa; A.Option.Test<caret>");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("Aaa.Option.t.Test", ((PsiVariantDeclaration) e.getParent()).getQualifiedName());
    }

    public void testOcl_withModuleAliasInner() {
        configureCode("Aaa.ml", "module Option = struct type t = | Test end");
        configureCode("Bbb.ml", "module A = Aaa \nlet _ = A.Option.Test<caret>");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("Aaa.Option.t.Test", ((PsiVariantDeclaration) e.getParent()).getQualifiedName());
    }

    public void testRml_constructor() {
        configureCode("A.re", "type a = | Variant(int);");
        configureCode("B.re", "A.Variant<caret>(1)");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.a.Variant", ((PsiVariantDeclaration) e.getParent()).getQualifiedName());
    }

    public void testOcl_constructor() {
        configureCode("A.ml", "type a = | Variant(int)");
        configureCode("B.ml", "A.Variant<caret>(1)");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.a.Variant", ((PsiVariantDeclaration) e.getParent()).getQualifiedName());
    }

}
