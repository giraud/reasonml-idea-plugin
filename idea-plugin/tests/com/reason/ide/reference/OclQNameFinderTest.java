package com.reason.ide.reference;

import com.reason.ide.ORBasePlatformTestCase;
import com.reason.lang.ocaml.OclQNameFinder;

import java.util.*;

public class OclQNameFinderTest extends ORBasePlatformTestCase {

    public void testLetBinding() {
        configureCode("A.ml", "let make = increase<caret>()");

        Set<String> paths = OclQNameFinder.INSTANCE.extractPotentialPaths(myFixture.getElementAtCaret());
        assertSameElements(paths, "A.make", "A");
    }

    // Local module alias can be resolved/replaced in the qname finder
    public void testLocalModuleAliasResolution() {
        configureCode("A.ml", "module B = Belt\n module M = struct module O = B.Option let _ = O.m<caret>");

        Set<String> paths = OclQNameFinder.INSTANCE.extractPotentialPaths(myFixture.getElementAtCaret());
        assertSameElements(paths, "A.O", "O", "A.Belt.Option", "Belt.Option");
    }
}
