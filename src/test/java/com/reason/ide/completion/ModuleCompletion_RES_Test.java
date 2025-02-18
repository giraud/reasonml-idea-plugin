package com.reason.ide.completion;

import com.intellij.codeInsight.completion.*;
import com.reason.ide.*;
import org.junit.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class ModuleCompletion_RES_Test extends ORBasePlatformTestCase {
    @Test
    public void test_empty() {
        configureCode("A.res", "let x = 1; module A1 = {}");
        configureCode("B.res", "let y = 1");
        configureCode("C.res", "open <caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSameElements(strings, "A", "B");
    }

    @Test
    public void test_basic() {
        configureCode("A.res", "let x = 1\n module A1 = {}");
        configureCode("B.res", "open A.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSameElements(strings, "A1");
    }

    @Test
    public void test_deep() {
        configureCode("A.res", "module A1 = { module A2 = { module A3 = {}\n module A4 = {} } }");
        configureCode("B.res", "open A.A1.A2.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSameElements(strings, "A3", "A4");
    }

    @Test
    public void test_alias() {
        configureCode("A.res", "module A1 = {}");
        configureCode("B.res", "module B1 = A");
        configureCode("C.res", "open B.B1.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSameElements(strings, "A1");
    }

    @Test
    public void test_file_include() {
        configureCode("A.res", "module A1 = {}");
        configureCode("B.res", "module B1 = {}");
        configureCode("C.res", "include A\n include B");
        configureCode("D.res", "open C.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSameElements(strings, "A1", "B1");
    }

    @Test
    public void test_inner_include() {
        configureCode("A.res", "module A1 = { module A2 = {} }");
        configureCode("B.res", "module B1 = { include A.A1 }");
        configureCode("C.res", "open B.B1.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSameElements(strings, "A2");
    }
}
