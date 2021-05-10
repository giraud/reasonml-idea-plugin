package com.reason.ide.reference;

import com.intellij.psi.*;
import com.reason.ide.*;
import com.reason.lang.core.psi.*;

public class ResolveLowerElementOCLTest extends ORBasePlatformTestCase {
    public void test_let_in_module_binding() {
        configureCode("A.ml", "let foo = 2 module X = struct let foo = 1 let z = foo<caret> end");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.X.foo", ((PsiQualifiedNamedElement) e.getParent()).getQualifiedName());
    }

    public void test_let_inner_scope() {
        configureCode("A.ml", "let x = 1\n let a = let x = 2 in x<caret> + 10");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.a.x", ((PsiQualifiedNamedElement) e.getParent()).getQualifiedName());
    }

    public void test_inner_scope_in_function() {
        configureCode("A.ml", "let x = 1\n let fn = let x = 2 in fn1 x<caret>");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.fn.x", ((PsiQualifiedNamedElement) e.getParent()).getQualifiedName());
    }

    public void test_inner_scope_in_impl() {
        configureCode("A.mli", "val x:int");
        configureCode("A.ml", "let x = 1\n let fn = let foo = 2 in fn1 foo<caret>");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.fn.foo", ((PsiQualifiedNamedElement) e.getParent()).getQualifiedName());
        assertEquals("A.ml", e.getContainingFile().getName());
    }

    public void test_let_local_module_alias() {
        configureCode("A.mli", "val x:int");
        configureCode("B.ml", "let x = 1\n module X = A\n let _ = X.x<caret>");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.x", ((PsiQualifiedNamedElement) e.getParent()).getQualifiedName());
    }

    public void test_alias_path() {
        configureCode("A.ml", "module X = struct module Y = struct let z = 1 end end");
        configureCode("B.ml", "module C = A.X.Y\n let _ = C.z<caret>");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.X.Y.z", ((PsiQualifiedNamedElement) e.getParent()).getQualifiedName());
    }

    public void test_open() {
        configureCode("B.ml", "let x = 1");
        configureCode("A.ml", "let x = 2 open B let _ = x<caret>");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("B.x", ((PsiQualifiedNamedElement) e.getParent()).getQualifiedName());
    }

    public void test_alias_open() {
        configureCode("B.ml", "let x = 1");
        configureCode("A.ml", "let x = 2 module C = B open C let _ = x<caret>");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("B.x", ((PsiQualifiedNamedElement) e.getParent()).getQualifiedName());
    }

    public void test_let_local_open_parens() {
        configureCode("A.ml", "module A1 = struct let a = 1 end");
        configureCode("B.ml", "let a = 2 let b = A.(A1.a<caret>)");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.A1.a", ((PsiQualifiedNamedElement) e.getParent()).getQualifiedName());
    }

    public void test_let_local_open_parens_2() {
        configureCode("A.ml", "module A1 = struct let a = 3 end");
        configureCode("B.ml", "let a = A.A1.(a<caret>)");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.A1.a", ((PsiQualifiedNamedElement) e.getParent()).getQualifiedName());
    }

    public void test_type() {
        configureCode("A.ml", "type t type t' = t<caret>");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.t", ((PsiQualifiedNamedElement) e.getParent()).getQualifiedName());
    }

    public void test_type_with_path() {
        configureCode("A.ml", "type t");
        configureCode("B.ml", "type t = A.t<caret>");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.t", ((PsiQualifiedNamedElement) e.getParent()).getQualifiedName());
    }

    public void test_type_with_path_2() {
        configureCode("A.ml", "type t\n type y = X.Y.t<caret>");

        assertThrows(AssertionError.class, "element not found in file A.ml", () -> {
            PsiElement e = myFixture.getElementAtCaret();
        });
    }

    public void test_function() {
        configureCode("A.ml", "module B = struct let bb = 1 end\n module C = struct let cc x = x end let z = C.cc(B.bb<caret>)");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.B.bb", ((PsiQualifiedNamedElement) e.getParent()).getQualifiedName());
    }

    public void test_function_open() {
        configureCode("B.ml", "module C = struct let make x = x\n let convert x = x end");
        configureCode("A.ml", "open B\n let _ = C.make([| C.convert<caret> |])");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("B.C.convert", ((PsiQualifiedNamedElement) e.getParent()).getQualifiedName());
    }

    public void test_param_parenLess() {
        configureCode("A.ml", "let add10 x = x<caret> + 10;");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.add10[x]", ((PsiQualifiedNamedElement) e.getParent()).getQualifiedName());
    }

    public void test_local_open_parens() {
        configureCode("A.ml", "module A1 = struct external a : int = \"\" end");
        configureCode("B.ml", "let b = A.(A1.a<caret>)");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.A1.a", ((PsiQualifiedNamedElement) e.getParent()).getQualifiedName());
    }

    public void test_local_open_parens_2() {
        configureCode("A.ml", "module A1 = struct external a : int = \"\" end");
        configureCode("B.ml", "let a = A.A1.(a<caret>)");

        PsiElement elementAtCaret = myFixture.getElementAtCaret();
        assertEquals("A.A1.a", ((PsiQualifiedNamedElement) elementAtCaret.getParent()).getQualifiedName());
    }

    public void test_include() {
        configureCode("A.ml", "module B = struct type t end\n module C = B\n include C\n type x = t<caret>");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.B.t", ((PsiQualifiedNamedElement) e.getParent()).getQualifiedName());
    }

    public void test_include_qualified() {
        configureCode("A.ml", "module B = struct module C = struct type t end end\n module D = B\n include D.C");
        configureCode("C.ml", "type t = A.t<caret>");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.B.C.t", ((PsiQualifiedNamedElement) e.getParent()).getQualifiedName());
    }

    public void test_multiple_module() {
        configureCode("Command.ml", "module Settings = struct module Action = struct let convert x = x end end");
        configureCode("A.ml", "module C = Y\n open Command\n Settings.Action.convert<caret>");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("Command.Settings.Action.convert", ((PsiQualifiedNamedElement) e.getParent()).getQualifiedName());
    }

    public void test_open_include() {
        configureCode("Css_Core.ml", "let fontStyle x = x");
        configureCode("Css.ml", "include Css_Core");
        configureCode("A.ml", "open Css\n let _ = fontStyle<caret>");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("Css_Core.fontStyle", ((PsiQualifiedNamedElement) e.getParent()).getQualifiedName());
    }

    public void test_open_include_deep() {
        configureCode("Css_Rule.ml", "let fontStyle x = x");
        configureCode("Css_Core.ml", "module Rules = struct include Css_Rule end");
        configureCode("Css.ml", "include Css_Core");
        configureCode("A.ml", "open Css.Rules\n let _ = fontStyle<caret>");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("Css_Rule.fontStyle", ((PsiQualifiedNamedElement) e.getParent()).getQualifiedName());
    }

    /*
    public void test_functor_body() {
        configureCode("A.ml", "module Make(M:I) = struct let a = 3 end");
        configureCode("B.ml", "module Instance = A.Make(struct end) let b = Instance.a<caret>");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.Make.a", ((PsiQualifiedNamedElement) e.getParent()).getQualifiedName());
    }

    public void test_file_include_functor() {
        configureCode("A.re", "module Make = (M:I) => { let a = 3; }; include Make({})");
        configureCode("B.re", "let b = A.a<caret>;");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.Make.a", ((PsiQualifiedNamedElement) e.getParent()).getQualifiedName());
    }

    public void test_functor_result_with_alias() {
        configureCode("A.ml", "module type Result = sig let a: int end");
        configureCode("B.ml", "module T = A\n module Make(M:Intf): T.Result = struct let b = 3 end");
        configureCode("C.ml", "module Instance = Make(struct end) let c = Instance.a<caret>");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.Result.a", ((PsiQualifiedNamedElement) e.getParent()).getQualifiedName());
    }
    */

    public void test_GH_303() {
        configureCode("B.ml", "type t1 = {bar: string}");
        configureCode("A.ml", "type t = {bar: string} let bar item = item.bar<caret>");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.t.bar", ((PsiQualifiedNamedElement) e.getParent()).getQualifiedName());
    }

    public void test_GH_303_2() {
        configureCode("B.ml", "type t1 = {bar:string}");
        configureCode("A.ml", "type t = {bar: string} let bar<caret> item = item.bar");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.bar", ((PsiQualifiedNamedElement) e.getParent()).getQualifiedName());
    }
}
