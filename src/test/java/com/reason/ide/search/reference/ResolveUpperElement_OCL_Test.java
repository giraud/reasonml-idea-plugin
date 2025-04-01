package com.reason.ide.search.reference;

import com.intellij.psi.*;
import com.reason.ide.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

public class ResolveUpperElement_OCL_Test extends ORBasePlatformTestCase {
    @Test
    public void test_basic_file() {
        configureCode("Dimensions.ml", "let space = 5");
        configureCode("Comp.ml", "Dimensions<caret>");

        PsiQualifiedNamedElement e = (PsiQualifiedNamedElement) myFixture.getElementAtCaret();
        assertEquals("Dimensions.ml", e.getName());
    }

    @Test
    public void test_interface_implementation() {
        configureCode("A.mli", "type t");
        configureCode("A.ml", "type t");
        configureCode("B.ml", "A<caret>");

        PsiNamedElement e = (PsiNamedElement) myFixture.getElementAtCaret();
        assertEquals("A.mli", e.getName());
    }

    @Test
    public void test_let_alias() {
        configureCode("Dimensions.ml", "let space = 5");
        configureCode("Comp.ml", "let s = Dimensions<caret>.space");

        PsiQualifiedNamedElement e = (PsiQualifiedNamedElement) myFixture.getElementAtCaret();
        assertEquals("Dimensions.ml", e.getName());
    }

    @Test
    public void test_alias() {
        configureCode("A1.ml", "module A11 = struct end");
        configureCode("A.ml", "module A1 = struct end");
        configureCode("B.ml", "module X = A\n let _ = X.A1<caret>");

        RPsiModule e = (RPsiModule) myFixture.getElementAtCaret();
        assertEquals("A.A1", e.getQualifiedName());
    }

    @Test
    public void test_alias_path_no_resolution() {
        configureCode("A.ml", "module X = struct module Y = struct let z = 1 end end");
        configureCode("B.ml", "module C = A.X\n let _ = C<caret>.Y");

        RPsiModule e = (RPsiModule) myFixture.getElementAtCaret();
        assertEquals("B.C", e.getQualifiedName());
    }

    @Test
    public void test_alias_path_resolution() {
        configureCode("A.ml", "module X = struct module Y = struct let z = 1 end end");
        configureCode("B.ml", "module C = A.X\n let _ = C.Y<caret>");

        RPsiModule e = (RPsiModule) myFixture.getElementAtCaret();
        assertEquals("A.X.Y", e.getQualifiedName());
    }

    @Test
    public void test_local_alias() {
        configureCode("Belt.ml", "let x = 1;");
        configureCode("A.ml", "module B = Belt\n let _ = B<caret>");

        RPsiModule e = (RPsiModule) myFixture.getElementAtCaret();
        assertEquals("A.B", e.getQualifiedName());
    }

    @Test
    public void test_alias_of_alias() {
        configureCode("A.ml", """
                module A1 = struct
                    module A2 = struct
                      let id = "_new_"
                    end
                end
                """);

        configureCode("B.ml", """
                module B1 = struct
                  module B2 = struct
                    module B3 = struct
                      let id = A.A1.A2.id
                    end
                  end
                end
                
                module B4 = struct
                  include A
                  module B5 = B1.B2
                end
                """);

        configureCode("C.ml", """
                module C1 = B.B4
                module C2 = C1.B5.B3
                let _ = C2.id<caret>
                """);

        PsiElement e = myFixture.getElementAtCaret();

        assertEquals("B.B1.B2.B3.id", ((RPsiQualifiedPathElement) e).getQualifiedName());
    }

    @Test
    public void test_alias_interface() {
        configureCode("C.mli", "module A1 = struct end");
        configureCode("D.ml", "module X = C\n let _ = X.A1<caret>");

        RPsiModule e = (RPsiModule) myFixture.getElementAtCaret();
        assertEquals("C.A1", e.getQualifiedName());
    }

    @Test
    public void test_function_call() {
        configureCode("AsyncHooks.ml", "module XhrAsync = struct let make = () => () end");
        configureCode("A.ml", "let _ = AsyncHooks.useCancellableRequest AsyncHooks<caret>.XhrAsync.make");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("AsyncHooks", ((RPsiQualifiedPathElement) e).getQualifiedName());
    }

    @Test
    public void test_open() {
        configureCode("Belt.ml", "module Option = struct end");
        configureCode("Dummy.ml", "open Belt.Option<caret>");

        RPsiModule e = (RPsiModule) myFixture.getElementAtCaret();
        assertEquals("Belt.Option", e.getQualifiedName());
        assertEquals("Belt.ml", e.getContainingFile().getName());
    }

    @Test
    public void test_include_path() {
        configureCode("Css_Core.mli", "let display: string -> rule");
        configureCode("Css.ml", "include Css_Core<caret>\n include Css_Core.Make({})");

        PsiQualifiedNamedElement e = (PsiQualifiedNamedElement) myFixture.getElementAtCaret();
        assertEquals("Css_Core", e.getQualifiedName());
    }

    @Test
    public void test_include_alias() {
        configureCode("Css_AtomicTypes.mli", "module Color = struct type t end");
        configureCode("Css_Core.mli", "module Types = Css_AtomicTypes");
        configureCode("Css.ml", "include Css_Core");
        configureCode("A.ml", "Css.Types.Color<caret>");

        RPsiModule e = (RPsiModule) myFixture.getElementAtCaret();
        assertEquals("Css_AtomicTypes.Color", e.getQualifiedName());
    }

    //region Variants
    @Test
    public void test_local_variant() {
        configureCode("A.ml", "type a = | Variant\n let _ = Variant<caret>");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.Variant", ((RPsiVariantDeclaration) e).getQualifiedName());
    }

    @Test
    public void test_variant_with_path() {
        configureCode("A.ml", "type a = | Variant");
        configureCode("B.ml", "type b = | Variant");
        configureCode("C.ml", "A.Variant<caret>");

        RPsiVariantDeclaration e = (RPsiVariantDeclaration) myFixture.getElementAtCaret();
        assertEquals("A.Variant", e.getQualifiedName());
    }

    @Test
    public void test_variant_module_alias() {
        configureCode("Aaa.ml", "type t = | Test");
        configureCode("Bbb.ml", "module A = Aaa\n let _ = A.Test<caret>");

        RPsiVariantDeclaration e = (RPsiVariantDeclaration) myFixture.getElementAtCaret();
        assertEquals("Aaa.Test", e.getQualifiedName());
    }

    @Test
    public void test_variant_module_alias_inner() {
        configureCode("Aaa.ml", "module Option = struct type t = | Test end");
        configureCode("Bbb.ml", "module A = Aaa\n let _ = A.Option.Test<caret>");

        RPsiVariantDeclaration e = (RPsiVariantDeclaration) myFixture.getElementAtCaret();
        assertEquals("Aaa.Option.Test", e.getQualifiedName());
    }

    @Test
    public void test_variant_constructor() {
        configureCode("A.ml", "type a = | Variant(int)");
        configureCode("B.ml", "let _ = A.Variant<caret>(1)");

        RPsiVariantDeclaration e = (RPsiVariantDeclaration) myFixture.getElementAtCaret();
        assertEquals("A.Variant", e.getQualifiedName());
    }
    //endregion

    //region Poly-variants
    @Test
    public void test_local_poly_variant() {
        configureCode("A.ml", "type a = [ | `Variant ]\n let _ = `Variant<caret>");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.#Variant", ((RPsiVariantDeclaration) e).getQualifiedName());
    }

    @Test
    public void test_poly_variant_with_path() {
        configureCode("A.ml", "type a = [ | `Variant ]");
        configureCode("B.ml", "type b = [ | `Variant ]");
        configureCode("C.ml", "A.`Variant<caret>");

        RPsiVariantDeclaration e = (RPsiVariantDeclaration) myFixture.getElementAtCaret();
        assertEquals("A.#Variant", e.getQualifiedName());
    }

    @Test
    public void test_poly_variant_module_alias() {
        configureCode("Aaa.ml", "type t = [ | `Test ]");
        configureCode("Bbb.ml", "module A = Aaa\nlet _ = A.`Test<caret>");

        RPsiVariantDeclaration e = (RPsiVariantDeclaration) myFixture.getElementAtCaret();
        assertEquals("Aaa.#Test", e.getQualifiedName());
    }

    @Test
    public void test_poly_variant_module_alias_inner() {
        configureCode("Aaa.ml", "module Option = struct type t = [ | `Test ] end");
        configureCode("Bbb.ml", "module A = Aaa\nlet _ = A.Option.`Test<caret>");

        RPsiVariantDeclaration e = (RPsiVariantDeclaration) myFixture.getElementAtCaret();
        assertEquals("Aaa.Option.#Test", e.getQualifiedName());
    }

    @Test
    public void test_poly_variant_constructor() {
        configureCode("A.ml", "type a = | `Variant of int");
        configureCode("B.ml", "let _ = A.`Variant<caret> 1");

        RPsiVariantDeclaration e = (RPsiVariantDeclaration) myFixture.getElementAtCaret();
        assertEquals("A.#Variant", e.getQualifiedName());
    }
    //endregion

    @Test
    public void test_exception() {
        myFixture.configureByText("A.ml", "exception ExceptionName\n let _ = raise ExceptionName<caret>");

        RPsiException e = (RPsiException) myFixture.getElementAtCaret();
        assertEquals("A.ExceptionName", e.getQualifiedName());
    }

    @Test
    public void test_belt_alias() {
        configureCode("String.ml", "type t");
        configureCode("Belt.ml", "module Map = Belt_Map");
        configureCode("Belt_Map.ml", "module String = Belt_MapString");
        configureCode("Belt_MapString.ml", "type t");
        configureCode("A.ml", "Belt.Map.String<caret>");

        RPsiModule e = (RPsiModule) myFixture.getElementAtCaret();
        assertEquals("Belt_Map.String", e.getQualifiedName());
    }

    @Test
    public void test_functor_inside() {
        configureCode("F.ml", """
                module type S  = sig module X : sig  end end
                module M() : S = struct module X = struct  end end\s
                module A = M(struct  end)
                module X2 = struct module X1 = struct module X = struct end end end
                module V = A.X<caret>""");

        RPsiModule e = (RPsiModule) myFixture.getElementAtCaret();
        assertEquals("F.M.X", e.getQualifiedName());
    }

    @Test
    public void test_functor_outside() {
        configureCode("F.ml", """
                module type S  = sig module X : sig  end end
                module M() : S = struct module X = struct  end end
                module A = M(struct  end)""");
        configureCode("B.ml", "module X2 = struct module X1 = struct module X = struct end end end\n" +
                              "module V = F.A.X<caret>");

        RPsiModule e = (RPsiModule) myFixture.getElementAtCaret();
        assertEquals("F.M.X", e.getQualifiedName());
    }

    @Test
    public void test_module_signature() {
        configureCode("A.ml", """
                module B = struct
                  module C = struct
                    module type S = sig end
                  end
                  module D = C
                end
                
                module M : B.D.S<caret> = struct end
                """);

        RPsiModule e = (RPsiModule) myFixture.getElementAtCaret();
        assertEquals("A.B.C.S", e.getQualifiedName());
    }

    @Test
    public void test_not_resolve_alternate_name() {
        configureCode("A.ml", "");
        configureCode("B.ml", """
                module B1 = struct
                  include A
                end
                """);
        configureCode("C.ml", """
                module C1 = B.B1<caret>
                """);

        RPsiModule e = (RPsiModule) myFixture.getElementAtCaret();
        assertEquals("B.B1", e.getQualifiedName());
    }

    @Test
    public void test_with_include() {
        configureCode("A.ml", "module A1 = struct module A2 = struct module A3 = struct end end end");
        configureCode("B.ml", """
                module B1 = struct
                  include A
                end
                """);
        configureCode("C.ml", """
                module C1 = B.B1
                module C2 = C1.A1.A2
                module M = C2.A3<caret>
                """);

        RPsiModule e = (RPsiModule) myFixture.getElementAtCaret();
        assertEquals("A.A1.A2.A3", e.getQualifiedName());
    }

    @Test
    public void test_pervasives_modules() {
        configureCode("JsxDOMC.ml", "type style");
        configureCode("pervasives.ml", "module JsxDOM = JsxDOMC");
        configureCode("A.ml", "module A1 = JsxDOM<caret>");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("Pervasives.JsxDOM", ((RPsiModule) e).getQualifiedName());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/426
    @Test
    public void test_alias_resolution_same_file() {
        configureCode("Dummy.ml", """
                module A = struct
                  module B = struct
                    module C = struct
                      module D = struct
                      end
                    end
                  end
                end
                
                module Bbb = A.B
                module Ddd = Bbb.C<caret>.D
                """);

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("Dummy.A.B.C", ((RPsiModule) e).getQualifiedName());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/476
    @Test
    public void test_GH_476_and_module() {
        configureCode("Dummy.ml", """
                module rec A:sig end = struct type t = B<caret>.b end
                and B : sig  type b end= struct type b end
                """);

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("Dummy.B", ((RPsiModule) e).getQualifiedName());
    }
}
