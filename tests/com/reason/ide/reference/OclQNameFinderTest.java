package com.reason.ide.reference;

import java.util.*;
import com.reason.ide.ORBasePlatformTestCase;
import com.reason.lang.ocaml.OclQNameFinder;

public class OclQNameFinderTest extends ORBasePlatformTestCase {

    public void testLetBinding() {
        configureCode("A.ml", "let make = increase<caret>()");

        Set<String> paths = new OclQNameFinder().extractPotentialPaths(myFixture.getElementAtCaret());
        assertSameElements(paths, "A.make", "A");
    }
}
