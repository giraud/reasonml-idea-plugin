package com.reason.ide.completion;

import com.intellij.codeInsight.completion.*;
import com.reason.ide.*;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
@RunWith(JUnit4.class)
public class DotCompletionOCLTest extends ORBasePlatformTestCase {
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
    public void test_multiple_alias() {
        // like Belt
        configureCode("string.mli", "external length : string -> int = \"%string_length\"");
        configureCode("belt_MapString.mli", "type key = string");
        configureCode("belt_Map.mli", "module String = Belt_MapString");
        configureCode("belt.ml", "module Map = Belt_Map");

        configureCode("Dummy.re", "Belt.Map.String.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertSize(1, elements);
        assertEquals("key", elements.get(0));
    }

    @Test
    public void test_alias() {
        configureCode("A.ml", "module A1 = struct end");
        configureCode("B.ml", "module B1 = struct include A end");
        configureCode("C.ml", "module C1 = B.B1.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertSize(1, elements);
        assertEquals("A1", elements.get(0));
    }

    @Test
    public void test_no_pervasives() {
        configureCode("pervasives.mli", "val int_of_string : str -> int");
        configureCode("belt_Array.mli", "val length: t -> int");
        configureCode("belt.ml", "module Array = Belt_Array");

        configureCode("Dummy.re", "Belt.Array.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertSize(1, elements);
        assertEquals("length", elements.get(0));
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
}
