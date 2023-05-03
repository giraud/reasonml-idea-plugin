package com.reason.ide.completion;

import com.intellij.codeInsight.completion.*;
import com.reason.ide.*;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
@RunWith(JUnit4.class)
public class Jsx3NameCompletionRMLTest extends ORBasePlatformTestCase {
    // need multiple components because completeBasic returns null if there is only one lookup element
    @Test
    public void test_local_component() {
        configureCode("Dialog.re",
                "module DialogHeader = { [@react.component] let make = () => { <div/> }; };\n" +
                        "module DialogFooter = { [@react.component] let make = () => { <div/> }; };\n" +
                        "[@react.component] let make = () => <Dia<caret>");

        myFixture.completeBasic();
        List<String> completions = myFixture.getLookupElementStrings();

        assertContainsElements(completions, "DialogHeader", "DialogFooter");
        assertSize(2, completions);
    }

    @Test
    public void test_outside_components() {
        configureCode("DialogHeader.re", "[@react.component] let make = () => { <div/> };");
        configureCode("DialogFooter.re", "[@react.component] let make = () => { <div/> };");
        configureCode("Dialog.re", "[@react.component] let make = () => <Dia<caret>");

        myFixture.completeBasic();
        List<String> completions = myFixture.getLookupElementStrings();

        assertContainsElements(completions, "DialogHeader", "DialogFooter");
        assertSize(2, completions);
    }

    @Test
    public void test_dont_display_properties() {
        configureCode("DialogHeader.re", "[@react.component] let make = () => { <div/> };");
        configureCode("Dummy.re", "let _ = <<caret>Dialog");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> completions = myFixture.getLookupElementStrings();

        assertEquals(1, completions.size());
        assertEquals("DialogHeader", completions.get(0));
    }

    @Test
    public void test_inner_component() {
        configureCode("Hidden.re", "");
        configureCode("Dialog.re", "module Other = { module Title = { [@react.component] let make = () => <div/>; }; };");
        configureCode("A.re", "let _ = <<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> completions = myFixture.getLookupElementStrings();

        assertEquals("Dialog", completions.get(0));
        assertEquals(1, completions.size());
    }

    @Test
    public void test_dot() {
        configureCode("Dialog.re", "module Title = { [@react.component] let make = () => <div/>; }; [@react.component] let make = () => <div/>;");
        configureCode("A.re", "let _ = <Dialog.<caret>");

        myFixture.complete(CompletionType.BASIC, 1);
        List<String> completions = myFixture.getLookupElementStrings();

        assertEquals("Title", completions.get(0));
        assertEquals(1, completions.size());
    }

    @Test
    public void test_local_recursive() {
        configureCode("A.re",
                "module Confirm = = { [@react.component] let make = () => <div/>; };\n" +
                        "module type ContainerType = { [@react.component] let make = () => <div/>; };\n" +
                        "module rec Container:ContainerType = { [@react.component] let make = () => <div/>; };\n" +
                        "[@react.component] let make = () => <Con<caret> ");

        myFixture.completeBasic();
        List<String> completions = myFixture.getLookupElementStrings();

        assertContainsElements(completions, "Confirm", "Container");
        assertEquals(2, completions.size());
    }
}
