package com.reason.ide.completion;

import com.intellij.codeInsight.completion.*;
import com.reason.ide.*;
import org.junit.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class ModuleCompletion_OCL_Test extends ORBasePlatformTestCase {
    @Test
    public void test_empty() {
        configureCode("A.ml", "let x = 1\n module A1 = struct end");
        configureCode("B.ml", "let y = 1");
        configureCode("C.ml", "open <caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSameElements(strings, "A", "B");
    }

    @Test
    public void test_basic() {
        configureCode("A.ml", "let x = 1\n module A1 = struct end");
        configureCode("B.ml", "open A.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSameElements(strings, "A1");
    }

    @Test
    public void test_deep() {
        configureCode("A.ml", "module A1 = struct module A2 = struct module A3 = struct end\n module A4 = struct end end end");
        configureCode("B.ml", "open A.A1.A2.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSameElements(strings, "A3", "A4");
    }

    @Test
    public void test_alias() {
        configureCode("A.ml", "module A1 = struct end");
        configureCode("B.ml", "module B1 = A");
        configureCode("C.ml", "open B.B1.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSameElements(strings, "A1");
    }

    @Test
    public void test_file_include() {
        configureCode("A.ml", "module A1 = struct end");
        configureCode("B.ml", "module B1 = struct end");
        configureCode("C.ml", "include A; include B");
        configureCode("D.ml", "open C.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSameElements(strings, "A1", "B1");
    }

    @Test
    public void test_inner_include() {
        configureCode("A.ml", "module A1 = struct module A2 = struct end end");
        configureCode("B.ml", "module B1 = struct include A.A1 end");
        configureCode("C.ml", "open B.B1.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSameElements(strings, "A2");
    }
}
