package com.reason.ide.reference;

import com.intellij.psi.*;
import com.reason.ide.*;
import com.reason.lang.core.psi.*;

public class ResolveUpperElementOCLTest extends ORBasePlatformTestCase {
    public void test_basic_file() {
        configureCode("Dimensions.ml", "let space = 5");
        configureCode("Comp.ml", "Dimensions<caret>");

        PsiElement elementAtCaret = myFixture.getElementAtCaret();
        assertEquals("Dimensions.ml", ((PsiQualifiedNamedElement) elementAtCaret).getName());
    }

    public void test_interface_implementation() {
        configureCode("A.mli", "type t");
        configureCode("A.ml", "type t");
        configureCode("B.ml", "A<caret>");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.ml", ((PsiNamedElement) e).getName());
    }

    public void test_let_alias() {
        configureCode("Dimensions.ml", "let space = 5");
        configureCode("Comp.ml", "let s = Dimensions<caret>.space");

        PsiElement elementAtCaret = myFixture.getElementAtCaret();
        assertEquals("Dimensions.ml", ((PsiQualifiedNamedElement) elementAtCaret).getName());
    }

    public void test_alias() {
        configureCode("A1.ml", "module A11 = struct end");
        configureCode("A.ml", "module A1 = struct end");
        configureCode("B.ml", "module X = A\n let _ = X.A1<caret>");

        PsiElement elementAtCaret = myFixture.getElementAtCaret();
        assertEquals("A.A1", ((PsiQualifiedNamedElement) elementAtCaret.getParent()).getQualifiedName());
    }

    public void test_alias_path_no_resolution() {
        configureCode("A.ml", "module X = struct module Y = struct let z = 1 end end");
        configureCode("B.ml", "module C = A.X\n let _ = C<caret>.Y");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("B.C", ((PsiQualifiedNamedElement) e.getParent()).getQualifiedName());
    }

    public void test_alias_path_resolution() {
        configureCode("A.ml", "module X = struct module Y = struct let z = 1 end end");
        configureCode("B.ml", "module C = A.X\n let _ = C.Y<caret>");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.X.Y", ((PsiQualifiedNamedElement) e.getParent()).getQualifiedName());
    }

    public void test_local_alias() {
        configureCode("Belt.ml", "let x = 1;");
        configureCode("A.ml", "module B = Belt\n B<caret>");

        PsiElement elementAtCaret = myFixture.getElementAtCaret();
        assertEquals("A.B", ((PsiQualifiedNamedElement) elementAtCaret.getParent()).getQualifiedName());
    }

    public void test_alias_interface() {
        configureCode("C.mli", "module A1 = struct end");
        configureCode("D.ml", "module X = C\n let _ = X.A1<caret>");

        PsiElement elementAtCaret = myFixture.getElementAtCaret();
        assertEquals("C.A1", ((PsiQualifiedNamedElement) elementAtCaret.getParent()).getQualifiedName());
    }

    public void test_open() {
        configureCode("Belt.ml", "module Option = struct end");
        configureCode("Dummy.ml", "open Belt.Option<caret>");

        PsiElement elementAtCaret = myFixture.getElementAtCaret();
        assertEquals("Belt.Option", ((PsiQualifiedNamedElement) elementAtCaret.getParent()).getQualifiedName());
        assertEquals("Belt.ml", elementAtCaret.getContainingFile().getName());
    }

    public void test_include_path() {
        configureCode("Css_Core.mli", "let display: string -> rule");
        configureCode("Css.ml", "include Css_Core<caret>\n include Css_Core.Make({})");

        PsiElement elementAtCaret = myFixture.getElementAtCaret();
        assertEquals("Css_Core", ((PsiQualifiedNamedElement) elementAtCaret).getQualifiedName());
    }

    public void test_include_alias() {
        configureCode("Css_AtomicTypes.mli", "module Color = struct type t end");
        configureCode("Css_Core.mli", "module Types = Css_AtomicTypes");
        configureCode("Css.ml", "include Css_Core");
        configureCode("A.ml", "Css.Types.Color<caret>");

        PsiElement elementAtCaret = myFixture.getElementAtCaret();
        assertEquals("Css_AtomicTypes.Color", ((PsiQualifiedNamedElement) elementAtCaret.getParent()).getQualifiedName());
    }

    public void test_variant_with_path() {
        configureCode("A.ml", "type a = | Variant");
        configureCode("B.ml", "type b = | Variant");
        configureCode("C.ml", "A.Variant<caret>");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.a.Variant", ((PsiVariantDeclaration) e.getParent()).getQualifiedName());
    }

    public void test_variant_module_alias() {
        configureCode("Aaa.ml", "type t = | Test");
        configureCode("Bbb.ml", "module A = Aaa\n let _ = A.Test<caret>");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("Aaa.t.Test", ((PsiVariantDeclaration) e.getParent()).getQualifiedName());
    }

    public void test_variant_module_alias_inner() {
        configureCode("Aaa.ml", "module Option = struct type t = | Test end");
        configureCode("Bbb.ml", "module A = Aaa\n let _ = A.Option.Test<caret>");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("Aaa.Option.t.Test", ((PsiVariantDeclaration) e.getParent()).getQualifiedName());
    }

    public void test_variant_constructor() {
        configureCode("A.ml", "type a = | Variant(int)");
        configureCode("B.ml", "let _ = A.Variant<caret>(1)");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.a.Variant", ((PsiVariantDeclaration) e.getParent()).getQualifiedName());
    }

    public void test_exception() {
        myFixture.configureByText("A.ml", "exception ExceptionName\n let _ = raise ExceptionName<caret>");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.ExceptionName", ((PsiQualifiedNamedElement) e.getParent()).getQualifiedName());
    }

    public void test_belt_alias() {
        configureCode("String.ml", "type t");
        configureCode("Belt.ml", "module Map = Belt_Map");
        configureCode("Belt_Map.ml", "module String = Belt_MapString");
        configureCode("Belt_MapString.ml", "type t");
        configureCode("A.ml", "Belt.Map.String<caret>");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("Belt_Map.String", ((PsiQualifiedNamedElement) e.getParent()).getQualifiedName());
    }

    /*
    public void test_functor_inside() {
        configureCode("F.ml", "module type S  = sig module X : sig  end end\n" +
                "module M() : S = struct module X = struct  end end \n" +
                "module A = M(struct  end)\n" +
                "module X2 = struct module X1 = struct module X = struct end end end\n" +
                "module V = A.X<caret>");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("F.S.X", ((PsiQualifiedElement) e.getParent()).getQualifiedName());
    }

    public void test_functor_outside() {
        configureCode("F.ml", "module type S  = sig module X : sig  end end\n" +
                "module M() : S = struct module X = struct  end end \n" +
                "module A = M(struct  end)");
        configureCode("B.ml", "module X2 = struct module X1 = struct module X = struct end end end\n" +
                "module V = F.A.X<caret>");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("F.S.X", ((PsiQualifiedElement) e.getParent()).getQualifiedName());
    }
    */
}
