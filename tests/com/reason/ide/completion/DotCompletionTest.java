package com.reason.ide.completion;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.DebugUtil;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

import java.util.List;

@SuppressWarnings("ConstantConditions")
public class DotCompletionTest extends BasePlatformTestCase {

    public void testModuleLetCompletion() {
        configureCode("A.re", "let x = 1;");
        configureCode("B.re", "A.<caret>;");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSameElements(strings, "x");
    }

    public void testBeforeCaret() {
        configureCode("A.re", "type x;");
        configureCode("B.re", "A.<caret>;");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();

        assertSameElements(strings, "x");
    }

    public void testEndOfFile() {
        configureCode("A.re", "type x;");
        configureCode("B.re", "A.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertSameElements(elements, "x");
    }

    public void testMultipleAlias() {
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

    public void testSingleAlias() {
        // like ReasonReact.Router
        configureCode("ReasonReactRouter.rei", "type watcherID;");
        configureCode("ReasonReact.rei", "module Router = ReasonReactRouter;");

        configureCode("Dummy.re", "ReasonReact.Router.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> elements = myFixture.getLookupElementStrings();

        assertSize(1, elements);
        assertEquals("watcherID", elements.get(0));
    }

    @SuppressWarnings("UnusedReturnValue")
    private PsiFile configureCode(String fileName, String code) {
        return configureCode(fileName, code, false);
    }

    @SuppressWarnings("SameParameterValue")
    private PsiFile configureCode(String fileName, String code, boolean debug) {
        PsiFile file = myFixture.configureByText(fileName, code);
        if (debug) {
            System.out.println("» " + fileName + " " + this.getClass());
            System.out.println(DebugUtil.psiToString(file, true, true));
        }
        return file;
    }

}
