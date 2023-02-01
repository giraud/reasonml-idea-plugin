package com.reason.ide.completion;

import com.intellij.codeInsight.completion.*;
import com.reason.ide.*;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
@RunWith(JUnit4.class)
public class ModuleCompletionRMLTest extends ORBasePlatformTestCase {
    @Test
    public void test_empty() {
        configureCode("A.re", "let x = 1; module A1 = {};");
        configureCode("B.re", "let y = 1;");
        configureCode("C.re", "open <caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSameElements(strings, "A", "B");
    }

    @Test
    public void test_basic() {
        configureCode("A.re", "let x = 1; module A1 = {};");
        configureCode("B.re", "open A.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSameElements(strings, "A1");
    }

    @Test
    public void test_deep() {
        configureCode("A.re", "module A1 = { module A2 = { module A3 = {}; module A4 = {}; }; };");
        configureCode("B.re", "open A.A1.A2.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSameElements(strings, "A3", "A4");
    }

    @Test
    public void test_alias() {
        configureCode("A.re", "module A1 = {};");
        configureCode("B.re", "module B1 = A;");
        configureCode("C.re", "open B.B1.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSameElements(strings, "A1");
    }

    @Test
    public void test_file_include() {
        configureCode("A.re", "module A1 = {};");
        configureCode("B.re", "module B1 = {};");
        configureCode("C.re", "include A; include B;");
        configureCode("D.re", "open C.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSameElements(strings, "A1", "B1");
    }

    @Test
    public void test_inner_include() {
        configureCode("A.re", "module A1 = { module A2 = {}; };");
        configureCode("B.re", "module B1 = { include A.A1; };");
        configureCode("C.re", "open B.B1.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSameElements(strings, "A2");
    }
}
