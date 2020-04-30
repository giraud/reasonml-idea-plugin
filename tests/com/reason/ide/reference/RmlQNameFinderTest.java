package com.reason.ide.reference;

import com.reason.ide.ORBasePlatformTestCase;
import com.reason.lang.reason.RmlQNameFinder;

import java.util.Set;

public class RmlQNameFinderTest extends ORBasePlatformTestCase {

    public void testLetBinding() {
        configureCode("A.re", "let make = { increase<caret>(); }");

        Set<String> paths = new RmlQNameFinder().extractPotentialPaths(myFixture.getElementAtCaret(), false);
        assertSameElements(paths, "A.make", "A");
    }

    public void testLocalOpenList() {
        configureCode("A.re", "let item = Css.[ margin<caret>");

        Set<String> paths = new RmlQNameFinder().extractPotentialPaths(myFixture.getElementAtCaret(), false);
        assertSameElements(paths, "A", "A.item", "A.item.Css", "A.Css", "Css");
    }

    // Local module alias must be resolved/replaced in the qname finder
    public void testLocalModuleAliasResolution() {
        configureCode("A.re", "module B = Belt; module M = { module O = B.Option; O.m<caret>");

        Set<String> paths = new RmlQNameFinder().extractPotentialPaths(myFixture.getElementAtCaret(), false);
        assertSameElements(paths, "A.O", "O");
    }
}
