package com.reason.ide.completion;

import com.intellij.codeInsight.completion.*;
import com.reason.ide.*;
import org.junit.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class DotCompletion_OCL_Test extends ORBasePlatformTestCase {
    @Test
    public void test_basic() {
        configureCode("A.ml", "let x = 1");
        configureCode("B.ml", "type t let y = 2 module B = struct end let _ = A.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSameElements(strings, "x");
    }

    @Test
    public void test_module_override() {
        configureCode("A.ml", "let x = 1");
        configureCode("B.ml", "module A = struct let y = 2 end let _ = A.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSameElements(strings, "y");
    }

    @Test
    public void test_before_caret() {
        configureCode("A.ml", "type x");
        configureCode("B.ml", "let _ = A.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSameElements(strings, "x");
    }

    @Test
    public void test_end_of_file() {
        configureCode("A.ml", "type t");
        configureCode("B.ml", "let _ = A.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSameElements(strings, "t");
    }

    @Test
    public void test_single_alias() {
        configureCode("C.mli", "type t");
        configureCode("B.mli", "module B1 = C");
        configureCode("A.ml", "let _ = B.B1.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSize(1, strings);
        assertEquals("t", strings.getFirst());
    }

    @Test
    public void test_multiple_alias() {
        // like Belt
        configureCode("string.mli", "external length : string -> int = \"%string_length\"");
        configureCode("belt_MapString.mli", "type key = string");
        configureCode("belt_Map.mli", "module String = Belt_MapString");
        configureCode("belt.ml", "module Map = Belt_Map");

        configureCode("Dummy.re", "Belt.Map.String.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSize(1, strings);
        assertEquals("key", strings.getFirst());
    }

    @Test
    public void test_alias() {
        configureCode("A.ml", "module A1 = struct end");
        configureCode("B.ml", "module B1 = struct include A end");
        configureCode("C.ml", "module C1 = B.B1.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSize(1, strings);
        assertEquals("A1", strings.getFirst());
    }

    @Test
    public void test_alias_of_alternates() {
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
                let _ = C1.<caret>
                """);

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertContainsElements(elements, "A1", "B5");
        assertSize(2, elements);
    }

    @Test
    public void test_no_pervasives() {
        configureCode("pervasives.mli", "val int_of_string : str -> int");
        configureCode("belt_Array.mli", "val length: t -> int");
        configureCode("belt.ml", "module Array = Belt_Array");

        configureCode("Dummy.re", "Belt.Array.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSize(1, strings);
        assertEquals("length", strings.getFirst());
    }

    @Test
    public void test_functor_no_return_type() {
        configureCode("A.ml", "module type Intf  = sig val x : bool end\n module MakeOcl(I:Intf) = struct let y = 1 end");
        configureCode("B.ml", "open A\n module Instance = MakeOcl(struct let x = true end)");
        configureCode("C.ml", "open B let _ = Instance.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertSameElements(elements, "y");
    }

    @Test
    public void test_functor_with_return_type() {
        configureCode("A.ml", "module type Intf  = sig val x : bool end\n module MakeIntf(I:Intf) : Intf = struct let y = 1 end");
        configureCode("B.ml", "open A\n module Instance = MakeIntf(struct let x = true end)");
        configureCode("C.ml", "open B let _ = Instance.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertSameElements(elements, "x");
    }

    @Test
    public void test_functor_include() {
        configureCode("A.ml", "module type Intf  = sig val x : bool end\n module MakeIntf(I:Intf) = struct let y = 1 end");
        configureCode("B.ml", "include A.MakeIntf(struct let x = true end)");
        configureCode("C.ml", "let _ = B.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertSameElements(elements, "y");
    }

    @Test
    public void test_parameter() {
        configureCode("A.ml", """
                type store = {x: int; y: int}
                let fn (store: store) = store.<caret>
                """);

        myFixture.completeBasic();
        List<String> elements = myFixture.getLookupElementStrings();

        assertContainsElements(elements, "x", "y");
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/452
    @Test
    public void test_GH_452_unpacked_module() {
        configureCode("A.ml", """
                module type I = sig
                  val x: int
                end
                
                let x ~p:(p:(module I)) =
                    let module S = (val p) in
                    S.<caret>
                };
                """);

        myFixture.completeBasic();
        List<String> elements = myFixture.getLookupElementStrings();

        assertSize(1, elements);
        assertContainsElements(elements, "x");
    }
}
